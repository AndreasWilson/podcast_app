package com.example.wilson.podcast_app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wilson.podcast_app.R;
import com.example.wilson.podcast_app.Objects.iTunesItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by andre on 28.02.2017.
 */

public class ImageAdapter extends BaseAdapter {

    private ArrayList<iTunesItem> items;
    private Context mContext;
    private LayoutInflater mInflater;

    public ImageAdapter(Context context, ArrayList<iTunesItem> items2){
        mContext = context;
        this.items = items2;
        mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
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
        //ImageView img = (ImageView) convertView;
        GridHolder holder = null;
        /*if (img == null) {
            img = new ImageView(mContext);
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            img.setLayoutParams(new RelativeLayout.LayoutParams(350, 350));
            img.setPadding(8,8,8,8);
        }*/
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_grid_item, parent, false);
            holder = new GridHolder();
            holder.gridimage = (ImageView) convertView.findViewById(R.id.imageView);
            holder.gridimage.setLayoutParams(new LinearLayout.LayoutParams(350, 350));
            holder.txtgrid = (TextView) convertView.findViewById(R.id.tv_emp_id);
            convertView.setTag(holder);
        } else {
            holder = (GridHolder) convertView.getTag();
        }


        //String url = getItem(position);
        iTunesItem item;
        item = items.get(position);
        String imgUrl = item.getImg();
        String name = item.getName();

        Picasso.with(mContext).load(imgUrl).into(holder.gridimage);
        holder.txtgrid.setText(name);

        return convertView;
    }
    public class GridHolder
    {
        ImageView gridimage;
        TextView txtgrid;
    }
}