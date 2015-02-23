package com.lindycoder.glenn.rentapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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
        new LoadInbox(rootView.getContext(),(ListView)rootView.findViewById(android.R.id.list)).execute();
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
        private final WeakReference<ListView> listViewReference;

        private Context mContext;

        public LoadInbox(Context context, ListView listView) {
            mContext = context;
            listViewReference = new WeakReference<>(listView);
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

            return null;
        }

        @Override
        protected void onPostExecute(String string) {
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

            if (listViewReference != null) {
                final ListView listView = listViewReference.get();
                if (listView != null) {
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            HashMap<String, String> itemMap = (HashMap<String, String>)listView.getItemAtPosition(position);
                            new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT)
                                    .setTitle(itemMap.get(TAG_TITLE))
                                    .setMessage(itemMap.get(TAG_TEXT))
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                    });
                }
            }
        }

        private String parseDate(String inDate, String timeZone) {
            String retDateString = null;
            Calendar cal = ParseUtils.parseCalFromJSON(inDate, timeZone);
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
