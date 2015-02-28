package com.lindycoder.glenn.rentapp;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Product {

	private int id;
	private String name;
	private String description;
	private double price;
	private List<String> urlList;
	private int quantity;
    private int ordered;
    private int minPurchaseQuantity;
    private int maxPurchaseQuantity;
    private int hotbuyMinQuantity;
    private Calendar endTime;
    private Calendar timeAdded;
    private static final Calendar today = Calendar.getInstance();

	public static final int DEFAULT_EVENT_ICON = 0;
	public static final int COLOR_RED = 1;
	public static final int COLOR_BLUE = 2;
	public static final int COLOR_YELLOW = 3;
	public static final int COLOR_PURPLE = 4;
	public static final int COLOR_GREEN = 5;

	public Product(int id,
                   String name,
                   String description,
                   double price,
                   Calendar endTime,
                   Calendar timeAdded,
                   int quantity,
                   int ordered,
                   int minPurchaseQuantity,
                   int maxPurchaseQuantity,
                   int hotbuyMinQuantity,
                   List<String> urlList
                   ){
		this.id = id;
		this.name = name;
        this.description = description;
        this.price = price;
        this.endTime = endTime;
        this.timeAdded = timeAdded;
        this.quantity = quantity;
        this.ordered = ordered;
        this.minPurchaseQuantity = minPurchaseQuantity;
        this.maxPurchaseQuantity = maxPurchaseQuantity;
        this.hotbuyMinQuantity = hotbuyMinQuantity;
        this.urlList = urlList;
	}
	
	public void setUrlList(List<String> list){
		this.urlList = list;
	}

    public String getPreviewUrl() {
        String url = null;
        if(!urlList.isEmpty())
        {
            url = urlList.get(0);
        }
        return url;
    }
	
	public boolean isExpired(){
        boolean bIsExpired = (today.getTimeInMillis() > endTime.getTimeInMillis());
        return bIsExpired;
	}

    public String getTimeRemainingString(String defaultString) {
        if(!isExpired()) {
            final int secInMs = 1000;
            final int minInMs = 60 * secInMs;
            final int hourInMs = 60 * minInMs;
            long diff = endTime.getTimeInMillis() - today.getTimeInMillis();
            int dhours = (int)(diff / hourInMs);
            int dmin = (int)( (diff % hourInMs ) / minInMs );
            int dsec = (int)( (diff % minInMs) / secInMs );
            String hours = Integer.toString(dhours);
            String minutes = Integer.toString(dmin);
            String seconds = Integer.toString(dsec);
            return hours + " hr, " + minutes + " min, " + seconds + " sec";
        } else {
            return defaultString;
        }
    }

    public boolean isSoldOut(){
        return (quantity > ordered);
    }

    public int getRemaining() {
        return(quantity - ordered);
    }

    public int getOrdered() {
        return ordered;
    }

    public int getMinOrder() {
        return minPurchaseQuantity;
    }

    public int getId() {
        return id;
    }

    public boolean isAcceptableOrderQuantity(int num) {
        return (num >= minPurchaseQuantity) && (num <= maxPurchaseQuantity);
    }

	public String getName(){
		return name;
	}
	
    public double getPrice() {
        return price;
    }

}
