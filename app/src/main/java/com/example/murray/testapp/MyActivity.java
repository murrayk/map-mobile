package com.example.murray.testapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class MyActivity extends Activity implements  AdapterView.OnItemClickListener {
    public static final String MAP_DB_NAME = "18hi.mbtiles";

    public static final String ROUTE_CHOSEN_KEY = "ROUTE_CHOSEN_KEY";

    private Utils utils = Utils.getInstance();

    ListView listView;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // 1. Access the TextView defined in layout XML
        // and then set its text
        adapter = new MyAdapter(this);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);



        LoadData loadData = new LoadData();
        loadData.execute();

    }

    private void goToMap(SingleRow row){
        // create an Intent to take you over to a new DetailActivity
        Intent detailIntent = new Intent(this, MainMapView.class);

        detailIntent.putExtra(ROUTE_CHOSEN_KEY, row);

        // TODO: add any other data you'd like as Extras

        // start the next Activity using your prepared Intent
        startActivity(detailIntent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu.
        // Adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);


        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        SingleRow row =(SingleRow)adapter.getItem(i);
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
            progressBar = new ProgressDialog(MyActivity.this);
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
            utils.copyOfflineMap(MyActivity.MAP_DB_NAME, MyActivity.this.getAssets(), MyActivity.this.getPackageName(), this);
        }

        @Override
        public void updateProgressBar(int percentage) {
            publishProgress(percentage);
        }
    }


}

class SingleRow implements Serializable{

    private String title;
    private int imageId;
    private String description;
    private String routeKmlFile;
    private int elevationId;

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

    SingleRow(String title, int imageId, String description, String routeKmlFile, int elevationId, int initialZoomLevel) {
        this.title = title;
        this.imageId = imageId;
        this.description = description;
        this.routeKmlFile = routeKmlFile;
        this.elevationId = elevationId;
        this.initialZoomLevel = initialZoomLevel;
    }


}

class  MyAdapter extends BaseAdapter{

    private final Context context;
    ArrayList<SingleRow> rows = new ArrayList<SingleRow>();

    MyAdapter(Context context){
        this.context = context;
        Resources resources = context.getResources();
        String[] titles = resources.getStringArray(R.array.titles);
        String[] descriptions = resources.getStringArray(R.array.descriptions);
        String[] routes = resources.getStringArray(R.array.routes);
        int[] images = new int[]{R.drawable.green_icon,R.drawable.blue_icon,R.drawable.red_icon,R.drawable.black_icon};
        int[] evelationArray = new int[]{R.array.green, R.array.blue, R.array.red, R.array.black};
        int[] initialZoomLevels = resources.getIntArray(R.array.initial_zoom_levels);
        String[] routeKmlFiles = resources.getStringArray(R.array.routes_kml_filenames);
        for(int i =0 ; i< titles.length;i++ ){
            rows.add(new SingleRow( titles[i],images[i], descriptions[i], routeKmlFiles[i], evelationArray[i], initialZoomLevels[i]));
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
        View row = inflater.inflate(R.layout.row_layout, viewGroup,false);
        TextView title = (TextView) row.findViewById(R.id.textTitle);
        TextView description = (TextView) row.findViewById(R.id.textDescription);
        ImageView imageView = (ImageView)row.findViewById(R.id.img_thumbnail);
        SingleRow singleRow = rows.get(i);

        title.setText(singleRow.getTitle());
        description.setText(singleRow.getDescription());
        imageView.setImageResource(singleRow.getImageId());
        return row;
    }
}
