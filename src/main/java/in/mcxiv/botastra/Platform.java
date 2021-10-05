package in.mcxiv.botastra;

import com.mcxiv.logger.decorations.Format;
import in.mcxiv.botastra.db.DatabaseInterface;
import in.mcxiv.botastra.db.Memory;
import in.mcxiv.botastra.eventsmanagers.MessageCompiler;
import net.dv8tion.jda.api.JDA;

import java.sql.SQLException;

@Format("b")
public class Platform {

    private static Platform instance;
    public Object schema;

    public static void initializeFor(JDA jda) {
        instance = new Platform(jda);
    }

    public volatile Memory memory;
    private final DatabaseInterface databaseInterface;
    private final String tableName;

    private Platform(JDA jda) {

        try {
            databaseInterface = new DatabaseInterface("JDAPlatform");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // todo: use thread to retrieve class name which called this constructor
        tableName = "TABLENAME";
        databaseInterface.createTable(tableName);

//        jda.addEventListener(new MessageCompiler());
//        jda.awaitReady();
    }

    public Memory createDefaultMemory(String methodName, MemoryContext context) {
        return new Memory(databaseInterface, tableName, methodName, context);
    }

}
