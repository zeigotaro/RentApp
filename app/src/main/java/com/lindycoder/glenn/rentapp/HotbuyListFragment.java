package com.lindycoder.glenn.rentapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Map;

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
        // products map
        private HashMap<Integer, Product> productMap = null;

        public LoadList(ListView listView) {
            // Use a WeakReference to ensure the listView can be garbage collected
            listViewReference = new WeakReference<>(listView);
        }

        /**
         * getting Products JSON
         */
        protected String doInBackground(String... args) {

            productMap = ((AccountMainActivity) getActivity()).getProductMap();
            // looping through All products
            for (Map.Entry<Integer, Product> entry : productMap.entrySet()) {
                Product p = entry.getValue();
                if (p != null) {
                    String id = Integer.toString(entry.getKey());

                    HashMap<String, String> map = new HashMap<>();

                    map.put(TAG_ID, id);
                    map.put(TAG_NAME, p.getName());
                    map.put(TAG_CATEGORY, "");
                    map.put(TAG_QUANTITY, Integer.toString(p.getRemaining()));
                    map.put(TAG_PRICE, Double.toString(p.getPrice()));

                    hotbuyList.add(map);
                }
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
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            HashMap<String, String> itemMap = (HashMap<String, String>)listView.getItemAtPosition(position);
                            ((AccountMainActivity) getActivity()).productItemSelected(Integer.parseInt(itemMap.get(TAG_ID)));
                        }
                    });
                }
            }
        }
    }
}
