package com.murrayking.trailapp.com.murrayking.trailapp.converter;

/**
 * Created by murrayking on 21/04/2015.
 */
public class Elipsoid {


    /**
     * the semi-major ellipsoid axis length(metres)
     */
    double a=  0.0;

    /**
     * the semi-minor ellipsoid axis length(metres)
     */
    double b;

    /**
     * Constructor: Edina.Elipsoid
     *
     * a - {Number} the semi-major ellipsoid axis length(metres)
     * b - {Number} the semi-minor ellipsoid axis length(metres)
     */
    public Elipsoid(double a,double  b){
        this.a  = a;
        this.b = b ;
    }

    double[] convertCartesianToLatLongHeight (double x,double y,double z ){

        double height;
        double[] latLong = new double[3];

        double lamda = Math.atan( y / x );

        double phi = Math.atan( z / ( this.p( x, y ) * ( 1 - this.eSq() ) ) );
        double previousPhi = 0.0;
        while ( Math.abs( phi - previousPhi ) > 0.0000000000000001 ) {
            previousPhi = phi;
            phi = Math.atan( ( z + this.eSq() * this.v( previousPhi ) * Math.sin( previousPhi ) ) / this.p( x, y ) );
        }

        height = ( this.p( x, y ) / Math.cos( phi ) ) - this.v( phi );

        latLong[0] = MathUtils.toDegrees( phi );
        latLong[1] = MathUtils.toDegrees( lamda );
        latLong[2] = height;

        return latLong;

    };

    double v(double latitude) {
        return this.a / Math.sqrt( 1 - ( this.eSq() * Math.pow( Math.sin( latitude ), 2.0 ) ) );
    };

    double p (double x,double y) {
        return Math.sqrt( Math.pow( x, 2.0 ) + Math.pow( y, 2.0 ) );
    };

    double eSq (){
        double aSq = Math.pow( this.a, 2.0 );
        return ( aSq - Math.pow( this.b, 2.0 ) ) / aSq;
    };

    double[] convertLatLongToCartesian (double lat,double lon){
        return this.convertLatLongHeightToCartesian( lat, lon, 0.0 );
    };

    double[] convertLatLongHeightToCartesian (double lat,double lon,double height ) {

        double phi = MathUtils.toRadians( lat );
        double lamda = MathUtils.toRadians( lon );

        double vAndHeight = this.v( phi ) + height;
        double[] cartesian = new double[3];
        cartesian[0] = vAndHeight * Math.cos( phi ) * Math.cos( lamda );
        cartesian[1] = vAndHeight * Math.cos( phi ) * Math.sin( lamda );
        cartesian[2] = ( ( ( 1 - this.eSq() ) * this.v( phi ) ) + height ) * Math.sin( phi );

        return cartesian;
    };

}
