package com.murrayking.trailapp.converter;

/**
 * Created by murrayking on 21/04/2015.
 */
public class MathUtils {

    public static double toRadians (double val){

        return (val * (Math.PI/180) );
    };

    public static double toDegrees (double val){

        return val * (180/Math.PI) ;
    };

    public static double sec (double angle_radians){
        return ( 1.0 / Math.cos( angle_radians ) );
    };
}
