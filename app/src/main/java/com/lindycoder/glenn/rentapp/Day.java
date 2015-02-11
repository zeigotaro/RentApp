package com.lindycoder.glenn.rentapp;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import android.text.format.Time;

public class Day{
	
	int monthEndDay;
	int day;
	int year;
	int month;

	Day(int day, int month, int year){
		this.day = day;
		this.month = month;
        this.year = year;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, day);
		int end = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(year, month, end);
		TimeZone tz = TimeZone.getDefault();
		monthEndDay = Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));
	}
	
	public int getMonth(){
		return month;
	}
	
	public int getYear(){
		return year;
	}
	
	public void setDay(int day){
		this.day = day;
	}
	
	public int getDay(){
		return day;
	}

    public boolean Equals(Day day) {
        return ((day.getYear() == this.year) && (day.getMonth() == this.month) && (day.getDay() == this.day));
    }

}
