package in.mcxiv.botastra.util;

public class Try {

    public static void Ignore(Runnable action) {
        try {
            action.perform();
        } catch (Exception ignored) {
        }
    }

    public static <T> T Ignore(Supplier<T> action, T def) {
        try {
            return action.perform();
        } catch (Exception e) {
            return def;
        }
    }

    public interface Runnable {
        void perform() throws Exception;
    }

    public interface Supplier<T> {
        T perform() throws Exception;
    }

}
