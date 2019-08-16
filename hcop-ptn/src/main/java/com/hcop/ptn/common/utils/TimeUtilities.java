package com.hcop.ptn.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class TimeUtilities {
	private static Log log = LogFactory.getLog(TimeUtilities.class);
	private static TimeUtilities _instance = new TimeUtilities();
	private static final int DATEDIFF_DAY = 1;
	private static final int DATEDIFF_HOUR = 2;
	private static final int DATEDIFF_MINUTE = 3;
	private static final int DATEDIFF_SECOND = 4;


	private  TimeUtilities(){

	}

	public static TimeUtilities instance(){
		return _instance;
	}

	public String formatDate(Date date, String format){
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}

	public long getDateGap(Date fDate, Date sDate, int calendar) {
		long x = fDate.getTime() - sDate.getTime();

		double x1 = new Double(x);
		double ret = x1;
		switch (calendar) {
			case DATEDIFF_DAY:
				ret = div(x1,(1000 * 60 * 60 * 24),2);
				break;
			case DATEDIFF_HOUR:
				ret =  div(x1,(1000 * 60 * 60),2);
				break;
			case DATEDIFF_MINUTE:
				ret =  div(x1,(1000 * 60),2);
				break;
			case DATEDIFF_SECOND:
				ret = div(x1,100,2);
				break;
			default:
				break;
		}
		double retdouble = round(ret,0);
		return  (long)retdouble;
	}

	public Date parse(String format,String timeStr){
		DateFormat df = new SimpleDateFormat(format);

		try {
			return df.parse(timeStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public boolean isTimeInScope(Date checkTime, Date start, Date end){

		return (checkTime.getTime()>= start.getTime()) && (checkTime.getTime() <= end.getTime());
	}

	public boolean isTimeValueInScope(long checkTimeValue, long startTimeValue, long endTimeValue){
		return (checkTimeValue >= startTimeValue) && (checkTimeValue <= endTimeValue);
	}

	/**
	 *
	 * if is left out of the scope return -1, in the scope return 0, right out of the scope return 1
	 *
	 * @param checkTime
	 * @param start
	 * @param end
	 * @return
	 */
	public int timeCompareScope(Date checkTime, Date start, Date end){
		if(checkTime.before(start))return -1;
		if((checkTime.after(start) || checkTime.compareTo(start) == 0) && (checkTime.before(end) || checkTime.compareTo(end) == 0) )return 0;
		if(checkTime.after(end))return 1;
		return -2;
	}

	public boolean timeLast15MinLong(Date frontDate, Date backDate){
		return timeLastAmountInFieldLong(frontDate, backDate, Calendar.MINUTE, 15);
	}

	public boolean timeLastOneHourLong(Date frontDate, Date backDate){
		return timeLastAmountInFieldLong(frontDate, backDate, Calendar.HOUR_OF_DAY, 1);
	}

	/**
	 * return true if the backDate is great than frontDate more than one day.
	 *
	 * @param frontDate
	 * @param backDate
	 * @return
	 */
	public boolean timeLastOneDayLong(Date frontDate, Date backDate){
		return timeLastAmountInFieldLong(frontDate, backDate, Calendar.DATE, 1);
	}


	/**
	 * return true if backDate is great than frontDate more than one week
	 * @param frontDate
	 * @param backDate
	 * @return
	 */
	public boolean timeLastOneWeekLong(Date frontDate, Date backDate){
		return timeLastAmountInFieldLong( frontDate,  backDate, Calendar.WEEK_OF_YEAR, 1);

	}

	/**
	 * return true if backDate is great than frontDate more than one month
	 * @param frontDate
	 * @param backDate
	 * @return
	 */
	public boolean timeLastOneMonthLong(Date frontDate, Date backDate){
		return timeLastAmountInFieldLong( frontDate,  backDate, Calendar.MONTH, 1);
	}



	/**
	 * return true if backDate is great than frontDate more than one quarter long
	 * @param frontDate
	 * @param backDate
	 * @return
	 */
	public boolean timeLastOneQuarterLong(Date frontDate, Date backDate){
		return timeLastAmountInFieldLong(frontDate, backDate, Calendar.MONTH, 3);
	}

	/**
	 * return true if backDate is great than frontDate more than a half  years long
	 * @param frontDate
	 * @param backDate
	 * @return
	 */
	public boolean timeLastHalfYearLong(Date frontDate, Date backDate){
		return timeLastAmountInFieldLong(frontDate, backDate, Calendar.MONTH, 6);
	}
	/**
	 * return true if backDate is great than frontDate more than one  year long
	 * @param frontDate
	 * @param backDate
	 * @return
	 */
	public boolean timeLastOneYearLong(Date frontDate, Date backDate){
		return timeLastAmountInFieldLong(frontDate, backDate, Calendar.YEAR, 1);
	}
	/**
	 *
	 * return true if backDate is great than frontDate more than amount of field time long
	 *
	 * @param frontDate
	 * @param backDate
	 * @param field    the static time field of <code>Calendar</code> class
	 * @param amount
	 * @return
	 */
	public  boolean timeLastAmountInFieldLong(Date frontDate, Date backDate, int field, int amount){
		if(frontDate.after(backDate))return false;
		Calendar start = Calendar.getInstance();
		start.setTime(frontDate);
		Calendar end = Calendar.getInstance();
		end.setTime(backDate);
		start.add(field, amount);
		return !start.after(end);
	}

	/**
	 *
	 * return the quarter number of destTime specified
	 * @param destTime
	 * @param monthInQuarter
	 * @return
	 */
	public int getQuarterOfTime(Date destTime){
		Calendar c = Calendar.getInstance();
		c.setTime(destTime);
		int month = c.get(Calendar.MONTH);

		return month/3+1;
	}

	/**
	 *
	 * calculate the month  of the monthIndexInQuarter in quarter number
	 *
	 * @param quarterNum
	 * @param monthIndexInQuarter
	 * @return
	 */
	public int getMonthOfQuarterBy(int quarterNum, int monthIndexInQuarter){
		if(quarterNum<=0 || quarterNum>4){
			throw new IllegalArgumentException("quarter number must > 0 and < = 4");
		}
		if(monthIndexInQuarter <=0 || monthIndexInQuarter > 3){
			throw new IllegalArgumentException("monthIndexInQuarter  must > 0 and < = 3");
		}

		return (quarterNum - 1)*3 + monthIndexInQuarter;
	}

	/**
	 * return month index in quarter of the destTime specified
	 * @param destTime
	 * @return
	 */
	public int getMonthIndexInQuarterOfTime(Date destTime){
		Calendar c = Calendar.getInstance();
		c.setTime(destTime);
		int month = c.get(Calendar.MONTH);

		return (month+1)%3;
	}


	/**
	 * calculate which half year  the destTime belonged.
	 * there will be only two values returned, 1 and 2
	 * return 1 means first half year, return 2 means last half year.
	 * @param destTime
	 * @return
	 */
	public int getHalfYearIndexOfTime(Date destTime){
		Calendar c = Calendar.getInstance();
		c.setTime(destTime);
		int month = c.get(Calendar.MONTH);
		return (month+1)/6+1;
	}


	/**
	 * calculate the real month in calendar by passing half index   and month index in half year
	 * @param halfIndex
	 * @param monthIndexInHalfYear
	 * @return
	 */
	public int getMonthOfHalfYear(int halfIndex, int monthIndexInHalfYear){
		if(halfIndex<=0 || halfIndex>2){
			throw new IllegalArgumentException("quarter number must > 0 and < = 2");
		}
		if(monthIndexInHalfYear <=0 || monthIndexInHalfYear > 6){
			throw new IllegalArgumentException("monthIndexInQuarter  must > 0 and < = 6");
		}

		return (halfIndex - 1)*6 + monthIndexInHalfYear;
	}


	/**
	 *
	 * calculate the index in half year of destTime specified
	 * @param destTime
	 * @return
	 */
	public int getMonthIndexInHalfYear(Date destTime){
		Calendar c = Calendar.getInstance();
		c.setTime(destTime);
		int month = c.get(Calendar.MONTH);
		return (month+1)%6;
	}


	/**

	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指

	 * 定精度，以后的数字四舍五入。

	 * @param v1 被除数

	 * @param v2 除数

	 * @param scale 表示表示需要精确到小数点以后几位。

	 * @return 两个参数的商

	 */

	public static double div(double v1,double v2,int scale)
	{
		if(scale<0)
		{
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();

	}

	/**

	 * 提供精确的小数位四舍五入处理。
	 * @param v 需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的结果
	 */

	public static double round(double v,int scale){
		if(scale<0)
		{

			throw new IllegalArgumentException("The scale must be a positive integer or zero");

		}
		BigDecimal b = new BigDecimal(v);
		BigDecimal one = new BigDecimal("1");
		return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * @根据时间转成成周
	 * @param date
	 * @return
	 */
	public static int getWeekOfDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * @根据时间转成年
	 * @param date
	 * @return
	 */
	public static int getYearOfDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * @根据周转换成日期字符串
	 * @param week
	 * @return
	 */
	public static String getDateOfWeek(int week){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.WEEK_OF_YEAR,week);
		return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
	}

	public static void main(String[] args){

		Date time = new Date();
		Date time1 = new Date();
		time.setTime(1457827200000l);
		time1.setTime(1457827200000l);
		String timeStr = TimeUtilities.instance().formatDate(time, "yyyy-MM-dd HH:mm:ss");
		String timeStr1 = TimeUtilities.instance().formatDate(time1, "yyyy-MM-dd HH:mm:ss");
		System.out.println("time is "+ timeStr);
		System.out.println("time is "+ timeStr1);
//		Date time = new Date();
		time = TimeUtilities.instance().parse("yyyy-MM-dd HH:mm:ss", "2016-04-08 00:00:00");
		time1 = TimeUtilities.instance().parse("yyyy-MM-dd HH:mm:ss", "2016-04-08 00:00:00");
		System.out.println("time is "+ time.getTime());
		System.out.println("time is "+ time1.getTime());

	}

	/**
	 *  获得当前日期与本周日相差的天数
	 * @param date 参考日期
	 * @return
	 */
	private int getMondayPlus(Date date) {
		Calendar cd = Calendar.getInstance();
		cd.setTime(date);
		int year = cd.get(Calendar.YEAR);
		Date currentDate = new Date();
		cd.setTime(currentDate);
		int currentYear = cd.get(Calendar.YEAR);

		// 获得今天是一周的第几天，星期日是第一天，星期一是第二天......
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
		if(currentYear != year){
			cd.setTime(date);
			dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
		}
		return 1 - dayOfWeek;
	}

	/**
	 *  获得上周星期一的日期
	 * @return
	 */
	public String getPreviousMonday() {
		int mondayPlus = this.getMondayPlus(new Date());
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus - 7);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	/**
	 *  获得本周星期一的日期
	 * @return
	 */
	public String getCurrentMonday() {
		int mondayPlus = this.getMondayPlus(new Date());
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	/**
	 *  获得下周星期一的日期
	 * @return
	 */
	public String getNextMonday() {
		int mondayPlus = this.getMondayPlus(new Date());
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	/**
	 *  获得相应周的周日的日期
	 * @param weeks 当年的第几个周
	 * @return
	 */
	public String getSunday(int weeks) {
		int mondayPlus = this.getMondayPlus(new Date());
		int week = weeks - TimeUtilities.getWeekOfDate(new Date());
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * week + 6);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}
	/**
	 * 获取指定周的指定天的日期
	 * @param weeks 当年的第几个周
	 * @param dayofweek 指定周的第几天（取值范围在0-6之间。0表示周日，1表示周一，依次类推。），此处默认周日是每周的第一天
	 * @return
	 */
	public String getDateByWeekAndDay(Date date, int weeks, int dayofweek) {
		Calendar cal = Calendar.getInstance();
		int mondayPlus = this.getMondayPlus(date);
		Date nowDate = new Date();
		cal.setTime(nowDate);
		int currentYear = cal.get(Calendar.YEAR);
		cal.setTime(date);
		int userYear = cal.get(Calendar.YEAR);

		int week = weeks - TimeUtilities.getWeekOfDate(nowDate);
		cal.setTime(nowDate);
		if(currentYear != userYear){ //判断用户选择的是否为当前年
			week = weeks - TimeUtilities.getWeekOfDate(date);
			cal.setTime(date);
		}
		int day = dayofweek; //dayofweek - 1;
		cal.add(Calendar.DATE, mondayPlus + 7 * week + day);
		Date monday = cal.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	/**
	 *
	 * 获得所在月份的最后一天
	 *
	 * @param 当前月份所在的时间
	 *
	 * @return 月份的最后一天 , 返回的日期为yyyy-MM-dd 23:59:59
	 */

	public static Date getLastDateByMonth(Date date) {

		/*Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.MONTH, now.get(Calendar.MONTH) + 1);
		now.set(Calendar.DATE, 1);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - 1);
		now.set(Calendar.HOUR_OF_DAY, 23);
		now.set(Calendar.MINUTE, 59);
		now.set(Calendar.SECOND, 59);
		now.set(Calendar.MILLISECOND, 999);*/

		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.DATE, now.getActualMaximum(Calendar.DATE));
		now.set(Calendar.HOUR_OF_DAY, 23);
		now.set(Calendar.MINUTE, 59);
		now.set(Calendar.SECOND, 59);
		now.set(Calendar.MILLISECOND, 999);

		return now.getTime();

	}

	/**
	 *
	 * 获得所在月份的第一天
	 *
	 * @param 当前月份所在的时间
	 *
	 * @return 月份的第一天, 返回的日期为yyyy-MM-dd 00:00:00
	 */

	public static Date getFirstDateByMonth(Date date) {

		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.DATE, 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		return now.getTime();

	}

	/**
	 *
	 * 获得所在年份的最后一刻
	 *
	 * @param 当前年份所在的时间
	 *
	 * @return 年份的最后一刻 , 返回的日期为yyyy-MM-dd 23:59:59
	 */

	public Date getLastDateByYear(Date date) {

		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.YEAR, now.get(Calendar.YEAR) + 1);
		now.set(Calendar.MONTH, 0);
		now.set(Calendar.DAY_OF_MONTH, 1);

		now.set(Calendar.DATE, now.get(Calendar.DATE) - 1);

		now.set(Calendar.HOUR_OF_DAY, 23);
		now.set(Calendar.MINUTE, 59);
		now.set(Calendar.SECOND, 59);
		now.set(Calendar.MILLISECOND,999);
		return now.getTime();

	}

	public Date getLastDateByYear(int year) {

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		now.set(Calendar.YEAR, year + 1);
		now.set(Calendar.MONTH, 0);
		now.set(Calendar.DAY_OF_MONTH, 1);

		now.set(Calendar.DATE, now.get(Calendar.DATE) - 1);

		now.set(Calendar.HOUR_OF_DAY, 23);
		now.set(Calendar.MINUTE, 59);
		now.set(Calendar.SECOND, 59);
		now.set(Calendar.MILLISECOND,999);
		return now.getTime();

	}

	/**
	 *
	 * 获得所在年份的第一天
	 *
	 * @param 当前年份所在的时间
	 *
	 * @return 年份的第一天, 返回的日期为yyyy-MM-dd 00:00:00
	 */

	public Date getFirstDateByYear(Date date) {

		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.YEAR, now.get(Calendar.YEAR));
		now.set(Calendar.MONTH, 0);
		now.set(Calendar.DAY_OF_MONTH, 1);

		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND,000);
		return now.getTime();
	}

	/**
	 *
	 * 获得上一天
	 *
	 *
	 * @return 当前日期的上一天, 返回的日期为yyyy-MM-dd 00:00:00
	 */

	public Date getPreviousDay() {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		now.set(Calendar.DATE, now.get(Calendar.DATE) - 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND,000);
		return now.getTime();
	}

	public Date getPreviousDay(int previousDay) {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		now.set(Calendar.DATE, now.get(Calendar.DATE) - 1 - previousDay);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND,000);
		return now.getTime();
	}

	/**
	 *
	 * 获得上一月起始时间
	 *
	 *
	 * @return 当前日期的上一月, 返回的日期为yyyy-MM-dd 00:00:00
	 */

	public Date getPreviousMonthStart() {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		now.set(Calendar.MONTH, now.get(Calendar.MONTH) - 1);
		now.set(Calendar.DAY_OF_MONTH, 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND,000);
		return now.getTime();
	}

	public Date getPreviousMonthStart(int previous) {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		//月份本身有一个月, 所以不-1
		now.set(Calendar.MONTH, now.get(Calendar.MONTH) - previous);
		now.set(Calendar.DAY_OF_MONTH, 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND,000);
		return now.getTime();
	}

	/**
	 *
	 * 获得上一月结束时间
	 *
	 *
	 * @return 当前日期的上一月, 返回的日期为yyyy-MM-dd 00:00:00
	 */

	public Date getPreviousMonthEnd() {

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		now.set(Calendar.DAY_OF_MONTH, 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND,000);
		now.set(Calendar.MILLISECOND,now.get(Calendar.MILLISECOND) - 1);
		return now.getTime();
	}

	/**
	 *
	 * 获得上一年起始时间
	 *
	 *
	 * @return 当前日期的上一年起始时间, 返回的日期为yyyy-MM-dd 00:00:00
	 */

	public Date getPreviousYearStart() {

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		now.set(Calendar.YEAR, now.get(Calendar.YEAR) - 1);
		now.set(Calendar.MONTH, 0);
		now.set(Calendar.DAY_OF_MONTH, 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND,000);
		return now.getTime();
	}

	/**
	 *
	 * 获得上一年结束时间
	 *
	 *
	 * @return 当前日期的上一年, 返回的日期为yyyy-MM-dd 00:00:00
	 */

	public Date getPreviousYearEnd() {

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		now.set(Calendar.MONTH, 0);
		now.set(Calendar.DAY_OF_MONTH, 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND,000);
		now.set(Calendar.MILLISECOND,now.get(Calendar.MILLISECOND) - 1);
		return now.getTime();
	}

	/**
	 *
	 * 获得本年起始时间
	 *
	 *
	 * @return 当前日期的所在年起始时间, 返回的日期为yyyy-01-01 00:00:00 000
	 */

	public Date getCurrentYearStart() {

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		now.set(Calendar.MONTH, 0);
		now.set(Calendar.DAY_OF_MONTH, 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND,000);
		return now.getTime();
	}

	/**
	 *
	 * 获得本年结束时间
	 *
	 *
	 * @return 当前日期的所在年结束时间, 返回的日期为yyyy-12-31 23:59:59 999
	 */

	public Date getCurrentYearEnd() {

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		now.set(Calendar.YEAR, now.get(Calendar.YEAR)+1);
		now.set(Calendar.MONTH, 0);
		now.set(Calendar.DAY_OF_MONTH, 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND,000);
		now.set(Calendar.MILLISECOND,now.get(Calendar.MILLISECOND) - 1);
		return now.getTime();
	}

	public Date getFirstDateByYear(int year) {

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		now.set(Calendar.YEAR, year);
		now.set(Calendar.MONTH, 0);
		now.set(Calendar.DAY_OF_MONTH, 1);

		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND,0);
		return now.getTime();
	}

	/**
	 * 获取一个小时的开始时间00:00
	 * @param day
	 * @return
	 */
	public Date getStartTimeByHour(Date day) {

		Calendar now = Calendar.getInstance();
		now.setTime(day);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND,0);
		return now.getTime();
	}

	/**
	 * 获取一个小时的结束时间59:59
	 * @param day
	 * @return
	 */
	public Date getEndTimeByHour(Date day) {

		Calendar now = Calendar.getInstance();
		now.setTime(day);

		now.set(Calendar.MINUTE, 59);
		now.set(Calendar.SECOND, 59);
		now.set(Calendar.MILLISECOND,999);
		return now.getTime();
	}

	/**
	 * 获取一天的开始时间00:00:00
	 * @param day
	 * @return
	 */
	public Date getStartTimeByDay(Date day) {

		Calendar now = Calendar.getInstance();
		now.setTime(day);

		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND,0);
		return now.getTime();
	}
	/**
	 * 获取一天的结束时间23:59:59
	 * @param day
	 * @return
	 */
	public Date getEndTimeByDay(Date day) {

		Calendar now = Calendar.getInstance();
		now.setTime(day);

		now.set(Calendar.HOUR_OF_DAY, 23);
		now.set(Calendar.MINUTE, 59);
		now.set(Calendar.SECOND, 59);
		now.set(Calendar.MILLISECOND,999);
		return now.getTime();
	}

	/**
	 * 获取两个时间之间的差，差的形式为：XX天XX小时XX分XX秒
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return 返回long数组，数组中的值就是时间差值，分别为long[0] 天；long[1] 时； long[2] 分；long[3] 秒
	 */
	public static long[] timeDifference(long start, long end){
		Date begin = new Date(start);
		Date endt = new Date(end);
		long between = (endt.getTime() - begin.getTime()) / 1000;//除以1000是为了转换成秒
		long day = between / (24 * 3600);
		long hour = between % (24 * 3600) / 3600;
		long minute = between % 3600 / 60;
		long second = between % 60 ;
		return new long[]{day, hour, minute, second};
	}

	/**
	 * 日期计算
	 * @param date 具体时间
	 * @param type 需要计算的时间类型（年:Calendar.YEAR,月:Calendar.MONTH,日:Calendar.DATE,以此类推）
	 * @param value 需要计算的值，往后计算：x，往前计算：-x
	 * @return
	 */
	public Date calculateTime(Date date, int type, int value) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.add(type, value);
		return now.getTime();
	}

	/**
	 * 当前时间往前推半年
	 * @return
	 */
	public Date halfYearBefore(){
		Date date = new Date();
		return calculateTime(date, Calendar.MONTH, -6);
	}

	/**
	 * 当前时间往后推半年
	 * @return
	 */
	public Date halfYearAfter(){
		Date date = new Date();
		return calculateTime(date, Calendar.MONTH, 6);
	}

	/**
	 * 当前时间往前推半年的零时
	 * @return
	 */
	public Date halfYearBeforeBegin(){
		return getStartTimeByDay(halfYearBefore());
	}

	/**
	 * 当前时间往后推半年的末时
	 * @return
	 */
	public Date halfYearAfterEnd(){
		return getEndTimeByDay(halfYearAfter());
	}

	/**
	 * 当前时间往后推半年的零时
	 * @return
	 */
	public Date halfYearAfterBegin(){
		return getStartTimeByDay(halfYearAfter());
	}

	public static String MS2TimeFormat(long ms)
	{
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;

		long day = ms / dd;
		long hour = (ms - day * dd) / hh;
		long minute = (ms - day * dd - hour * hh) / mi;
		long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

		String strDay = day < 10 ? "0" + day : "" + day;
		String strHour = hour < 10 ? "0" + hour : "" + hour;
		String strMinute = minute < 10 ? "0" + minute : "" + minute;
		String strSecond = second < 10 ? "0" + second : "" + second;
		//String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;
		//strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;


		String result = "";
		if(day > 0)
		{
			result += day + " " + s("tool.days") + " ";
		}
		if(hour > 0)
		{
			result += hour + " " + s("tool.hours") + " ";
		}
		if(minute > 0)
		{
			result += minute + " " + s("tool.minutes") + " ";
		}
		if(second > 0)
		{
			result += second + " " + s("tool.seconds");
		}


		return result;
	}

	private static String s(String key) {
		return I18N.getInstance().getString(key);
	}

	public static double MS2HOURS(long ms)
	{
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;

		double hour = ms / (double)hh;
		BigDecimal b = new BigDecimal(hour);
		hour = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return hour;
	}
}
