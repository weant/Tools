package com.otn.tool.task;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.util.Assert;

import java.text.MessageFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Date;

/**
 * 时间工具类 基于jdk8
 */
public abstract class DateUtils {
    private static Logger logger = LogManager.getLogger();
    public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";

    public static LocalDateTime toLocalDateTime(Date date) {
        Assert.notNull(date, "date can not be null.");
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static LocalDate toLocalDate(Date date) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return localDateTime.toLocalDate();
    }

    public static LocalTime toLocalTime(Date date) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return localDateTime.toLocalTime();
    }

    public static Date toDate(LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "localDateTime can not be null.");
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * 格式化时间 使用系统时区
     * @param time  毫秒
     * @param formatter 格式化器
     * @return
     */
    public static String formatWithDefaultZone(long time, DateTimeFormatter formatter) {
        return  format(time, formatter, ZoneId.systemDefault());
    }

    /**
     * 格式化时间 使用系统时区
     * @param time  毫秒
     * @param pattern   格式化字符串
     * @return
     */
    public static String formatWithDefaultZone(long time, String pattern) {
        return format(time, pattern, ZoneId.systemDefault());
    }

    /**
     * 格式化时间
     * @param time  毫秒
     * @param pattern   格式化字符串
     * @param zoneId  时区
     * @return
     */
    public static String format(long time, String pattern, ZoneId zoneId) {
        return format(time, DateTimeFormatter.ofPattern(pattern), zoneId);
    }

    /**
     * 格式化时间
     * @param time  毫秒
     * @param formatter 格式化器
     * @param zoneId 时区
     * @return
     */
    public static String format(long time, DateTimeFormatter formatter,ZoneId zoneId) {
        LocalDateTime localDateTime = parse(time, zoneId);
        String formatDate = localDateTime.format(formatter);
        return formatDate;
    }

    /**
     * 解析时间戳
     * @param time
     * @param zoneId
     * @return
     */
    public static LocalDateTime parse(long time, ZoneId zoneId) {
        Instant instant = Instant.ofEpochMilli(time);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        return localDateTime;
    }

    /**
     * 解析时间戳
     * @param time
     * @param zoneId
     * @return
     */
    public static LocalDateTime parse(long time) {
        return parse(time, ZoneId.systemDefault());
    }

    /**
     * 解析时间字符串 获取时间信息（年月日 时分秒）
     * @param dateStr
     * @param pattern
     * @param temporalField     例如获取一个月的第几天 ChronoField.DAY_OF_MONTH
     * @return
     */
    public static int parse(String dateStr, String pattern, TemporalField temporalField) {
        return parse(dateStr, DateTimeFormatter.ofPattern(pattern), temporalField);
    }

    /**
     * 解析时间字符串 获取日期信息（年月日 时分秒）
     * @param dateStr
     * @param formatter
     * @param temporalField 例如获取一个月的第几天 ChronoField.DAY_OF_MONTH
     * @return
     */
    public static int parse(String dateStr, DateTimeFormatter formatter, TemporalField temporalField) {
        TemporalAccessor temporalAccessor = formatter.parse(dateStr);
        if (!temporalAccessor.isSupported(temporalField)) {
            String message = MessageFormat.format("can not parse dateStr:{0} with TemporalField:{1}", dateStr, temporalField);
            throw  new UnsupportedTemporalTypeException(message);
        }
        return temporalAccessor.get(temporalField);
    }

    /**
     * 解析时间字符串获取毫秒 使用系统时区
     * @param dateStr
     * @param pattern
     * @return
     */
    public static long parseMillsWithDefaultZone(String dateStr, String pattern) {
        return parseMills(dateStr, DateTimeFormatter.ofPattern(pattern), OffsetDateTime.now().getOffset());
    }

    /**
     * 解析时间字符串获取毫秒 使用系统时区
     * @param dateStr
     * @param formatter
     * @return
     */
    public static long parseMillsWithDefaultZone(String dateStr, DateTimeFormatter formatter) {
        return parseMills(dateStr, formatter, OffsetDateTime.now().getOffset());
    }

    /**
     * 解析时间字符串获取毫秒
     * @param dateStr
     * @param pattern
     * @return
     */
    public static long parseMills(String dateStr, String pattern, ZoneOffset zoneOffset) {
        return parseMills(dateStr, DateTimeFormatter.ofPattern(pattern), zoneOffset);
    }

    /**
     * 解析时间字符串获取毫秒
     * @param dateStr
     * @param formatter
     * @return
     */
    public static long parseMills(String dateStr, DateTimeFormatter formatter, ZoneOffset zoneOffset) {
        TemporalAccessor temporalAccessor = formatter.parse(dateStr);
        LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
        return localDateTime.toEpochSecond(zoneOffset) * 1000;
    }

    /**
     * 获取两个毫秒之间日期差绝对值（年月日）使用系统时区
     * @param before
     * @param after
     * @param util  单位 例如获取相差多少天 ChronoUnit.DAYS
     * @return
     */
    public static long getAbsDateDiffWithDefaultZone(long before, long after, TemporalUnit util) {
        return Math.abs(getDateDiff(before, after, util, ZoneId.systemDefault()));
    }

    /**
     * 获取两个毫秒之间日期差（年月日）使用系统时区
     * @param before
     * @param after
     * @param util  单位 例如获取相差多少天 ChronoUnit.DAYS
     * @return
     */
    public static long getDateDiffWithDefaultZone(long before, long after, TemporalUnit util) {
        return getDateDiff(before, after, util, ZoneId.systemDefault());
    }

    /**
     * 获取两个毫秒之间日期差绝对值（年月日）
     * @param before
     * @param after
     * @param util  单位 例如获取相差多少天 ChronoUnit.DAYS
     * @param zoneId
     * @return
     */
    public static long getAbsDateDiff(long before, long after, TemporalUnit util, ZoneId zoneId) {
        return Math.abs(getDateDiff(before, after, util, zoneId));
    }

    /**
     * 获取两个毫秒之间日期差（年月日）
     * @param before
     * @param after
     * @param util  单位 例如获取相差多少天 ChronoUnit.DAYS
     * @param zoneId
     * @return
     */
    public static long getDateDiff(long before, long after, TemporalUnit util, ZoneId zoneId) {
        LocalDateTime beforeTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(before), zoneId);
        LocalDateTime afterTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(after), zoneId);
        return Period.between(beforeTime.toLocalDate(), afterTime.toLocalDate()).get(util);
    }

    /**
     * 获取两个毫秒之间相差的秒数（绝对值）
     * @param before
     * @param after
     * @return
     */
    public static long getAbsSecondDiff(long before, long after) {
        return getAbsSecondDiff(before, after);
    }

    /**
     * 获取两个指定单位之间相差的秒数
     * @param before
     * @param after
     * @return
     */
    public static long getSecondDiff(long before, long after) {
        Instant beforeInstant = Instant.ofEpochMilli(before);
        Instant afterInstant = Instant.ofEpochMilli(after);
        return Duration.between(beforeInstant, afterInstant).getSeconds();
    }

    /**
     * 比较输入时间是否在范围内
     * @param start
     * @param end
     * @param input 输入时间
     * @return
     */
    public static boolean isInRange(LocalDateTime start, LocalDateTime end, LocalDateTime input) {
        return (start.isBefore(input) && end.isAfter(input)) || start.isEqual(input) || end.isEqual(input);
    }

    public static void main(String[] args) {
//        //localDate 不包含当天时间信息 也不包含时区信息
//        LocalDate localDate = LocalDate.of(2018, 3, 20);
//        int year = localDate.getYear();
//        Month month =  localDate.getMonth();
//        int monthValue = localDate.getMonthValue();
//        int dayOfMonth = localDate.getDayOfMonth();
//        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
//        int i = localDate.lengthOfMonth();
//        int i1 = localDate.lengthOfYear();
//        boolean leapYear = localDate.isLeapYear();
//        LocalDate now = LocalDate.now();
//        LocalDate parse = LocalDate.parse("2018-03-20");
//
//        //localTime 包含时间信息
//        LocalTime localTime = LocalTime.of(11, 44, 20);
//        int hour = localTime.getHour();
//        int minute = localTime.getMinute();
//        int second = localTime.getSecond();
//        LocalTime parse1 = LocalTime.parse("11:44:20");
//
//        //localDateTime 不包含时区
//        LocalDateTime localDateTime = LocalDateTime.of(2018, 3, 20, 11, 49, 20);
//        LocalDateTime of = LocalDateTime.of(localDate, localTime);
//        LocalDateTime localDateTime1 = localDate.atTime(11, 49, 20);
//        LocalDateTime localDateTime2 = localDate.atTime(localTime);
//        LocalDateTime localDateTime3 = localTime.atDate(localDate);
//
//        LocalDate localDate1 = localDateTime.toLocalDate();
//        LocalTime localTime1 = localDateTime.toLocalTime();
//
//        //Instant 机器时间 Unix经过的秒数
//        Instant instant = Instant.ofEpochMilli(123);
//        Instant instant1 = Instant.ofEpochSecond(1234);
//        Instant now1 = Instant.now();
//        long epochSecond = instant.getEpochSecond();
//
//        //Duration 时间间隔 参数类型要一至 不能传递localDate 参数
//        Duration between = Duration.between(localDateTime1, localDateTime2);
//        Duration of1 = Duration.of(3, ChronoUnit.MINUTES);
//        Duration duration = Duration.ofMinutes(3);
//        long seconds = between.getSeconds();
//
//        //以年月日建模
//        Period period = Period.between(localDate1, localDate);
//        Period period1 = Period.ofYears(3);
//        int days = period.getDays();

        long parse = parseMillsWithDefaultZone("2018-03-30 14:24:30", "yyyy-MM-dd HH:mm:ss");
        String format = formatWithDefaultZone(parse, "yyyy-MM-dd HH:mm:ss");
        System.out.print(parse);
    }
}
