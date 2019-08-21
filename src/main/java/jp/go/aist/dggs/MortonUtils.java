package jp.go.aist.dggs;

import javafx.util.Pair;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.locationtech.jts.geom.Coordinate;

import java.math.BigInteger;
import java.util.Map;

/**
 *
 * @author TaehoonKim AIST DPRT, Research Assistant
 * */

public class MortonUtils {
    /**
     * Function to generate binary number with resolution applied
     *
     * @param resolution Resolution of generate Morton code
     * @param binaryList Input binary numbers
     * @return Binary number with resolution applied
     */
    static String[] applyResolution(int resolution, String[] binaryList) {
        String[] result = new String[binaryList.length];

        int fixedLength = binaryList[0].length();
        for (String binary: binaryList) {
            if(fixedLength < binary.length())
                fixedLength = binary.length();
        }

        for (int i = 0; i < binaryList.length; i++) {
            String binary = binaryList[i];
            if(binary.length() < fixedLength) {
                StringBuilder sb = new StringBuilder(binary);
                while(sb.length() < fixedLength) {
                    sb.insert(0, '0');
                }
                binaryList[i] = sb.toString();
            }
            result[i] = applyResolution(resolution, binaryList[i]);
        }

        return result;
    }

    /**
     * Function to generate binary number with resolution applied
     *
     * @param resolution Resolution of generate Morton code
     * @param binary Input binary number
     * @return Binary number with resolution applied
     */
    private static String applyResolution(int resolution, String binary) {
        String result = binary;

        if(binary.length() < resolution) {
            StringBuilder sb = new StringBuilder(binary);
            while(sb.length() < resolution) {
                sb.insert(0, '0');
            }
            result = sb.toString();
        }
        else if(binary.length() > resolution) {
            result = binary.substring(0, resolution);
        }

        return result;
    }

    /**
     * Searching a greatest common ancestor from given Morton code list
     *
     * @param pdCodes List of DGGS Morton code for point cloud
     * @return A greatest common ancestor Morton code
     */
    public static String getGreatestCommonAncestor(String[] pdCodes) {
        String result = null;

        // Search for length of the shortest code in a given code list
        int leastCodeLength = pdCodes[0].length();
        for (String pdCode: pdCodes) {
            if(pdCode.length() < leastCodeLength) {
                leastCodeLength = pdCode.length();
            }
        }

        for(int i = 1; i < leastCodeLength; i++) { // start from 1 to exclude face information (resolution 0)
            boolean isSame = true;
            String baseMCode = pdCodes[0];
            for(int j = 1; j < pdCodes.length; j++) { // start from 1 to exclude baseMCode
                if(!baseMCode.substring(0,i).equals(pdCodes[j].substring(0,i))) {
                    isSame = false;
                    break;
                }
            }

            if(!isSame) {
                result = baseMCode.substring(0,i - 1);
                break;
            }

        }

        // pdCodes[0]とpdCodes[1]が一致していた場合
        if(result == null) {
            String baseMCode = pdCodes[0];
            result = baseMCode.substring(0,leastCodeLength);
        }

        return result;
    }

    /**
     *
     *
     * @param pdCode
     * @param UNIT_SIZE
     * @return
     */
    public static String getFixedLengthCode(String pdCode, final int UNIT_SIZE) {
        if (pdCode.length() != 0) {
            if (pdCode.length() % UNIT_SIZE != 0) {
                int remainIndex = UNIT_SIZE - pdCode.length() % UNIT_SIZE;
                StringBuilder sb = new StringBuilder(pdCode);
                for (int i = 0; i < remainIndex; i++) {
                    sb.insert(0, '0');
                }
                pdCode = sb.toString();
            }
        }
        return pdCode;
    }

    /**
     * Convert normal Morton code to PD-Morton code (Morton code based on DGGS for point cloud data)
     *
     * @param mCode Normal Morton code (made by bit interleaving)
     * @param UNIT_SIZE
     * @param DIMENSION
     * @param BIT_MASK
     * @param LUT
     * @return Morton code based on DGGS for point cloud data. It doesn't have an index of face
     */
    public static String getPDCode(long mCode, final int UNIT_SIZE, final int DIMENSION, final long BIT_MASK, final String[] LUT) {
        final int LOOP_COUNT = Long.SIZE / (UNIT_SIZE * DIMENSION);
        String pdCode;
        StringBuilder sb = new StringBuilder();

        for(int i = LOOP_COUNT; i > 0; --i) {
            int shift = (i - 1) * UNIT_SIZE * DIMENSION;
            sb.append(LUT[(int) ((mCode >> shift) & BIT_MASK)]);
        }

        pdCode = new BigInteger(sb.toString()).toString();
        return pdCode;
    }

