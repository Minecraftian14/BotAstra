package in.mcxiv.botastra.db;

import in.mcxiv.botastra.MemoryContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import static in.mcxiv.botastra.MemoryContext.RootMemoryContexts.CHANNEL;
import static in.mcxiv.botastra.MemoryContext.RootMemoryContexts.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseInterfaceTest {

    private static DatabaseInterface dbi;

    @BeforeAll
    static void beforeAll() throws SQLException {
        dbi = new DatabaseInterface(DatabaseInterface.class.getSimpleName());
    }


    @Test
    void testCreationOfTableIfOldTableDoesNotExist() {

        dropAll();

        ResultSet resultSet = dbi.showTables();
        assertEquals(0, DatabaseInterface.rowCount(resultSet));
        DatabaseInterface.poopResultSet(resultSet);

        dbi.createTable("test_1");
        resultSet = dbi.showTables();
        assertEquals(1, DatabaseInterface.rowCount(resultSet));
        DatabaseInterface.poopResultSet(resultSet);

        dbi.createTable("test_2");
        resultSet = dbi.showTables();
        assertEquals(2, DatabaseInterface.rowCount(resultSet));
        DatabaseInterface.poopResultSet(resultSet);

        dbi.createTable("test_1");
        resultSet = dbi.showTables();
        assertEquals(2, DatabaseInterface.rowCount(resultSet));
        DatabaseInterface.poopResultSet(resultSet);

    }

    @Test
    void testMemory() {

        JUST_CHECKING_IF_WE_HAVE_MEMORIES_OF_PAST:
        {
            if (new EntryAstra(dbi.showTables()).colContains(0, "MEMORY_TEST")) {
                DatabaseInterface.poopResultSet(dbi.postQuery("SELECT * FROM MEMORY_TEST;", false));
                dropAll();
            }
        }

        dbi.createTable("MEMORY_TEST");
        DatabaseInterface.poopResultSet(dbi.selectStar("MEMORY_TEST"));

        Memory memory = new Memory(dbi, "MEMORY_TEST", "testMemory()", new MemoryContext(USER).add(CHANNEL));

        Random random = new Random();
        int ranI = random.nextInt();
        float ranF = random.nextFloat();
        String ranS = "" + random.nextDouble();
        List<?> ranL = List.of(ranI, ranF, ranS);

        memory.put("ranI", ranI);
        memory.put("ranF", ranF);
        memory.put("ranS", ranS);
        memory.put("ranL", ranL);

        DatabaseInterface.poopResultSet(dbi.selectStar("MEMORY_TEST"));

        int new_ranI = memory.getI("ranI");
        float new_ranF = memory.getF("ranF");
        String new_ranS = memory.getS("ranS");
        // Because data was stored like String, it's not currently supported...
        // Though creating it's support is easy.
//        List<?> new_ranL = memory.getT("ranL");

        assertEquals(ranI, new_ranI);
        assertEquals(ranF, new_ranF);
        assertEquals(ranS, new_ranS);
//        assertEquals(ranL, new_ranL);
    }

    @Test
    void testMemoryUpdating() {

        dbi.createTable("MEMORY_TEST");

        DatabaseInterface.poopResultSet(dbi.selectStar("MEMORY_TEST"));

        Memory memory = new Memory(dbi, "MEMORY_TEST", "testMemoryUpdating()", new MemoryContext(USER).add(CHANNEL));

        Random random = new Random();
        float ranF = random.nextFloat();

        memory.put("ranF", memory.getF("ranF")+ranF);
        float new_ranF = memory.getF("ranF");

        System.out.println(ranF +" "+ new_ranF);
        assertTrue(ranF < new_ranF);
    }

    public void dropAll() {
        ResultSet resultSet = dbi.showTables();
        try {
            while (resultSet.next()) {
                dbi.postUpdate(String.format("DROP TABLE IF EXISTS %s;", resultSet.getString(1)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}