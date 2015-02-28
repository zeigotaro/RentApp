package com.lindycoder.glenn.rentapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


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
    private HashMap<Integer, Product> mProductMap = null;
    private Date mLastUpdated = null;

    private UserGetListsTask getListsTask = null;

    private ActionBar mActionBar = null;
    private final FragmentId[] myFragmentIdValues = FragmentId.values();
    private String mApiToken;
    private int mCurrentItemId;
    private FragmentId currentFragment = FragmentId.LOGOUT;
    private DisplayImageOptions options;
    public final static String LOGOUT_TOKEN = "com.lindycoder.glenn.rentapp.LOGOUT";


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

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
        // Get the message from the intent
        Intent intent = getIntent();
        mApiToken = intent.getStringExtra(LoginActivity.API_TOKEN);
        getListsTask = new UserGetListsTask();
        getListsTask.execute((Void) null);

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(android.R.drawable.alert_light_frame)
                .showImageOnFail(android.R.drawable.alert_light_frame)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        changeToFragment(myFragmentIdValues[position]);
    }

    public void changeToFragment(FragmentId fragmentId) {
        // update the main content by replacing fragments
        if(fragmentId != FragmentId.LOGOUT) {
            if(fragmentId != currentFragment) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if(fragmentManager != null) {
                    Fragment fragment = getFragmentInstance(fragmentId);
                    if(fragment != null) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, fragment)
                                .commit();
                    }
                }
            }
        } else {
            //Logout of app
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(LOGOUT_TOKEN, false);
            startActivity(intent);
        }
    }

    public void onSectionAttached(FragmentId fragmentId) {
        switch (fragmentId) {
            case HOME:
                mTitle = getString(R.string.title_dashboard);
                break;
            case HOTBUYS:
                mTitle = getString(R.string.title_hot_buys);
                break;
            case MESSAGES:
                mTitle = getString(R.string.title_messages);
                break;
            case EVENTS:
                mTitle = getString(R.string.title_calendar);
                break;
            case LOGOUT:
                mTitle = getString(R.string.title_logout);
                break;
            default:
                mTitle = "";
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

    private Fragment getFragmentInstance(FragmentId fragmentId)
    {
        Log.i("MAIN", "getFragInstance, id: " + Integer.toString(fragmentId.getValue()));
        currentFragment = fragmentId;
        switch (fragmentId) {
            case HOME:       return HomeFragment.newInstance();
            case HOTBUYS:    return HotbuyListFragment.newInstance(options);
            case MESSAGES:   return InboxFragment.newInstance();
            case EVENTS:     return CalendarFragment.newInstance();
            case HOTBUY_ITEM:     return HotbuyFragment.newInstance(mProductMap.get(mCurrentItemId), options);
            default:         return PlaceholderFragment.newInstance(fragmentId.getValue());
        }
    }

    public String getAPIToken() {
        return mApiToken;
    }

    public JSONArray getEvents() {
        return mListMap.get(getString(R.string.events));
    }

    public JSONArray getMessages() {
        return mListMap.get(getString(R.string.messages));
    }

    public HashMap<Integer, Product> getProductMap() {
        return mProductMap;
    }

    public void productItemSelected(int id) {
        Log.i("PRODUCT_SELECTED", Integer.toString(id));
        mCurrentItemId = id;
        changeToFragment(FragmentId.HOTBUY_ITEM);
    }

    public void onOrderSuccess() {
        getListsTask = new UserGetListsTask();
        getListsTask.execute((Void) null);
    }

    @Override
    public void onBackPressed() {
        if(currentFragment != FragmentId.HOME)
            changeToFragment(FragmentId.HOME);
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
            ((AccountMainActivity) activity).onSectionAttached(FragmentId.LOGOUT);
        }
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserGetListsTask extends AsyncTask<Void, Void, Boolean> {

        UserGetListsTask() { }
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
            parseToObjectLists();
            updateLists();
            return true;
        }

        private int fillList(String post_url, String param_name) {
            int retCount = 0;
            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
            nameValuePair.add(new BasicNameValuePair(getString(R.string.token_param_name), mApiToken));
            // Encode the POST parameters to URL format
            JSONObject result = ParseUtils.getJSONResultFromPost(post_url, nameValuePair);
            if(result != null) {
                try {
                    String resultCode = result.getString(getString(R.string.result_param_name));
                    String resultMsg = result.getString(getString(R.string.message_param_name));
                    Log.i("RESULT_CODE", resultCode);
                    Log.i("RESULT_MSG", resultMsg);
                    if ((resultCode != null) && resultCode.equals(getString(R.string.post_success))) {
                        JSONArray jArray = result.getJSONArray(param_name);
                        if(jArray != null) {
                            retCount = jArray.length();
                            mListMap.put(param_name, jArray);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return retCount;
        }

        private void parseToObjectLists() {
            JSONArray j = mListMap.get(getString(R.string.products));
            if(j != null) {
                mProductMap = ParseUtils.parseProductMapFromJSON(j);
            }
        }

        @Override
        protected void onPostExecute(Boolean b) {
            changeToFragment(FragmentId.HOME);
        }

    }

}
