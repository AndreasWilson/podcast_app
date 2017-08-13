package com.example.wilson.podcast_app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wilson.podcast_app.Objects.iTunesSearch;
import com.example.wilson.podcast_app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by andre on 13.08.2017.
 */

public class SearchListAdapter extends BaseAdapter {

    private ArrayList<iTunesSearch> list;
    private Context context;

    public SearchListAdapter(Context context, ArrayList<iTunesSearch> list) {
        this.list = list;
        this.context = context;

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.search_list, parent, false);
            holder.textView = (TextView) convertView.findViewById(R.id.textViewSearch);
            holder.img = (ImageView) convertView.findViewById(R.id.imageView3Search);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            holder.textView.setText(list.get(position).getName());
            Picasso.with(context).load(list.get(position).getImageUrl()).into(holder.img);

        return convertView;
    }

    private class ViewHolder {
        TextView textView;
        ImageView img;
    }
}
