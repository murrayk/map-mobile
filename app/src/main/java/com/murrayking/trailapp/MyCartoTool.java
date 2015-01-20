package com.murrayking.trailapp;

/**
 * Created by murrayking on 14/01/2015.
 */
public class MyCartoTool {

    private final double initialResolution;
    private final int tileSize;
    private int mapViewHeightPixels;
    private int mapViewWidthPixels;
    private final double PADDING = 0.7;
    double LENGTH_OF_EQUATOR = 40075.016686 * 1000;

    public MyCartoTool(int mapViewWidthPixels, int mapViewHeightPixels) {
        this.tileSize = 256;
        this.initialResolution = 2 * Math.PI * 6378137 / this.tileSize;
        this.mapViewWidthPixels = mapViewWidthPixels;
        this.mapViewHeightPixels = mapViewHeightPixels;
    }

    private double metersPerPixel(int zoomLevel) {
        return (initialResolution * PADDING) /(Math.pow(2, zoomLevel));
    }

    public double getDistancePerPixel( int zoomLevel) {
        double latitude = 56;
        return (LENGTH_OF_EQUATOR*Math.cos(Math.toRadians(latitude))/Math.pow(2, zoomLevel+8));
    }


    public int zoomLevelForBoundingBox(int bbWidthInMeters, int bbHeightInMeters ){
        int bestZoomLevelForWidth = -1;
        int bestZoomLevelForHeight = -1;

        for(int i=20; i>0; i--){
            double widthInPixels = bbWidthInMeters / getDistancePerPixel(i);
            if( widthInPixels < mapViewWidthPixels){
                bestZoomLevelForWidth = i;
                break;
            }
        }
        for(int i=20; i> 0; i--){
            double heightInPixels = bbHeightInMeters / getDistancePerPixel(i);
            if( heightInPixels < mapViewHeightPixels){
                bestZoomLevelForHeight = i;
                break;
            }
        }
        return Math.min(bestZoomLevelForWidth, bestZoomLevelForHeight);
    }

}
