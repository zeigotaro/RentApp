package com.lindycoder.glenn.rentapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * A fragment representing a list of "hot buy" products.
 */
public class HotbuyListFragment extends android.support.v4.app.ListFragment {

    private static final FragmentId fragmentId = FragmentId.HOTBUYS;
    ArrayList<HashMap<String, String>> hotbuyList;


    // ALL JSON node names
    private static final String TAG_ID = "ID";
    private static final String TAG_NAME = "Name";
    private static final String TAG_QUANTITY = "Quantity";
    private static final String TAG_PRICE = "Price";
    private static final String TAG_CATEGORY = "Category";
    private static final String TAG_END_DATE = "SaleEndDate";
    private static final String TAG_DATE_ADDED = "DateAdded";
    private static final String TAG_IMAGES = "Images";
    private static final String TAG_DATE= "date";
    private static final String TAG_TIMEZONE_TYPE= "timezone_type";
    private static final String TAG_TIMEZONE= "timezone";

    public static HotbuyListFragment newInstance() {
        HotbuyListFragment fragment = new HotbuyListFragment();
        return fragment;
    }

    public HotbuyListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hotbuy_list, container, false);
        ((AccountMainActivity) getActivity()).setActionBarTitle(getString(R.string.hot_buys));

        hotbuyList = new ArrayList<>();

        // Loading Product list in Background Thread
        new LoadList((ListView)rootView.findViewById(android.R.id.list)).execute();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AccountMainActivity) activity).onSectionAttached(fragmentId);
    }

    /**
     * Background Async Task to Load all Products
     */
    private class LoadList extends AsyncTask<String, String, String> {

        private final WeakReference<ListView> listViewReference;
        private Context mContext;
        // products JSONArray
        private JSONArray hbList = null;

        public LoadList(ListView listView) {
            // Use a WeakReference to ensure the listView can be garbage collected
            listViewReference = new WeakReference<>(listView);
        }

        /**
         * getting Products JSON
         */
        protected String doInBackground(String... args) {

            try {
                hbList = ((AccountMainActivity) getActivity()).getHotbuys();
                // looping through All products
                for (int i = 0; i < hbList.length(); i++) {
                    JSONObject c = hbList.getJSONObject(i);
                    Log.i("HOT_BUY_LIST", c.toString());
                    JSONObject end_date = c.getJSONObject(TAG_END_DATE);
                    Calendar cal = GlennUtils.parseCalFromJSON(end_date.getString(TAG_DATE), end_date.getString(TAG_TIMEZONE));
                    Calendar rightNow = Calendar.getInstance();

                    if(cal.compareTo(rightNow) >= 0) {
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);
                        String quantity = c.getString(TAG_QUANTITY);
                        //String category = c.getString(TAG_CATEGORY);
                        String category = ""; //NEED Category info from JSON object!
                        String price = c.getString(TAG_PRICE);
                        //String parsed_date = parseDate(date, "UTC");

                        HashMap<String, String> map = new HashMap<>();

                        map.put(TAG_ID, id);
                        map.put(TAG_NAME, name);
                        map.put(TAG_CATEGORY, category);
                        map.put(TAG_QUANTITY, quantity);
                        map.put(TAG_PRICE, price);

                        hotbuyList.add(map);
                    }
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
                            hotbuyList,
                            R.layout.hotbuy_list_item,
                            new String[] { TAG_NAME, TAG_CATEGORY, TAG_QUANTITY, TAG_PRICE},
                            new int[] { R.id.hotbuy_item_title, R.id.hotbuy_item_category, R.id.hotbuy_item_quantity, R.id.hotbuy_item_price });
                    // updating listview
                    setListAdapter(adapter);
                }
            });
            if (listViewReference != null) {
                final ListView listView = listViewReference.get();
                if (listView != null) {
                    listView.setDivider(new ColorDrawable(Color.RED));
                    listView.setDividerHeight(2);
                }
            }
        }
    }
}
