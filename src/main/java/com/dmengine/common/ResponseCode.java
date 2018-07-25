package com.dmengine.common;

/**
 * 2018-06-21-9:27 Author By AgainP
 */
public enum ResponseCode {

    SUCCESS(0,"SUCCESS"),
    REPEAT(4,"REPEAT_EXECUTE"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT"),
    ERROR(1,"ERROR"),
    DATA_REWRITE(-2,"DATA_REWRITE");

    private final int code;

    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
