package com.sample.contsant.rest;

/**
 * 封装json返回
 *
 * @author www
 */
public class RestUtils {

    public static RestJson success() {
        return new RestJson<>(RestEnum.SUCCESS.getStatus(), RestEnum.SUCCESS.getMessage(), null);
    }

    public static RestJson success(Object o) {
        return new RestJson<>(RestEnum.SUCCESS.getStatus(), RestEnum.SUCCESS.getMessage(), o);
    }


    public static RestJson error(int code, String msg) {
        return new RestJson<>(code, msg, null);
    }

    public static RestJson error(RestEnum restEnum) {
        return new RestJson<>(restEnum.getStatus(), restEnum.getMessage(), null);
    }

    public static RestJson error(RestEnum restEnum, String msg) {
        return new RestJson<>(restEnum.getStatus(), msg, null);
    }
    public static RestJson error(RestEnum restEnum,String msg, Object data) {
        return new RestJson<>(restEnum.getStatus(), msg, data);
    }
}