    /**
     * Convert normal Morton code to PD-Morton code (Morton code based on DGGS for point cloud data)
     *
     * @param mCode Normal Morton code (made by bit interleaving)
     * @param UNIT_SIZE
     * @param DIMENSION
     * @param BIT_MASK
     * @param LUT
     * @return Morton code based on DGGS for point cloud data. It doesn't have an index of face
     */
    public static String getPDCode(BigInteger mCode, final int UNIT_SIZE, final int DIMENSION, final long BIT_MASK, final String[] LUT) {
        final int LOOP_COUNT =  (int) Math.ceil((double)mCode.bitLength() / (UNIT_SIZE * DIMENSION));
        String pdCode;
        StringBuilder sb = new StringBuilder();

        for(int i = LOOP_COUNT; i > 0; --i) {
            int shift = (i - 1) * UNIT_SIZE * DIMENSION;
            sb.append(LUT[mCode.shiftRight(shift).and(BigInteger.valueOf(BIT_MASK)).intValue()]);
        }

        pdCode = new BigInteger(sb.toString()).toString();
        return pdCode;
    }

    /**
     * Convert PD-Morton code (Morton code based on DGGS for point cloud data) to normal Morton code
     *
     * @param pdCode Morton code based on DGGS for point cloud data. It doesn't have an index of face
     * @param MAX_RESOLUTION
     * @param UNIT_SIZE
     * @param DIMENSION
     * @param LUT
     * @return Normal Morton code
     */
    public static long getMCode_old(String pdCode, final int MAX_RESOLUTION, final int UNIT_SIZE, final int DIMENSION, final Map<String, Integer> LUT) throws IllegalArgumentException {
        long mCode = 0;

        if(pdCode.length() > MAX_RESOLUTION) {
            throw new IllegalArgumentException();
        }

        pdCode = getFixedLengthCode(pdCode, UNIT_SIZE);

        for(int i = 0; i < pdCode.length(); i += UNIT_SIZE) {
            mCode = mCode << (UNIT_SIZE * DIMENSION)
                    | LUT.get(pdCode.substring(i, i + UNIT_SIZE));
        }

        return mCode;
    }

    /**
     * Convert PD-Morton code (Morton code based on DGGS for point cloud data) to normal Morton code
     *
     * @param pdCode Morton code based on DGGS for point cloud data. It doesn't have an index of face
     * @param UNIT_SIZE
     * @param DIMENSION
     * @param LUT
     * @return Normal Morton code
     */
    public static BigInteger getMCode(String pdCode, final int UNIT_SIZE, final int DIMENSION, final Map<String, Integer> LUT) throws IllegalArgumentException {
        BigInteger mCode = new BigInteger("0");
        pdCode = getFixedLengthCode(pdCode, UNIT_SIZE);

        for(int i = 0; i < pdCode.length(); i += UNIT_SIZE) {
            mCode = mCode.shiftLeft(UNIT_SIZE * DIMENSION).or(new BigInteger(LUT.get(pdCode.substring(i, i + UNIT_SIZE)).toString()));
        }

        return mCode;
    }


