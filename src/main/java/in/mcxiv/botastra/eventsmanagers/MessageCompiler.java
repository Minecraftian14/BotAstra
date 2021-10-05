package in.mcxiv.botastra.eventsmanagers;

import in.mcxiv.botastra.proc.BaseCompatibilityListenerAdapter;
import in.mcxiv.botastra.util.Try;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MessageCompiler {

    private enum Type {
        INT,
        FLOAT,
        BOOLEAN,
        STRING;

        static Type classToType(Class<?> clazz) {
            if (clazz.equals(int.class)) return Type.INT;
            if (clazz.equals(float.class)) return Type.FLOAT;
            if (clazz.equals(boolean.class)) return Type.BOOLEAN;
            if (clazz.equals(String.class)) return Type.STRING;
            throw new IllegalArgumentException("Unknown primitive class " + clazz);
        }
    }

    public static <T> T createSchemaObject(String text, Class<T> clazz) {
        try {
            return createSchemaObjectE(text, clazz);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        for (Method method : BaseCompatibilityListenerAdapter.TestClass.class.getDeclaredMethods()) {
            System.out.println(method.getName());
        }
    }

    public static <T> T createSchemaObjectE(String text, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        T instance = clazz.getConstructor().newInstance();
        Method constructor = clazz.getDeclaredMethods()[0];
        Class<?>[] paramCls = constructor.getParameterTypes();

        String[] args = text.split(" ");

        Object[] paramVal = new Object[paramCls.length];

        for (int i = 0; i < Math.min(args.length, paramVal.length); i++) {
            paramVal[i] = switch (Type.classToType(paramCls[i])){
                case INT -> intify(args[i]);
                case FLOAT -> floatify(args[i]);
                case BOOLEAN -> booleanify(args[i]);
                case STRING -> stringify(args[i]);
            };
        }

        constructor.invoke(instance, paramVal);

        return instance;

    }

    private static int intify(String arg) {
        return Try.Ignore(() -> Integer.parseInt(arg.replaceAll("[^0-9]", "")), -1);
    }

    private static float floatify(String arg) {
        return Try.Ignore(() -> Float.parseFloat(arg.replaceAll("[^0-9.]", "")), -1f);
    }

    private static boolean booleanify(String arg) {
        return Try.Ignore(() -> Boolean.parseBoolean(arg), false);
    }

    private static String  stringify(String arg) {
        return arg;
    }

}
