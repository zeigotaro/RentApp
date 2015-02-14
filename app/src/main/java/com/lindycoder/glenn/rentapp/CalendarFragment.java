package com.lindycoder.glenn.rentapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CalendarFragment extends Fragment {
    private static final FragmentId fragmentId = FragmentId.EVENTS;
    private EventCalendarView calendarView;
    private int yr, mon, dy;
    private Calendar selectedDate;

    private static final String TAG_ID = "ID";
    private static final String TAG_TITLE = "Title";
    private static final String TAG_START_DATE = "StartDate";
    private static final String TAG_DESCRIPTION = "Description";
    private static final String TAG_DATE_ADDED = "DateAdded";
    private static final String TAG_DATE= "date";
    private static final String TAG_TIMEZONE_TYPE= "timezone_type";
    private static final String TAG_TIMEZONE= "timezone";

    public static CalendarFragment newInstance() {
        CalendarFragment fragment = new CalendarFragment();
        return fragment;
    }

    public CalendarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        // products JSONArray
        JSONArray eventArray = ((AccountMainActivity) getActivity()).getEvents();
        HashMap<Integer, HashMap<Integer, Event>> monthEventMap = parseArrayToMap(eventArray);

        ((AccountMainActivity) getActivity()).setActionBarTitle(getString(R.string.calendar));
        Calendar c = Calendar.getInstance();
        yr = c.get(Calendar.YEAR);
        mon = c.get(Calendar.MONTH);
        dy = c.get(Calendar.DAY_OF_MONTH);
        EventCalendarView calendar = (EventCalendarView) rootView.findViewById(R.id.calendar_view);

        calendar.setMonthEventMap(monthEventMap);
        calendar.refreshCalendar();

/*
        calendar.setOnDayClickListener( new EventCalendarView.OnDayClickListener() {
            @Override
            public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day day) {
                for(int i = 0; i < eventDates.length; i++){
                    if( ( eventDates[i][0] <= day.getYear() && day.getYear() <= eventDates[i][3] ) &&
                            ( eventDates[i][1] <= day.getMonth()+1 && day.getMonth()+1 <= eventDates[i][4] ) &&
                            ( eventDates[i][2] <= day.getDay() && day.getDay() <= eventDates[i][5]) ){
                        Bundle bund = new Bundle();
                        bund.putInt("year", eventDates[i][0]);
                        bund.putInt("month", eventDates[i][1]);
                        bund.putInt("day", eventDates[i][2]);
                        Intent intent = new Intent(view.getContext(), ListEventActivity.class);
                        intent.putExtras(bund);
                        startActivity(intent);
                    }
                }
            }
        });
        calendarView.setOnDateChangeListener(new
             OnDateChangeListener() {
                 @Override
                 public void onSelectedDayChange(CalendarView view,
                                                 int year, int month, int dayOfMonth) {
                     //Toast.makeText(getActivity().getApplicationContext(),"Selected date is "+(month+1)+"-"+dayOfMonth+"-"+
                     //        year, Toast.LENGTH_SHORT). show();
                 }
         });
         */
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AccountMainActivity) activity).onSectionAttached(fragmentId);
    }

    private HashMap<Integer, HashMap<Integer, Event>> parseArrayToMap(JSONArray array) {

        HashMap<Integer, HashMap<Integer, Event>> retMap = new HashMap<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject c = array.getJSONObject(i);

                // Storing each json item in variable
                String id = c.getString(TAG_ID);
                String description = c.getString(TAG_DESCRIPTION);
                String title = c.getString(TAG_TITLE);
                JSONObject startDate = c.getJSONObject(TAG_START_DATE);
                JSONObject dateAdded = c.getJSONObject(TAG_DATE_ADDED);
                String jDate = startDate.getString(TAG_DATE);
                String timeZone = startDate.getString(TAG_TIMEZONE);
                Calendar cal = GlennUtils.parseCalFromJSON(jDate, timeZone);
                if(cal != null) {
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int year = cal.get(Calendar.YEAR);
                    Event event = new Event(id, new Day(day, month, year));
                    if(!retMap.containsKey(month)) {
                        HashMap<Integer, Event> map = new HashMap<>();
                        map.put(day, event);
                        retMap.put(month, map);
                    } else {
                        retMap.get(month).put(day, event);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return retMap;
    }

}
