package in.mcxiv.botastra.util;

import java.io.File;

public class Strings {

    public static String join(String joiner, String... words) {
        if (words.length == 0) return "";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length - 1; builder.append(words[i++]).append(joiner)) ;
        return builder.append(words[words.length - 1]).toString();
    }

    public static String strip(String context, String word) {
        word = word.trim();
        if (word.startsWith(context)) word = word.substring(context.length());
        if (word.endsWith(context)) word = word.substring(0, word.length() - context.length());
        return word;
    }

    public static String joinPath(String... subDirs) {
        String joiner = File.separator;
        for (int i = 0; i < subDirs.length; subDirs[i] = subDirs[i].replace("/", joiner), i++) ;
        for (int i = 0; i < subDirs.length; subDirs[i] = strip(joiner, subDirs[i]), i++) ;
        return join(joiner, subDirs);
    }

    public static String truncateIfLong(String value, int length) {
        assert length > 3;
        if (value.length() > length)
            return value.substring(0, length - 3) + "...";
        return value;
    }

}
