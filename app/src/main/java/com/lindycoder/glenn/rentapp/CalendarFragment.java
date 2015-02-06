package com.lindycoder.glenn.rentapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.Toast;
import java.util.Calendar;

import android.view.View;
import android.view.View.OnClickListener;

public class CalendarFragment extends Fragment {
    private CustomCalendarView calendarView;
    private int yr, mon, dy;
    private Calendar selectedDate;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static CalendarFragment newInstance(int sectionNumber) {
        CalendarFragment fragment = new CalendarFragment();
        Log.i("CALENDAR_FRAG", Integer.toString(sectionNumber));
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public CalendarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        ((AccountMainActivity) getActivity()).setActionBarTitle(getString(R.string.calendar));
        Calendar c = Calendar.getInstance();
        yr = c.get(Calendar.YEAR);
        mon = c.get(Calendar.MONTH);
        dy = c.get(Calendar.DAY_OF_MONTH);
        calendarView = (CustomCalendarView) rootView.findViewById(R.id.calendar_view);

        calendarView.setOnDateChangeListener(new
             OnDateChangeListener() {
                 @Override
                 public void onSelectedDayChange(CalendarView view,
                                                 int year, int month, int dayOfMonth) {
                     //Toast.makeText(getActivity().getApplicationContext(),"Selected date is "+(month+1)+"-"+dayOfMonth+"-"+
                     //        year, Toast.LENGTH_SHORT). show();
                 }
         });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AccountMainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

}
