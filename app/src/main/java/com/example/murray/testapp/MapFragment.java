package com.example.murray.testapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.murray.testapp.ListRoutesFragment.SingleRow;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.kml.KmlDocument;
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
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;

/**
 * Created by murrayking on 15/12/2014.
 */
public class MapFragment extends Fragment {


    private SharedPreferences prefs;
    public static final String PREFS_NAME = "com.example.murray.testapp.prefs";
    public static final String PREFS_SCROLL_X = "scrollX";
    public static final String PREFS_SCROLL_Y = "scrollY";
    public static final String PREFS_ZOOM_LEVEL = "zoomLevel";
    public static final String PREFS_SHOW_LOCATION = "showLocation";
    public static final String PREFS_SHOW_COMPASS = "showCompass";
    private MyLocationNewOverlay locationOverlay;
    private CompassOverlay compassOverlay;


    KmlDocument kmlDocument;
    FixedMapView mapView;
    Utils utils = Utils.getInstance();


    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);


        SingleRow routeRow = (SingleRow)getActivity().getIntent().getSerializableExtra(ListRoutesFragment.ROUTE_CHOSEN_KEY);

        kmlDocument = new KmlDocument();
        File route = utils.copyFileFromAssets(routeRow.getRouteKmlFile(), this.getActivity().getAssets(), this.getActivity().getPackageName(), null);

        kmlDocument.parseKMLFile(route);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Context context = inflater.getContext();

        DefaultResourceProxyImpl resProxy;
        resProxy = new DefaultResourceProxyImpl(context);

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
        SimpleRegisterReceiver sr = new SimpleRegisterReceiver(context);

        MapTileModuleProviderBase moduleProvider;
        moduleProvider = new MapTileFileArchiveProvider(sr, tSource, files);

        MapTileModuleProviderBase[] pBaseArray;
        pBaseArray = new MapTileModuleProviderBase[] { moduleProvider };

        MapTileProviderArray provider;
        provider = new MapTileProviderArray(tSource, null, pBaseArray);

        mapView = new FixedMapView(context, 256, resProxy, provider);


        return mapView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageButton mButton = (ImageButton) getActivity().findViewById(R.id.locate_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MapFragment.this.locationOverlay.enableFollowLocation();

            }
        });

        Context context = this.getActivity();
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        mapView.setBuiltInZoomControls(true);
        this.locationOverlay = new MyLocationNewOverlay(context, new GpsMyLocationProvider(context), mapView);


        final BoundingBoxE6 bb =  kmlDocument.mKmlRoot.getBoundingBox();

        FolderOverlay kmlOverlay = (FolderOverlay)kmlDocument.mKmlRoot.buildOverlay(mapView, null, null, kmlDocument);

        mapView.getOverlays().add(kmlOverlay);
        mapView.getOverlays().add(this.locationOverlay);
        mapView.setClickable(true);

        mapView.setMultiTouchControls(true);




        final ViewTreeObserver vto = mapView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                //work out zoomlevel mapView.zoomToBoundingBox(); doesn't always so wrote own work.
                //use own code.

                GeoPoint nw = new GeoPoint(bb.getLatNorthE6(), bb.getLonWestE6());
                GeoPoint ne = new GeoPoint(bb.getLatNorthE6(), bb.getLonEastE6());
                int widthMeters =ne.distanceTo(nw);
                GeoPoint se = new GeoPoint(bb.getLatSouthE6(), bb.getLonEastE6());
                int heightMeters = nw.distanceTo(se);


                MyCartoTool cartoTool = new MyCartoTool( mapView.getWidth(), mapView.getHeight());
                int zoomLevel = cartoTool.zoomLevelForBoundingBox(widthMeters,heightMeters);
                mapView.getController().setZoom(zoomLevel);
                mapView.getController().animateTo(bb.getCenter());

            }
        });



        this.compassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context),
                mapView);
        mapView.getController().setZoom(prefs.getInt(PREFS_ZOOM_LEVEL, 1));
        mapView.scrollTo(prefs.getInt(PREFS_SCROLL_X, 0), prefs.getInt(PREFS_SCROLL_Y, 0));

        locationOverlay.enableMyLocation();
        compassOverlay.enableCompass();
        // init example series data

        //get string array pf plot
        Resources resources = context.getResources();
        SingleRow routeRow = (SingleRow)getActivity().getIntent().getSerializableExtra(ListRoutesFragment.ROUTE_CHOSEN_KEY);

        String[] points = resources.getStringArray(routeRow.getElevationId());

        DataPoint data[] =  new DataPoint[points.length];

        for(int i = 0; i < points.length; i++){
            String[] p = points[i].split(",");
            double x = Double.valueOf(p[0]);
            double y = Double.valueOf(p[1]);
            data[i] = new DataPoint(x,y);

        }


        LineGraphSeries<DataPoint>  series =  new LineGraphSeries<DataPoint>(data);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.rgb(189, 183, 107));
        GraphView graphView = new GraphView(
                this.getActivity() // context
        );
        graphView.getViewport().setXAxisBoundsManual(true);
        //get last x
        int lastXCoord = (int)Math.ceil(data[data.length-1].getX());
        //make sure we have an even number
        if(lastXCoord % 2 == 1) {
            lastXCoord++;
        }
        graphView.getViewport().setMaxX(lastXCoord);
        graphView.addSeries(series); // data
        graphView.setTitle("Terrain (m)");

        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Distance(km)");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;
        params.setMargins(10, 10, 10, 10);
        graphView.setLayoutParams(params);
        LinearLayout layout = (LinearLayout) this.getActivity().findViewById(R.id.replace);

        ImageView routeIcon = (ImageView)this.getActivity().findViewById(R.id.routeIcon);
        routeIcon.setImageResource(routeRow.getImageId());
        TextView routeInfo = (TextView)this.getActivity().findViewById(R.id.routeInfo);

        routeInfo.setText(Html.fromHtml(routeRow.getRouteInfo()));
        layout.addView(graphView);
    }



    @Override
    public void onPause()
    {
        final SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(PREFS_SCROLL_X, mapView.getScrollX());
        edit.putInt(PREFS_SCROLL_Y, mapView.getScrollY());
        edit.putInt(PREFS_ZOOM_LEVEL,  mapView.getZoomLevel());
        edit.putBoolean(PREFS_SHOW_LOCATION, locationOverlay.isMyLocationEnabled());
        edit.putBoolean(PREFS_SHOW_COMPASS, compassOverlay.isCompassEnabled());
        edit.commit();

        this.locationOverlay.disableMyLocation();
        this.compassOverlay.disableCompass();

        super.onPause();
    }


    @Override
    public void  onResume() {
        super.onResume();

        if (prefs.getBoolean(PREFS_SHOW_LOCATION, false)) {
            this.locationOverlay.enableMyLocation();
        }
        if (prefs.getBoolean(PREFS_SHOW_COMPASS, false)) {
            this.compassOverlay.enableCompass();
        }
    }

}
