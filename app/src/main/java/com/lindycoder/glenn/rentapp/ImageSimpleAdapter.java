package com.lindycoder.glenn.rentapp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageSimpleAdapter extends SimpleAdapter {
    private static final String TAG_IMG = "PreviewImage";
    private Context mContext;
    public LayoutInflater inflater = null;
    private int[] mTo;
    private String[] mFrom;
    private DisplayImageOptions options;

    public ImageSimpleAdapter(Context context,
                           List<? extends Map<String, ?>> data, int resource, String[] from,
                           int[] to,
                           DisplayImageOptions options) {
        super(context, data, resource, from, to);
        mContext = context;
        mFrom = from;
        mTo = to;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.options = options;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.hotbuy_list_item, null);
        }

        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
        final int count = mTo.length;
        for(int i = 0; i < count; i++) {
            if(!mFrom[i].equals(TAG_IMG)) {
                final TextView v = (TextView)vi.findViewById(mTo[i]);
                if (v != null) {
                    final Object str = data.get(mFrom[i]);
                    String text = str == null ? "" : str.toString();
                    if (text == null) {
                        text = "";
                    }
                    v.setText(text);
                }
            }
        }


        ImageView imgView = (ImageView) vi.findViewById(R.id.hotbuy_img_preview);
        ImageLoader.getInstance().displayImage((String) data.get(TAG_IMG),imgView, options);

        return vi;
    }

}
