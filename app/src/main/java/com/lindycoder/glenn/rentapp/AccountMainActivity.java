package com.lindycoder.glenn.rentapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import android.text.format.DateUtils;


public class AccountMainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private HashMap<String, JSONArray> mListMap = new HashMap<>();
    private Date mLastUpdated = null;

    private UserGetObjectCountsTask mCountTask = null;

    private ActionBar mActionBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //make width half the screen
        ViewGroup navDrawer = (ViewGroup)findViewById(R.id.navigation_drawer);
        ViewGroup.LayoutParams params = navDrawer.getLayoutParams();
        int width = getResources().getDisplayMetrics().widthPixels/2;
        params.width = width;
        navDrawer.setLayoutParams(params);

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                drawerLayout);

        // Get the message from the intent
        Intent intent = getIntent();
        String apiToken = intent.getStringExtra(LoginActivity.API_TOKEN);
        mCountTask = new UserGetObjectCountsTask(apiToken);
        mCountTask.execute((Void) null);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager != null) {
            Fragment fragment = getFragmentInstance(position + 1);
            if(fragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_my_account);
                break;
            case 2:
                mTitle = getString(R.string.title_hot_buys);
                break;
            case 3:
                mTitle = getString(R.string.title_messages);
                break;
            case 4:
                mTitle = getString(R.string.title_calendar);
                break;
            case 5:
                mTitle = getString(R.string.title_logout);
                break;
        }
    }

    public void restoreActionBar() {
        if(mActionBar == null) {
            mActionBar = getSupportActionBar();
            mActionBar.setTitle(getString(R.string.dashboard));
        }
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
    }

    public void setActionBarTitle(String newTitle) {
        if(mActionBar != null) {
            mActionBar.setTitle(newTitle);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.account_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateLists() {
        for (HashMap.Entry<String, JSONArray> entry : mListMap.entrySet()) {
            String key = entry.getKey();
            JSONArray jArray = entry.getValue();
            int count = jArray.length();
            for(int i = 0; i < count; i++) {
                try {
                    Log.i(key, jArray.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.i(key + "_COUNT", Integer.toString(count));
            refreshImg(key + "_img_id", count);
        }
    }

    private void refreshImg(String img_id, int count) {
        if(count > 0) {
            //TODO: Refresh/set bubble
        } else {
            //TODO: Remove bubble
        }
    }

    private Fragment getFragmentInstance(int id)
    {
        switch (id) {
            case 3:
                return MessageFragment.newInstance(id);
            default:
                return PlaceholderFragment.newInstance(id);
        }
    }

    public static class MessageFragment extends ListFragment {
        // Progress Dialog
        private ProgressDialog pDialog;

        ArrayList<HashMap<String, String>> inboxList;

        // products JSONArray
        JSONArray inbox = null;

        // ALL JSON node names
        private static final String TAG_ID = "ID";
        private static final String TAG_SENDER = "Sender";
        private static final String TAG_EMAIL = "email";
        private static final String TAG_TITLE = "Title";
        private static final String TAG_DATE_ADDED = "DateAdded";
        private static final String TAG_DATE = "date";

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static MessageFragment newInstance(int sectionNumber) {
            MessageFragment fragment = new MessageFragment();
            Log.i("MESSAGE_FRAG", Integer.toString(sectionNumber));
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public MessageFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.inbox_list, container, false);
            ((AccountMainActivity) getActivity()).setActionBarTitle(getString(R.string.inbox));

            // Hashmap for ListView
            inboxList = new ArrayList<HashMap<String, String>>();

            // Loading INBOX in Background Thread
            new LoadInbox(rootView.getContext()).execute();
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((AccountMainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        /**
         * Background Async Task to Load all INBOX messages by making HTTP Request
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
                    inbox = ((AccountMainActivity) getActivity()).mListMap.get(getString(R.string.messages));
                    // looping through All messages
                    for (int i = 0; i < inbox.length(); i++) {
                        JSONObject c = inbox.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_ID);
                        String sender = c.getString(TAG_SENDER);
                        String title = c.getString(TAG_TITLE);
                        JSONObject d = c.getJSONObject(TAG_DATE_ADDED);
                        String date = d.getString(TAG_DATE);
                        String parsed_date = parseDate(date);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_SENDER, sender);
                        map.put(TAG_TITLE, title);
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
                                new String[] { TAG_SENDER, TAG_TITLE, TAG_DATE },
                                new int[] { R.id.from, R.id.subject, R.id.date });
                         // updating listview
                         setListAdapter(adapter);
                    }
                });

                return null;
            }

            private String parseDate(String inDate) {
                SimpleDateFormat old_fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
                old_fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
                try {
                    Date parsedDate = old_fmt.parse(inDate);
                    long timeInMillis = parsedDate.getTime();
                    String retDateString;
                    if(DateUtils.isToday(timeInMillis)) {
                        retDateString  = DateUtils.formatDateTime(mContext, timeInMillis, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
                    } else {
                        retDateString  = DateUtils.formatDateTime(mContext, timeInMillis, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
                    }
                    return retDateString;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            Log.i("PLACEHOLDER_FRAG", Integer.toString(sectionNumber));
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_account_main, container, false);
            ((AccountMainActivity) getActivity()).setActionBarTitle(getString(R.string.hot_buys));
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((AccountMainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserGetObjectCountsTask extends AsyncTask<Void, Void, Boolean> {

        private final String mApiToken;

        UserGetObjectCountsTask(String apiToken) {
            mApiToken = apiToken;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HashMap<String, String> map = new HashMap<>();
            map.put(getString(R.string.messages), getString(R.string.http_message_post_url));
            map.put(getString(R.string.products), getString(R.string.http_hot_buy_post_url));
            map.put(getString(R.string.events), getString(R.string.http_event_post_url));

            for(HashMap.Entry<String, String> entry : map.entrySet()) {
                fillList(entry.getValue(), entry.getKey());
            }
            mLastUpdated = new Date();
            updateLists();
            return true;
        }

        private int fillList(String post_url, String param_name) {
            int retCount = 0;
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(post_url);
            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
            nameValuePair.add(new BasicNameValuePair(getString(R.string.token_param_name), mApiToken));
            // Encode the POST parameters to URL format
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                HttpResponse response = client.execute(httpPost);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    JSONObject result = new JSONObject(EntityUtils.toString(resEntity));
                    String resultCode = result.getString(getString(R.string.result_param_name));
                    String resultMsg = result.getString(getString(R.string.message_param_name));
                    Log.i("RESULT_CODE", resultCode);
                    Log.i("RESULT_MSG", resultMsg);
                    if ((resultCode != null) && resultCode.equals(getString(R.string.post_success))) {
                        JSONArray jArray = result.getJSONArray(param_name);
                        if(jArray != null) {
                            retCount = jArray.length();

                            /*
                            //fill object list and add it to map
                            ArrayList<String> list = new ArrayList<>();
                            for (int i=0;i<jArray.length();i++){
                                list.add(jArray.get(i).toString());
                            }
                            */
                            mListMap.put(param_name, jArray);
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                // write exception to log
                e.printStackTrace();
            }
            return retCount;
        }
    }

}
