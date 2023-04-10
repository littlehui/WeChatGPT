package com.idaymay.dzt.common.utils.date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * TODO
 * @ClassName DateActionUtils
 * @author littlehui
 * @date 2021/7/27 15:44
 * @version 1.0
 **/
public class DateActionUtils {

    /**
     * 默认的日期格式,yyyy-MM-dd.
     */
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 数字格式的日期格式,yyyyMMdd.
     */
    public static final String NUMBER_DATE_FORMAT = "yyyyMMdd";

    /**
     * 数字格式的时间字符串,HHmmss.
     */
    public static final String NUMBER_TIME_FORMAT = "HHmmss";

    /**
     * 数字格式的日期时间字符串, yyyyMMddHHmmss.
     */
    public static final String NUMBER_DATE_TIME_FORMAT = "yyyyMMddHHmmss";

    /**
     * 默认的日期时间格式,yyyy-MM-dd' 'HH:mm:ss.
     */
    public static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd' 'HH:mm:ss";

    /**
     * 一天的毫秒数
     */
    public static final long ONE_DATE_MILLIS = 1000 * 60 * 60 * 24;

    /**
     * 转换时间格式
     * @param date
     * @param oldFormat 旧的时间格式
     * @param newFormat 新的时间格式
     * @author littlehui
     * @date 2021/11/15 15:46
     * @return java.lang.String
     */
    public static String transformDateFormat(String date, String oldFormat,
                                             String newFormat) {
        if (date == null) {
            return null;
        }
        Date tempDate = parseDate(date, oldFormat);
        return formatDate(tempDate, newFormat);
    }

    /**
     * 时间格式转换成yyyy-MM-dd
     * @param stringDate 原始时间
     * @author littlehui
     * @date 2021/11/15 15:48
     * @return java.util.Date
     */
    public static Date parseDate(String stringDate) {
        return parseDate(stringDate, ISO_DATE_FORMAT);
    }

