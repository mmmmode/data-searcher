package com.uestc.mode.modetest;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author mode
 * date: 2019/2/22
 */
public class Utils {
    public static String timeStamp2Date(long seconds) {
        if(seconds == 0)return "";
        String  format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }
}
