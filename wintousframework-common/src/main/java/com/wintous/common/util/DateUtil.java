package com.ctg.itrdc.event.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class DateUtil {
    
    // ~ Static fields/initializers
    // =============================================
    
    public final static String  Y4M2D2            = "yyyyMMdd";
    public final static String  Y4M2D2WithSeprate = "yyyy-MM-dd";
    public final static String  Y2M2D2            = "yyMMdd";
    public final static String  YMDHMS            = "yyyyMMddHHmmss";
    public final static String  Y2M2DHMS          = "yyMMddHHmmss";
    public final static String  YMDHMSL           = "HHmmssSSS";
    public final static String  HHmmss            = "HHmmss";
    public final static String  HHmmssWithSeprate = "HH:mm:ss";
    public final static String  Y2M2D2HMSL        = "yyyyMMddHHmmssSSS";
    
    public final static String  dateTimePattern   = "yyyy-MM-dd HH:mm:ss";
    
    private static Log          log               = LogFactory.getLog(DateUtil.class);
    private final static String shortDatePattern  = "yyMMdd";
    
    //	private static String					timePattern			= "HH:mm";
    
    public static synchronized Long getDateSequence() {
        return Long.parseLong(DateUtil.convertDateToString(new Date(), DateUtil.Y2M2DHMS));
    }
    
    public static Calendar changeDate(Date thisDate, int year, int month, int day, int hour,
        int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        if (thisDate != null) {
            calendar.setTime(thisDate);
        }
        calendar.add(Calendar.YEAR, year);
        calendar.add(Calendar.MONTH, month);
        calendar.add(Calendar.DATE, day);
        calendar.add(Calendar.HOUR, hour);
        calendar.add(Calendar.MINUTE, minute);
        calendar.add(Calendar.SECOND, second);
        return calendar;
    }
    
    // ~ Methods
    // ================================================================
    
    public static Calendar changeDate(int year, int month, int day, int hour, int minute, int second) {
        return changeDate(null, year, month, day, hour, minute, second);
    }
    
    /**
     * combine a end date by give year,month,day the time is 23:59:59
     * 
     * @version create by libh on 2006-6-1
     * @param sYear
     * @param sMonth
     * @param sDate
     * @return
     */
    public static Date combineDateEnd(String sYear, String sMonth, String sDate) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(sYear));
        cal.set(Calendar.MONTH, Integer.parseInt(sMonth) - 1);
        cal.set(Calendar.DATE, Integer.parseInt(sDate));
        setEndTime(cal);
        return cal.getTime();
    }
    
    // ************* end day ****************
    /**
     * combine a start date by give year,month,date, the time is 00:00:00
     * 
     * @version create by libh on 2006-6-1
     * @param sYear
     * @param sMonth
     * @param sDate
     * @return
     */
    public static Date combineDateStart(String sYear, String sMonth, String sDate) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(sYear));
        cal.set(Calendar.MONTH, Integer.parseInt(sMonth) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sDate));
        setStartTime(cal);
        return cal.getTime();
    }
    
    /**
     * Sometimes the Datetime is baneful, such as make reporting. This method is
     * remove time info.And set 0:0:00
     * 
     * @param date
     * @return
     */
    public static Date convertDateTimeToDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        setStartTime(cal);
        Date d = cal.getTime();
        return d;
    }
    
    /**
     * This method generates a string representation of a date based on the
     * System Property 'dateFormat' in the format you specify on input
     * 
     * @param aDate
     *            A date to convert
     * @return a string representation of the date
     */
    public static final String convertDateToString(Date aDate) {
        return getDateTime(getDatePattern(), aDate);
    }
    
    public static final String convertDateToString(Date aDate, String datePattern) {
        return getDateTime(datePattern, aDate);
    }
    
    /**
     * 将日期转换成(日期+时间)的字符串
     * @param aDate
     * @return
     * @author pengzhirong
     */
    public static final String convertDateTimeToString(Date aDate) {
        return getDateTime(dateTimePattern, aDate);
    }
    
    /**
     * This method converts a String to a date using the datePattern
     * 
     * @param strDate
     *            the date to convert (in format MM/dd/yyyy)
     * @return a date object
     * 
     * @throws ParseException
     */
    public static Date convertStringToDate(String strDate) throws ParseException {
        Date aDate = null;
        
        try {
            if (log.isDebugEnabled()) {
                log.debug("converting date with pattern: " + getDatePattern());
            }
            
            aDate = convertStringToDate(getDatePattern(), strDate);
        } catch (ParseException pe) {
            log.error("Could not convert '" + strDate + "' to a date, throwing exception");
            pe.printStackTrace();
            throw new ParseException(pe.getMessage(), pe.getErrorOffset());
            
        }
        
        return aDate;
    }
    
    /**
     * This method generates a string representation of a date/time in the
     * format you specify on input
     * 
     * @param aMask
     *            the date pattern the string is in
     * @param strDate
     *            a string representation of a date
     * @return a converted Date object
     * @see java.text.SimpleDateFormat
     * @throws ParseException
     */
    public static final Date convertStringToDate(String aMask, String strDate)
        throws ParseException {
        SimpleDateFormat df = null;
        Date date = null;
        df = new SimpleDateFormat(aMask);
        
        if (log.isDebugEnabled()) {
            log.debug("converting '" + strDate + "' to date with mask '" + aMask + "'");
        }
        
        try {
            date = df.parse(strDate);
        } catch (ParseException pe) {
            // log.error("ParseException: " + pe);
            throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }
        
        return (date);
    }
    
    public static String fmtTodayToFive() {
        Calendar calendar = Calendar.getInstance();
        String yy = String.valueOf(calendar.get(Calendar.YEAR) % 100);
        if (yy.length() == 1) {
            yy = "0" + yy;
        }
        String mmm = String.valueOf(calendar.get(Calendar.DAY_OF_YEAR));
        if (mmm.length() == 1) {
            mmm = "00" + mmm;
        }
        if (mmm.length() == 2) {
            mmm = "0" + mmm;
        }
        return yy + mmm;
    }
    
    public static String fmtTodayToSix() {
        Calendar calendar = Calendar.getInstance();
        String yy = String.valueOf(calendar.get(Calendar.YEAR) % 100);
        if (yy.length() == 1) {
            yy = "0" + yy;
        }
        String mm = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        if (mm.length() == 1) {
            mm = "0" + mm;
        }
        String dd = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        if (dd.length() == 1) {
            dd = "0" + dd;
        }
        return yy + mm + dd;
    }
    
    public static Date getAddDay(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        Date d = cal.getTime();
        return d;
    }
    
    // ************* start years *********
    
    /**
     * This method returns the current date in the format: MM/dd/yyyy
     * 
     * @return the current date
     * @throws ParseException
     */
    public static Calendar getCalendarOfToday() throws ParseException {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat(getDatePattern());
        
        // This seems like quite a hack (date -> string -> date),
        // but it works ;-)
        String todayAsString = df.format(today);
        Calendar cal = new GregorianCalendar();
        cal.setTime(convertStringToDate(todayAsString));
        
        return cal;
    }
    
    /**
     * This method attempts to convert an Oracle-formatted date in the form
     * dd-MMM-yyyy to mm/dd/yyyy.
     * 
     * @param aDate
     *            date from database as a string
     * @return formatted string for the ui
     */
    public static final String getDate(Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";
        
        if (aDate != null) {
            df = new SimpleDateFormat(getDatePattern());
            returnValue = df.format(aDate);
        }
        
        return (returnValue);
    }
    
    public static final String getDate(Date aDate, String pattern) {
        SimpleDateFormat df = null;
        String returnValue = "";
        
        if (aDate != null) {
            df = new SimpleDateFormat(pattern);
            returnValue = df.format(aDate);
        }
        
        return (returnValue);
    }
    
    /**
     * Return locale specific datePattern (default is MM/dd/yyyy)
     * 
     * @return a string representing the date pattern on the UI
     */
    public static String getDatePattern() {
        return "yyyy-MM-dd";
    }
    
    public static final String getDateTime(Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";
        
        if (aDate != null) {
            df = new SimpleDateFormat(getDateTimePattern());
            returnValue = df.format(aDate);
        }
        
        return (returnValue);
    }
    
    /**
     * This method generates a string representation of a date's date/time in
     * the format you specify on input
     * 
     * @param aMask
     *            the date pattern the string is in
     * @param aDate
     *            a date object
     * @return a formatted string representation of the date
     * 
     * @see java.text.SimpleDateFormat
     */
    public static final String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";
        
        if (aDate == null) {
            log.error("aDate is null!");
        } else {
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }
        
        return (returnValue);
    }
    
    public static String getDateTimePattern() {
        return dateTimePattern;
    }
    
    public static Date getEndOfAllTime() {
        return getEndOfThisYear();
    }
    
    public static Date getEndOfLastMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DATE, -1);
        setEndTime(cal);
        return cal.getTime();
    }
    
    /**
     * End of last week
     * 
     * @return
     */
    public static Date getEndOfLastWeek() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        cal.set(Calendar.DAY_OF_WEEK, 7);
        setEndTime(cal);
        return cal.getTime();
    }
    
    public static Date getEndOfLastYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.add(Calendar.DATE, -1);
        setEndTime(cal);
        return cal.getTime();
    }
    
    public static Date getEndOfNext30days() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 30);
        setEndTime(cal);
        return cal.getTime();
    }
    
    public static Date getEndOfNext60days() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 60);
        setEndTime(cal);
        return cal.getTime();
    }
    
    public static Date getEndOfNext7days() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        setEndTime(cal);
        return cal.getTime();
    }
    
    public static Date getEndOfNextMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 2);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DATE, -1);
        setEndTime(cal);
        return cal.getTime();
    }
    
    public static Date getEndOfNextWeek() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        cal.set(Calendar.DAY_OF_WEEK, 7);
        setEndTime(cal);
        return cal.getTime();
    }
    
    public static Date getEndOfNextYear() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 2);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.add(Calendar.DATE, -1);
        setEndTime(cal);
        return cal.getTime();
    }
    
    // ********** end days ***********
    /**
     * get the last datetime of today create by libh on 2006-5-28
     * 
     * @return
     */
    public static Date getEndOfThisDay() {
        Calendar cal = Calendar.getInstance();
        setEndTime(cal);
        Date d = cal.getTime();
        return d;
    }
    
    public static Date getEndOfThisDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        setEndTime(cal);
        Date d = cal.getTime();
        return d;
    }
    
    /**
     * get the last date of current month
     * 
     * @version create by libh on 2006-5-26
     * @return
     */
    public static Date getEndOfThisMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DATE, -1);
        setEndTime(cal);
        return cal.getTime();
    }
    
    public static Date getEndOfThisMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DATE, -1);
        setEndTime(cal);
        return cal.getTime();
    }
    
    public static Date getEndOfThisQuarter() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, getQuarterEndMonth(cal.getTime()));
        return getEndOfThisMonth(cal.getTime());
    }
    
    /**
     * get the last date of current week
     * 
     * @version create by libh on 2006-5-26
     * @return
     */
    public static Date getEndOfThisWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 7);
        setEndTime(cal);
        return cal.getTime();
    }
    
    public static Date getEndOfThisWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, 7);
        setEndTime(cal);
        return cal.getTime();
    }
    
    /**
     * get the last date of current year
     * 
     * @version create by libh on 2006-5-26
     * @return
     */
    public static Date getEndOfThisYear() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.add(Calendar.DATE, -1);
        setEndTime(cal);
        return cal.getTime();
    }
    
    public static Date getNow() {
        return new Date();
    }
    
    public static String getNowStr() {
        return new SimpleDateFormat(dateTimePattern).format(new Date());
    }
    
    public static Date getNumOfDaysBeforeNow(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -days);
        return cal.getTime();
    }
    
    public static int getQuarterEndMonth(Date date) {
        return getQuarterStartMonth(date) + 2;
    }
    
    public static int getQuarterStartMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int startMonth = 0;
        switch (cal.get(Calendar.MONTH)) {
            case 0:
                startMonth = 0;
                break;
            case 1:
                startMonth = 0;
                break;
            case 2:
                startMonth = 0;
                break;
            case 3:
                startMonth = 3;
                break;
            case 4:
                startMonth = 3;
                break;
            case 5:
                startMonth = 3;
                break;
            case 6:
                startMonth = 6;
                break;
            case 7:
                startMonth = 6;
                break;
            case 8:
                startMonth = 6;
                break;
            case 9:
                startMonth = 9;
                break;
            case 10:
                startMonth = 9;
                break;
            case 11:
                startMonth = 9;
                break;
            
            default:
                break;
        }
        
        return startMonth;
    }
    
    public final static String getShortDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat(shortDatePattern);
        return sdf.format(new Date());
    }
    
    public static Date getStartOfAllTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2005);
        return cal.getTime();
    }
    
    public static Date getStartOfLast30days() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -30);
        setStartTime(cal);
        return cal.getTime();
    }
    
    public static Date getStartOfLast60days() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -60);
        setStartTime(cal);
        return cal.getTime();
    }
    
    public static Date getStartOfLast7days() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        setStartTime(cal);
        return cal.getTime();
    }
    
    // ***************** end years ****************
    // ***************** start month **************
    public static Date getStartOfLastMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        setStartTime(cal);
        return cal.getTime();
    }
    
    // ******* end months ***********
    // ******* start weeks **********
    /**
     * Start of last week
     * 
     * @return
     */
    public static Date getStartOfLastWeek() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        setStartTime(cal);
        return cal.getTime();
    }
    
    public static Date getStartOfLastYear() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        setStartTime(cal);
        return cal.getTime();
    }
    
    public static Date getStartOfNextMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        setStartTime(cal);
        return cal.getTime();
    }
    
    public static Date getStartOfNextWeek() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        setStartTime(cal);
        return cal.getTime();
    }
    
    public static Date getStartOfNextYear() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        setStartTime(cal);
        return cal.getTime();
    }
    
    /**
     * get the first datetime of today create by libh on 2006-5-28
     * 
     * @return
     */
    public static Date getStartOfThisDay() {
        Calendar cal = Calendar.getInstance();
        setStartTime(cal);
        return cal.getTime();
    }
    
    public static Date getStartOfThisDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        setStartTime(cal);
        return cal.getTime();
    }
    
    /**
     * get the first date of current month
     * 
     * @version create by libh on 2006-5-26
     * @return
     */
    public static Date getStartOfThisMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        setStartTime(cal);
        return cal.getTime();
    }
    
    public static Date getStartOfThisMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        setStartTime(cal);
        return cal.getTime();
    }
    
    public static Date getStartOfThisQuarter() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, getQuarterStartMonth(cal.getTime()));
        return getStartOfThisMonth(cal.getTime());
    }
    
    /**
     * get the first date of current week
     * 
     * @version create by libh on 2006-5-26
     * @return
     */
    public static Date getStartOfThisWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 1);
        setStartTime(cal);
        return cal.getTime();
    }
    
    public static Date getStartOfThisWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        setStartTime(cal);
        return cal.getTime();
    }
    
    /**
     * get the first date of current year
     * 
     * @version create by libh on 2006-5-26
     * @return
     */
    public static Date getStartOfThisYear() {
        Calendar cal = Calendar.getInstance();
        setStartTime(cal);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }
    
    // ************ end weeks *********
    // ************ start day *********
    public static Date getToday() {
        return new Date();
    }
    
    /**
     * Returns tomorrow
     * 
     * @return
     */
    public static Date getTomorrow() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }
    
    /**
     * Returns yesterday
     * 
     * @return
     */
    public static Date getYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTime();
    }
    
    public static void main(String[] args) {
        System.out.println(DateUtil.getShortDateString());
        System.out.println(DateUtil.isValidDate("20130218", DateUtil.Y4M2D2));
    }
    
    /**
     * Set the calendar time to 23:59:59
     */
    private static void setEndTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
    }
    
    /**
     * Set the calendar time to 0:0:00
     * 
     * @param cal
     */
    private static void setStartTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
    
    public static Date addDate(Date tempDate, int seconds) {
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }
    
    /**
     * 验证时间格式是否正确
     * @param strDate
     * @param dateFormat
     * @return
     */
    public static boolean isValidDate(String strDate, String dateFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            sdf.parse(strDate);
            return true;
        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException说明格式不对
            return false;
        }
        
    }
    
    private DateUtil() {
    }
    
}
