package com.lindycoder.glenn.rentapp;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

public class Product {

	private int id;
	private String name;
	private String description;
	private double price;
	private List<Bitmap> imageList;
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
                   int hotbuyMinQuantity
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
	}
	
	public void setImageList(List<Bitmap> list){
		this.imageList = list;
	}
	
	public boolean isExpired(){
        boolean bIsExpired = (today.getTimeInMillis() > endTime.getTimeInMillis());
        return bIsExpired;
	}

    public boolean isSoldOut(){
        return (quantity > ordered);
    }

    public int getRemaining() {
        return(quantity - ordered);
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
