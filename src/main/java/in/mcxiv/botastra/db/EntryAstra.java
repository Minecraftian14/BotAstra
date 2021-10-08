package in.mcxiv.botastra.db;

import in.mcxiv.botastra.util.Try;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class EntryAstra {

    private int rows;
    private int columns;
    private final Object[] array;
    private final boolean isEmpty;

    public EntryAstra(ResultSet resultSet) {
        columns = Try.Ignore(() -> resultSet.getMetaData().getColumnCount(), -1);
        assert columns != -1;

        ArrayList<Object> list = new ArrayList<>();

        boolean flagIsEmpty = true; // let's assume it's empty.
        try {
            while (resultSet.next()) {
                flagIsEmpty = false; // we got a value, therefore it's not empty.
                for (int colIdx = 0; colIdx < columns; colIdx++)
                    list.add(resultSet.getObject(colIdx + 1));
                rows++;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        isEmpty = flagIsEmpty;

        array = list.toArray();
    }

    public Object at(int x, int y) {
        return array[x * columns + y];
    }

    public String atS(int x, int y) {
        return at(x, y).toString();
    }

    public int atI(int x, int y) {
        return Try.Ignore(() -> Integer.parseInt(atS(x, y)), -1);
    }

    public float atF(int x, int y) {
        return Try.Ignore(() -> Float.parseFloat(atS(x, y)), -1f);
    }

    @SuppressWarnings("unchecked")
    public <T> T atT(int x, int y) {
        return (T) at(x, y);
    }

    public boolean colContains(int colIdx, String value) {
        for (int i = colIdx; i < array.length; i += columns)
            if (Objects.equals(array[i], value))
                return true;
        return false;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
