package in.mcxiv.botastra.csd;

import com.squareup.javapoet.TypeSpec;
import in.mcxiv.antlr.CSDBaseVisitor;
import in.mcxiv.antlr.CSDParser;

import javax.lang.model.element.Modifier;

public class ObjectVisitor extends CSDBaseVisitor<TypeSpec.Builder> {

    final TypeSpec.Builder type;
    final CsdToJavaSourceGenerator2 csdGenerator;

    final String className;
    final TypeSpec.Builder baseClass;

    // if 0  -> the base class
    // if !0 -> some inner class
    private static int count = 0;

    /**
     * To be called when defining baseclass.
     */
    public ObjectVisitor(CsdToJavaSourceGenerator2 csdGenerator, boolean areWeDefiningBaseClass) {
        count = 0;
        this.csdGenerator = csdGenerator;
        this.className = this.csdGenerator.className;
        this.baseClass = this.type = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC);
    }

    /**
     * To be called when defining internal objects.
     */
    public ObjectVisitor(CsdToJavaSourceGenerator2 csdGenerator) {
        count++;
        this.csdGenerator = csdGenerator;
        this.className = this.csdGenerator.className;
        this.baseClass = this.csdGenerator.baseClass;
        this.type = TypeSpec.classBuilder(className + count)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC);
    }

    @Override
    public TypeSpec.Builder visitObj(CSDParser.ObjContext ctx) {

        for (CSDParser.EntryContext entry : ctx.entry()) {
            type.addField(new CsdToJavaSourceGenerator.PairVisitor().visitPair(pair));
        }
        for (CSDParser.PairContext pair : ) {
        }

        return type;
    }

    @Override
    public TypeSpec.Builder visitEntry(CSDParser.EntryContext ctx) {
        return visitChildren(ctx);
    }



}
