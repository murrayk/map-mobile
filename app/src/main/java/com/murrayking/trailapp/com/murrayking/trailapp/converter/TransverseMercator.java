package com.murrayking.trailapp.com.murrayking.trailapp.converter;

/**
 * Created by murrayking on 21/04/2015.
 */
public class TransverseMercator {


    /**
     *  semi-major ellipsoid axis (metres)
     */
    double a = 6377563.396;

    /**
     *  semi-minor ellipsoid axis (metres)
     */
    double b= 6356256.910;

    /**
     *  Northing of true origin
     */
    double N0 = -100000.0;

    /**
     *  Easting of true origin
     */
    double E0 = 400000.0;

    /**
     *  Scale factor on actual meridian
     */
    double F0= 0.9996012717;

    /**
     * Latitude of true origin
     */
    double phi_0 = MathUtils.toRadians( 49.0 );

    /**
     * Longitude of true origin and central meridian
     */
    double lamda_0 = MathUtils.toRadians( -2.0 );

    /**
     * Constructor: Edina.Geo.TransverseMercator
     * Create a new map location.
     *
     */


    double calcNu (double e_sq, double  phi ) {
        return this.a * this.F0 * Math.pow( ( 1.0 - ( e_sq * Math.pow( Math.sin( phi ), 2.0 ) ) ), -0.5 );
    };

    double calcN () {
        return ( this.a - this.b ) / ( this.a + this.b );
    };

    double calcRho(double e_sq,double phi ) {
        return this.a * this.F0 * ( 1.0 - e_sq ) * Math.pow( ( 1.0 - ( e_sq * Math.pow( Math.sin( phi ), 2.0 ) ) ), -1.5 );
    };

    double calcEtaSq (double nu,double rho ) {
        return ( nu / rho ) - 1.0;
    };

    double calcM(double  n, double phi_prime ) {
        double part_1;
        double part_2;
        double part_3;
        double part_4;

        part_1 = ( 1.0 + n + ((5.0/4.0) * (n*n)) + ((5.0/4.0) * (n*n*n)) ) * ( phi_prime - this.phi_0 );
        part_2 = ( (3.0*n) + (3.0 * (n*n)) + ((21.0/8.0) * (n*n*n))) * Math.sin( phi_prime - this.phi_0 ) * Math.cos( phi_prime + this.phi_0 );

        part_3 = (((15.0/8.0) * (n*n)) + ((15.0/8.0) * (n*n*n) )) * Math.sin( 2.0*( phi_prime - this.phi_0 ) ) * Math.cos( 2.0*( phi_prime + this.phi_0 ) );

        part_4 = (35.0/24.0) * (n*n*n) * Math.sin( 3.0*( phi_prime - this.phi_0 ) ) * Math.cos( 3.0*( phi_prime + this.phi_0 ) );

        return this.b * this.F0 * ( part_1 - part_2 + part_3 - part_4 );
    };

    void setIrishGrid () {
        this.a = 6377340.189;
        this.b = 6356034.447;
        this.N0 = 250000.0;
        this.E0 = 200000.0;
        this.F0 = 1.000035;
        this.phi_0 = MathUtils.toRadians( 53.5 );
        this.lamda_0 = MathUtils.toRadians( -8.0 );
    };

    void setEllipsoid (double a,double b,double northing_origin,double easting_origin,double scale_factor,double lat_origin,double long_origin ) {
        this.a = a;
        this.b = b;
        this.N0 = northing_origin;
        this.E0 = easting_origin;
        this.F0 = scale_factor;
        this.phi_0 = MathUtils.toRadians( lat_origin );
        this.lamda_0 = MathUtils.toRadians( long_origin );
    };

