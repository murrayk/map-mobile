package com.example.murray.testapp;

/**
 * Created by murrayking on 14/01/2015.
 */
public class MyCartoTool {

    private final double initialResolution;
    private final int tileSize;
    private int mapViewHeightPixels;
    private int mapViewWidthPixels;

    public MyCartoTool(int mapViewWidthPixels, int mapViewHeightPixels) {
        this.tileSize = 256;
        this.initialResolution = 2 * Math.PI * 6378137 / this.tileSize;
        this.mapViewWidthPixels = mapViewWidthPixels;
        this.mapViewHeightPixels = mapViewHeightPixels;
    }

    private double metersPerPixel(int zoomLevel) {
        return this.initialResolution/(Math.pow(2, zoomLevel));
    }


    public int zoomLevelForBoundingBox(int bbWidthInMeters, int bbHeightInMeters ){
        int bestZoomLevelForWidth = -1;
        int bestZoomLevelForHeight = -1;

        for(int i=20; i>0; i--){
            double widthInPixels = bbWidthInMeters / metersPerPixel(i);
            if( widthInPixels < mapViewWidthPixels){
                bestZoomLevelForWidth = i;
                break;
            }
        }
        for(int i=20; i> 0; i--){
            double heightInPixels = bbHeightInMeters / metersPerPixel(i);
            if( heightInPixels < mapViewHeightPixels){
                bestZoomLevelForHeight = i;
                break;
            }
        }
        return Math.min(bestZoomLevelForWidth, bestZoomLevelForHeight);
    }

}
