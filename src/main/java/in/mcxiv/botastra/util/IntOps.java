package in.mcxiv.botastra.util;

public class IntOps {

    public static int UNION(int A, int B) {
        return A | B;
    }

    public static int UNION(int... As) {
        int res = 0;
        for (int i = 0; i < As.length; res |= As[i++]) ;
        return res;
    }

    public static int UNION(Identifiable... As) {
        int res = 0;
        for (int i = 0; i < As.length; res |= As[++i].identity()) ;
        return res;
    }

}
