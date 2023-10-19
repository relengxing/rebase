package com.relengxing.rebase.pass;



public class PassThroughLoaded {

    public static final String clazz = "com.relengxing.rebase.context.PassThroughHolder.class";

    private static boolean passThroughAvailable = true;


    static {
        try {
            Class.forName(clazz);
            passThroughAvailable = true;
        } catch (Throwable t) {
            passThroughAvailable = false;
        }
    }


    public static boolean passThroughLoaded() {
        return passThroughAvailable;
    }


}