    private static final int MAX_XY_RESOLUTION = 32;
    private static final int MAX_Z_RESOLUTION = 25;
    private static final double M_PI = Math.PI;
    private static final double NEW_ORIG_X = -0.6022955012659694; // # TABLE_G * (-1) #old:
    private static final double NEW_ORIG_Y = -0.3477354703761901; // # TABLE_H * (-2) #old:
    private static final double H_RANGE = 3355.4432d; // Unit = mm
    private static final double TOTAL_RANGE = Math.pow(2, MAX_XY_RESOLUTION);
    private static final double TOTAL_RANGE_Z = Math.pow(2, MAX_Z_RESOLUTION);
    public static String convertToMorton(double latitude, double longitude, double height, int resolution) {
        double DEG2RAD = M_PI / 180.0d;
        ISEA_Geo point = new ISEA_Geo(longitude * DEG2RAD, latitude * DEG2RAD);
        // # out contains coordinates from center of triangle
        Pair<ISEA_Point, Integer> pair = ISEA_Snyder_Forward(point);
        ISEA_Point triangleCenter = pair.getKey();
        Integer face = pair.getValue();

        // # Find new coordinates of point from lower left/upper left origin
        double newPointX = 0.0d;
        double newPointY = 0.0d;
        if ((face >= 1 && face <= 5) || (face >= 11 && face <= 15)) {
            newPointX = triangleCenter.getX() - NEW_ORIG_X;
            newPointY = triangleCenter.getY() - NEW_ORIG_Y;
        } else {
            newPointX = (triangleCenter.getX() + NEW_ORIG_X) * (-1);
            newPointY = (triangleCenter.getY() - NEW_ORIG_Y) * (-1);
        }

        // # Rotate the axes, round down to nearest integer since addressing begins at 0
        // # Scale coordinates of all dimensions to match resolution of DGGS
        double origX = ((newPointX - ((1 / (Math.sqrt(3))) * newPointY)) / (NEW_ORIG_X * (-2))) * TOTAL_RANGE;
        double origY = ((newPointX + ((1 / (Math.sqrt(3))) * newPointY)) / (NEW_ORIG_X * (-2))) * TOTAL_RANGE;
        double origZ = ((H_RANGE + height) / (H_RANGE * 2.0d)) * TOTAL_RANGE_Z;

        long intX = Double.valueOf(origX).longValue();
        long intY = Double.valueOf(origY).longValue();
        long intZ = Double.valueOf(origZ).longValue();

        // # Convert triangle face number to rhombus face number
        if (face == 1 || face == 6) {
            face = 0;
        } else if (face == 11 || face == 16) {
            face = 1;
        } else if (face == 2 || face == 7) {
            face = 2;
        } else if (face == 12 || face == 17) {
            face = 3;
        } else if (face == 3 || face == 8) {
            face = 4;
        } else if (face == 13 || face == 18) {
            face = 5;
        } else if (face == 4 || face == 9) {
            face = 6;
        } else if (face == 14 || face == 19) {
            face = 7;
        } else if (face == 5 || face == 10) {
            face = 8;
        } else {
            face = 9;
        }

        String pdCode = Morton3D.encode(intX, intY, intZ, face, resolution);

        return pdCode;
    }

    public static Coordinate convertFromMorton(String mortonCode, int resolution) {
        // TODO: utilizing morton code by given resolution
        return convertFromMorton(mortonCode);
    }

    public static Coordinate convertFromMorton(String mortonCode) {
        // # Get face number and Morton code
        // # First number indicates rhombus face!
        int face = Integer.parseInt(mortonCode.substring(0, 1));
        String morton = mortonCode.substring(1);

        // # Compute the X, Y, and Z values on rhombus
        long[] coordinateOnRhombus = Morton3D.decode(morton);
        return convertMortonToLatLong3D(coordinateOnRhombus[0], coordinateOnRhombus[1], coordinateOnRhombus[2], face);
    }

