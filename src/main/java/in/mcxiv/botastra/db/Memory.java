package in.mcxiv.botastra.db;

import in.mcxiv.botastra.MemoryContext;

import java.util.List;

public class Memory {

    public static final int VALUE_INDEX = 4;

    private final DatabaseInterface dbi;
    private final String tableName;
    private final String reference;
    private final MemoryContext context;

    public Memory(DatabaseInterface dbi, String tableName, String reference, MemoryContext context) {
        assert new EntryAstra(dbi.showTables()).colContains(0, tableName);
        this.dbi = dbi;
        this.tableName = tableName;
        this.reference = reference;
        this.context = context;
    }

    public void put(String key, Object value) {
        dbi.insertValues(
                tableName,
                reference,
                context.identity(),
                key,
                value
        );
    }

    protected EntryAstra get(String key) {
        return dbi.queryValues(
                tableName,
                reference,
                String.valueOf(context.identity()),
                key);
    }

    public Object getO(String key) {
        return get(key).at(0, VALUE_INDEX);
    }

    public String getS(String key) {
        return get(key).atS(0, VALUE_INDEX);
    }

    public int getI(String key) {
        return get(key).atI(0, VALUE_INDEX);
    }

    public float getF(String key) {
        return get(key).atF(0, VALUE_INDEX);
    }

    public <T> T getT(String key) {
        return get(key).atT(0, VALUE_INDEX);
    }
}
