package in.mcxiv.botastra.db;

import com.mcxiv.logger.formatted.FLog;
import com.mcxiv.logger.tables.Table;
import in.mcxiv.botastra.util.Strings;
import in.mcxiv.botastra.util.Try;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInterface {

    private final JdbcDataSource dataSource;
    private final Connection connection;

    public DatabaseInterface(String database) throws SQLException {

        dataSource = new JdbcDataSource();
        dataSource.setURL(Strings.joinPath("jdbc:h2:", System.getProperty("user.dir"), database));
        dataSource.setUser("bot-astra");
        dataSource.setPassword("bot-astra");
        connection = dataSource.getConnection();

    }

    public void createTable(String table_name) {
        postUpdate(String.format("""
                    CREATE TABLE IF NOT EXISTS %1$s (
                        index INT PRIMARY KEY AUTO_INCREMENT,
                        reference VARCHAR(255) NOT NULL,
                        data_point_1 VARCHAR(255),
                        data_point_2 VARCHAR(255),
                        data_point_3 VARCHAR(255)
                    );
                """, table_name));
    }

    public void insertValues(String table, Object reference, Object data_point_1, Object data_point_2, Object data_point_3) {
        insertValues(table, reference.toString(), data_point_1.toString(), data_point_2.toString(), data_point_3.toString());
    }

    public void insertValues(String table, String reference, String data_point_1, String data_point_2, String data_point_3) {
        postUpdate(String.format("""
                INSERT INTO %1$s
                VALUES (default, '%2$s', '%3$s', '%4$s', '%5$s');
                """, table, reference, data_point_1, data_point_2, data_point_3));
    }

    public EntryAstra queryValues(String table, String reference) {
        return new EntryAstra(postQuery(String.format("""
                    SELECT *
                    FROM   %1$S
                    WHERE  reference='%2$s';
                """, table, reference), false));
    }

    public EntryAstra queryValues(String table, String reference, String data_point_1) {
        return new EntryAstra(postQuery(String.format("""
                    SELECT *
                    FROM   %1$S
                    WHERE
                        reference='%2$s' AND
                        data_point_1='%3$s';
                """, table, reference, data_point_1), false));
    }

    public EntryAstra queryValues(String table, String reference, String data_point_1, String data_point_2) {
        return new EntryAstra(postQuery(String.format("""
                    SELECT *
                    FROM   %1$S
                    WHERE
                        reference='%2$s'      AND
                        data_point_1='%3$s'   AND
                        data_point_2='%4$s';
                """, table, reference, data_point_1, data_point_2), false));
    }

    public void postUpdate(String update) {
        try (
                Statement statement = connection.createStatement();
        ) {
            statement.executeUpdate(update);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ResultSet postQuery(String query, boolean scrollable) {
        try {
            Statement statement;
            if (scrollable)
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            else statement = connection.createStatement();

            return statement.executeQuery(query);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public ResultSet showTables() {
        return postQuery("SHOW TABLES;", true);
    }

    public static int rowCount(ResultSet resultSet) {
        Try.Ignore(resultSet::last);
        int count = Try.Ignore(resultSet::getRow, -1);
        Try.Ignore(resultSet::beforeFirst);
        return count;
    }

    public static void poopResultSet(ResultSet resultSet) {

        try {

            int columnCount = resultSet.getMetaData().getColumnCount();

            int[] columnWidths = new int[columnCount];
            String[] columnNames = new String[columnCount];
            for (int colIdx = 0; colIdx < columnCount; colIdx++) {
                columnWidths[colIdx] = getSuitableWidthForType(resultSet.getMetaData().getColumnTypeName(colIdx + 1));

                String value = resultSet.getMetaData().getColumnName(colIdx + 1);
                if (value.length() > columnWidths[colIdx])
                    value = value.substring(0, columnWidths[colIdx] - 3) + "...";
                columnNames[colIdx] = value;
            }

            Table table = Table.box().head(columnNames);

            while (resultSet.next()) {
                String[] entries = new String[columnCount];
                for (int colIdx = 0; colIdx < columnCount; colIdx++) {
                    int finalColIdx = colIdx;
                    String value = Try.Ignore(() -> resultSet.getObject(finalColIdx + 1).toString(), "!!ERROR!!");
                    entries[colIdx] = Strings.truncateIfLong(value, columnWidths[colIdx]);
                }
                table.row(entries);
            }

            table.create(FLog.getNew());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static int getSuitableWidthForType(String type) {
        switch (type) {
            case "INTEGER":
                return 20;
            default:
                System.out.printf("WARNING: Unregistered Type %s found in DatabaseInterface.getSuitableWidthForType.\n", type);
            case "VARCHAR":
                return 50;
        }
    }

}
