package com.lindycoder.glenn.rentapp;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.util.Log;

import java.lang.ref.WeakReference;


public class HomeFragment extends Fragment {

    private static final FragmentId fragmentId = FragmentId.HOME;
    private View rootView;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            ViewHolder holder = new ViewHolder();
            holder.imgHotbuy = (ImageView) rootView.findViewById(R.id.hotbuy_space);
            holder.imgMessages = (ImageView) rootView.findViewById(R.id.message_space);
            holder.imgEvents = (ImageView) rootView.findViewById(R.id.cal_space);
            holder.btnHotbuy = (Button) rootView.findViewById(R.id.hotbuys_button);
            rootView.setTag(holder);
        }

        ((AccountMainActivity) getActivity()).setActionBarTitle(getString(R.string.dashboard));
        if(rootView != null) {
            ViewHolder holder = (ViewHolder) rootView.getTag();
            loadBitmap(R.drawable.hotbuys, holder.imgHotbuy, FragmentId.HOTBUYS);
            loadBitmap(R.drawable.messages, holder.imgMessages, FragmentId.MESSAGES);
            loadBitmap(R.drawable.calendar, holder.imgEvents, FragmentId.EVENTS);
            holder.btnHotbuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AccountMainActivity) getActivity()).changeToFragment(FragmentId.HOTBUYS);
                }
            });
        }
        return rootView;
    }

    public void loadBitmap(int resId, ImageView imageView, FragmentId fragmentId) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView, rootView.getResources(), fragmentId);
        task.execute(resId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AccountMainActivity) activity).onSectionAttached(fragmentId);
    }

    static class ViewHolder {
        ImageView imgHotbuy;
        ImageView imgMessages;
        ImageView imgEvents;
        Button btnHotbuy;
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;
        private Resources resources;
        private FragmentId fragmentId;

        public BitmapWorkerTask(ImageView imageView, Resources resources, FragmentId fragmentId) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
            this.resources = resources;
            this.fragmentId = fragmentId;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return BitmapFactory.decodeResource(resources, data);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((AccountMainActivity) getActivity()).changeToFragment(fragmentId);
                        }
                    });
                }
            }
        }
    }
}



