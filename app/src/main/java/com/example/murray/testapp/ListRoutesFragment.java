package com.example.murray.testapp;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by murrayking on 19/01/2015.
 */
public class ListRoutesFragment extends ListFragment{

    public static final String MAP_DB_NAME = "18hi.mbtiles";

    public static final String ROUTE_CHOSEN_KEY = "ROUTE_CHOSEN_KEY";

    private Utils utils = Utils.getInstance();


    MyAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // 1. Access the TextView defined in layout XML
        // and then set its text
        adapter = new MyAdapter(this.getActivity());

        setListAdapter(adapter);



        LoadData loadData = new LoadData();
        loadData.execute();

    }

    private void goToMap(SingleRow row){
        // create an Intent to take you over to a new DetailActivity
        Intent detailIntent = new Intent(this.getActivity(), MainMapView.class);

        detailIntent.putExtra(ROUTE_CHOSEN_KEY, row);

        // TODO: add any other data you'd like as Extras

        // start the next Activity using your prepared Intent
        this.getActivity().startActivity(detailIntent);
    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        SingleRow row =(SingleRow)adapter.getItem(position);
        goToMap(row);
    }
    public interface UpdateProgress {

        void updateProgressBar(int percentage);
    }

    public class LoadData extends AsyncTask<Void, Integer, Void> implements UpdateProgress {
        ProgressDialog progressBar;

        //declare other objects as per your need
        @Override
        protected void onPreExecute()
        {
            progressBar = new ProgressDialog(ListRoutesFragment.this.getActivity());
            progressBar.setCancelable(false);
            progressBar.setMessage("Unpacking offline map ...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.show();
            //progressDialog= ProgressDialog.show(MyActivity.this, "Progress Dialog Title Text","Process Description Text", true);

            //do initialization of required objects objects here
        };
        @Override
        protected Void doInBackground(Void... params)
        {

            //do loading operation here
            copyOfflineMap();
            return null;
        }

        @Override
        protected void onProgressUpdate(final Integer... values) {
            super.onProgressUpdate(values);

            progressBar.setProgress(values[0]);

            Log.i("makemachine", "onProgressUpdate(): " + String.valueOf(values[0]));

        }

        @Override
        protected void onPostExecute(Void v){

            progressBar.dismiss();
        }


        public void copyOfflineMap() {
            utils.copyOfflineMap(ListRoutesFragment.MAP_DB_NAME, ListRoutesFragment.this.getActivity().getAssets(),
                    ListRoutesFragment.this.getActivity().getPackageName(), this);
        }

        @Override
        public void updateProgressBar(int percentage) {
            publishProgress(percentage);
        }
    }


    static class SingleRow implements Serializable {

        private String title;
        private int imageId;
        private String description;
        private String routeKmlFile;
        private int elevationId;
        private String routeInfo;

        public int getInitialZoomLevel() {
            return initialZoomLevel;
        }

        private int initialZoomLevel;

        public int getImageId() {
            return imageId;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getRouteKmlFile() {
            return routeKmlFile;
        }

        public int getElevationId() { return elevationId; }

        public String getRouteInfo(){ return routeInfo;}

        SingleRow(String title, int imageId, String description, String routeKmlFile, int elevationId, int initialZoomLevel, String routeInfo) {
            this.title = title;
            this.imageId = imageId;
            this.description = description;
            this.routeKmlFile = routeKmlFile;
            this.elevationId = elevationId;
            this.initialZoomLevel = initialZoomLevel;
            this.routeInfo = routeInfo;
        }


    }

    class  MyAdapter extends BaseAdapter {

        private final Context context;
        ArrayList<SingleRow> rows = new ArrayList<SingleRow>();

        MyAdapter(Context context) {
            this.context = context;
            Resources resources = context.getResources();
            String[] titles = resources.getStringArray(R.array.titles);
            String[] descriptions = resources.getStringArray(R.array.descriptions);

            int[] images = new int[]{R.drawable.green_icon, R.drawable.blue_icon, R.drawable.red_icon, R.drawable.black_icon};
            int[] elevationArray = new int[]{R.array.green, R.array.blue, R.array.red, R.array.black};
            int[] initialZoomLevels = resources.getIntArray(R.array.initial_zoom_levels);
            String[] routeKmlFiles = resources.getStringArray(R.array.routes_kml_filenames);
            String[] routeInfo = resources.getStringArray(R.array.route_info);
            for (int i = 0; i < titles.length; i++) {
                rows.add(new SingleRow(titles[i], images[i], descriptions[i], routeKmlFiles[i], elevationArray[i], initialZoomLevels[i], routeInfo[i]));
            }

        }

        ;

        @Override
        public int getCount() {
            return rows.size();
        }

        @Override
        public Object getItem(int i) {
            return rows.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.row_layout, viewGroup, false);
            TextView title = (TextView) row.findViewById(R.id.textTitle);
            TextView description = (TextView) row.findViewById(R.id.textDescription);
            ImageView imageView = (ImageView) row.findViewById(R.id.img_thumbnail);
            SingleRow singleRow = rows.get(i);

            title.setText(singleRow.getTitle());
            description.setText(singleRow.getDescription());
            imageView.setImageResource(singleRow.getImageId());
            return row;
        }
    }

}
