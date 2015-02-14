package com.lindycoder.glenn.rentapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by gh250086 on 2/10/2015.
 */

public class GlennUtils {
    public static Calendar parseCalFromJSON(String inDate, String timeZone) {
        SimpleDateFormat old_fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        old_fmt.setTimeZone(TimeZone.getTimeZone(timeZone));
        Calendar retCal = null;
        try {
            Date parsedDate = old_fmt.parse(inDate);
            retCal = Calendar.getInstance();
            retCal.setTime(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retCal;
    }
}
