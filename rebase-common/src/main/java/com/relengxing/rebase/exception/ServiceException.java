package com.relengxing.rebase.exception;

/**
 * @author relengxing
 * @date 2023-09-24 12:12
 * @Description
 **/
public class ServiceException extends RuntimeException{

    private String code;

    public ServiceException(Throwable e) {
        super(e);
        this.code = "500";
    }

    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(String code, Throwable ex) {
        super(ex);
        this.code = code;
    }

    public ServiceException(String code, String message, Throwable ex) {
        super(message, ex);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
