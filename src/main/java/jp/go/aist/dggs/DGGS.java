package jp.go.aist.dggs;

import java.util.AbstractMap;

public class DGGS {
    static final int MAX_XY_RESOLUTION = 32;
    static final int MAX_Z_RESOLUTION = 25;
    static final double M_PI = Math.PI;
    static final double NEW_ORIG_X = -0.6022955012659694; // # TABLE_G * (-1) #old:
    static final double NEW_ORIG_Y = -0.3477354703761901; // # TABLE_H * (-2) #old:
    static final double H_RANGE = 3355.4432d; // Unit = mm
    static final double TOTAL_RANGE = Math.pow(2, MAX_XY_RESOLUTION);
    static final double TOTAL_RANGE_Z = Math.pow(2, MAX_Z_RESOLUTION);
    static final double DEG2RAD = M_PI / 180.0d;
    static final double RAD2DEG = 180.0d / M_PI;
    static final double LOWER_G = 37.37736814 * DEG2RAD;
    static final double UPPER_G = 36.0 * DEG2RAD;
    static final double THETA = 30.0 * DEG2RAD;
    static final double R_PRIME = 0.91038328153090290025;
    static final double COS_LOWER_G = Math.cos(LOWER_G);
    static final double TAN_LOWER_G = Math.tan(LOWER_G);
    static final double TAN_THETA = Math.tan(THETA);
    static final double COTTHETA = 1.0 / TAN_THETA;
    static final double SIN_UPPER_G = Math.sin(UPPER_G);
    static final double COS_UPPER_G = Math.cos(UPPER_G);
    static final double DEG120 = 2.09439510239319549229;
    static final double DEG72 = 1.25663706143591729537;
    static final double DEG90 = 1.57079632679489661922;
    static final double DEG144 = 2.51327412287183459075;
    static final double DEG36 = 0.62831853071795864768;
    static final double DEG108 = 1.88495559215387594306;
    static final double DEG180 = M_PI;
    static final double E_RAD = 0.91843818702186776133;
    static final double F_RAD = 0.18871053072122403508;

    static final int R = 6378137;   // # semi-major axis, meters
    static final double b = 6356752.314245; // # semi-minor axis, meters
    static final double FLATTENING = 1 / 298.257223563; // #flattening=(a-b)/a, a=298.257223563,b=297.257223563;
    static final ISEA_Geo[] ICOS_TRIANGLE_CENTER = {    // # icosahedron triangle centers
            new ISEA_Geo(0.0d, 0.0d),
            new ISEA_Geo(-DEG144, E_RAD),
            new ISEA_Geo(-DEG72, E_RAD),
            new ISEA_Geo(0.0d, E_RAD),
            new ISEA_Geo(DEG72, E_RAD),
            new ISEA_Geo(DEG144, E_RAD),
            new ISEA_Geo(-DEG144, F_RAD),
            new ISEA_Geo(-DEG72, F_RAD),
            new ISEA_Geo(0.0d, F_RAD),
            new ISEA_Geo(DEG72, F_RAD),
            new ISEA_Geo(DEG144, F_RAD),
            new ISEA_Geo(-DEG108, -F_RAD),
            new ISEA_Geo(-DEG36, -F_RAD),
            new ISEA_Geo(DEG36, -F_RAD),
            new ISEA_Geo(DEG108, -F_RAD),
            new ISEA_Geo(DEG180, -F_RAD),
            new ISEA_Geo(-DEG108, -E_RAD),
            new ISEA_Geo(-DEG36, -E_RAD),
            new ISEA_Geo(DEG36, -E_RAD),
            new ISEA_Geo(DEG108, -E_RAD),
            new ISEA_Geo(DEG180, -E_RAD)
    };


