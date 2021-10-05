package in.mcxiv.botastra.proc;

import in.mcxiv.botastra.MemoryContext;
import in.mcxiv.botastra.Platform;
import in.mcxiv.botastra.eventsmanagers.MessageCompiler;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BaseCompatibilityListenerAdapter extends ListenerAdapter {

    private final Platform platform;

    public BaseCompatibilityListenerAdapter(Platform platform) {
        this.platform = platform;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

        platform.memory = platform.createDefaultMemory("methodHere", MemoryContext.DEFAULT);
        platform.schema = MessageCompiler.createSchemaObject(event.getMessage().toString(), TestClass.class);
        methodHere(platform, event);

    }

    public static void methodHere(Platform platform, GuildMessageReceivedEvent event) {

    }

    public class TestClass {
        public int size;

        public String members;

        public TestClass1 author;

        void construct(int size, String members, int id, String name, String name2) {
            this.size = size;
            this.members = members;
            this.author.id = id;
            this.author.name = name;
            this.author.sibi.name = name2;
        }

        public class TestClass2 {
            public String name;
        }

        public class TestClass1 {
            public int id;

            public String name;

            public TestClass2 sibi;
        }
    }


}
