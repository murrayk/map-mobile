package com.murrayking.trailapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by murray on 07/02/15.
 */
public class LocationsRowAdapter  extends BaseAdapter {

    private final Context context;
    String[] rows ;
    int trailIcon;

    LocationsRowAdapter(Context context,String[] trailNamesArray, int trailIcon) {
        this.context = context;
        this.rows = trailNamesArray;
        this.trailIcon = trailIcon;
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
        ImageView imageView = (ImageView) row.findViewById(R.id.trailIcon);
        imageView.setImageResource(trailIcon);
        String name = rows[i];

        textTrailName.setText(name);
        //description.setText(singleRow.getDescription());
        //imageView.setImageResource(singleRow.getImageId());
        return row;
    }
}
