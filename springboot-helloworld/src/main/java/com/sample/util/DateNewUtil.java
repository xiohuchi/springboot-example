package com.sample.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/3/22.
 */
public class DateNewUtil {
    protected final static Logger logger = LoggerFactory.getLogger(DateNewUtil.class);
    private final static DateFormat yyyyMM = new SimpleDateFormat("yyyyMM");
    private final static DateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

    //由时间,返回字符串
    public static String getDateFormat(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String getDateFormatNull(Date date, String format) {
        if (date == null) return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    //由字符串返回时间
    public static Date getDateByString(String date, String format) {
        if (date.trim().length() != 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            try {
                return dateFormat.parse(date);
            } catch (ParseException e) {
                logger.info("时间转换异常", e);
                return null;
            }
        }
        return null;
    }

    /**
     * 判断时间格式 格式必须为“YYYY-MM-dd”
     * 2004-2-30 是无效的
     * 2003-2-29 是无效的
     *
     * @param sDate
     * @return
     */
    public static boolean isValidDate(String sDate) {
        //String str = "2017-01-02";
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = formatter.parse(sDate);
            return sDate.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }


    public static String getLastMonth(String yearMonth, String rtformat) {
        Calendar cal = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyyMM").parse(yearMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        SimpleDateFormat dft = new SimpleDateFormat(rtformat);
        String lastMonth = dft.format(cal.getTime());
        return lastMonth;
    }

    public static String getNextMonth(String yearMonth, String rtformat) {
        Calendar cal = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyyMM").parse(yearMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        SimpleDateFormat dft = new SimpleDateFormat(rtformat);
        String lastMonth = dft.format(cal.getTime());
        return lastMonth;
    }

    public static Date getLastDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        return cal.getTime();
    }

    public static Date getNextDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day + 1);
        return cal.getTime();
    }

    public static String getSpecifiedDayBefore(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);

        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayBefore;
    }

    public static String getSpecifiedDayAfter(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + 1);

        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayBefore;
    }

    public static List getDateList(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        LocalDate localDate = LocalDate.of(year, month, 1);
        LocalDate friday = localDate.with(TemporalAdjusters.firstInMonth(DayOfWeek.FRIDAY));
        LocalDate saturday = friday.minusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        List<String> dateList = new ArrayList<>();
        String nextDate = formatter.format(saturday) + "-" + formatter.format(friday);
        while (nextDate.contains(date) && afterThisWeek(saturday)) {
            dateList.add(nextDate);
            friday = friday.plusWeeks(1);
            saturday = saturday.plusWeeks(1);
            nextDate = formatter.format(saturday) + "-" + formatter.format(friday);
        }

        return dateList;
    }

    /**
     * 检查日期是否超出提交周报日期的星期一
     *
     * @param date
     * @return
     */
    private static boolean afterThisWeek(LocalDate date) {
        LocalDate sunday = date.with(DayOfWeek.SUNDAY);
        LocalDate now = LocalDate.now();
        return now.isAfter(sunday);
    }

    public static String getCurrWeekDate() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, 6);
        String end = yyyyMMdd.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_WEEK, -6);
        String start = yyyyMMdd.format(calendar.getTime());

        return start + "-" + end;
    }

//    public static String coverJsDate(String jsDate, String format) {
//        if (StringUtils.isEmpty(jsDate)) return "";
//        jsDate = jsDate.replace("Z", " UTC");
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
//        Date d = null;
//        try {
//            d = simpleDateFormat.parse(jsDate);
//            return DateNewUtil.getDateFormat(d, format);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return jsDate;
//        }
//    }

    /**
     * 获得该月第一天
     *
     * @param year
     * @param month
     * @return
     */
    public static String getFirstDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String firstDayOfMonth = sdf.format(cal.getTime());
        return firstDayOfMonth;
    }

    /**
     * 获得该月最后一天
     *
     * @param year
     * @param month
     * @return
     */
    public static String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String lastDayOfMonth = sdf.format(cal.getTime());
        return lastDayOfMonth;
    }

    /**
     * 描述:获取下月
     *
     * @return
     */
    public static String getNextMonth() {
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMM");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return dft.format(calendar.getTime());
    }

    /**
     * 描述:获取上月yyyyMM
     *
     * @return
     */
    public static String getLastMonth(String yearMonth) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMM");
        Calendar calendar = Calendar.getInstance();
        Date nowDate = DateNewUtil.getDateByString(yearMonth, "yyyyMM");
        calendar.setTime(nowDate); // 设置为当前时间
        calendar.add(Calendar.MONTH, -1); // 设置为上一个月
        return dft.format(calendar.getTime());
    }

    /**
     * 描述:获取下月
     *
     * @return
     */
    public static String getNextMonth(String yearMonth) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMM");
        Calendar calendar = Calendar.getInstance();
        Date nowDate = DateNewUtil.getDateByString(yearMonth, "yyyyMM");
        calendar.setTime(nowDate); // 设置为当前时间
        calendar.add(Calendar.MONTH, 1);
        return dft.format(calendar.getTime());
    }

    public static boolean todayIsLastOfMonth() {
        //获取当前月最后一天
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDay = format.format(ca.getTime());
        String now = DateNewUtil.getDateFormat(new Date(), "yyyyMMdd");
        if (lastDay.equals(now)) {
            return true;
        } else {
            return false;
        }
    }

    //获取每月的天数
    public static int getDaysOfMonth(String yearMonth) {
        Date date = DateNewUtil.getDateByString(yearMonth, "yyyyMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }


    //获取yyyy-MM-dd 的天
    public static int getDayByYearMonthDay(String time) {
        Date date = DateNewUtil.getDateByString(time, "yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    //根据入职日期获取，转正日期，合同开始日期，合同结束日期
    public static String[] threeDate(String entryDate) {
        LocalDate entry = LocalDate.parse(entryDate);
        LocalDate plusMonths = entry.plusMonths(2);
        LocalDate plusYears = entry.plusYears(2);

        String[] dates = new String[]{entryDate, plusMonths.toString(), plusYears.toString()};
        return dates;
    }


    /**
     * 根据上传的周报日期获取上一周的日期
     *
     * @param date
     * @return
     */
    public static String getLastDate(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));
        LocalDate localDate = LocalDate.of(year, month, day);
        LocalDate begin = localDate.minusWeeks(1L);
        LocalDate end = localDate.minusDays(1L);
        DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
        return yyyyMMdd.format(begin) + "-" + yyyyMMdd.format(end);
    }

    public static String getYearMonth() {
        return yyyyMM.format(new Date());
    }


    //=date1-date2
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) { //同一年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {  //闰年
                    timeDistance += 366;
                } else {  //不是闰年
                    timeDistance += 365;
                }
            }

            return timeDistance + (day1 - day2);
        } else { //不同年
            return day1 - day2;
        }
    }

}
