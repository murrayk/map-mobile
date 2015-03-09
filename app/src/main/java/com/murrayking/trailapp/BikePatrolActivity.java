package com.murrayking.trailapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

/**
 * Created by murrayking on 23/01/2015.
 */
public class BikePatrolActivity extends Activity {
    final private String HOSTED_ID_PARAM_NAME = "hosted_button_id";
    final private String HOSTED_ID_PARAM_VALUE = "4PEBKCFVLSBK4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bike_patrol_screen);

        View goToDonate = this.findViewById(R.id.goToDonate);

        goToDonate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                //https://www.paypal.com/cgi-bin/webscr

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("www.paypal.com")
                        .appendPath("cgi-bin")
                        .appendPath("webscr")
                        .appendQueryParameter(HOSTED_ID_PARAM_NAME, HOSTED_ID_PARAM_VALUE)
                        .appendQueryParameter("cmd","_s-xclick");

                String donatePaypal = builder.build().toString();

                openWebPage(donatePaypal);
            }
        });

    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