    private static final double RAD2DEG = 180.0d / M_PI;
    private static Coordinate convertMortonToLatLong3D(
            long x, long y, long h, int face) {
        // # Convert h/Z to height above/below ellipsoid
        double height = (h * 2 * H_RANGE) / TOTAL_RANGE_Z - H_RANGE;

        // # Scale coordinates to scale of Cartesian system
        double scaledX = (x / TOTAL_RANGE) * (NEW_ORIG_X * -2);
        double scaledY = (y / TOTAL_RANGE) * (NEW_ORIG_X * -2);

        // # Convert coordinates from skewed system to Cartesian system (origin at left)
        double[][] a = { { 1, (-1 / Math.sqrt(3)) }, { 1, (1 / Math.sqrt(3)) } };
        RealMatrix rma = MatrixUtils.createRealMatrix(a);
        RealMatrix rmai = MatrixUtils.blockInverse(rma, 0);
        double[] b = { scaledX, scaledY };
        // MatrixUtils.createColumnRealMatrix -- a columnData x 1 FieldMatrix
        RealMatrix rmb = MatrixUtils.createColumnRealMatrix(b);
        // MatrixUtils.createRowRealMatrix -- a 1 x rowData.length RealMatrix -- TODO
        RealMatrix rmx = rmai.multiply(rmb); // TODO

        // x[0] -- TODO
        double xCoord = rmx.getData()[0][0];
        // x[1] -- TODO
        double yCoord = rmx.getData()[1][0]; // TODO : [0][1]

        // # Get triangle face from rhombus face based on values of y.
        // # If y is negative, triangles will be downward oriented
        if (yCoord >= 0) {
            if (face == 0) {
                face = 1;
            } else if (face == 1) {
                face = 11;
            } else if (face == 2) {
                face = 2;
            } else if (face == 3) {
                face = 12;
            } else if (face == 4) {
                face = 3;
            } else if (face == 5) {
                face = 13;
            } else if (face == 6) {
                face = 4;
            } else if (face == 7) {
                face = 14;
            } else if (face == 8) {
                face = 5;
            } else if (face == 9) {
                face = 15;
            }
        } else {
            if (face == 0) {
                face = 6;
            } else if (face == 1) {
                face = 16;
            } else if (face == 2) {
                face = 7;
            } else if (face == 3) {
                face = 17;
            } else if (face == 4) {
                face = 8;
            } else if (face == 5) {
                face = 18;
            } else if (face == 6) {
                face = 9;
            } else if (face == 7) {
                face = 19;
            } else if (face == 8) {
                face = 10;
            } else if (face == 9) {
                face = 20;
            }
        }

        // # Translate coordinates to center (origin) of icosahedron triangle,
        // # taking into account triangle orientation
        double xOrigin = 0.0d;
        double yOrigin = 0.0d;

        if ((face >= 1 && face <= 5) || (face >= 11 && face <= 15)) {
            xOrigin = ((-NEW_ORIG_X) - xCoord) * (-1);
            yOrigin = ((-NEW_ORIG_Y) - yCoord) * (-1);
        } else {
            xOrigin = (-NEW_ORIG_X) - xCoord;
            yOrigin = ((-NEW_ORIG_Y) + yCoord) * (-1);
        }

        // # Equation 17
        double Azprime = Math.atan2(xOrigin, yOrigin);

        // # Equation 18
        double rho = Math.sqrt((Math.pow(xOrigin, 2) + Math.pow(yOrigin, 2)));

        // # Adjust Azprime to fall within 0 to 120 degrees
        int Azprime_adjust_multiples = 0;

        // while Azprime < 0.0:
        while (Azprime < 0.0d) {
            Azprime += DEG120;
            Azprime_adjust_multiples -= 1;
        }
        // while (Azprime > DEG120):
        while (Azprime > DEG120) {
            Azprime -= DEG120;
            Azprime_adjust_multiples += 1;
        }

        double AzprimeCopy = Azprime;

        // #Equation 19
        double AG = (Math.pow(R_PRIME, 2) * Math.pow(TAN_LOWER_G, 2)) / (2 * ((1 / (Math.tan(Azprime))) + COTTHETA));

        // # Iteration, Azprime (plane) converges to Az (ellipsoid)
        for (int i = 0; i <= 4; i++) {
            double H = Math.acos((Math.sin(Azprime) * SIN_UPPER_G * COS_LOWER_G) - (Math.cos(Azprime) * COS_UPPER_G));
            double FAZ = (AG - UPPER_G - H - Azprime + DEG180);
            double FPRIMEAZ = (((Math.cos(Azprime) * SIN_UPPER_G * COS_LOWER_G) + (Math.sin(Azprime) * COS_UPPER_G))
                    / (Math.sin(H))) - 1;
            double DeltaAzprime = -FAZ / (FPRIMEAZ);
            Azprime = Azprime + DeltaAzprime;
        }

        double Az = Azprime;

        // # Equations 9-11, 23 to obtain z
        double q = Math.atan((TAN_LOWER_G) / (Math.cos(Az) + (Math.sin(Az) * COTTHETA)));

        // # eq 10
        double dprime = ((R_PRIME * TAN_LOWER_G) / (Math.cos(AzprimeCopy) + (Math.sin(AzprimeCopy) * COTTHETA)));

        // # eq 11
        double f = dprime / (2.0 * R_PRIME * Math.sin(q / 2.0));

        // #eq 23, obtain z
        double z = 2 * Math.asin((rho) / (2 * R_PRIME * f));

        // # Add back 120 degree adjustments to Az
        Az += DEG120 * Azprime_adjust_multiples;

        // # Adjust Az to be clockwise from north (needed for final calculation)
        if ((face >= 1 && face <= 5) || (face >= 11 && face <= 15)) {
            if (Az < 0) {
                Az = (M_PI - (Az * (-1))) + M_PI;
            }
        } else {
            if (Az < 0) {
                Az = M_PI - (Az * (-1));
            } else {
                Az = Az + M_PI;
            }
        }

        z = z * R;

        // # triangle center
        ISEA_Geo center = ICOS_TRIANGLE_CENTER[face];

        Pair<Double, Double> vdPair = vincentyDirect(FLATTENING, R,
                center.getLatitude() * RAD2DEG, center.getLongitude() * RAD2DEG, Az * RAD2DEG, z);

        double lat2 = vdPair.getKey();
        double lon2 = vdPair.getValue();

        Coordinate point = new Coordinate(lat2, lon2, height);
        return point;
    }

