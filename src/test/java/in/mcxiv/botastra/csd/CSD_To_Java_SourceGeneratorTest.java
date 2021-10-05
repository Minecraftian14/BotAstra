package in.mcxiv.botastra.csd;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import in.mcxiv.antlr.CSDLexer;
import in.mcxiv.antlr.CSDParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class CSD_To_Java_SourceGeneratorTest {

    static final String csd = """
                {
                    "title": "example glossary",
                    "GlossDiv": {
                        "title": "S",
                        "GlossList": {
                            "GlossEntry": {
                                "ID": "SGML",
                                "SortAs": "SGML",
                                "GlossTerm": "Standard Generalized Markup Language",
                                "Acronym": "SGML",
                                "Abbrev": "ISO 8879:1986",
                                "GlossDef": {
                                    "para": "A meta-markup language, used to create markup languages such as DocBook.",
                                    "GlossSeeAlso": ["GML", "XML"]
                                },
                                "GlossSee": "markup"
                            }
                        }
                    }
                }
                """;

    @Test
    void SimpleParsingTest2() throws IOException {

        CSDLexer lexer = new CSDLexer(CharStreams.fromString("""
                { size:int, members:String, author: { id:int, name:String, sibi:{name2:String} } }
                """));

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CSDParser parser = new CSDParser(tokens);

        CSDParser.CsdContext tree = parser.csd();
        CsdToJavaSourceGenerator visitor = new CsdToJavaSourceGenerator("TestClass");
        TypeSpec build = visitor.visit(tree).build();

        JavaFile.builder("in.mcxiv.gen", build)
                .build()
                .writeTo(System.out);

    }
}