    /**
     * 转换成format格式
     * @param stringDate 原始时间
     * @param format 格式
     * @author littlehui
     * @date 2021/11/15 15:48
     * @return java.util.Date
     */
    public static Date parseDate(String stringDate, String format) {
        if (stringDate == null) {
            return null;
        }
        try {
            return DateUtils.parseDate(stringDate, new String[] { format });
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将Date格式时间转成yyyy-MM-dd格式
     * @param srcDate 原始时间
     * @author littlehui
     * @date 2021/11/15 15:48
     * @return java.lang.String
     */
    public static String formatDate(Date srcDate) {
        return formatDate(srcDate, ISO_DATE_FORMAT);
    }

    /**
     * 将Date格式转成需要的patter格式
     * @param srcDate 原始时间
     * @param pattern 格式
     * @author littlehui
     * @date 2021/11/15 15:49
     * @return java.lang.String
     */
    public static String formatDate(Date srcDate, String pattern) {
        if (srcDate == null) {
            return null;
        }
        return DateFormatUtils.format(srcDate, pattern);
    }

    /**
     * 将时间戳毫秒数转成响应的格式并返回
     * @param time 时间戳毫秒数
     * @param pattern 格式
     * @author littlehui
     * @date 2021/11/15 15:49
     * @return java.lang.String
     */
    private static String formatDate(long time, String pattern) {
        return DateFormatUtils.format(time, pattern);
    }

    /**
     * 根据时间戳毫秒数返回当前的季度
     * @param timeMills 时间戳毫秒数
     * @author littlehui
     * @date 2021/11/15 15:50
     * @return java.lang.Integer
     */
    public static Integer quarter(Long timeMills) {
        Timestamp timestamp = new Timestamp(timeMills);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        Integer monthValue = Integer.parseInt(monthFormat.format(timestamp));
        Integer quarter = (monthValue - 1) / 3 + 1;
        Integer dateQuarter = Integer.parseInt(Calendar
                .getInstance().get(Calendar.YEAR)
                + "0"
                + quarter);
        return dateQuarter;
    }

    /**
     * 根据时间戳毫秒数计算天yyyyMMdd格式返回
     * @param timeMills 时间戳毫秒
     * @author littlehui
     * @date 2021/11/15 15:50
     * @return java.lang.Integer
     */
    public static Integer day(Long timeMills) {
        Timestamp timestamp = new Timestamp(timeMills);
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
        Integer dayValue = Integer.parseInt(dayFormat.format(timestamp));
        return dayValue;
    }

    /**
     * 根据时间戳毫秒数计算月份yyyyMM格式返回
     * @param timeMills 时间戳毫秒
     * @author littlehui
     * @date 2021/11/15 15:51
     * @return java.lang.Integer
     */
    public static Integer month(Long timeMills) {
        Timestamp timestamp = new Timestamp(timeMills);
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
        Integer monthValue = Integer.parseInt(monthFormat.format(timestamp));
        return monthValue;
    }

    /**
     * 根据时间戳毫秒数计算分钟yyyyMMddmm格式返回
     * @param timeMills 时间戳毫秒
     * @author littlehui
     * @date 2021/11/15 15:51
     * @return java.lang.String
     */
    public static String minute(Long timeMills) {
        Timestamp timestamp = new Timestamp(timeMills);
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String minute = monthFormat.format(timestamp);
        return minute;
    }

    /**
     * 获取前N分钟
     * @param latest 最近的第几分钟
     * @author littlehui
     * @date 2021/11/15 15:52
     * @return java.util.List<java.lang.String>
     */
    public static List<String> latestMinutes(Integer latest) {
        Long currentMills = System.currentTimeMillis();
        List<String> latestMinutes = new ArrayList<>();
        for (int i = 0; i < latest; i++) {
            String minute = minute(currentMills - (i + 1) * 60);
            latestMinutes.add(minute);
        }
        return latestMinutes;
    }

    /**
     * 根据时间戳毫秒数计算当前年yyyy格式返回
     * @param timeMills 时间戳毫秒
     * @author littlehui
     * @date 2021/11/15 15:52
     * @return java.lang.Integer
     */
    public static Integer year(Long timeMills) {
        Timestamp timestamp = new Timestamp(timeMills);
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy");
        Integer yearValue = Integer.parseInt(monthFormat.format(timestamp));
        return yearValue;
    }

    /**
     * 计算当天开始的时间戳毫秒数并返回
     * @param
     * @author littlehui
     * @date 2021/11/15 15:53
     * @return java.lang.Long
     */
    public static Long getDayStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime().getTime();
    }

    /**
     * 计算当天的最后一毫秒并返回
     * @param
     * @author littlehui
     * @date 2021/11/15 15:53
     * @return java.lang.Long
     */
    public static Long getDayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime().getTime();
    }

