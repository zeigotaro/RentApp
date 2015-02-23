package com.lindycoder.glenn.rentapp;

import java.lang.String;import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.graphics.Bitmap;

public class Event {
	
	private int color;
	private String title;
	private String description;
	private String location;
	private Bitmap image;
	private String eventId;
    private Day day;
	
	public static final int DEFAULT_EVENT_ICON = 0;
	public static final int COLOR_RED = 1;
	public static final int COLOR_BLUE = 2;
	public static final int COLOR_YELLOW = 3;
	public static final int COLOR_PURPLE = 4;
	public static final int COLOR_GREEN = 5;
	
	public Event(String eventID, String title, String description, Day day){
		this.eventId = eventID;
        this.title = title;
        this.description = description;
		this.day = day;
	}
	
	public int getColor(){
		return color;
	}
	
	public void setColor(int color){
		this.color = color;
	}
	
	/**
	 * Get the event title
	 * 
	 * @return title
	 */
	public String getTitle(){
		return title;
	}
	
	/**
	 * Get the event description
	 * 
	 * @return description
	 */
	public String getDescription(){
		return description;
	}
	
	
	public Bitmap getImage(){
		return image;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public void setLocation(String location){
		this.location = location;
	}
	
	public String getLocation(){
		return location;
	}
	
	/**
	 * Set the title of the event
	 * 
	 * @param title
	 */
	public void setTitle(String title){
		this.title = title;
	}
	
	/**
	 * Gets the event id
	 * 
	 * @return event id
	 */
	public String getEventId(){
		return eventId;
	}
	
	/**
	 * Get date of the event
	 * 
	 * @return event date
	 */
	public Day getDay(){
		return this.day;
	}
	
}
