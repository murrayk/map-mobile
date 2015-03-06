package com.murrayking.trailapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by murrayking on 21/01/2015.
 */
public class IntroActivity extends Activity {

    private  static String HD_MAP_PREFIX = "hd_";

    private Utils utils = Utils.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_screen);




        Button goToMap = (Button)this.findViewById(R.id.goMapButton);

        goToMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(IntroActivity.this, MainTrailActivity.class);
                startActivity(intent);
            }
        });
        ImageButton goBikePatrol = (ImageButton)this.findViewById(R.id.goBikePatrol);

        goBikePatrol.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(IntroActivity.this, BikePatrolActivity.class);
                startActivity(intent);
            }
        });

        LoadData loadData = new LoadData();
        loadData.execute();


        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Executed after timer is finished (Opens MainActivity)
                Intent intent = new Intent(IntroActivity.this, MainTrailActivity.class);
                startActivity(intent);

                // Kills this Activity
                finish();
            }
        }, SPLASH_SCREEN_DELAY);*/
    }

    private boolean isHighDensityScreen() {
        float density = getResources().getDisplayMetrics().density;
        //set the offline map to high or low density
        // return 0.75 if it's LDPI
        // return 1.0 if it's MDPI
        // return 1.5 if it's HDPI
        float xhdpi = 2.0f;
        return density >= xhdpi;

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
            progressBar = new ProgressDialog(IntroActivity.this);
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
            Context context = IntroActivity.this.getApplicationContext();
            String mapFileName = context.getResources().getString(R.string.map_db_file_name);
            if(isHighDensityScreen()){

                mapFileName = HD_MAP_PREFIX + mapFileName;
            }

            utils.copyOfflineMap(mapFileName, context.getAssets(),
                    context.getPackageName(), this);
        }

        @Override
        public void updateProgressBar(int percentage) {
            publishProgress(percentage);
        }
    }
}
