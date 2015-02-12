package com.murrayking.trailapp;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

/**
 * Created by murrayking on 12/02/2015.
 */
public class LocationOverlayWithLocationUpdates extends MyLocationNewOverlay {
    private BoundingBoxE6 mapLimitBox;
    private Context context;
    public LocationOverlayWithLocationUpdates(Context context, IMyLocationProvider myLocationProvider,
                                              MapView mapView, BoundingBoxE6 mapLimitBox) {
        super(myLocationProvider, mapView, new DefaultResourceProxyImpl(context));
        this.mapLimitBox = mapLimitBox;
        this.context = context;
    }



    @Override
    public void onLocationChanged(Location location, IMyLocationProvider source) {
        super.onLocationChanged(location, source);
        boolean isUserOutSideMapLimits = !mapLimitBox.contains(new GeoPoint(location.getLatitude(),location.getLongitude()));

        if(isUserOutSideMapLimits){
            Toast.makeText(context,
                    "You are outside the Map limits", Toast.LENGTH_LONG).show();
        }
    }
}
