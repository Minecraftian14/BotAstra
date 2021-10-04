package com.mcxiv.logger.processor;

import com.mcxiv.logger.decorations.Decorations;
import com.mcxiv.logger.formatted.FLog;
import com.mcxiv.logger.util.ByteConsumer;
import com.mcxiv.logger.util.StringsConsumer;

import java.io.OutputStream;
import java.util.HashMap;

public abstract class ALog extends FLog {

    public ALog() {
        setDecorationType(Decorations.CONSOLE);
    }

    public static FLog getNew() {
        return new Logger_AnnotationRetriever();
    }

    public static FLog getNew(OutputStream stream) {
        return new Logger_AnnotationRetriever(stream);
    }

    public static FLog getNew(ByteConsumer consumer) {
        return new Logger_AnnotationRetriever(consumer);
    }

    public static FLog getNew(StringsConsumer consumer) {
        return new Logger_AnnotationRetriever(consumer);
    }

    static HashMap<String, String[]> map = new HashMap<>();

    {
        map.put("in.mcxiv.botastra.Platform", new String[]{"b"});
    }

    @Override
    public void setDecorationType(String name) {
        super.setDecorationType(name);
        map.forEach((k, f) ->
                Decorations.put(
                        new Decorations.Tag(
                                k.substring(0, k.indexOf(":")),
                                k.substring(k.indexOf(":") + 1),
                                getDecorationType()
                        ),
                        Decorations.getSpecific(null, getDecorationType(), f)
                )
        );
    }
}