    private static final double V_LAT = 0.46364760899944494524;
    private static final ISEA_Geo[] VERTEX_TRIANGLE = { // # vertices of triangles
            new ISEA_Geo(0.0d, DEG90),
            new ISEA_Geo(DEG180, V_LAT),
            new ISEA_Geo(-DEG108, V_LAT),
            new ISEA_Geo(-DEG36, V_LAT),
            new ISEA_Geo(DEG36, V_LAT),
            new ISEA_Geo(DEG108, V_LAT),
            new ISEA_Geo(-DEG144, -V_LAT),
            new ISEA_Geo(-DEG72, -V_LAT),
            new ISEA_Geo(0.0d, -V_LAT),
            new ISEA_Geo(DEG72, -V_LAT),
            new ISEA_Geo(DEG144, -V_LAT),
            new ISEA_Geo(0.0d, -DEG90)
    };

    private static final int[] tri_v1 = {0, 0, 0, 0, 0, 0, 6, 7, 8, 9, 10, 2, 3, 4, 5, 1, 11, 11, 11, 11, 11};

    static class ISEA_Geo {
        private double longitude;
        private double latitude;

        ISEA_Geo(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        double getLongitude() {
            return longitude;
        }

        double getLatitude() {
            return latitude;
        }
    }

    static class ISEA_Point {
        private double x;
        private double y;

        ISEA_Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        double getX() { return x; }

        double getY() { return y; }
    }

    static class Pair<K, V> extends AbstractMap.SimpleEntry<K, V> {
        Pair(final K key, final V value) {
            super(key, value);
        }
    }

    static Pair<ISEA_Point, Integer> ISEA_Snyder_Forward(ISEA_Geo iseaGeo) {
        for (int i = 0; i < ICOS_TRIANGLE_CENTER.length; i++) {
            ISEA_Geo center = ICOS_TRIANGLE_CENTER[i];
            // # step 1 , returns z(scaled meters) and Az (in radians)
            // z, Az = vincentyInverse(center.lat, center.lon, ll.lat, ll.lon)
            Pair<Double, Double> viPair = vincentyInverse(
                    center.getLatitude(), center.getLongitude(),
                    iseaGeo.getLatitude(), iseaGeo.getLongitude());
            double z = viPair.getKey();
            double Az = viPair.getValue();
            if (Az > M_PI) {
                Az = Az - (2 * M_PI);
            }
            // # not on this triangle
            if (z > LOWER_G) {
                continue;
            }

            // # step 2
            // # This calculates a vertex coordinate whose azimuth is going to be assigned 0
            double az_offset = azAdjustment(i);

            // # This gives that vertex an azimuth of 0. For south pointing triangles the range of azimuths changes
            // # from [-3.14 - 3.14] to [-6.28 0].
            // # For north pointing triangles, makes no difference.
            Az -= az_offset;

            // # Adjust Az to fall between 0 and 120 degrees, record adjustment amount
            int Az_adjust_multiples = 0;

            while (Az < 0.0) {
                Az += DEG120;
                Az_adjust_multiples -= 1;
            }

            while (Az > DEG120) {
                Az -= DEG120;
                Az_adjust_multiples += 1;
            }

            // # Calculate q from eq 9.
            double q = Math.atan(TAN_LOWER_G / (Math.cos(Az) + (Math.sin(Az) * COTTHETA)));

            // # not in this triangle
            if (z > q) {
                continue;
            }

            // # Apply equations 5-8 and 10-12 in order
            // # eq 6
            double H = Math.acos((Math.sin(Az) * SIN_UPPER_G * COS_LOWER_G) - (Math.cos(Az) * COS_UPPER_G));

            // # eq 7
            double AG = (Az + UPPER_G + H - DEG180);

            // # eq 8
            double Azprime = Math.atan2((2.0 * AG),
                    ((R_PRIME * R_PRIME * TAN_LOWER_G * TAN_LOWER_G) - (2.0 * AG * COTTHETA)));

            // # eq 10
            double dprime = (R_PRIME * TAN_LOWER_G) / (Math.cos(Azprime) + (Math.sin(Azprime) * COTTHETA));

            // # eq 11
            double f = dprime / (2.0 * R_PRIME * Math.sin(q / 2.0));

            // # eq 12
            double rho = 2.0 * R_PRIME * f * Math.sin(z / 2.0);

            // #add back the same 120 degree multiple adjustment from step 2 to Azprime
            Azprime += DEG120 * Az_adjust_multiples;

            // # calculate rectangular coordinates
            double x = rho * Math.sin(Azprime);
            double y = rho * Math.cos(Azprime);

            ISEA_Point out = new ISEA_Point(x, y);
            return new Pair<>(out, i);
        }

        return null; // TODO
    }

