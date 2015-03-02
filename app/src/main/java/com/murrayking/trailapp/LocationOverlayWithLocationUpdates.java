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
    private Toast toast;

    public LocationOverlayWithLocationUpdates(Context context, IMyLocationProvider myLocationProvider,
                                              MapView mapView, BoundingBoxE6 mapLimitBox) {
        super(myLocationProvider, mapView, new DefaultResourceProxyImpl(context));
        this.mapLimitBox = mapLimitBox;
        this.context = context;
    }

    /**
     * <strong>public void showAToast (String st)</strong></br>
     * this little method displays a toast on the screen.</br>
     * it checks if a toast is currently visible</br>
     * if so </br>
     * ... it "sets" the new text</br>
     * else</br>
     * ... it "makes" the new text</br>
     * and "shows" either or
     * @param message the string to be toasted
     */

    public void showAToast (String message){ //"Toast toast" is declared in the class


        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();

    }


    @Override
    public void onLocationChanged(Location location, IMyLocationProvider source) {
        super.onLocationChanged(location, source);
        boolean isUserOutSideMapLimits = !mapLimitBox.contains(new GeoPoint(location.getLatitude(),location.getLongitude()));

        if(isUserOutSideMapLimits){
            showAToast("You are outside the Map limits");
        }
    }

    public void onPause() {
        if(toast != null)
            toast.cancel();
    }
}
