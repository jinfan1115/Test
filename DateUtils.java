package com.glp.collie.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {

  private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

  static String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

  public static final String FORMAT = "yyyy-MM-dd";
  
  public static final String FORMAT_TIME = "yyyy-MM-dd HH:mm:ss";


  public static final String FORMAT_DATE="yyyyMMdd";
  public static final String FORMAT_CHINESE_DATE_TIME = "yyyy年MM月dd日HH时mm分";

  private DateUtils() {}

  /**
   * 取当前时间
   */
  public static Date getCurrent() {
    return Calendar.getInstance().getTime();
  }

  /**
   * 格式化当前时间
   */
  public static String formatCurrentDate(String format) {
    return formatDate(Calendar.getInstance().getTime(), format);
  }
  
  /**
   * 格式化时间
   */
  public static String formatDate(Date date) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);
    return dateFormat.format(date);
  }

  /**
   * 格式化时间
   */
  public static String formatDate(Date date, String format) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    return dateFormat.format(date);
  }

  /**
   * 格式化时间
   */
  public static Date formatDate(String date, String format) {
    try {
      return new SimpleDateFormat(format).parse(date);
    } catch (ParseException e) {
      logger.error("format string to date error, string:" + date, e);
      return getCurrent();
    }
  }

  public static boolean isToday(Date date) {
    Date current = getCurrent();
    Date day000000 = getDay000000(current);
    Date day235959 = getDay235959(current);
    return date.after(day000000) && date.before(day235959);
  }
  
  /**
   * 星期
   */
  public static String getWeekOfDate(Date dt) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(dt);
    int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
    if (w < 0)
      w = 0;
    return weekDays[w];
  }

  /**
   * 上午、下午
   */
  public static String getAM_PM(Date dt) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(dt);

    int am_pm = cal.get(Calendar.AM_PM);
    return am_pm == Calendar.AM ? "上午" : "下午";
  }

  /**
   * 一天的开始
   */
  public static Date getDay000000(Date d) {
    Calendar c = Calendar.getInstance();
    c.setTime(d);
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    return c.getTime();
  }

  /**
   * 一天的结束
   */
  public static Date getDay235959(Date d) {
    Calendar c = Calendar.getInstance();
    c.setTime(d);
    c.set(Calendar.HOUR_OF_DAY, 23);
    c.set(Calendar.MINUTE, 59);
    c.set(Calendar.SECOND, 59);
    c.set(Calendar.MILLISECOND, 999);
    return c.getTime();
  }

  /**
   * 计算分钟
   * 
   * @param minute 为正时增加， 为负时减少
   */
  public static Date calculateMinute(Date date, int minute, boolean init) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.MINUTE, minute);
    if(init) {
      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND, 0);
    }
    return c.getTime();
  }
  
  /**
   * 计算小时
   * 
   * @param hour 为正时增加， 为负时减少
   */
  public static Date calculateHour(Date date, int hour, boolean init) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.HOUR_OF_DAY, hour);
    if(init) {
      c.set(Calendar.MINUTE, 0);
      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND, 0);
    }
    return c.getTime();
  }
  
  /**
   * 计算天
   * 
   * @param day 为正时增加， 为负时减少
   */
  public static Date calculateDay(Date date, int day, boolean init) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.DAY_OF_MONTH, day);
    if(init) {
      c.set(Calendar.HOUR_OF_DAY, 0);
      c.set(Calendar.MINUTE, 0);
      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND, 0);
    }
    return c.getTime();
  }

  /**
   * 计算周
   * 
   * @param week 为正时增加， 为负时减少
   */
  public static Date calculateWeek(Date date, int week, boolean init) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.WEEK_OF_YEAR, week);
    if(init) {
      c.set(Calendar.DAY_OF_WEEK, 2); // 按习惯 一周的开始从周一开始算 , 改为1则从周日开始算一周开始
      c.set(Calendar.HOUR_OF_DAY, 0);
      c.set(Calendar.MINUTE, 0);
      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND, 0);
    }
    return c.getTime();
  }

  /**
   * 计算月
   * 
   * @param month 为正时增加， 为负时减少
   */
  public static Date calculateMonth(Date date, int month, boolean init) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.MONTH, month);
    if(init) {
      c.set(Calendar.DAY_OF_MONTH, 1);
      c.set(Calendar.HOUR_OF_DAY, 0);
      c.set(Calendar.MINUTE, 0);
      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND, 0);
    }
    return c.getTime();
  }

  /**
   * 计算年
   * 
   * @param year 为正时增加， 为负时减少
   */
  public static Date calculateYear(Date date, int year, boolean init) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.YEAR, year);
    if(init) {
      c.set(Calendar.MONTH, 0);
      c.set(Calendar.DAY_OF_MONTH, 1);
      c.set(Calendar.HOUR_OF_DAY, 0);
      c.set(Calendar.MINUTE, 0);
      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND, 0);
    }
    return c.getTime();
  }
  
  /**
   * 获取参数月份月末时间，精确到秒，如20170331 23：59：59
   * @param year
   * @param month
   * @return
   */
  public static Date getMonthMaxDateTime(int year, int month)
  {
    //获取Calendar
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    //设置日期为本月最大日期
    calendar.set(Calendar.YEAR, Integer.valueOf(String.valueOf(year)));
//    calendar.set(Calendar.MONTH, Integer.valueOf(String.valueOf(month)) -1);
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); 
    calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
    calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
    calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
    //设置日期格式
    return calendar.getTime();
  }
  
  /**
   * 获取参数月份月初时间，精确到秒，如20170301 00:00:00
   * @param yearMonth 201703
   * @return
   */
  public static Date getMonthMinDateTime(String yearMonth)
  {
    SimpleDateFormat timeStartFormat = new SimpleDateFormat("yyyyMM");
    Date timeStartDate;
    try {
      timeStartDate = timeStartFormat.parse(yearMonth);
    } catch (ParseException e) {
      timeStartDate = null;
    }
    
    return timeStartDate;
  }
  
  public static Long monthToLong(Integer month) {
    if (month == null)
      return null;
    
    Long millisTime = null;
    long now = System.currentTimeMillis();
    
    Date date = calculateMonth(new Date(), month, false);
    
    millisTime = now - date.getTime();
    
    return millisTime;
  }
  
  public static long getUnixTimestamp() {
    return System.currentTimeMillis() / 1000;
  }

  /**
   * Long转换为Date，精确到秒，如2017/03/01 00:00:00
   * @param timestamp 是乘了1000的
   * @return
   */
  public static String LongToDateTime(Long timestamp){
    if(null==timestamp){
      return "";
    }
    SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");  
    java.util.Date date = new Date(timestamp);  
    String str = sdf.format(date);  
    return str;
  }
  
  /**
   * Long转换为Date，精确到日期，如2017/03/01
   * @param timestamp 是乘了1000的
   * @return
   */
  public static String LongToDate(Long timestamp){
    if(null==timestamp){
      return "";
    }
    SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd");  
    java.util.Date date = new Date(timestamp);  
    String str = sdf.format(date);  
    return str;
  }
  
  public static void main(String[] args) {
    System.out.println(Calendar.getInstance().get(Calendar.YEAR));
    
    System.out.println(monthToLong(-3));
    System.out.println(getUnixTimestamp());
    
    System.out.println(new Date(1501559560000L));
    System.out.println(calculateDay(new Date(1501559560000L),-1,false));
    
    System.out.println("***"+formatDate(new Date() ,FORMAT_CHINESE_DATE_TIME));
  }
  
}
