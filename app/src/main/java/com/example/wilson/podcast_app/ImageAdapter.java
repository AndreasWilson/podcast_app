package com.example.wilson.podcast_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andre on 28.02.2017.
 */

public class ImageAdapter extends BaseAdapter {

    private String img1;
    private Context mContext;

    public ImageAdapter(Context context, String img){
        mContext = context;
        img1 = img;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        TextView textView;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
        ImageLoader.getInstance().init(config);
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (convertView == null){
            textView = new TextView(mContext);
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setMaxHeight(600);
            imageView.setMaxWidth(600);
            imageView.setPadding(8, 8, 8, 8);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        imageLoader.displayImage(img1, imageView);
        //imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    public String[] mThumbIds = {
            img1
    };
}