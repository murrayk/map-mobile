package com.murrayking.trailapp;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.murrayking.trailapp.ListRoutesFragment.SingleRow;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.bonuspack.overlays.Marker;
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
 * Created by murray on 25/08/14.
 */
public class MainMapView  extends Fragment{



    private SharedPreferences prefs;
    public static final String PREFS_NAME = "com.example.murray.testapp.prefs";
    public static final String PREFS_SCROLL_X = "scrollX";
    public static final String PREFS_SCROLL_Y = "scrollY";
    public static final String PREFS_ZOOM_LEVEL = "zoomLevel";
    public static final String PREFS_SHOW_LOCATION = "showLocation";
    public static final String PREFS_SHOW_COMPASS = "showCompass";
    private MyLocationNewOverlay locationOverlay;
    private CompassOverlay compassOverlay;

    private Marker marker;
    KmlDocument kmlDocument;
    FixedMapView mapView;
    Utils utils = Utils.getInstance();


    public MainMapView(){


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main, container, false);
        Context context = inflater.getContext();


        getActivity().getActionBar().hide();


        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        SingleRow routeRow = getRow();

        kmlDocument = new KmlDocument();
        File route = utils.copyFileFromAssets(routeRow.getRouteKmlFile(), this.getActivity().getAssets(), this.getActivity().getPackageName(), null);

        kmlDocument.parseKMLFile(route);

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
                8, 18, 256, ".png", new String[]{"http://who.cares/"});


        IArchiveFile[] files = { MBTilesFileArchive.getDatabaseFileArchive(utils.getOfflineMap()) };
        SimpleRegisterReceiver sr = new SimpleRegisterReceiver(context);

        MapTileModuleProviderBase moduleProvider;
        moduleProvider = new MapTileFileArchiveProvider(sr, tSource, files);

        MapTileModuleProviderBase[] pBaseArray;
        pBaseArray = new MapTileModuleProviderBase[] { moduleProvider };

        MapTileProviderArray provider;
        provider = new MapTileProviderArray(tSource, null, pBaseArray);

        mapView = new FixedMapView(context, 256, resProxy, provider);


        ImageButton mButton = (ImageButton) rootView.findViewById(R.id.locate_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainMapView.this.locationOverlay.enableFollowLocation();

            }
        });

        ImageButton locationsButton = (ImageButton) rootView.findViewById(R.id.play_button);
        locationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showGotoLocationsPopup();

            }
        });





        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        mapView.setBuiltInZoomControls(true);



        BoundingBoxE6  mapLimitBox = getMapLimits();
        this.locationOverlay = new LocationOverlayWithLocationUpdates(context, new GpsMyLocationProvider(context), mapView, mapLimitBox);
        this.compassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context),
                mapView);



        mapView.setScrollableAreaLimit(mapLimitBox);
        final BoundingBoxE6 bb =  kmlDocument.mKmlRoot.getBoundingBox();

        FolderOverlay kmlOverlay = (FolderOverlay)kmlDocument.mKmlRoot.buildOverlay(mapView, null, null, kmlDocument);

        mapView.getOverlays().add(kmlOverlay);
        mapView.getOverlays().add(this.locationOverlay);
        mapView.getOverlays().add(this.compassOverlay);
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




        mapView.getController().setZoom(prefs.getInt(PREFS_ZOOM_LEVEL, 1));
        mapView.scrollTo(prefs.getInt(PREFS_SCROLL_X, 0), prefs.getInt(PREFS_SCROLL_Y, 0));

        locationOverlay.enableMyLocation();
        compassOverlay.enableCompass();

        //get string array pf plot
        // create
        Resources resources = context.getResources();

        if(routeRow.hasElevations()) {
            GraphView graphView = getGraphView(routeRow, resources);


            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.replace);


            layout.addView(graphView);
        }


        ImageView routeIcon = (ImageView)rootView.findViewById(R.id.routeIcon);
        routeIcon.setImageResource(routeRow.getImageId());
        TextView routeInfo = (TextView)rootView.findViewById(R.id.routeInfo);

        routeInfo.setText(Html.fromHtml(routeRow.getRouteInfo()));
        RelativeLayout mapContainer = (RelativeLayout)rootView.findViewById(R.id.map_container);
        mapContainer.addView(mapView, 0);
        return rootView;
    }

    private BoundingBoxE6 getMapLimits() {
        double north = Double.valueOf(R.string.bbNorth);
        double east  = Double.valueOf(R.string.bbEast);
        double south = Double.valueOf(R.string.bbSouth);
        double west  =  Double.valueOf(R.string.bbWest);
        return new BoundingBoxE6(north, east, south, west);
    }

    private GraphView getGraphView(SingleRow routeRow, Resources resources) {

        String[] points = resources.getStringArray(routeRow.getElevationId());

        DataPoint data[] =  new DataPoint[points.length];

        for(int i = 0; i < points.length; i++){
            String[] p = points[i].split(",");
            double x = Double.valueOf(p[0]);
            double y = Double.valueOf(p[1]);
            data[i] = new DataPoint(x,y);

        }


        LineGraphSeries<DataPoint> series =  new LineGraphSeries<DataPoint>(data);
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
        return graphView;
    }

    private void showGotoLocationsPopup() {
        // Create custom dialog object
        final Dialog dialog = new Dialog(MainMapView.this.getActivity(), R.style.NewDialog);
        // Include dialog.xml file
        dialog.setContentView(R.layout.locations_dialog);
        // Set dialog title
        dialog.setTitle("Trail Locations ...");

        SingleRow routeRow = getRow();



        ListView list = (ListView) dialog.findViewById(R.id.listview);


        String[] values = getResources().getStringArray(routeRow.getTrailNames());

        LocationsRowAdapter adapter = new LocationsRowAdapter(MainMapView.this.getActivity(), values, routeRow.getTrailIcon());


        list.setAdapter(adapter);
        final String[] coords = getResources().getStringArray(routeRow.getTrailCoords());

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(!mapView.getOverlays().contains(marker) ){
                    marker = new Marker(mapView);
                }
                dialog.dismiss();
                String coord = coords[position];
                String[] lonlat = coord.split(",");
                double lon = Double.valueOf(lonlat[0]);
                double lat = Double.valueOf(lonlat[1]);

                mapView.getController().setZoom(17);
                GeoPoint loc = new GeoPoint(lat, lon);
                mapView.getController().animateTo(new GeoPoint(lat, lon));

                marker.setPosition(loc);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mapView.getOverlays().add(marker);


            }
        });



        dialog.show();


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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


    public static MainMapView newInstance(SingleRow row) {
        if(row == null){
            throw new NullPointerException("Need a map route row to display");
        }
        MainMapView f = new MainMapView();
        Bundle args = new Bundle();
        args.putSerializable(ListRoutesFragment.ROUTE_CHOSEN_KEY, row);

        f.setArguments(args);

        return f;
    }

    public SingleRow getRow() {
        return (SingleRow) getArguments().getSerializable(ListRoutesFragment.ROUTE_CHOSEN_KEY);
    }
}
