package com.yungoal.onealert.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zj
 * @date 2019/7/25 11:38
 */
public class DateUtil {
    /**
     * 时间转换成时间戳
     * @param s 传入的时间
     * @return 返回时间戳 单位秒(s)
     * @throws ParseException
     */
    public static String timeToTimeStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime()/1000;
        return String.valueOf(ts);
    }

    /**
     * 时间戳转换成时间
     * @param s 传入的时间戳
     * @return 返回格式化时间
     */
    public static String timeStampToTime(String s){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt*1000);
        return simpleDateFormat.format(date);
    }
}
