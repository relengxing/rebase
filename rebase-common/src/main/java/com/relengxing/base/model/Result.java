package com.relengxing.base.model;

/**
 * @author relengxing
 * @date 2023-09-24 12:12
 * @Description
 **/
public class Result<T> {

    /**
     * 自定义业务码
     */
    private String code;

    /**
     * 自定义业务提示说明
     */
    private String msg;

    /**
     * 自定义返回 数据结果集
     */
    private T data;


    public static <T> Result<T> build(String stateCode, String message, T body) {
        Result<T> result  = new Result<>();
        result.setCode(stateCode);
        result.setMsg(message);
        result.setData(body);
        return result;
    }

    public String getCode() {
        return code;
    }

    public Result<T> setCode(String code) {
        this.code = code;
        return this;
    }


    public Result<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "Result{" +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public String getMsg() {
        return msg;
    }




}
