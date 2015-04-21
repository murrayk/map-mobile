package com.murrayking.trailapp.com.murrayking.trailapp.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EPSG_27700Test {
    private static final String TAG = EPSG_27700Test.class.getName();
    private static final double DELTA = 1e-15;
    private final double northing = 639600.0928136349;
    private final double easting = 328361.9535626852;

    @Test
    public void testToLocalSystem() throws Exception {
        EPSG_27700 natgrid =  new EPSG_27700() ;


        double latt = 55.644643;
        double lont = -3.139772;

        //328398 , 639568

        double[] coords = natgrid.toLocalSystem( latt, lont );

        assertEquals( "Northing ", northing,coords[0], DELTA);
        assertEquals( "Easting ", easting, coords[1], DELTA );

        //Location.convert();

    }

    @Test
    public void testgetGridRef() throws Exception {
        EPSG_27700 natgrid =  new EPSG_27700() ;
        String gridRef = natgrid.getGridRef(5,  northing, easting);

        assertEquals("NT 28362 39600", gridRef);

    }


}