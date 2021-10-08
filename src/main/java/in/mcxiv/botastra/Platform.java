package in.mcxiv.botastra;

import com.mcxiv.logger.decorations.Format;
import in.mcxiv.botastra.csd.CsdToJavaSourceGenerator;
import in.mcxiv.botastra.db.DatabaseInterface;
import in.mcxiv.botastra.db.Memory;
import in.mcxiv.botastra.eventsmanagers.CommandCentre;
import in.mcxiv.botastra.eventsmanagers.PlatformManager;
import in.mcxiv.botastra.proc.lang.LisAdaMeta;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

@Format("b")
public class Platform {

    private static Platform instance;
    public Object schema;

    public static void initializeFor(JDA jda) {
        instance = new Platform(jda);
    }

    public static void useDefaultSetup() {

        Properties properties = new Properties();
        try {
            properties.load(Platform.class.getClassLoader().getResourceAsStream("app.properties"));
            String token = properties.getProperty("bot-token");

            JDA jda = JDABuilder.createDefault(token)
                    .setStatus(OnlineStatus.ONLINE)
                    .setActivity(Activity.playing("no game..."))
                    .build();

            Platform.initializeFor(jda);

        } catch (IOException | LoginException e) {
            e.printStackTrace();
        }

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
        jda.addEventListener(new CommandCentre());
        jda.addEventListener(new PlatformManager());

        try {
            Class<?> _clazz = Class.forName(CsdToJavaSourceGenerator.PACKAGE + ".BaseCompatibilityListenerAdapter");
            Class<? extends ListenerAdapter> clazz = _clazz.asSubclass(ListenerAdapter.class);
            ListenerAdapter instance = clazz.getDeclaredConstructor(Platform.class).newInstance(this);
            jda.addEventListener(instance);
        } catch (Exception e) {
            System.err.println("Classes Not Yet Generated!!!");
            e.printStackTrace();
        }

//        jda.awaitReady();
    }

    public Memory createDefaultMemory(String methodName, MemoryContext context) {
        return new Memory(databaseInterface, tableName, methodName, context);
    }

}
