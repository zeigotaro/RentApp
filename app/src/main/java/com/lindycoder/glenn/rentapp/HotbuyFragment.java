package com.lindycoder.glenn.rentapp;

import android.app.Activity;
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
import java.util.List;


public class HotbuyFragment extends Fragment {
    private static final FragmentId fragmentId = FragmentId.HOTBUY_ITEM;

    private static Product product;
    private View rootView;
    public static HotbuyFragment newInstance(Product p) {
        HotbuyFragment fragment = new HotbuyFragment();
        product = p;
        return fragment;
    }

    public HotbuyFragment() {
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
            holder.btnOrderNow = (Button) rootView.findViewById(R.id.order_button);
            rootView.setTag(holder);
        }

        ((AccountMainActivity) getActivity()).setActionBarTitle(getString(R.string.open_hot_buys));

        if(rootView != null) {
            ViewHolder holder = (ViewHolder) rootView.getTag();
            holder.txtName.setText(product.getName());
            holder.txtPrice.setText(Double.toString(product.getPrice()));
            holder.btnOrderNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("ITEM_PAGE", "ORDER NOW clicked");
                }
            });
            String apiToken = ((AccountMainActivity) getActivity()).getAPIToken();
            new UserGetIndividualProduct(apiToken, product.getId(),holder.txtDescription).execute();
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
            EditText edittext = (EditText) rootView.findViewById(R.id.editText_input);

            edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        Log.i("EDIT_TEXT_LISTEN", "done action rec'd");
                    }
                    return false;
                }
            });
        }
        return rootView;
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
        ImageView imgPreview;
        Button btnOrderNow;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserGetIndividualProduct extends AsyncTask<Void, Void, Boolean> {

        private final WeakReference<TextView> textViewReference;
        private final String mApiToken;
        private final int mId;
        private String description;

        UserGetIndividualProduct (String apiToken, int id, TextView view) {
            // Use a WeakReference to ensure the textView can be garbage collected
            textViewReference = new WeakReference<>(view);
            mApiToken = apiToken;
            mId = id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(getString(R.string.http_hot_buy_post_url));
            // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<>(2);
            nameValuePair.add(new BasicNameValuePair(getString(R.string.token_param_name), mApiToken));
            nameValuePair.add(new BasicNameValuePair(getString(R.string.id_param_name), Integer.toString(mId)));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                HttpResponse response = client.execute(httpPost);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    JSONObject result = new JSONObject(EntityUtils.toString(resEntity));
                    JSONObject jProduct = ParseUtils.parseResultToObject(result, getString(R.string.product));
                    if(jProduct != null) {
                        Log.i("PRODUCT_DETAIL", jProduct.toString());
                        description = jProduct.getString(getString(R.string.description_param_name));
                    }

                }
            } catch (IOException | JSONException e) {
                // write exception to log
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(Boolean b) {
            if (textViewReference != null) {
                final TextView textView = textViewReference.get();
                if (textView != null) {
                    textView.setText(description);
                }
            }
        }
    }
}


