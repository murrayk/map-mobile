package com.murrayking.trailapp;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by murray on 07/02/15.
 */
public class LocationsRowAdapter  extends BaseAdapter {

    private final Context context;
    String[] rows ;

    LocationsRowAdapter(Context context,String[] trailNamesArray) {
        this.context = context;
        rows = trailNamesArray;
    }

    ;

    @Override
    public int getCount() {
        return rows.length;
    }

    @Override
    public Object getItem(int i) {
        return rows[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.row_layout_locations, viewGroup, false);
        TextView textTrailName = (TextView) row.findViewById(R.id.textTrailName);
        TextView description = (TextView) row.findViewById(R.id.textDescription);
        ImageView imageView = (ImageView) row.findViewById(R.id.img_thumbnail);
        String name = rows[i];

        textTrailName.setText(name);
        //description.setText(singleRow.getDescription());
        //imageView.setImageResource(singleRow.getImageId());
        return row;
    }
}
