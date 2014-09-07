package com.example.murray.testapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.MBTilesFileArchive;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by murray on 25/08/14.
 */
public class MainMapView extends Activity {




    KmlDocument kmlDocument;
    FixedMapView mapView;
    Utils utils = Utils.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SingleRow routeRow = (SingleRow)getIntent().getSerializableExtra(MyActivity.ROUTE_CHOSEN_KEY);


        kmlDocument = new KmlDocument();



        File route = utils.copyFileFromAssets(routeRow.getRouteKmlFile(), this.getAssets(), this.getPackageName());

        boolean success = kmlDocument.parseKMLFile(route);

        // Tell the activity which XML layout is right
        setContentView(R.layout.map_view);
        /**
         * This whole thing revolves around instantiating a MainMapView class, way,
         * way below. And MainMapView requires a ResourceProxy. Who are we to deny
         * its needs? Let's create one!
         *
         * It would have been nice if this was taken care of in the MainMapView
         * constructor. Interestingly MainMapView *has* a constructor that creates a
         * new DefaultResourceProxyImpl but unfortunately that one doesn't allow
         * us to specify the parameters we *do* need to set ...
         */
        DefaultResourceProxyImpl resProxy;
        resProxy = new DefaultResourceProxyImpl(this.getApplicationContext());

        /**
         * A class that implements the ITileSource interface knows how to
         * convert an InputStream or a file path into a Drawable. It doesn't do
         * much more than that. The real 'sourcery' is performed by
         * MapTileFileArchiveProvider which will be introduced shortly.
         *
         * What we need is really a BitmapTileSourceBase instance, but this
         * class is defined as abstract. XYTileSource is not and comes closest
         * to what we want.
         *
         * Comment: I don't quite get why BitmapTileSource base is abstract; it
         * doesn't contain any abstract methods.
         */

        XYTileSource tSource;
        tSource = new XYTileSource("mbtiles",
                ResourceProxy.string.offline_mode,
                8, 19, 256, ".png", new String[]{"http://who.cares/"});



        IArchiveFile[] files = { MBTilesFileArchive.getDatabaseFileArchive(utils.getOfflineMap()) };
        SimpleRegisterReceiver sr = new SimpleRegisterReceiver(this);

        MapTileModuleProviderBase moduleProvider;
        moduleProvider = new MapTileFileArchiveProvider(sr, tSource, files);

        MapTileModuleProviderBase[] pBaseArray;
        pBaseArray = new MapTileModuleProviderBase[] { moduleProvider };

        MapTileProviderArray provider;
        provider = new MapTileProviderArray(tSource, null, pBaseArray);

        mapView = new FixedMapView(this, 256, resProxy, provider);
        mapView.setBuiltInZoomControls(true);


        Button myUselessButton = new Button(this);

        myUselessButton.setText("Click");



        final RelativeLayout relativeLayout = new RelativeLayout(this);

        final RelativeLayout.LayoutParams mapViewLayoutParams = new RelativeLayout.LayoutParams(

                RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);

        final RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(

                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        final RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(

                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        buttonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);



        relativeLayout.addView(mapView, mapViewLayoutParams);


        relativeLayout.addView(myUselessButton,buttonLayoutParams);

        setContentView(relativeLayout);

        final BoundingBoxE6 bb =  kmlDocument.mKmlRoot.getBoundingBox();

        myUselessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.getController().setCenter(bb.getCenter());
            }
        });

        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(this);
        this.mapView.getOverlays().add(scaleBarOverlay);


        FolderOverlay kmlOverlay = (FolderOverlay)kmlDocument.mKmlRoot.buildOverlay( mapView, null, null, kmlDocument);

        mapView.getOverlays().add(kmlOverlay);

        mapView.setClickable(true);

        mapView.setMultiTouchControls(true);


        //controller.zoomToSpan(boundingBoxE6.getLatitudeSpanE6(), boundingBoxE6.getLongitudeSpanE6());

        // Set the MainMapView as the root View for this Activity; done!
        setContentView(relativeLayout);
        mapView.getController().setZoom(15); //set initial zoom-level, depends on your need


        final ViewTreeObserver vto = mapView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                mapView.getController().animateTo(bb.getCenter());
            }
        });

        //controller.zoomToSpan(boundingBoxE6.getLatitudeSpanE6(),boundingBoxE6.getLongitudeSpanE6());
        // Enable the "Up" button for more navigation options
        getActionBar().setDisplayHomeAsUpEnabled(true);


    }





}
