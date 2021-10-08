package in.mcxiv.botastra.proc.lang;

import com.squareup.javapoet.*;
import in.mcxiv.botastra.proc.CommandProcessor;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
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

    public MethodSpec.Builder createAMethod(String eventClassName) {
        assert map.containsKey(eventClassName);

        String packageName = eventClassName.substring(0,eventClassName.lastIndexOf("."));
        String className = eventClassName.substring(eventClassName.lastIndexOf(".")+1);

        return MethodSpec.methodBuilder(map.get(eventClassName))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(ClassName.get(packageName, className), "event" )
                        .addAnnotation(NotNull.class)
                        .build())
                .addCode("""
                        if(event.getAuthor().isBot()) return;
                        String raw = verifyCommand(event.getMessage().getContentRaw());
                        if(raw==null) return;
                        """);

        // TODO: add prefix check
        // use String rawContent = event.getMessage().getContentRaw();
    }



}
