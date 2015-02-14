package com.lindycoder.glenn.rentapp;

import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;

public class CalendarAdapter extends BaseAdapter{
	
	static final int FIRST_DAY_OF_WEEK =0;
	Context context;
	Calendar cal;
	public String[] days;
//	OnAddNewEventClick mAddEvent;
	
	ArrayList<Day> dayList = new ArrayList<>();
    private HashMap<Integer, Event> eventMap = new HashMap<>();
    private final Day today;

	public CalendarAdapter(Context context, Calendar cal, HashMap<Integer, Event> newMap ){
		this.cal = cal;
		this.context = context;
		cal.set(Calendar.DAY_OF_MONTH, 1);
        Calendar rightNow = Calendar.getInstance();
        today = new Day(rightNow.get(Calendar.DAY_OF_MONTH), rightNow.get(Calendar.MONTH), rightNow.get(Calendar.YEAR));

		refreshDays(newMap);
	}

	@Override
	public int getCount() {
		return days.length;
	}

	@Override
	public Object getItem(int position) {
		return dayList.get(position);
	}

    public int getMonth(){
        return cal.get(Calendar.MONTH);
    }

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public int getPrevMonth(){
		if(cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)-1);
		}
		int month = cal.get(Calendar.MONTH);
		return (month == 0) ? 11 : (month - 1);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(position >= 0 && position < 7){
			view = vi.inflate(R.layout.day_of_week, null);
			TextView day = (TextView)view.findViewById(R.id.textView1);
            day.setText(posToString(position));

		} else {
			
	        view = vi.inflate(R.layout.day_view, null);
			FrameLayout today_layout = (FrameLayout)view.findViewById(R.id.today_frame);
            FrameLayout event_layout = (FrameLayout)view.findViewById(R.id.event_frame);
			Day d = dayList.get(position);
			if(d.Equals(today)){
				today_layout.setVisibility(View.VISIBLE);
			}else{
				today_layout.setVisibility(View.GONE);
			}

            int day = d.getDay();
            if(eventMap.containsKey(day) && (eventMap.get(day).getDay().getYear() == d.getYear())){
                Log.i("EVENT_MAP_LAYOUT", "Event found, day: " + Integer.toString(d.getDay()));
                event_layout.setVisibility(View.VISIBLE);
            }else{
                event_layout.setVisibility(View.GONE);
            }

			TextView dayTV = (TextView)view.findViewById(R.id.textView1);
			
			RelativeLayout relativeLayout = (RelativeLayout)view.findViewById(R.id.rl);
			ImageView imageView = (ImageView)view.findViewById(R.id.imageView1);

			imageView.setVisibility(View.VISIBLE);
			dayTV.setVisibility(View.VISIBLE);
			relativeLayout.setVisibility(View.VISIBLE);

			if(d.getDay() == 0){
				relativeLayout.setVisibility(View.GONE);
			}else{
				dayTV.setVisibility(View.VISIBLE);
				dayTV.setText(String.valueOf(d.getDay()));
			}
		}
		
		return view;
	}

    private String posToString(int pos) {
        switch(pos) {
            case 0: case 6:  return "S";
            case 1: return "M";
            case 2: case 4: return "T";
            case 3: return "W";
            case 5: return "F";
            default: return "";
        }
    }
	public void refreshDays(HashMap<Integer, Event> newMap)
    {
        if(newMap != null) {
            this.eventMap = newMap;
        } else {
            this.eventMap = new HashMap<>();
        }

    	// clear items
    	dayList.clear();
    	
    	int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)+7;
        int firstDay = (int)cal.get(Calendar.DAY_OF_WEEK);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        TimeZone tz = TimeZone.getDefault();
        
        // figure size of the array
        if(firstDay==1){
        	days = new String[lastDay+(FIRST_DAY_OF_WEEK*6)];
        }
        else {
        	days = new String[lastDay+firstDay-(FIRST_DAY_OF_WEEK+1)];
        }
        
        int j=FIRST_DAY_OF_WEEK;
        
        // populate empty days before first real day
        if(firstDay>1) {
	        for(j=0;j<(firstDay-FIRST_DAY_OF_WEEK)+7;j++) {
	        	days[j] = "";
	        	Day d = new Day(0,0,0);
	        	dayList.add(d);
	        }
        }
	    else {
	    	for(j=0;j<(FIRST_DAY_OF_WEEK*6)+7;j++) {
	        	days[j] = "";
	        	Day d = new Day(0,0,0);
	        	dayList.add(d);
	        }
	    	j = (FIRST_DAY_OF_WEEK*6)+1; // sunday => 1, monday => 7
	    }
        
        // populate days
        int dayNumber = 1;
        
        if(j>0 && dayList.size() > 0 && j != 1){
        	dayList.remove(j-1);
        }
        
        for(int i=j-1;i<days.length;i++) {
        	Day d = new Day(dayNumber,month,year);
        	
        	Calendar cTemp = Calendar.getInstance();
        	cTemp.set(year, month, dayNumber);

        	days[i] = "" + dayNumber;
        	dayNumber++;
        	dayList.add(d);
        }
    }

//	public abstract static class OnAddNewEventClick{
//		public abstract void onAddNewEventClick();
//	}
	
}
