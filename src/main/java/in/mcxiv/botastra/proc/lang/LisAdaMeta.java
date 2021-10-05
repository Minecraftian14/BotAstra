package in.mcxiv.botastra.proc.lang;

import in.mcxiv.botastra.proc.CommandProcessor;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LisAdaMeta {

    private final HashMap<String, String> map = new HashMap<>();

    public LisAdaMeta(CommandProcessor processor) {

        LanguageAPI __ = processor.__;
        Class<ListenerAdapter> clazz = ListenerAdapter.class;

        List<Method> methods = Arrays.stream(clazz.getMethods())
                .filter(__::outDeprecatedMethods)
                .filter(__::inOnlyPublicMethods)
                .filter(__::out__onEvent)
                .filter(__::inEventMethods)
                .toList();

        for (Method method : methods) {
            if (method.getParameterCount() != 0)
                map.put(
                        method.getParameterTypes()[0].getName(),
                        method.getName()
                );
            else
                throw new RuntimeException("Unhandled method came into existence! ListenerAdaptor#" + method.getName() + " should not be in the final list!");
        }

    }

    public String createAMethod(String eventName) {
        assert map.containsKey(eventName);

        return String.format(
                """
                            public void %1$s(@Nonnull %2$s event) {
                                
                            }
                        """, map.get(eventName), eventName);
    }

}
