package com.cninsure.cp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class CalendarUtil {
	
	public static Calendar CAL = Calendar.getInstance();
	/**"yyyy-MM-dd HH:mm:ss"*/
	public static SimpleDateFormat SFT=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**"yyyy-MM-dd"*/
	public static SimpleDateFormat SFD=new SimpleDateFormat("yyyy-MM-dd");

	
	/**
     * 根据提供的年月日获取该月份的第一天
     * @Description: (这里用一句话描述这个方法的作用)
     * @Author: gyz
     * @Since: 2017-1-9下午2:26:57
     * @param year
     * @param monthOfYear
     * @return
     */
    public static Date getBeginDayofMonth(int year, int monthOfYear) {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = cal.getTime();
        return firstDate;
    }

    /**
     * 根据提供的年月获取该月份的最后一天
     * @Description: (这里用一句话描述这个方法的作用)
     * @Author: gyz
     * @Since: 2017-1-9下午2:29:38
     * @param year
     * @param monthOfYear
     * @return
     */
    public static Date getEndDayofMonth(int year, int monthOfYear) {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = cal.getTime();
        return lastDate;
    }
    /***
     * 获取当月的第一天 格式 "yyyy-MM-dd"
     * @return
     */
    public static String getBeginDayofMonthShort(){
		return SFD.format(getBeginDayofMonth(CAL.get(Calendar.YEAR),CAL.get(Calendar.MONTH)+1));
    }
    /***
     * 获取当月的最后一天 格式 "yyyy-MM-dd"
     * @return
     */
    public static String getEndDayofMonthshort(){
    	return SFD.format(getEndDayofMonth(CAL.get(Calendar.YEAR),CAL.get(Calendar.MONTH)+1));
    }
    /***
     * 获取当月的第一天 格式 "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String getBeginDayofMonthLong(){
		return SFT.format(getBeginDayofMonth(CAL.get(Calendar.YEAR),CAL.get(Calendar.MONTH)+1));
    }
    /***
     * 获取当月的最后一天 格式 "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String getEndDayofMonthLong(){
    	return SFT.format(getEndDayofMonth(CAL.get(Calendar.YEAR),CAL.get(Calendar.MONTH)+1));
    }
    
    /***
     * 获取当年的第一天 格式 "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String getBeginDayofYearLong(){
		return SFT.format(getBeginDayofMonth(CAL.get(Calendar.YEAR),1));
    }
    /***
     * 获取当年的第一天 格式 "yyyy-MM-dd"
     * @return
     */
    public static String getEndDayofYearShort(){
    	return SFD.format(getEndDayofMonth(CAL.get(Calendar.YEAR),1));
    }
    
}
