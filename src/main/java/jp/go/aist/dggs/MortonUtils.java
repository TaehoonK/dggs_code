package jp.go.aist.dggs;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.locationtech.jts.geom.Coordinate;

import static jp.go.aist.dggs.DGGS.R_PRIME;

/**
 * @author TaehoonKim AIST DPRT, Research Assistant
 */

public class MortonUtils {
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
        for (String pdCode : pdCodes) {
            if (pdCode.length() < leastCodeLength) {
                leastCodeLength = pdCode.length();
            }
        }

        for (int i = 1; i < leastCodeLength; i++) { // start from 1 to exclude face information (resolution 0)
            boolean isSame = true;
            String baseMCode = pdCodes[0];
            for (int j = 1; j < pdCodes.length; j++) { // start from 1 to exclude baseMCode
                if (!baseMCode.substring(0, i).equals(pdCodes[j].substring(0, i))) {
                    isSame = false;
                    break;
                }
            }

            if (!isSame) {
                result = baseMCode.substring(0, i - 1);
                break;
            }

        }

        // pdCodes[0]とpdCodes[1]が一致していた場合
        if (result == null) {
            String baseMCode = pdCodes[0];
            result = baseMCode.substring(0, leastCodeLength);
        }

        return result;
    }

    /**
     * @param longitude
     * @param latitude
     * @param height
     * @param resolution
     * @return
     */
    public static String convertToMorton(double latitude, double longitude, double height, int resolution) {
        double DEG2RAD = DGGS.M_PI / 180.0d;
        DGGS.ISEA_Geo point = new DGGS.ISEA_Geo(longitude * DEG2RAD, latitude * DEG2RAD);
        // # out contains coordinates from center of triangle
        DGGS.Pair<DGGS.ISEA_Point, Integer> pair = DGGS.ISEA_Snyder_Forward(point);
        DGGS.ISEA_Point triangleCenter = pair.getKey();
        Integer face = pair.getValue();

        // # Find new coordinates of point from lower left/upper left origin
        double newPointX = 0.0d;
        double newPointY = 0.0d;
        if ((face >= 1 && face <= 5) || (face >= 11 && face <= 15)) {
            newPointX = triangleCenter.getX() - DGGS.NEW_ORIG_X;
            newPointY = triangleCenter.getY() - DGGS.NEW_ORIG_Y;
        } else {
            newPointX = (triangleCenter.getX() + DGGS.NEW_ORIG_X) * (-1);
            newPointY = (triangleCenter.getY() - DGGS.NEW_ORIG_Y) * (-1);
        }

        // # Rotate the axes, round down to nearest integer since addressing begins at 0
        // # Scale coordinates of all dimensions to match resolution of DGGS
        double origX = ((newPointX - ((1 / (Math.sqrt(3))) * newPointY)) / (DGGS.NEW_ORIG_X * (-2))) * DGGS.TOTAL_RANGE;
        double origY = ((newPointX + ((1 / (Math.sqrt(3))) * newPointY)) / (DGGS.NEW_ORIG_X * (-2))) * DGGS.TOTAL_RANGE;
        double origZ = ((DGGS.H_RANGE + height) / (DGGS.H_RANGE * 2.0d)) * DGGS.TOTAL_RANGE_Z;

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

    /**
     * @param mortonCode
     * @param resolution
     * @return
     */
    public static Coordinate convertFromMorton(String mortonCode, int resolution) {
        // TODO: utilizing morton code by given resolution
        return convertFromMorton(mortonCode);
    }

    /**
     * @param mortonCode
     * @return
     */
    public static Coordinate convertFromMorton(String mortonCode) {
        // # Get face number and Morton code
        // # First number indicates rhombus face!
        int face = Integer.parseInt(mortonCode.substring(0, 1));
        String morton = mortonCode.substring(1);

        // # Compute the X, Y, and Z values on rhombus
        long[] coordinateOnRhombus = Morton3D.decode(morton);
        return convertMortonToLatLong3D(coordinateOnRhombus[0], coordinateOnRhombus[1], coordinateOnRhombus[2], face);
    }

    private static Coordinate convertMortonToLatLong3D(
            long x, long y, long h, int face) {
        // # Convert h/Z to height above/below ellipsoid
        double height = (h * 2 * DGGS.H_RANGE) / DGGS.TOTAL_RANGE_Z - DGGS.H_RANGE;

        // # Scale coordinates to scale of Cartesian system
        double scaledX = (x / DGGS.TOTAL_RANGE) * (DGGS.NEW_ORIG_X * -2);
        double scaledY = (y / DGGS.TOTAL_RANGE) * (DGGS.NEW_ORIG_X * -2);

        // # Convert coordinates from skewed system to Cartesian system (origin at left)
        double[][] a = {{1, (-1 / Math.sqrt(3))}, {1, (1 / Math.sqrt(3))}};
        RealMatrix rma = MatrixUtils.createRealMatrix(a);
        RealMatrix rmai = MatrixUtils.blockInverse(rma, 0);
        double[] b = {scaledX, scaledY};
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
            xOrigin = ((-DGGS.NEW_ORIG_X) - xCoord) * (-1);
            yOrigin = ((-DGGS.NEW_ORIG_Y) - yCoord) * (-1);
        } else {
            xOrigin = (-DGGS.NEW_ORIG_X) - xCoord;
            yOrigin = ((-DGGS.NEW_ORIG_Y) + yCoord) * (-1);
        }

        // # Equation 17
        double Azprime = Math.atan2(xOrigin, yOrigin);

        // # Equation 18
        double rho = Math.sqrt((Math.pow(xOrigin, 2) + Math.pow(yOrigin, 2)));

        // # Adjust Azprime to fall within 0 to 120 degrees
        int Azprime_adjust_multiples = 0;

        // while Azprime < 0.0:
        while (Azprime < 0.0d) {
            Azprime += DGGS.DEG120;
            Azprime_adjust_multiples -= 1;
        }
        // while (Azprime > DEG120):
        while (Azprime > DGGS.DEG120) {
            Azprime -= DGGS.DEG120;
            Azprime_adjust_multiples += 1;
        }

        double AzprimeCopy = Azprime;

        // #Equation 19
        double AG = (Math.pow(R_PRIME, 2) * Math.pow(DGGS.TAN_LOWER_G, 2)) / (2 * ((1 / (Math.tan(Azprime))) + DGGS.COTTHETA));

        // # Iteration, Azprime (plane) converges to Az (ellipsoid)
        for (int i = 0; i <= 4; i++) {
            double H = Math.acos((Math.sin(Azprime) * DGGS.SIN_UPPER_G * DGGS.COS_LOWER_G) - (Math.cos(Azprime) * DGGS.COS_UPPER_G));
            double FAZ = (AG - DGGS.UPPER_G - H - Azprime + DGGS.DEG180);
            double FPRIMEAZ = (((Math.cos(Azprime) * DGGS.SIN_UPPER_G * DGGS.COS_LOWER_G) + (Math.sin(Azprime) * DGGS.COS_UPPER_G))
                    / (Math.sin(H))) - 1;
            double DeltaAzprime = -FAZ / (FPRIMEAZ);
            Azprime = Azprime + DeltaAzprime;
        }

        double Az = Azprime;

        // # Equations 9-11, 23 to obtain z
        double q = Math.atan((DGGS.TAN_LOWER_G) / (Math.cos(Az) + (Math.sin(Az) * DGGS.COTTHETA)));

        // # eq 10
        double dprime = ((R_PRIME * DGGS.TAN_LOWER_G) / (Math.cos(AzprimeCopy) + (Math.sin(AzprimeCopy) * DGGS.COTTHETA)));

        // # eq 11
        double f = dprime / (2.0 * R_PRIME * Math.sin(q / 2.0));

        // #eq 23, obtain z
        double z = 2 * Math.asin((rho) / (2 * R_PRIME * f));

        // # Add back 120 degree adjustments to Az
        Az += DGGS.DEG120 * Azprime_adjust_multiples;

        // # Adjust Az to be clockwise from north (needed for final calculation)
        if ((face >= 1 && face <= 5) || (face >= 11 && face <= 15)) {
            if (Az < 0) {
                Az = (DGGS.M_PI - (Az * (-1))) + DGGS.M_PI;
            }
        } else {
            if (Az < 0) {
                Az = DGGS.M_PI - (Az * (-1));
            } else {
                Az = Az + DGGS.M_PI;
            }
        }

        z = z * DGGS.R;

        // # triangle center
        DGGS.ISEA_Geo center = DGGS.ICOS_TRIANGLE_CENTER[face];
        DGGS.Pair<Double, Double> vdPair = DGGS.vincentyDirect(DGGS.FLATTENING, DGGS.R,
                center.getLatitude() * DGGS.RAD2DEG, center.getLongitude() * DGGS.RAD2DEG, Az * DGGS.RAD2DEG, z);

        double lat2 = vdPair.getKey();
        double lon2 = vdPair.getValue();

        Coordinate point = new Coordinate(lat2, lon2, height);
        return point;
    }
}
