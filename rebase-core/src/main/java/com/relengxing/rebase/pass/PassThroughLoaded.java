package com.relengxing.rebase.pass;


import com.relengxing.rebase.context.PassThroughHolder;

public class PassThroughLoaded {

    private static boolean passThroughAvailable = false;


    public static boolean passThroughLoaded() {
        try {
            PassThroughHolder.getAll();
            passThroughAvailable = true;
        } catch (Throwable t) {
            passThroughAvailable = false;
        }
        return passThroughAvailable;
    }


}
