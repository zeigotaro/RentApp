package com.lindycoder.glenn.rentapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HotbuyFragment extends Fragment {
    private static final FragmentId fragmentId = FragmentId.HOTBUY_ITEM;

    private static Product product;
    private String productName;
    private String price;
    private String quantityString;
    private String apiToken;
    private View rootView;
    private int currentUnitAmount = 0;
    private static HotbuyFragment currentInstance = null;
    private static DisplayImageOptions options;

    public static HotbuyFragment newInstance(Product p, DisplayImageOptions opts) {
        HotbuyFragment fragment = new HotbuyFragment();
        product = p;
        currentInstance = fragment;
        options = opts;
        return fragment;
    }

    public HotbuyFragment() {
    }

    public String getDialogMsg() {
        String msgString = productName + "\n" +"$" + price + "\n\n" + quantityString;
        return msgString;
    }

    public int getCurrentUnitAmount() {
        return currentUnitAmount;
    }

    public static HotbuyFragment getCurrentInstance() {
        return currentInstance;
    }

    public void postBuyItem() {
        new UserPostItemBuy(apiToken, product.getId(),currentUnitAmount).execute();
    }

    public void showResult(String result) {
        new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT)
                .setTitle(result)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((AccountMainActivity)getActivity()).changeToFragment(FragmentId.HOTBUYS);
                    }
                })
                .show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_hotbuy, container, false);
            ViewHolder holder = new ViewHolder();
            holder.txtName = (TextView) rootView.findViewById(R.id.name_space);
            holder.txtPrice = (TextView) rootView.findViewById(R.id.price_space);
            holder.txtDescription = (TextView) rootView.findViewById(R.id.item_description);
            holder.txtMinOrder = (TextView) rootView.findViewById(R.id.amount_required_space);
            holder.txtExpirationTime = (TextView) rootView.findViewById(R.id.offer_time_space);
            holder.txtRequested = (TextView) rootView.findViewById(R.id.requested_space);
            holder.txtAvailable = (TextView) rootView.findViewById(R.id.available_space);
            holder.imgPreview = (ImageView) rootView.findViewById(R.id.image_space);
            holder.btnOrderNow = (Button) rootView.findViewById(R.id.order_button);
            rootView.setTag(holder);
        }

        ((AccountMainActivity) getActivity()).setActionBarTitle(getString(R.string.open_hot_buys));

        if(rootView != null) {
            ViewHolder holder = (ViewHolder) rootView.getTag();
            productName = product.getName();
            price = Double.toString(product.getPrice());
            holder.txtName.setText(productName);
            holder.txtPrice.setText(price);
            holder.txtExpirationTime.setText(product.getTimeRemainingString(getString(R.string.sold_out)));
            holder.txtMinOrder.setText(getString(R.string.amount_required) + " " + product.getMinOrder());
            holder.txtAvailable.setText(getString(R.string.available) + " " + product.getRemaining());
            holder.txtRequested.setText(getString(R.string.requested) + " " + product.getOrdered());
            ImageLoader.getInstance().displayImage(product.getPreviewUrl(), holder.imgPreview, options);

            apiToken = ((AccountMainActivity) getActivity()).getAPIToken();
            HashMap <String, TextView> map = new HashMap<>();
            map.put(getString(R.string.description_param_name), holder.txtDescription);
            new UserGetIndividualProduct(apiToken, product.getId(),map).execute();
            rootView.setFocusableInTouchMode(true);
            rootView.requestFocus();
            rootView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if( keyCode == KeyEvent.KEYCODE_BACK ) {
                        ((AccountMainActivity) getActivity()).changeToFragment(FragmentId.HOTBUYS);
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            // get edittext component
            final EditText edittext = (EditText) rootView.findViewById(R.id.editText_input);

            edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String amount = edittext.getText().toString();
                        if(!amount.isEmpty()) {
                            currentUnitAmount = Integer.parseInt(amount);
                            quantityString = getString(R.string.quantity) + " " + amount;
                            Log.i("EDIT_TEXT", "done action, int: " + Integer.toString(currentUnitAmount));
                            HotbuyFragment.getCurrentInstance().updateMinWarning();
                        }
                    }
                    return false;
                }
            });

            edittext.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int actionId, KeyEvent event) {
                    if(actionId == KeyEvent.KEYCODE_BACK) {
                        ((AccountMainActivity) getActivity()).changeToFragment(FragmentId.HOTBUYS);
                        return true;
                    }
                    return false;
                }
            });

            holder.btnOrderNow.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View view) {
                    final HotbuyFragment hbInstance = HotbuyFragment.getCurrentInstance();
                    if(hbInstance != null)
                    {
                        if (hbInstance.getCurrentUnitAmount() > 0) {
                            new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT)
                                    .setTitle(R.string.dialog_confirm_order)
                                    .setMessage(hbInstance.getDialogMsg())
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // place order
                                            hbInstance.postBuyItem();
                                        }
                                    })
                                    .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // cancel, exit dialog
                                        }
                                    })
                                    .show();
                        }
                    }
                }
            });
        }
        return rootView;
    }

    public void updateMinWarning() {
        if(rootView != null) {
            final TextView minWarning = (TextView) rootView.findViewById(R.id.minimum_not_reached_space);
            if(currentUnitAmount >= product.getMinOrder()) {
                minWarning.setText("");
            } else {
               minWarning.setText(getString(R.string.minimum_not_reached));
            }
        }
    }

        /*
        loadBitmap(R.drawable.hotbuys, (ImageView) rootView.findViewById(R.id.hotbuy_space), FragmentId.HOTBUYS);
        loadBitmap(R.drawable.messages, (ImageView) rootView.findViewById(R.id.message_space), FragmentId.MESSAGES);
        loadBitmap(R.drawable.calendar, (ImageView) rootView.findViewById(R.id.cal_space), FragmentId.EVENTS);
        */

    static class ViewHolder {
        TextView txtName;
        TextView txtPrice;
        TextView txtDescription;
        TextView txtExpirationTime;
        TextView txtMinOrder;
        TextView txtAvailable;
        TextView txtRequested;
        ImageView imgPreview;
        Button btnOrderNow;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserGetIndividualProduct extends AsyncTask<Void, Void, Boolean> {

        private HashMap<WeakReference<TextView>, String> weakMap;
        private final String mApiToken;
        private final int mId;

        UserGetIndividualProduct (String apiToken, int id, HashMap<String, TextView> map) {
            // Use a WeakReferences to ensure the textView can be garbage collected
            weakMap = new HashMap<>();
            for (Map.Entry<String, TextView> entry : map.entrySet()) {
                WeakReference<TextView> viewReference = new WeakReference<>(entry.getValue());
                weakMap.put(viewReference, entry.getKey()); //Yes, this is correct, the key/value is flipped for weakMap
            }
            mApiToken = apiToken;
            mId = id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<>(2);
            nameValuePair.add(new BasicNameValuePair(getString(R.string.token_param_name), mApiToken));
            nameValuePair.add(new BasicNameValuePair(getString(R.string.id_param_name), Integer.toString(mId)));
            JSONObject result = ParseUtils.getJSONResultFromPost(getString(R.string.http_hot_buy_post_url), nameValuePair);
            if(result != null) {
                JSONObject jProduct = ParseUtils.parseResultToObject(result, getString(R.string.product));
                if(jProduct != null) {
                    Log.i("PRODUCT_DETAIL", jProduct.toString());
                    try {
                        for (Map.Entry<WeakReference<TextView>, String> entry : weakMap.entrySet()) {
                            String oldValue = entry.getValue();
                            String resultText;
                            if(jProduct.isNull(oldValue)) {
                                resultText = "0";
                            } else {
                                resultText = jProduct.getString(oldValue);
                            }

                            entry.setValue(getPrefixText(oldValue) + resultText);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }

        protected void onPostExecute(Boolean b) {
            for (Map.Entry<WeakReference<TextView>, String> entry : weakMap.entrySet()) {
                final TextView textView = entry.getKey().get();
                if (textView != null) {
                    textView.setText(entry.getValue());
                }
            }
        }

    }

    private String getPrefixText(String paramName) {
        String retString = "";
        if(paramName.equals(getString(R.string.min_order_param_name))) {
            retString = getString(R.string.amount_required) + " ";
        } else if(paramName.equals(getString(R.string.requested_param_name))) {
            retString = getString(R.string.requested) + " ";
        }
        return retString;
    }

    public class UserPostItemBuy extends AsyncTask<Void, Void, Boolean> {

        private final String mApiToken;
        private final int mId;
        private final int mQuantity;

        UserPostItemBuy (String apiToken, int id, int quantity) {
            mApiToken = apiToken;
            mId = id;
            mQuantity = quantity;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            List<NameValuePair> nameValuePair = new ArrayList<>(3);
            nameValuePair.add(new BasicNameValuePair(getString(R.string.token_param_name), mApiToken));
            nameValuePair.add(new BasicNameValuePair(getString(R.string.id_param_name), Integer.toString(mId)));
            nameValuePair.add(new BasicNameValuePair(getString(R.string.quantity_param_name), Integer.toString(mQuantity)));
            JSONObject result = ParseUtils.getJSONResultFromPost(getString(R.string.buy_item_post_url), nameValuePair);
            if(result != null) {
                return ParseUtils.testPostResult(result);
            }
            return false;
        }

        protected void onPostExecute(Boolean b) {
            String postResult = b ? "Order placed successfully" : "Order attempt failed";
            showResult(postResult);
        }
    }
}


