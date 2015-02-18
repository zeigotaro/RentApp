package com.lindycoder.glenn.rentapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class ParseUtils {
    // ALL JSON node names
    private static final String TAG_ID = "ID";
    private static final String TAG_NAME = "Name";
    private static final String TAG_QUANTITY = "Quantity";
    private static final String TAG_ORDERED = "OrderedQuantity";
    private static final String TAG_PRICE = "Price";
    private static final String TAG_CATEGORY = "Category";
    private static final String TAG_HOTBUY_MIN_QUANTITY = "HotBuyMinimumQuantity";
    private static final String TAG_MIN_QUANTITY = "MinQuantityPerOrder";
    private static final String TAG_MAX_QUANTITY = "MaxQuantityPerOrder";
    private static final String TAG_END_DATE = "SaleEndDate";
    private static final String TAG_DATE_ADDED = "DateAdded";
    private static final String TAG_IMAGES = "Images";
    private static final String TAG_DATE= "date";
    private static final String TAG_TIMEZONE_TYPE= "timezone_type";
    private static final String TAG_TIMEZONE= "timezone";
    private static final String TAG_CONTAINER_SALE = "ContainerSale";
    private static final String TAG_CONTAINER_QUANTITY = "ContainerQuantity";
    private static final String TAG_RESULT_PARAM = "ResultCode";
    private static final String TAG_MESSAGE_PARAM = "ResultMessage";
    private static final String TAG_POST_SUCCESS = "0";

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

    public static JSONObject parseResultToObject(JSONObject j, String tag) {
        JSONObject ret = null;
        try {
            String resultCode = j.getString(TAG_RESULT_PARAM);
            String resultMsg = j.getString(TAG_MESSAGE_PARAM);
            Log.i("RESULT_CODE", resultCode);
            Log.i("RESULT_MSG", resultMsg);
            if ((resultCode != null) && resultCode.equals(TAG_POST_SUCCESS)) {
                    ret = j.getJSONObject(tag);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static HashMap<Integer, Product> parseProductMapFromJSON(JSONArray jArray) {
        HashMap<Integer, Product> map = new HashMap<>();
        if(jArray != null) {
            try {
                // looping through All products
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject c = jArray.getJSONObject(i);
                    JSONObject end_jObj = c.getJSONObject(TAG_END_DATE);
                    Calendar end_date = ParseUtils.parseCalFromJSON(end_jObj.getString(TAG_DATE), end_jObj.getString(TAG_TIMEZONE));
                    JSONObject added_jObj = c.getJSONObject(TAG_DATE_ADDED);
                    Calendar date_added = ParseUtils.parseCalFromJSON(added_jObj.getString(TAG_DATE), added_jObj.getString(TAG_TIMEZONE));
                    int id = Integer.parseInt(c.getString(TAG_ID));
                    Product p = new Product(    id,
                                                c.getString(TAG_NAME),
                                                "",
                                                Double.parseDouble(c.getString(TAG_PRICE)),
                                                end_date,
                                                date_added,
                                                jsonParseToInt(c, TAG_QUANTITY),
                                                jsonParseToInt(c, TAG_ORDERED),
                                                jsonParseToInt(c, TAG_MIN_QUANTITY),
                                                jsonParseToInt(c, TAG_MAX_QUANTITY),
                                                jsonParseToInt(c, TAG_HOTBUY_MIN_QUANTITY)
                    );

                    //TODO: For production version, re-enable expiration check?
                    //if(p.isExpired() != Boolean.TRUE) {
                        map.put(id, p);
                    //}
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    private static int jsonParseToInt(JSONObject jsonObject, String tag) {
        int ret = 0;
        try {
            if(!jsonObject.getString(tag).equals(JSONObject.NULL)) {
                String s = jsonObject.getString(tag);
                ret = ((s != null) && s.matches("[-+]?\\d*\\.?\\d+"))? Integer.parseInt(s) : 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
