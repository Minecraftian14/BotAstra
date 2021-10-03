package in.mcxiv.botastra;

import in.mcxiv.botastra.util.Identifiable;

public class MemoryContexts implements Identifiable {

//    public static final MemoryContexts NONE = new MemoryContexts(RootMemoryContexts.NONE);

    private final int context_level;

    public MemoryContexts(int context_level) {
        this.context_level = context_level;
    }

    public MemoryContexts(RootMemoryContexts context) {
        this(context.identity());
    }

    @Override
    public int identity() {
        return context_level;
    }

    public boolean is(int sub_context) {
        return (context_level & sub_context) != 0;
    }

    public boolean is(RootMemoryContexts sub_context) {
        return is(sub_context.identity());
    }

    public boolean is(MemoryContexts sub_context) {
        return is(sub_context.identity());
    }

    public MemoryContexts add(int sub_context) {
        int neo_context = context_level | sub_context;
        if (context_level != neo_context)
            return new MemoryContexts(neo_context);
        return this;
    }

    public MemoryContexts add(RootMemoryContexts sub_context) {
        return add(sub_context.identity());
    }

    public MemoryContexts add(MemoryContexts sub_context) {
        return add(sub_context.identity());
    }

    public MemoryContexts clear(int sub_context) {
        // TODO: Test if it works
        int neo_context = context_level & (~sub_context);
        if (context_level != neo_context)
            return new MemoryContexts(neo_context);
        return this;
    }

    public MemoryContexts clear(RootMemoryContexts sub_context) {
        return clear(sub_context.identity());
    }

    public MemoryContexts clear(MemoryContexts sub_context) {
        return clear(sub_context.identity());
    }

    public enum RootMemoryContexts implements Identifiable {
        NONE,
        USER,
        CHANNEL,
        SERVER,
        UNION;

        private final int identity;

        RootMemoryContexts() {
            this.identity = 1 << ordinal();
        }

        @Override
        public int identity() {
            return identity;
        }

    }

}