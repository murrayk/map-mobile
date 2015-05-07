package com.murrayking.trailapp.com.murrayking.trailapp.converter;

/**
 * Created by murrayking on 21/04/2015.
 */
public class EPSG_27700 {

    String[][] prefixes = {
            {"SV", "SW", "SX", "SY", "SZ", "TV", "TW"},
            {"SQ", "SR", "SS", "ST", "SU", "TQ", "TR"},
            {"SL", "SM", "SN", "SO", "SP", "TL", "TM"},
            {"SF", "SG", "SH", "SJ", "SK", "TF", "TG"},
            {"SA", "SB", "SC", "SD", "SE", "TA", "TB"},
            {"NV", "NW", "NX", "NY", "NZ", "OV", "OW"},
            {"NQ", "NR", "NS", "NT", "NU", "OQ", "OR"},
            {"NL", "NM", "NN", "NO", "NP", "OL", "OM"},
            {"NF", "NG", "NH", "NJ", "NK", "OF", "OG"},
            {"NA", "NB", "NC", "ND", "NE", "OA", "OB"},
            {"HV", "HW", "HX", "HY", "HZ", "JV", "JW"},
            {"HQ", "HR", "HS", "HT", "HU", "JQ", "JR"},
            {"HL", "HM", "HN", "HO", "HP", "JL", "JM"}
    };

    Elipsoid osgb36 = new Elipsoid(6377563.396, 6356256.910);
    Elipsoid wgs84 = new Elipsoid(6378137.000, 6356752.3141);

    HelmertTransformation toWgs84 = new HelmertTransformation(446.448, -125.157, 542.060,
            0.1502, 0.2470, 0.8421,
            -20.4894);

    TransverseMercator coord = new TransverseMercator();

    public EPSG_27700() {
    }

    public double[] toLocalSystem(double lat, double lon) {
        double[] cartesian = this.wgs84.convertLatLongToCartesian(lat, lon);
        double[] newCartesian = this.toWgs84.reverseHelmertTransformation(cartesian[0],
                cartesian[1],
                cartesian[2]);
        double[] latlong = this.osgb36.convertCartesianToLatLongHeight(newCartesian[0],
                newCartesian[1],
                newCartesian[2]);
        return this.fromLatLong(latlong[0], latlong[1]);

    }

    ;


    public String getGridRef(int precision, double northings, double eastings) {
        int e =0;
        int n =0;
        int y =0;
        int x =0;


        if (precision < 0)
            precision = 0;
        if (precision > 5)
            precision = 5;


        if (precision > 0) {
            y = (int)Math.floor(northings / 100000);
            x = (int)Math.floor(eastings / 100000);


            e = (int)Math.round(eastings % 100000);
            n = (int) Math.round(northings % 100000);


            double div = (5 - precision);
            e = (int) Math.round(e / Math.pow(10, div));
            n = (int) Math.round(n / Math.pow(10, div));
        }

        String prefix = prefixes[y][x];

        return prefix + " " + this.zeropad(e, precision) + " " + this.zeropad(n, precision);
    }

    String zeropad (int num,int len)
    {
        String str= String.valueOf(num);
        while (str.length()<len)
        {
            str='0'+str;
        }
        return str;
    }

    double[] toGlobalLatLong(double north, double east) {
        double[] latlong = this.toLatLong(north, east);
        double[] cartesian = this.osgb36.convertLatLongToCartesian(latlong[0], latlong[1]);
        double[] newCartesian = this.toWgs84.performHelmertTransformation(cartesian[0],
                cartesian[1],
                cartesian[2]);
        return this.wgs84.convertCartesianToLatLongHeight(newCartesian[0],
                newCartesian[1],
                newCartesian[2]);
    }

    ;

    double[] toLatLong(double northing, double easting) {
        return this.coord.getLatLongFromCoord(northing, easting);
    }

    ;

    double[] fromLatLong(double lat, double lon) {
        return this.coord.getCoordFromLatLong(lat, lon);
    }

    ;

}