    private static class ISEA_Geo {
        private double longitude = 0.0d;
        private double latitude = 0.0d;

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

    private static class ISEA_Point {
        private double x = 0.0d;
        private double y = 0.0d;

        ISEA_Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        double getX() {
            return x;
        }
        double getY() {
            return y;
        }

    }

    private static final double DEG2RAD = M_PI / 180.0d;
    private static final double LOWER_G = 37.37736814 * DEG2RAD;
    private static final double UPPER_G = 36.0 * DEG2RAD;
    private static final double THETA = 30.0 * DEG2RAD;
    private static final double R_PRIME = 0.91038328153090290025;
    private static final double COS_LOWER_G = Math.cos(LOWER_G);
    private static final double TAN_LOWER_G = Math.tan(LOWER_G);
    private static final double TAN_THETA = Math.tan(THETA);
    private static final double COTTHETA = 1.0 / TAN_THETA;
    private static final double SIN_UPPER_G = Math.sin(UPPER_G);
    private static final double COS_UPPER_G = Math.cos(UPPER_G);
    private static Pair<ISEA_Point, Integer> ISEA_Snyder_Forward(ISEA_Geo iseaGeo) {
        // TODO : for i in range(1,21):
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
            Pair<ISEA_Point, Integer> pair = new Pair<>(out, i);
            return pair;
        }

        return null; // TODO
    }

