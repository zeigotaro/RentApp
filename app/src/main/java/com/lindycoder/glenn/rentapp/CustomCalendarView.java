package com.lindycoder.glenn.rentapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;


/**
 * TODO: document your custom view class.
 */
public class CustomCalendarView extends CalendarView {

    public CustomCalendarView(Context context) {
        super(context);
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
//      View v;
//      v.setOn

    }

    @Override
    public boolean performClick() {
        Log.e("event", "perform_click");
        return true;
    }
    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
