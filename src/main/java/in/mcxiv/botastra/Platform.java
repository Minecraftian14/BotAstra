package in.mcxiv.botastra;

import com.mcxiv.logger.decorations.Format;
import in.mcxiv.botastra.db.Memory;
import in.mcxiv.botastra.eventsmanagers.MessageCompiler;
import net.dv8tion.jda.api.JDA;

@Format("b")
public class Platform {

    private static Platform instance;

    public static void initializeFor(JDA jda) {
        instance = new Platform(jda);
    }

    public volatile Memory memory;

    private Platform(JDA jda) {
        jda.addEventListener(new MessageCompiler());
//        jda.awaitReady();
    }

}
