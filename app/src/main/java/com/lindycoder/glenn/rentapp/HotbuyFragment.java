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

import java.lang.ref.WeakReference;


public class HotbuyFragment extends Fragment {

    private static final FragmentId fragmentId = FragmentId.HOTBUYS;

    public static HotbuyFragment newInstance() {
        HotbuyFragment fragment = new HotbuyFragment();
        return fragment;
    }

    public HotbuyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        ((AccountMainActivity) getActivity()).setActionBarTitle(getString(R.string.hot_buys));
        /*
        loadBitmap(R.drawable.hotbuys, (ImageView) rootView.findViewById(R.id.hotbuy_space), FragmentId.HOTBUYS);
        loadBitmap(R.drawable.messages, (ImageView) rootView.findViewById(R.id.message_space), FragmentId.MESSAGES);
        loadBitmap(R.drawable.calendar, (ImageView) rootView.findViewById(R.id.cal_space), FragmentId.EVENTS);
        Button button = (Button) rootView.findViewById(R.id.hotbuys_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AccountMainActivity) getActivity()).changeToFragment(FragmentId.HOTBUYS);
            }
        });
        */
        return rootView;
    }

    /*
    public void loadBitmap(int resId, ImageView imageView, FragmentId fragmentId) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView, rootView.getResources(), fragmentId);
        task.execute(resId);
    }
    */

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AccountMainActivity) activity).onSectionAttached(fragmentId);
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;
        private Resources resources;
        private FragmentId fragmentId;

        public BitmapWorkerTask(ImageView imageView, Resources resources, FragmentId fragmentId) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
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
            if (imageViewReference != null && bitmap != null) {
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



