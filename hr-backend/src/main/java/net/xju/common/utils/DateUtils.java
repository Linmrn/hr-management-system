package net.xju.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static Date parse(String dateTxt) {
        return parseStr2Date(dateTxt);
    }
    /**
     * 字符串转日期类型
     * @param dateTxt
     * @return
     */
    public static Date parseStr2Date(String dateTxt){
        Date date = null;

        try {
            if(dateTxt!=null && dateTxt.trim().length()>0){
                date = simpleDateFormat.parse(dateTxt.trim());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String parseDate2Str(Date date){
        if(date!=null){
            return  simpleDateFormat.format(date);
    }
        return null;
    }
}
