package ch.epfl.test;

import ch.epfl.javelo.projection.SwissBounds;

public class TestUtile {
    public static final double LAUSANNE_MIN_E = 2_520_000;
    public static final double LAUSANNE_MAX_E = 2_550_000;
    public static final double LAUSANNE_MIN_N = 1_145_000;
    public static final double LAUSANNE_MAX_N = 1_165_000;
    public final static String LAUSANNE_PATH = "lausanne";

    public static int r(int minimum, int maximum){
        int random = (int) (Math.random()*(maximum-minimum)+minimum);
        //System.out.println(random + " ");
        return random;
    }
    public static byte b(){

        return (byte) r(Byte.MIN_VALUE, Byte.MAX_VALUE);
    }
    public static byte bp(){
        return (byte) r(0, Byte.MAX_VALUE);
    }

    public static short s(){
        return (short) r(Short.MIN_VALUE, Short.MAX_VALUE);
    }

    public static short sp(){
        return (short) r(0, Short.MAX_VALUE);
    }

    public static int i(){
        return r(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static int ip(){
        return r(0, Integer.MAX_VALUE);
    }

    public static int ipp(){
        return r(1, Integer.MAX_VALUE);
    }

    public static int eLaus(){
        return r((int) LAUSANNE_MIN_E, (int) LAUSANNE_MAX_E);
    }

    public static int nLaus(){
        return r((int) LAUSANNE_MIN_N, (int) LAUSANNE_MAX_N);
    }

    public static int e(){
        return r((int)SwissBounds.MIN_E, (int)SwissBounds.MAX_E);
    }

    public static int n(){
        return r((int)SwissBounds.MIN_N, (int)SwissBounds.MAX_N);
    }

}
