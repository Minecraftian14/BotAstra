package in.mcxiv.botastra.proc.lang;

import in.mcxiv.botastra.Platform;
import in.mcxiv.botastra.proc.CommandProcessor;
import net.dv8tion.jda.api.events.GenericEvent;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.reflect.Method;

public final class LanguageAPI {

    // Annotation Processor API Language

    public Types typeUtils;
    public Elements elementUtils;

    public LanguageAPI(CommandProcessor processor) {
        typeUtils = processor.typeUtils;
        elementUtils = processor.elementUtils;
    }

    public boolean inAllMethods(Element element) {
        return element.getKind().equals(ElementKind.METHOD);
    }

    public ExecutableElement asExecutables(Element element) {
        return (ExecutableElement) element;
    }

    public boolean inOnlyStaticMethods(ExecutableElement element) {
        return element.getModifiers().contains(Modifier.STATIC);
    }

    public boolean inOnlyDiparametericMethods(ExecutableElement element) {
        return element.getParameters().size() == 2;
    }

    public boolean inOnlyThoseMethodsWhichHavePlatformAsTheFirstParameter(ExecutableElement element) {
        return typeUtils.isSameType(
                element.getParameters().get(0).asType(),
                elementUtils.getTypeElement(Platform.class.getName()).asType()
        );
    }

    public boolean inOnlyThoseMethodsWhoseSecondParameterIsOneOfGenericEventImplementations(ExecutableElement element) {
        return typeUtils.isAssignable(
                element.getParameters().get(1).asType(),
                elementUtils.getTypeElement(GenericEvent.class.getName()).asType()
        );
    }

    // Reflection API Language

    public boolean outDeprecatedMethods(Method method) {
        return !method.isAnnotationPresent(Deprecated.class);
    }

    public boolean inOnlyPublicMethods(Method method) {
        return java.lang.reflect.Modifier.isPublic(method.getModifiers());
    }

    public boolean out__onEvent(Method method) {
        return !method.getName().equals("onEvent");
    }

    public boolean inEventMethods(Method method) {
        return method.getName().startsWith("on");
    }
}
