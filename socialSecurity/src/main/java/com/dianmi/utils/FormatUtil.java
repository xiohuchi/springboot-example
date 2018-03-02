package com.dianmi.utils;

/**
 * created by www
 * 2017/10/16 14:57
 */
public class FormatUtil {

    public static String yearMonth(String yearMonth) {
        return yearMonth.trim().replaceAll("—", "-");
    }

}
