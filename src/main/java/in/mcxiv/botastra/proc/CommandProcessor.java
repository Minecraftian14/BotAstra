package in.mcxiv.botastra.proc;

import com.google.auto.service.AutoService;
import com.mcxiv.logger.decorations.ConsoleDecoration;
import com.mcxiv.logger.decorations.Format;
import com.mcxiv.logger.formatted.FLog;
import com.squareup.javapoet.JavaFile;
import in.mcxiv.botastra.csd.CsdToJavaSourceGenerator;
import in.mcxiv.botastra.proc.lang.LanguageAPI;
import in.mcxiv.botastra.proc.lang.LisAdaMeta;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_16)
@SupportedAnnotationTypes("in.mcxiv.botastra.proc.BindCommand")
@AutoService(Processor.class)
public class CommandProcessor extends AbstractProcessor {

    public static FLog log;

    private final StringBuilder builder = new StringBuilder();
    private Messager messager;

    private boolean flagHasProcessedOnce = false;

    public Types typeUtils;
    public Elements elementUtils;

    public LanguageAPI __;
    public LisAdaMeta lisAdaMeta;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();

        doop("__Processor Initialized Phase I__");

        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();

        ConsoleDecoration.setColorMode(ConsoleDecoration.BIT_4);
        log = FLog.getNew(this::append);
        log.prt("Custom sysout Online!");

        __ = new LanguageAPI(this);
        lisAdaMeta = new LisAdaMeta(this);

        doop("__Processor Initialized Phase II__");
    }

    @Override
    @Format({":$BBu:", ":: > :$Yn:"})
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log.prt("Processing Requested", flagHasProcessedOnce ? "Declined" : "Accepted");
        if (flagHasProcessedOnce) return endProcess(false, roundEnv);
        log.prt("Processing Starts", System.currentTimeMillis());

        List<ExecutableElement> executableElements = roundEnv
                .getElementsAnnotatedWith(BindCommand.class)
                .stream()
                .filter(__::inAllMethods)
                .map(__::asExecutables)
                .filter(__::inOnlyStaticMethods)
                .filter(__::inOnlyDiparametericMethods)
                .filter(__::inOnlyThoseMethodsWhichHavePlatformAsTheFirstParameter)
                .filter(__::inOnlyThoseMethodsWhoseSecondParameterIsOneOfGenericEventImplementations)
                .toList();

        log.prt("Total number of commands found:", executableElements.size());

        processElements(executableElements);

        return flagHasProcessedOnce = endProcess(true, roundEnv);
    }

    @Format({":$G:", ":: > :$BYn:"})
    private void processElements(List<ExecutableElement> executableElements) {

        HashMap<String, List<ExecutableElement>> eventsUsed = new HashMap<>();

        for (ExecutableElement element : executableElements) {

//          VariableElement platformEle = element.getParameters().get(0);
            VariableElement eventEle = element.getParameters().get(1);

            String eventClassName = eventEle.asType().toString();
            log.prt("Element found as", eventClassName);
            log.prt("Constructed Method as", lisAdaMeta.createAMethod(eventClassName));

            // Add entry to map object.

            if(!eventsUsed.containsKey(eventClassName))
                eventsUsed.put(eventClassName, new ArrayList<>());

            eventsUsed.get(eventClassName).add(element);

            // Retrieve schema constraints if any.
            SchemaObject schema = element.getAnnotation(SchemaObject.class);
            if(schema!=null) {
                writeJavaFile(CsdToJavaSourceGenerator.generate(schema.value()));
            }

        }
    }

    private void writeJavaFile(JavaFile file) {
        try {
            file.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            log.raw(e.toString());
        }
    }

    private void append(String[] strings) {
        for (String string : strings) builder.append(string);
    }

    private boolean endProcess(boolean result, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) messager.printMessage(Diagnostic.Kind.NOTE, "\n" + builder.toString() + "\n");
        return result;
    }

    public void doop(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        return Set.of(BindCommand.class.getCanonicalName());
//    }
}