    /**
     * 获取指定时区时间戳毫秒数的所在天的开始毫秒数
     * @param timeStamp 时间戳毫秒
     * @param timeZone GMT+8或UTC+08:00
     * @author littlehui
     * @date 2021/11/15 15:54
     * @return java.lang.Long
     */
    public static Long getDailyStartTime(Long timeStamp, String timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(timeStamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 计算特定时区下时间戳毫秒数所在的那天的最后一毫秒，并返回
     * @param timeStamp 时间戳毫秒
     * @param timeZone GMT+8或UTC+08:00
     * @author littlehui
     * @date 2021/11/15 15:55
     * @return java.lang.Long
     */
    public static Long getDailyEndTime(Long timeStamp, String timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(timeStamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * 计算时间戳毫秒数在指定时区所在月的开始毫秒数
     * @param timeStamp 时间戳毫秒
     * @param timeZone GMT+8或UTC+08:00
     * @author littlehui
     * @date 2021/11/15 15:56
     * @return java.lang.Long
     */
    public static Long getMonthStartTime(Long timeStamp, String timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(timeStamp);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, 0);
        // 设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 计算指定时间毫秒数下的某个时区下所在月份的结束毫秒数
     * @param timeStamp 时间戳毫秒
     * @param timeZone GMT+8或UTC+08:00
     * @author littlehui
     * @date 2021/11/15 15:57
     * @return java.lang.Long
     */
    public static Long getMonthEndTime(Long timeStamp, String timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(timeStamp);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, 0);
        // 获取当前月最后一天
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取时间戳毫秒数在特定时区下的当前年的开始毫秒数
     * @param timeStamp 时间戳毫秒
     * @param timeZone GMT+8或UTC+08:00
     * @author littlehui
     * @date 2021/11/15 15:58
     * @return java.lang.Long
     */
    public static Long getYearStartTime(Long timeStamp, String timeZone) {
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(timeStamp);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.DATE, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取时间戳毫秒数在特定时区下所在年的最后一毫秒数
     * @param timeStamp 时间戳毫秒
     * @param timeZone GMT+8或UTC+08:00
     * @author littlehui
     * @date 2021/11/15 15:58
     * @return java.lang.Long
     */
    public static Long getYearEndTime(Long timeStamp, String timeZone) {
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(timeStamp);
        int year = calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTimeInMillis();
    }

    /**
     * 时间戳毫秒数转特定时区字符串并返回
     * @param timestamp 时间戳毫秒
     * @param zoneId 时区ID
     * @author littlehui
     * @date 2021/11/15 16:00
     * @return java.lang.String
     */
    public static String timestampToStr(long timestamp, String zoneId) {
        ZoneId timezone = ZoneId.of(zoneId);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), timezone);
        return localDateTime.toString();
    }

    /**
     * 天数累加
     * @param date 时间
     * @param amount 累计数
     * @author littlehui
     * @date 2021/11/15 16:00
     * @return java.util.Date
     */
    public static Date addDays(Date date, int amount) {
        return add(date, 5, amount);
    }

    /**
     * 月数累加
     * @param date 时间
     * @param amount 累计数
     * @author littlehui
     * @date 2021/11/15 16:01
     * @return java.util.Date
     */
    public static Date addMonths(Date date, int amount) {
        return add(date, 2, amount);
    }

    /**
     * 星期数累加
     * @param date 时间
     * @param amount 累计数
     * @author littlehui
     * @date 2021/11/15 16:01
     * @return java.util.Date
     */
    public static Date addWeeks(Date date, int amount) {
        return add(date, 3, amount);
    }

    /**
     * 年数累加
     * @param date 时间
     * @param amount 累计数
     * @author littlehui
     * @date 2021/11/15 16:01
     * @return java.util.Date
     */
    public static Date addYears(Date date, int amount) {
        return add(date, 1, amount);
    }

    /**
     * 根据日历字段累加
     * @param date 时间
     * @param calendarField 需要计算的日历字段
     * @param amount 累计数
     * @author littlehui
     * @date 2021/11/15 16:02
     * @return java.util.Date
     */
    public static Date add(Date date, int calendarField, int amount) {
        if(date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(calendarField, amount);
            return c.getTime();
        }
    }

    public static void main(String[] args) {
        Long currentTime = System.currentTimeMillis();
        System.out.println("Current Time : " + currentTime + " = " + timestampToStr(currentTime, "GMT+8"));
        Long dailyStart = getDailyStartTime(currentTime, "GMT+8:00");
        Long dailyEnd = getDailyEndTime(currentTime, "GMT+8:00");
        Long monthStart = getMonthStartTime(currentTime, "GMT+8:00");
        Long monthEnd = getMonthEndTime(currentTime, "GMT+8:00");
        Long yearStart = getYearStartTime(currentTime, "GMT+8:00");
        Long yearEnd = getYearEndTime(currentTime, "GMT+8:00");

        System.out.println("Daily Start : " + dailyStart + " = " + timestampToStr(dailyStart, "GMT+8") + " Daily End : " + dailyEnd + " = " + timestampToStr(dailyEnd, "GMT+8"));
        System.out.println("Month Start : " + monthStart + " = " + timestampToStr(monthStart, "GMT+8") + " Month End : " + monthEnd + " = " + timestampToStr(monthEnd, "GMT+8"));
        System.out.println("Year Start : " + yearStart + " = " + timestampToStr(yearStart, "GMT+8") + " Year End : " + yearEnd + " = " + timestampToStr(yearEnd, "GMT+8"));
    }
}
