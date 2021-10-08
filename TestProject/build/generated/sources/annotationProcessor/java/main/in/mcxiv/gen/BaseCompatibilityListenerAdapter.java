package in.mcxiv.gen;

import in.mcxiv.botastra.Platform;
import java.lang.Override;
import java.lang.String;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BaseCompatibilityListenerAdapter extends ListenerAdapter {
  public static final String[] prefixes = new String[] {"fb", "bb"};

  private final Platform platform;

  public BaseCompatibilityListenerAdapter(Platform platform) {
    this.platform = platform;
  }

  private static String verifyCommand(String msg) {
    for (int i = 0; i < prefixes.length; i++) 
        if(msg.startsWith(prefixes[i]))
            return msg.substring(prefixes[i].length()+1);
    return null;
  }

  @Override
  public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
    if(event.getAuthor().isBot()) return;
    String raw = verifyCommand(event.getMessage().getContentRaw());
    if(raw==null) return;
    platform.memory = platform.createDefaultMemory("testCommand", new in.mcxiv.botastra.MemoryContext(63));
    platform.schema = in.mcxiv.botastra.eventsmanagers.MessageCompiler.createSchemaObject(raw, in.mcxiv.gen.testCommandSchema.class);
    in.mcxiv.test.Bot.testCommand(platform, event);
    platform.memory = platform.createDefaultMemory("testCommand2", new in.mcxiv.botastra.MemoryContext(63));
    platform.schema = in.mcxiv.botastra.eventsmanagers.MessageCompiler.createSchemaObject(raw, in.mcxiv.gen.testCommand2Schema.class);
    in.mcxiv.test.Bot.testCommand2(platform, event);
  }
}