    private static Pair<Double, Double> vincentyInverse(double lat1, double lon1, double lat2, double lon2) {
        // # all values in radians!
        if (lon1 == -M_PI) {
            lon1 = M_PI;
        }

        double L = lon2 - lon1;
        double tanU1 = (1 - FLATTENING) * Math.tan(lat1);
        double cosU1 = 1 / Math.sqrt((1 + tanU1 * tanU1));
        double sinU1 = tanU1 * cosU1;
        double tanU2 = (1 - FLATTENING) * Math.tan(lat2);
        double cosU2 = 1 / Math.sqrt((1 + tanU2 * tanU2));
        double sinU2 = tanU2 * cosU2;

        double sinL = 0.0d;
        double cosL = 0.0d;
        double sinS = 0.0d;
        double cosS = 0.0d;
        double Sigma = 0.0d;
        double cosSqAlpha = 0.0d;
        double cos2SigmaM = 0.0d;

        double Lambda = L;
        double LambdaPrime = 0;
        int iterations = 0;
        while (Math.abs(Lambda - LambdaPrime) > 1e-12 && iterations < 1000) {
            iterations += 1;
            sinL = Math.sin(Lambda);
            cosL = Math.cos(Lambda);

            double sinSqSigma = (cosU2 * sinL) * (cosU2 * sinL) +
                    (cosU1 * sinU2 - sinU1 * cosU2 * cosL) * (cosU1 * sinU2 - sinU1 * cosU2 * cosL);
            if (sinSqSigma == 0.0d) {
                break; // #co-incident points
            }

            sinS = Math.sqrt(sinSqSigma);
            cosS = sinU1 * sinU2 + cosU1 * cosU2 * cosL;
            Sigma = Math.atan2(sinS, cosS);
            double sinAlpha = cosU1 * cosU2 * sinL / sinS;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;

            if (cosSqAlpha != 0) {
                cos2SigmaM = (cosS - 2 * sinU1 * sinU2 / cosSqAlpha);
            } else {
                cos2SigmaM = 0.0d;
            }

            double C = FLATTENING / 16 * cosSqAlpha * (4 + FLATTENING * (4 - 3 * cosSqAlpha));
            LambdaPrime = Lambda;
            Lambda = L + (1 - C) * FLATTENING * sinAlpha *
                    (Sigma + C * sinS * (cos2SigmaM + C * cosS * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        }

        if (iterations >= 1000) {
            System.out.println("Formula failed to converge");
        }

        double uSq = cosSqAlpha * ((1.0d * R * R) - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double DeltaSigma = B * sinS * (cos2SigmaM + B / 4 *
                (cosS * (-1 + 2 * cos2SigmaM * cos2SigmaM)
                        - B / 6 * cos2SigmaM * (-3 + 4 * sinS * sinS) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        double s = b * A * (Sigma - DeltaSigma);
        double Alpha1 = Math.atan2(cosU2 * sinL, cosU1 * sinU2 - sinU1 * cosU2 * cosL);
        Alpha1 = (Alpha1 + 2 * M_PI) % (2 * M_PI); // # normalise to 0..360;

        return new Pair<>(s / R, Alpha1);
    }

    private static double azAdjustment(int triangle) {
        // # vertex ID of triangle
        ISEA_Geo v = VERTEX_TRIANGLE[tri_v1[triangle]];
        // # center of triangle
        ISEA_Geo c = ICOS_TRIANGLE_CENTER[triangle];
        // # Azimuth from vertex to center of triangle
        double adj = Math.atan2(
                Math.cos(v.getLatitude()) * Math.sin(v.getLongitude() - c.getLongitude()),
                Math.cos(c.getLatitude()) * Math.sin(v.getLatitude())
                        - Math.sin(c.getLatitude()) * Math.cos(v.getLatitude())
                        * Math.cos(v.getLongitude() - c.getLongitude()));

        return adj;
    }

    static Pair<Double, Double> vincentyDirect(
            double f, int a, double phi0, double lembda0, double alpha0, double s) {
        // Returns the lat and long of projected point
        // given a reference point and a distance and azimuth to project.
        // Returns ( phi2,  lambda2) as a tuple, in decimal degrees.
        // Parameters:
        //
        // The code has been originally taken from
        // https://isis.astrogeology.usgs.gov/IsisSupport/index.php?topic=408.0 in Javascript,
        // and later converted into Python.

        double piD4 = Math.atan(1.0);
        double two_pi = piD4 * 8.0;
        double phi1 = phi0 * piD4 / 45.0;
        double lambda1 = lembda0 * piD4 / 45.0;
        double alpha12 = alpha0 * piD4 / 45.0;

        if (alpha12 < 0.0) {
            alpha12 = alpha12 + two_pi;
        }
        if (alpha12 > two_pi) {
            alpha12 = alpha12 - two_pi;
        }

        double b = a * (1.0 - f);
        double TanU1 = (1 - f) * Math.tan(phi1);
        double U1 = Math.atan(TanU1);
        double sigma1 = Math.atan2(TanU1, Math.cos(alpha12));
        double sinAlpha = Math.cos(U1) * Math.sin(alpha12);

        double cosAlpha_sq = 1.0 - sinAlpha * sinAlpha;
        double u2 = cosAlpha_sq * ((double) a * (double) a - b * b) / (b * b);
        double A = 1.0 + (u2 / 16384) * (4096 + u2 * (-768 + u2 * (320 - 175 * u2)));
        double B = (u2 / 1024) * (256 + u2 * (-128 + u2 * (74 - 47 * u2)));

        // # Starting with the approx
        double sigma = (s / (b * A));
        // # something impossible
        double last_sigma = 2.0 * sigma + 2.0;
        double two_sigma_m = 0.0d;

        // # Iterate the following 3 eqs unitl no sig change in sigma
        // # two_sigma_m , delta_sigma
        while (Math.abs((last_sigma - sigma) / sigma) > 1.0e-9) {
            two_sigma_m = 2 * sigma1 + sigma;
            double delta_sigma = B * Math.sin(sigma) * (Math.cos(two_sigma_m)
                    + (B / 4) * (Math.cos(sigma) * (-1 + 2 * Math.pow(Math.cos(two_sigma_m), 2)
                    - (B / 6) * Math.cos(two_sigma_m) * (-3 + 4 * Math.pow(Math.sin(sigma), 2))
                    * (-3 + 4 * Math.pow(Math.cos(two_sigma_m), 2)))));
            last_sigma = sigma;
            sigma = (s / (b * A)) + delta_sigma;
        }

        double phi2 = Math.atan2(
                (Math.sin(U1) * Math.cos(sigma) + Math.cos(U1) * Math.sin(sigma) * Math.cos(alpha12)),
                ((1 - f) * Math.sqrt(Math.pow(sinAlpha, 2) +
                        Math.pow(Math.sin(U1) * Math.sin(sigma) - Math.cos(U1) * Math.cos(sigma) * Math.cos(alpha12),
                                2))));
        double lambda = Math.atan2(
                (Math.sin(sigma) * Math.sin(alpha12)),
                (Math.cos(U1) * Math.cos(sigma) - Math.sin(U1) * Math.sin(sigma) * Math.cos(alpha12)));
        double C = (f / 16) * cosAlpha_sq * (4 + f * (4 - 3 * cosAlpha_sq));
        double omega = lambda - (1 - C) * f * sinAlpha * (sigma + C * Math.sin(sigma) * (Math.cos(two_sigma_m) +
                C * Math.cos(sigma) * (-1 + 2 * Math.pow(Math.cos(two_sigma_m), 2))));
        double lambda2 = lambda1 + omega;

        phi2 = phi2 * 45.0 / piD4;
        lambda2 = lambda2 * 45.0 / piD4;

        return new Pair<>(phi2, lambda2);
    }
}
