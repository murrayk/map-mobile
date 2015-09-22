package com.murrayking.trailapp.converter;

/**
 * Created by murrayking on 21/04/2015.
 */
public class HelmertTransformation {

    double tX= 0;
    double tY= 0;
    double tZ= 0;
    double rX= 0;
    double rY= 0;
    double rZ= 0;
    double scale= 0;

    /**
     * Constructor: Edina.HelmertTransformation 
     *
     */

    public HelmertTransformation (double tX,double tY,double tZ,double rX,double rY,double rZ,double scale ){

        this.tX = tX;
        this.tY = tY;
        this.tZ = tZ;
        this.rX = MathUtils.toRadians( rX / 3600 );
        this.rY = MathUtils.toRadians( rY / 3600 );
        this.rZ = MathUtils.toRadians( rZ / 3600 );
        this.scale = scale * 0.000001;
    };

    double[] helmertTransform (double x,double y,double z,double tX,double tY,double tZ,double rX,double rY,double rZ,double scale )  {
        double[] coord = new double[3];

        double sPlus1 = 1 + scale;
        double newX = (sPlus1 * x) - (rZ * y) + (rY * z);
        double newY = (rZ * x) + (sPlus1 * y) - (rX * z);
        double newZ = (-rY * x) + (rX * y) + (sPlus1 * z);

        coord[0] = newX + tX;
        coord[1] = newY + tY;
        coord[2] = newZ + tZ;

        return coord;
    };
    double[] performHelmertTransformation (double x,double  y,double  z )  {
        return this.helmertTransform( x, y, z, this.tX, this.tY, this.tZ, this.rX, this.rY, this.rZ, this.scale );
    };

    double[] reverseHelmertTransformation (double x,double y,double z )  {
        return this.helmertTransform( x, y, z, -this.tX, -this.tY, -this.tZ, -this.rX, -this.rY, -this.rZ, -this.scale );
    };


}
