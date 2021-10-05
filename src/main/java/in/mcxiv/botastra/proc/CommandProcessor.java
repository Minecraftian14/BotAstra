package in.mcxiv.botastra.proc;

import com.google.auto.service.AutoService;
import com.mcxiv.logger.decorations.ConsoleDecoration;
import com.mcxiv.logger.decorations.Format;
import com.mcxiv.logger.formatted.FLog;
import com.squareup.javapoet.*;
import in.mcxiv.botastra.MemoryContext;
import in.mcxiv.botastra.Platform;
import in.mcxiv.botastra.csd.CsdToJavaSourceGenerator;
import in.mcxiv.botastra.proc.lang.LanguageAPI;
import in.mcxiv.botastra.proc.lang.LisAdaMeta;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
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
        HashMap<ExecutableElement, String> schemaMap = new HashMap<>();
        HashMap<ExecutableElement, Integer> contextMap = new HashMap<>();

        SORT_METHODS_CREATED_BY_THE_EVENTS_THEY_ARE_REGISTERED_TO:
        AND:
        RETRIEVE_ALL_RELATED_ANNOTATION_DEFINITIONS:

        for (ExecutableElement element : executableElements) {

//          VariableElement platformEle = element.getParameters().get(0);
            VariableElement eventEle = element.getParameters().get(1);

            String eventClassName = eventEle.asType().toString();
            log.prt("Element found as", eventClassName);

            // Add entry to map object.

            if (!eventsUsed.containsKey(eventClassName))
                eventsUsed.put(eventClassName, new ArrayList<>());

            eventsUsed.get(eventClassName).add(element);

            // Retrieve schema constraints if any.
            SchemaObject schema = element.getAnnotation(SchemaObject.class);
            if (schema != null) {
                JavaFile schemaFile = CsdToJavaSourceGenerator.generate(schema.value(), element.getSimpleName() + "Schema");
                schemaMap.put(element, schemaFile.packageName + "." + schemaFile.typeSpec.name);
                writeJavaFile(schemaFile);
            }

            // Retrieve schema constraints if any.
            SetMemoryContext context = element.getAnnotation(SetMemoryContext.class);
            if (context != null) {
                contextMap.put(element, context.value());
            }
        }

        USE_ALL_THE_COLLECTED_DATA_TO_CREATE_A_LIS_ADA:
        {

            TypeSpec.Builder lisAda = TypeSpec.classBuilder("BaseCompatibilityListenerAdapter")
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(ListenerAdapter.class)
                    .addField(FieldSpec.builder(Platform.class, "platform", Modifier.PRIVATE, Modifier.FINAL).build())
                    .addMethod(MethodSpec.constructorBuilder()
                            .addParameter(Platform.class, "platform")
                            .addStatement("this.platform = platform")
                            .build());

            for (String eventClassName : eventsUsed.keySet()) {
                MethodSpec.Builder eventMethod = lisAdaMeta.createAMethod(eventClassName);

                for (ExecutableElement command : eventsUsed.get(eventClassName)) {

                    if(contextMap.containsKey(command))
                        eventMethod.addStatement("platform.memory = platform.createDefaultMemory($S, new MemoryContext($L))", command.getSimpleName(), contextMap.get(command));

                    if(schemaMap.containsKey(command))
                        eventMethod.addStatement("platform.schema = MessageCompiler.createSchemaObject(event.getMessage().toString(), $L.class)", schemaMap.get(command));

                    eventMethod.addStatement("methodHere(platform, event)");
                }

                lisAda.addMethod(eventMethod.build());
            }

            writeJavaFile(JavaFile.builder("in.mcxiv.gen", lisAda.build()).build());

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
