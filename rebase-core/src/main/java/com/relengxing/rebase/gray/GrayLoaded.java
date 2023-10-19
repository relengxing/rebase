package com.relengxing.rebase.gray;


/**
 * @author chaoli
 * @date 2023-10-19 22:21
 * @Description
 **/
public class GrayLoaded {

    public static final String clazz = "com.relengxing.rebase.gray.GrayConfiguration.class";
    private static boolean grayAvailable = true;


    static {
        try {
            Class.forName(clazz);
            grayAvailable = true;
        } catch (Throwable t) {
            grayAvailable = false;
        }
    }


    public static boolean grayLoaded() {
        return grayAvailable;
    }


}