    double[] getCoordFromLatLong (double latitude,double longitude ) {
        double a_sq = this.a * this.a;
        double b_sq = this.b * this.b;
        double e_sq = ( a_sq - b_sq ) / a_sq;
        double phi = MathUtils.toRadians( latitude );
        double lamda = MathUtils.toRadians( longitude );
        double n;
        double M;
        double nu;
        double rho;
        double eta_sq;
        double I;
        double II;
        double III;
        double IIIA;
        double IV;
        double V;
        double VI;
        double N;		//northings
        double E;		//eastings

        n = this.calcN();
        nu = this.calcNu( e_sq, phi );
        rho = this.calcRho( e_sq, phi );
        eta_sq = this.calcEtaSq( nu, rho );
        M = this.calcM( n, phi );

        I = M + this.N0;

        II = (nu/2.0) * Math.sin( phi ) * Math.cos( phi );

        III = (nu/24.0) * Math.sin( phi ) * Math.pow( Math.cos( phi ), 3.0 ) * ( 5.0 - Math.pow( Math.tan( phi ), 2.0 ) + (9.0 * eta_sq) );

        IIIA = (nu/720.0) * Math.sin( phi ) * Math.pow( Math.cos( phi ), 5.0 ) * ( 61.0 - (58.0 * Math.pow( Math.tan( phi ), 2.0 )) + Math.pow( Math.tan( phi ), 4.0 ) );

        IV = nu * Math.cos( phi );

        V = (nu/6.0) * Math.pow( Math.cos( phi ), 3.0 ) * ( (nu/rho) - Math.pow( Math.tan( phi ), 2.0 ) );

        VI = (nu/120.0) * Math.pow( Math.cos( phi ), 5.0 ) * ( 5.0 - (18.0 * Math.pow( Math.tan( phi ), 2.0 )) + Math.pow( Math.tan( phi ), 4.0 ) + (14.0 * eta_sq) - (58.0 * Math.pow( Math.tan( phi ), 2.0 ) * eta_sq) );

        N = I + (II * Math.pow( lamda - this.lamda_0, 2.0 )) + (III * Math.pow( lamda - this.lamda_0, 4.0 )) + (IIIA * Math.pow( lamda - this.lamda_0, 6.0 ));
        E = this.E0 + (IV * ( lamda - this.lamda_0 )) + (V * Math.pow( lamda - this.lamda_0, 3.0 )) + (VI * Math.pow( lamda - this.lamda_0, 5.0 ));
        double[] answer = new double[2];
        answer[0] = N;
        answer[1] = E;
        return answer;
    };

    double[] getLatLongFromCoord (double northing, double easting ) {
        double a_sq = this.a * this.a;
        double b_sq = this.b * this.b;
        double e_sq = ( a_sq - b_sq ) / a_sq;
        double phi_prime;
        double phi_new;
        double n;
        double M;
        double nu;
        double rho;
        double eta_sq;
        double VII;
        double VIII;
        double IX;
        double X;
        double XI;
        double XII;
        double XIIA;
        double phi;		//degrees north
        double lamda;	//degrees east

        phi_prime = ( ( northing - this.N0 ) / ( this.a * this.F0 ) ) + this.phi_0;
        n = this.calcN();
        M = this.calcM( n, phi_prime );

        while ( Math.abs(northing - this.N0 - M) >= (0.01/1000.0) ) {
            phi_new = ( ( northing - this.N0 - M ) / ( this.a * this.F0 ) ) + phi_prime;
            M = this.calcM( n, phi_new );
            phi_prime = phi_new;
        }

        nu = this.calcNu( e_sq, phi_prime );
        rho = this.calcRho( e_sq, phi_prime );
        eta_sq = this.calcEtaSq( nu, rho );

        VII = Math.tan( phi_prime ) / ( 2.0 * rho * nu );

        VIII = (Math.tan( phi_prime ) / ( 24.0 * rho * (nu*nu*nu) ))
                * 	( 5.0 + (3.0 * Math.pow( Math.tan( phi_prime ), 2.0 )) + eta_sq
                -	(9.0 * Math.pow( Math.tan( phi_prime ), 2.0 ) * eta_sq) );

        IX = ( Math.tan( phi_prime ) / (720.0 * rho * Math.pow( nu, 5.0 )) )
                *	( 61.0 + (90.0 * Math.pow( Math.tan( phi_prime ), 2.0 ))
                +	(45.0 * Math.pow( Math.tan( phi_prime ), 4.0 )) );

        X = MathUtils.sec( phi_prime ) / nu;

        XI = (MathUtils.sec( phi_prime ) / (6.0 * (nu*nu*nu)))
                *	( (nu/rho) + (2.0 * Math.pow( Math.tan( phi_prime ), 2.0 )) );

        XII = (MathUtils.sec( phi_prime ) / (120.0 * Math.pow( nu, 5.0 )))
                *	( 5.0 + (28.0 * Math.pow( Math.tan( phi_prime ), 2.0 ))
                +	(24.0 * Math.pow( Math.tan( phi_prime ), 4.0 )) );

        XIIA = (MathUtils.sec( phi_prime ) / (5040.0 * Math.pow( nu, 7.0 ) ))
                *	( 61.0 + (662.0 * Math.pow( Math.tan( phi_prime ), 2.0 ))
                +	(1320.0 * Math.pow( Math.tan( phi_prime ), 4.0 ))
                +	(720.0 * Math.pow( Math.tan( phi_prime ), 6.0 )) );

        phi = phi_prime - (VII * Math.pow( easting-this.E0, 2.0 )) + (VIII * Math.pow( easting- this.E0, 4.0 )) - (IX * Math.pow( easting- this.E0, 6.0 ));
        lamda = this.lamda_0 + (X * (easting- this.E0)) - (XI * Math.pow( easting- this.E0, 3.0 )) + (XII * Math.pow( easting- this.E0, 5.0 )) - (XIIA * Math.pow( easting- this.E0, 7.0 ));
        double[] answer = new double[2];
        answer[0] = MathUtils.toDegrees( phi );
        answer[1] = MathUtils.toDegrees( lamda );
        return answer;
    };

}