    private static final double DEG120 = 2.09439510239319549229;
    private static final double DEG72 = 1.25663706143591729537;
    private static final double DEG90 = 1.57079632679489661922;
    private static final double DEG144 = 2.51327412287183459075;
    private static final double DEG36 = 0.62831853071795864768;
    private static final double DEG108 = 1.88495559215387594306;
    private static final double DEG180 = M_PI;
    private static final double E_RAD = 0.91843818702186776133;
    private static final double F_RAD = 0.18871053072122403508;
    // # icosahedron triangle centers
    private static final ISEA_Geo[] ICOS_TRIANGLE_CENTER = {
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

    // # semi-major axis, meters
    private static final int R = 6378137;
    // # semi-minor axis, meters
    private static final double b = 6356752.314245;
    // #flattening=(a-b)/a, a=298.257223563,b=297.257223563;
    private static final double FLATTENING = 1 / 298.257223563;
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

        double sinL = 0.0d; // TODO
        double cosL = 0.0d; // TODO
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
            // #        if (abs(Lambda) > M_PI):
            // #            print('Lambda > π')
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
        double Alpha2 = Math.atan2(cosU1 * sinL, -sinU1 * cosU2 + cosU1 * sinU2 * cosL);

        Alpha1 = (Alpha1 + 2 * M_PI) % (2 * M_PI); // # normalise to 0..360;
        //Alpha2 = (Alpha2 + 2 * M_PI) % (2 * M_PI); // # normalise to 0..360;

        Pair<Double, Double> pair = new Pair<Double, Double>(s / R, Alpha1);
        return pair;
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

    // # vertices of triangles
    private static final double V_LAT = 0.46364760899944494524;
    private static final ISEA_Geo[] VERTEX_TRIANGLE = {
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

    private static final int[] tri_v1 = { 0, 0, 0, 0, 0, 0, 6, 7, 8, 9, 10, 2, 3, 4, 5, 1, 11, 11, 11, 11, 11 };

    private static Pair<Double, Double> vincentyDirect(
            double f, int a, double phi0, double lembda0, double alpha0, double s) {
        // Returns the lat and long of projected point
        // given a reference point and a distance and azimuth to project.
        // Returns ( phi2,  lambda2) as a tuple, in decimal degrees.
        // Parameters:
        //
        // The code has been originally taken from
        // https://isis.astrogeology.usgs.gov/IsisSupport/index.php?topic=408.0 in Javascript,
        // and later converted into Python.

        // piD4 = math.atan( 1.0 )
        double piD4 = Math.atan(1.0); // TODO : 定義位置を変更した方が良いかも
        // two_pi = piD4 * 8.0
        double two_pi = piD4 * 8.0; // TODO : 定義位置を変更した方が良いかも
        double phi1 = phi0 * piD4 / 45.0; // TODO : [piD4 / 45.0]は、定義位置を変更した方が良いかも
        double lembda1 = lembda0 * piD4 / 45.0;
        double alpha12 = alpha0 * piD4 / 45.0;

        if (alpha12 < 0.0) {
            alpha12 = alpha12 + two_pi;
        }
        if (alpha12 > two_pi) {
            alpha12 = alpha12 - two_pi;
        }

        double b = a * (1.0 - f); // TODO : 定義位置を変更した方が良いかも -- b : 固定値になっている
        double TanU1 = (1 - f) * Math.tan(phi1);
        double U1 = Math.atan(TanU1);
        double sigma1 = Math.atan2(TanU1, Math.cos(alpha12));
        double sinAlpha = Math.cos(U1) * Math.sin(alpha12);

        double cosAlpha_sq = 1.0 - sinAlpha * sinAlpha;
        double u2 = cosAlpha_sq * ((double) a * (double) a - b * b) / (b * b); // TODO：「(a * a - b * b ) / (b * b)」は固定値になっている
        double A = 1.0 + (u2 / 16384) * (4096 + u2 * (-768 + u2 * (320 - 175 * u2)));
        double B = (u2 / 1024) * (256 + u2 * (-128 + u2 * (74 - 47 * u2)));

        // # Starting with the approx
        double sigma = (s / (b * A));
        // # something impossible
        double last_sigma = 2.0 * sigma + 2.0;

        double two_sigma_m = 0.0d; // TODO

        // # Iterate the following 3 eqs unitl no sig change in sigma
        // # two_sigma_m , delta_sigma
        while (Math.abs((last_sigma - sigma) / sigma) > 1.0e-9) {
            two_sigma_m = 2 * sigma1 + sigma; // TODO
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
        double lembda = Math.atan2(
                (Math.sin(sigma) * Math.sin(alpha12)),
                (Math.cos(U1) * Math.cos(sigma) - Math.sin(U1) * Math.sin(sigma) * Math.cos(alpha12)));
        double C = (f / 16) * cosAlpha_sq * (4 + f * (4 - 3 * cosAlpha_sq));
        double omega = lembda - (1 - C) * f * sinAlpha * (sigma + C * Math.sin(sigma) * (Math.cos(two_sigma_m) +
                C * Math.cos(sigma) * (-1 + 2 * Math.pow(Math.cos(two_sigma_m), 2))));
        double lembda2 = lembda1 + omega;

        //		double alpha21 = Math.atan2 ( Sinalpha,
        //				(-Math.sin(U1) * Math.sin(sigma) + Math.cos(U1) * Math.cos(sigma) * Math.cos(alpha12)));
        //		alpha21 = alpha21 + two_pi / 2.0;
        //		if ( alpha21 < 0.0 ) {
        //			alpha21 = alpha21 + two_pi;
        //		}
        //		if ( alpha21 > two_pi ) {
        //			alpha21 = alpha21 - two_pi;
        //		}

        // 緯度
        phi2 = phi2 * 45.0 / piD4;
        // 経度
        lembda2 = lembda2 * 45.0 / piD4;

        Pair<Double, Double> pair = new Pair<>(phi2, lembda2);
        return pair;
    }
}
