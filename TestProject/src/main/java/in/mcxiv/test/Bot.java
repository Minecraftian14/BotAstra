package in.mcxiv.test;

import com.mcxiv.logger.decorations.Format;
import in.mcxiv.botastra.Platform;
import in.mcxiv.botastra.proc.BindCommand;
import in.mcxiv.botastra.proc.SchemaObject;
import in.mcxiv.botastra.proc.SetMemoryContext;
import in.mcxiv.botastra.proc.SetPrefix;
import in.mcxiv.gen.testCommandSchema;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Properties;

public class Bot {

    @SetPrefix({"fb", "bb"})
    public static void main(String[] args) throws Exception {
        Platform.useDefaultSetup();
    }

    @BindCommand
    @SetMemoryContext(63)
    @SchemaObject("caller:String, ader:{ a:int, b:int }")
    @Format(":$B:")
    public static void testCommand(Platform platform, GuildMessageReceivedEvent event) {
        int count = platform.memory.getI("count");
        platform.memory.put("count", count + 1);
        testCommandSchema schema = (testCommandSchema) platform.schema;
        System.out.println(schema.caller + " {" + schema.ader.a + ", " + schema.ader.b + "}");
        event.getChannel().sendMessage("'elo " + count).queue();
    }

    @BindCommand
    @SetMemoryContext(63)
    @SchemaObject("caller:int, ader:{ a:int b:int }")
    public static void testCommand2(Platform platform, GuildMessageReceivedEvent event) {
        int count = platform.memory.getI("count");
        System.out.println("Haul");
        event.getChannel().sendMessage("'elo " + count).queue();
    }

}
