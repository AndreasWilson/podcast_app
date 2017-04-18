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

    private ArrayList<iTunesItem> items;
    private Context mContext;
    private static LayoutInflater inflater = null;
    private String podURL;

    public ImageAdapter(Context context, ArrayList<iTunesItem> items2){
        mContext = context;
        this.items = items2;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
        ImageLoader.getInstance().init(config);
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_grid_item, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.tv_emp_id);
        /*ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setMaxHeight(600);
        imageView.setMaxWidth(600);
        imageView.setPadding(8, 8, 8, 8);*/

        //imageLoader.displayImage(img1, imageView);
        //imageView.setImageResource(mThumbIds[position]);

        iTunesItem item = new iTunesItem();
        item = items.get(position);
        textView.setText("Name: " + item.getName());

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iTunesItem i = new iTunesItem();
                System.out.println("Gridview: " + items.get(position).getName());
                //MainActivity main = new MainActivity();
                //main.podUrl = (items.get(position).getUrl());
                podURL = (items.get(position).getUrl());
                System.out.println(items.get(position).getUrl());
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("URLpref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("podURL", podURL);
                editor.apply();
                getPodcast podcast = new getPodcast();
                podcast.execute();
            }
        });
        //imageLoader.displayImage(item.getImg(), imageView);

        return convertView;
    }
}