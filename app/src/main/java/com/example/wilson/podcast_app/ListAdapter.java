package com.example.wilson.podcast_app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by andre on 23.04.2017.
 */

public class ListAdapter extends ArrayAdapter<String> {
    private final Activity _context;
    private ArrayList<String> _name;
    //private static LayoutInflater inflater = null;

    public ListAdapter(Activity context, ArrayList<String> name) {
        super(context, R.layout.list_view, name);
        this._context = context;
        this._name = name;
        //inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = _context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_view, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.text1);
        txtTitle.setText(_name.get(position));

        return rowView;
    }
}
