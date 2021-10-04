package in.mcxiv.botastra.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringsTest {

    @Test
    void testJoin() {
        assertEquals("", Strings.join("sep"));
        assertEquals("A A A A", Strings.join(" ", "A", "A", "A", "A"));
        assertEquals("Mississippi", Strings.join("i", "M", "ss", "ss", "pp", ""));
    }

    @Test
    void testStrip() {
        assertEquals(" World", Strings.strip("Hello", "Hello World"));
        assertEquals(" World", Strings.strip("Hello", "  Hello World  "));
        assertEquals("World ", Strings.strip("Hello", "World Hello"));
        assertEquals("World ", Strings.strip("Hello", "  World Hello  "));
        assertEquals(" World ", Strings.strip("Hello", "Hello World Hello"));
        assertEquals(" World ", Strings.strip("Hello", "  Hello World Hello  "));
    }

    @Test
    void testJoinPath() {
        assertEquals("D:\\A\\B\\C", Strings.joinPath("D:/A/B/C"));
        assertEquals("D:\\A\\B\\C\\D", Strings.joinPath("D:/A/B/", "/C/D/"));
        assertEquals("D:\\A\\B\\C\\D", Strings.joinPath("D:/A/B", "/C/D/"));
        assertEquals("D:\\A\\B\\C\\D", Strings.joinPath("D:/A/B/", "/C/D"));
        assertEquals("D:\\A\\B\\C\\D", Strings.joinPath("D:/A/B", "/C/D"));
        assertEquals("D:\\A\\B\\C\\D", Strings.joinPath("D:/A/B", "C/D"));
        assertEquals("D:\\A\\B\\C", Strings.joinPath("D:", "A","B","C"));
    }

    @Test
    void testTruncateIfLong() {
        assertEquals("ABC", Strings.truncateIfLong("ABC", 5));
        assertEquals("ABCDE", Strings.truncateIfLong("ABCDE", 5));
        assertEquals("AB...", Strings.truncateIfLong("ABCDEFGHI", 5));
    }
}