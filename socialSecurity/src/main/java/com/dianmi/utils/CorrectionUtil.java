package com.dianmi.utils;

/**
 * created by www
 * 2017/10/26 14:23
 */
public class CorrectionUtil {

    /**
     * @param cityName
     * @return
     */
    public static String correctionCity(String cityName){
         if(cityName.length()>2 && cityName.contains("市")){
            cityName = cityName.substring(0,cityName.length()-1);
         }
         return cityName;
    }

    public static void main(String args[]){
       System.out.println(correctionCity("深圳市"));
    }

}
