package in.mcxiv.botastra.eventsmanagers;

import in.mcxiv.botastra.proc.BaseCompatibilityListenerAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageCompilerTest {

    @Test
    void test() {

        TestClass schemaObject = MessageCompiler.createSchemaObject("7 @Anirudh 9864328 @Jaspinik #Homen", TestClass.class);

        assertEquals(schemaObject.size, 7);
        assertEquals(schemaObject.members, "@Anirudh");
        assertEquals(schemaObject.author.id, "9864328");
        assertEquals(schemaObject.author.name, "@Jaspinik");
        assertEquals(schemaObject.author.sibi.name, "#Homen");

    }

    public static class TestClass {

        public TestClass() {
        }

        public int size;

        public String members;

        public BaseCompatibilityListenerAdapter.TestClass.TestClass1 author;

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

            public BaseCompatibilityListenerAdapter.TestClass.TestClass2 sibi;
        }
    }

}