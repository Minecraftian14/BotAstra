package in.mcxiv.botastra.csd;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import in.mcxiv.antlr.CSDBaseVisitor;
import in.mcxiv.antlr.CSDLexer;
import in.mcxiv.antlr.CSDParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import javax.lang.model.element.Modifier;
import java.util.LinkedList;
import java.util.Queue;

public class CsdToJavaSourceGenerator2 extends CSDBaseVisitor<TypeSpec.Builder> {

    public static final String PACKAGE = "in.mcxiv.gen";

    public static JavaFile generate(String CSD, String className) {

        CSD = CSD.strip();
        if (!CSD.startsWith("{")) CSD = "{" + CSD;
        if (!CSD.startsWith("}")) CSD += "}";

        CSDLexer lexer = new CSDLexer(CharStreams.fromString(CSD));

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CSDParser parser = new CSDParser(tokens);

        CSDParser.CsdContext tree = parser.csd();
        CsdToJavaSourceGenerator2 visitor = new CsdToJavaSourceGenerator2(className);
        TypeSpec build = visitor.visit(tree).build();

        // TODO
//        CsdToJavaSourceGenerator.ObjectVisitor.count=0;

        return JavaFile.builder(PACKAGE, build)
                .build();
    }


    /**
     * Base class name.
     * This is the name, which will be used to create the final class to be generated.
     * All subclasses derive from it using a nested-counter.
     * <p>
     * Nested-counter? I'll explain later
     * // TODO: implement Nested-counter system, then explain here.
     */
    final String className;

    /**
     * The type spec representing the base class.
     */
    final TypeSpec.Builder baseClass;

    /**
     * The visitor used to create the base class.
     * NOTE, it's stored "not as a local reference because it's used in constructor and visitCsd.
     */
    final ObjectVisitor visitor;

    /**
     * A string to keep track of what kind of parameters can we expect to get.
     * This is an array of Objects consisting of classes of primitive types and
     * their array classes. This also contains int or String instances.
     *
     * When checking for identities, a class object entry requires the object under
     * test to be of the same type. Whereas an int/String object requires the object
     * under test to be ideally the same by == or equals respectively.
     *
     * Note: here we claim the object under test to be tested against the element
     * in this array with the same index as the object under test.
     *
     * This also gives a possibility for testing by simply checking the array length.
     * Important: However, if the last test object is a String Class object, the
     * upcoming test objects can also be treated as a single String!
     *
     * public static final Object[] identity = new Object[] {String.class, int.class, "addmany", 19, float.class};
     *
     * Note: the identity object here only stores the initialization part, ie the part
     * after the '=' sign.
     */
    final StringBuilder identity = new StringBuilder("new Object[] {");

    /**
     * A queue to keep track of how
     */
    final Queue<FieldSpec> fields = new LinkedList<>();
    final Queue<String> assignments = new LinkedList<>();

    public CsdToJavaSourceGenerator2(String className) {
        this.className = className;
        // 1.   Use 'class' received from ObjectVisitor...
        //      Ideally I would want CSD Visit to create a class and ObjectVisitor to create a record...
        //      But javaPoet lacks that feature.
        // 2.   We call a specially made constructor which internally keeps type and baseclass the same object.
        //      Well, that's what would happen when creating the object for base class. So no big deal.
        visitor = new ObjectVisitor(this, true);
        //      After that we also need to save that baseclass instance here!
        this.baseClass = visitor.type;
    }

    @Override
    public TypeSpec.Builder visitCsd(CSDParser.CsdContext ctx) {
        visitor.visitObj(ctx.obj());

        // Add a method for all fields
        MethodSpec.Builder construct = MethodSpec.methodBuilder("construct")
                .addModifiers(Modifier.PUBLIC);
        while (!fields.isEmpty()) {
            FieldSpec spec = fields.remove();
            construct.addParameter(spec.type, spec.name);
            construct.addStatement(assignments.remove());
        }

        baseClass.addMethod(construct.build());

        return baseClass;
    }
}
