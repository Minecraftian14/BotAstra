package in.mcxiv.botastra.csd;

import com.squareup.javapoet.*;
import in.mcxiv.antlr.CSDBaseVisitor;
import in.mcxiv.antlr.CSDLexer;
import in.mcxiv.antlr.CSDParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.lang.model.element.Modifier;
import java.util.LinkedList;
import java.util.Queue;

public class CsdToJavaSourceGenerator extends CSDBaseVisitor<TypeSpec.Builder> {

    public static JavaFile generate(String CSD) {
        CSDLexer lexer = new CSDLexer(CharStreams.fromString(CSD));

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CSDParser parser = new CSDParser(tokens);

        CSDParser.CsdContext tree = parser.csd();
        CsdToJavaSourceGenerator visitor = new CsdToJavaSourceGenerator("TestClass");
        TypeSpec build = visitor.visit(tree).build();

        return JavaFile.builder("in.mcxiv.gen", build)
                .build();
    }

    String className;

    private TypeSpec.Builder baseClass;
    private final Queue<FieldSpec> fields = new LinkedList<>();
    private final Queue<String> assignments = new LinkedList<>();

    public CsdToJavaSourceGenerator(String className) {
        this.className = className;
    }

    @Override
    public TypeSpec.Builder visitCsd(CSDParser.CsdContext ctx) {

        // Use 'class' received from ObjectVisitor...
        // Ideally I would want CSD Visit to create a class and ObjectVisitor to create a record...
        // But javaPoet lacks that feature.
        ObjectVisitor visitor = new ObjectVisitor();
        baseClass = visitor.type;
        visitor.visitObj(ctx.obj());

        // Add a method for all fields
        MethodSpec.Builder construct = MethodSpec.methodBuilder("construct");
        while (!fields.isEmpty()) {
            FieldSpec spec = fields.remove();
            construct.addParameter(spec.type, spec.name);
            construct.addStatement(assignments.remove());
        }

        baseClass.addMethod(construct.build());

        return baseClass;
    }

    public class ObjectVisitor extends CSDBaseVisitor<TypeSpec.Builder> {

        TypeSpec.Builder type;

        public ObjectVisitor() {
            this.type = TypeSpec.classBuilder(className + (count == 0 ? "" : count))
                    .addModifiers(Modifier.PUBLIC);
            count++;
        }

        // if 0  -> the base class
        // if !0 -> some inner class
        private static int count = 0;

        @Override
        public TypeSpec.Builder visitObj(CSDParser.ObjContext ctx) {

            for (CSDParser.PairContext pair : ctx.pair()) {
                type.addField(new PairVisitor().visitPair(pair));
            }

            return type;
        }

    }

    public class PairVisitor extends CSDBaseVisitor<FieldSpec> {

        @Override
        public FieldSpec visitPair(CSDParser.PairContext ctx) {
            String varName = ctx.VARIABLE_NAME().getText();
            TypeVisitor.MetaType type = new TypeVisitor().visitType(ctx.type());
            FieldSpec spec = FieldSpec.builder(type.name, varName, Modifier.PUBLIC).build();

            if (type.isPrimitive())
                fields.add(spec);

            String assignment = String.format("%s = %s", varName, varName);
            ParserRuleContext element = ctx;
            while (element.parent != null) {
                element = element.getParent();
                // if the parent is an object AND it was a value in some pair,
                // it must be a child of some other inner class or base class.
                if (element instanceof CSDParser.ObjContext object) {
                    if (object.getParent() instanceof CSDParser.Complex_typeContext) {
                        CSDParser.PairContext pair = (CSDParser.PairContext) object
                                .getParent() // Complex Type
                                .getParent() // Type
                                .getParent();// Pair
                        assignment = String.format("%s.%s", pair.VARIABLE_NAME().getText(), assignment);
                    }
                }
            }
            assignment = String.format("this.%s", assignment);
            assignments.add(assignment);

            return spec;
        }
    }

    public class TypeVisitor extends CSDBaseVisitor<TypeVisitor.MetaType> {

        @Override
        public MetaType visitType(CSDParser.TypeContext ctx) {
            return visitChildren(ctx);
        }

        @Override
        public MetaType visitPrimitive_type(CSDParser.Primitive_typeContext ctx) {
            return new MetaType(ctx.getText());
        }

        @Override
        public MetaType visitComplex_type(CSDParser.Complex_typeContext ctx) {
            return visitChildren(ctx);
        }

        @Override
        public MetaType visitObj(CSDParser.ObjContext ctx) {
            TypeSpec innerType = new ObjectVisitor().visitObj(ctx).build();
            baseClass.addType(innerType);
            return new MetaType(innerType);
        }

        @Override
        public MetaType visitArr(CSDParser.ArrContext ctx) {
            throw new UnsupportedOperationException("Arr not yet defined");
        }

        public class MetaType {

            public final TypeName name;
            public final boolean isPrimitive;

            public MetaType(String name) {
                this.name = switch (name) {
                    case "int" -> TypeName.INT;
                    case "float" -> TypeName.FLOAT;
                    case "boolean" -> TypeName.BOOLEAN;
                    case "String" -> TypeName.get(String.class);
                    default -> throw new RuntimeException("Unsupported Primitive " + name);
                };
                isPrimitive = true;
            }

            MetaType(TypeSpec spec) {
                this.name = ClassName.get("in.mcxiv.gen", spec.name);
                isPrimitive = false;
            }

            public boolean isPrimitive() {
                return isPrimitive;
            }
        }
    }

}
