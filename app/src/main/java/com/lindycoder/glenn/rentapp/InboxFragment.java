package com.lindycoder.glenn.rentapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 */
public class InboxFragment extends android.support.v4.app.ListFragment {

    private static final FragmentId fragmentId = FragmentId.MESSAGES;
    ArrayList<HashMap<String, String>> inboxList;

    // products JSONArray
    JSONArray inbox = null;

    // ALL JSON node names
    private static final String TAG_ID = "ID";
    private static final String TAG_SENDER = "Sender";
    private static final String TAG_TEXT = "Text";
    private static final String TAG_TITLE = "Title";
    private static final String TAG_DATE_ADDED = "DateAdded";
    private static final String TAG_DATE = "date";

    public static InboxFragment newInstance() {
        InboxFragment fragment = new InboxFragment();
        return fragment;
    }

    public InboxFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.inbox_list, container, false);
        ((AccountMainActivity) getActivity()).setActionBarTitle(getString(R.string.inbox));

        // Hashmap for ListView
        inboxList = new ArrayList<>();

        // Loading INBOX in Background Thread
        new LoadInbox(rootView.getContext()).execute();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AccountMainActivity) activity).onSectionAttached(fragmentId);
    }

    /**
     * Background Async Task to Load all INBOX messages
     */
    private class LoadInbox extends AsyncTask<String, String, String> {

        private Context mContext;

        public LoadInbox(Context context) {
            mContext = context;
        }

        /**
         * getting Inbox JSON
         */
        protected String doInBackground(String... args) {

            try {
                inbox = ((AccountMainActivity) getActivity()).getMessages();
                // looping through All messages
                for (int i = 0; i < inbox.length(); i++) {
                    JSONObject c = inbox.getJSONObject(i);

                    // Storing each json item in variable
                    String id = c.getString(TAG_ID);
                    String sender = c.getString(TAG_SENDER);
                    String title = c.getString(TAG_TITLE);
                    String text = c.getString(TAG_TEXT);
                    JSONObject d = c.getJSONObject(TAG_DATE_ADDED);
                    String date = d.getString(TAG_DATE);
                    String parsed_date = parseDate(date, "UTC");

                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<>();

                    // adding each child node to HashMap key => value
                    map.put(TAG_ID, id);
                    map.put(TAG_SENDER, sender);
                    map.put(TAG_TITLE, title);
                    map.put(TAG_TEXT, text);
                    map.put(TAG_DATE, parsed_date);

                    // adding HashList to ArrayList
                    inboxList.add(map);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            getActivity(),
                            inboxList,
                            R.layout.inbox_list_item,
                            new String[] { TAG_SENDER, TAG_TITLE, TAG_DATE, TAG_TEXT },
                            new int[] { R.id.from, R.id.subject, R.id.date, R.id.text });
                    // updating listview
                    setListAdapter(adapter);
                }
            });

            return null;
        }

        private String parseDate(String inDate, String timeZone) {
            String retDateString = null;
            Calendar cal = GlennUtils.parseCalFromJSON(inDate, timeZone);
            if(cal != null) {
                long timeInMillis = cal.getTimeInMillis();
                if (DateUtils.isToday(timeInMillis)) {
                    retDateString = DateUtils.formatDateTime(mContext, timeInMillis, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
                } else {
                    retDateString = DateUtils.formatDateTime(mContext, timeInMillis, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
                }
            }
            return retDateString;
        }
    }
}
