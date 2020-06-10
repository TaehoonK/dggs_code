package jp.go.aist.dggs;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.giscience.utils.geogrid.geometry.FaceCoordinates;
import org.giscience.utils.geogrid.geometry.GeoCoordinates;
import org.giscience.utils.geogrid.projections.ISEAProjection;
import org.locationtech.jts.geom.Coordinate;
import scala.Tuple4;

import java.util.Objects;

import static jp.go.aist.dggs.DGGS.*;

/**
 * Handling PD code (one of type of Morton code) for 3-dimensional coordinates (WGS 84 3D, EPSG 4979)
 * reference: Sirdeshmukh, Neeraj, et al. "Utilizing a discrete global grid system for handling point clouds with varying locations, times, and levels of detail."
 *            Cartographica: The International Journal for Geographic Information and Geovisualization 54.1 (2019): 4-15.
 *
 * @author TaehoonKim AIST DPRT, Research Assistant
 */

public class MortonUtils {
    /**
     * Searching a greatest common ancestor from given PD code list
     * PD code is one of type of Morton code, especially DGGS Morton for the point cloud
     * DGGS is discrete global grid system
     *
     * @param pdCodes List of PD code
     * @return A greatest common ancestor of PD codes
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

        boolean isSame = true;
        for (int i = 1; i < leastCodeLength; i++) { // start from 1, for exclude face information (resolution 0)
            String baseMCode = pdCodes[0];
            for (int j = 1; j < pdCodes.length; j++) { // start from 1, for exclude baseMCode
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

        if (isSame) {
            result = pdCodes[0].substring(0, leastCodeLength);
        }

        return result;
    }

    /**
     * PD code (Point cloud DGGS code, DGGS Morton for point cloud) encoding from 3-dimensional coordinates (WGS 84 3D, EPSG 4979)
     *
     * @param latitude latitude from WGS84 (UoM: degree)
     * @param longitude longitude from WGS84 (UoM: degree)
     * @param height ellipsoidal height (UoM: meter)
     * @param resolution Resolution of generate PD code
     * @return PD code (Point cloud DGGS code, DGGS Morton for point cloud)
     */
    public static String convertToMorton(double latitude, double longitude, double height, int resolution) {
        Tuple4<Long,Long,Long,Integer> t = null;
        try {
            t = convertLatLong3DToMorton(latitude, longitude, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Morton3D.encode(t._1(), t._2(), t._3(), t._4(), resolution);
    }

    public static Tuple4<Long,Long,Long,Integer> convertLatLong3DToMorton(double latitude, double longitude, double height) {
        ISEAProjection p = new ISEAProjection();
        p.setOrientation(0,0);
        GeoCoordinates point = null;
        try {
            point = new GeoCoordinates(latitude,longitude);

            // # out contains coordinates from center of triangle
            FaceCoordinates f = p.sphereToIcosahedron(point);
            int face = f.getFace() + 1;

            // # Find new coordinates of point from lower left/upper left origin
            double newPointX;
            double newPointY;
            if ((face >= 1 && face <= 5) || (face >= 11 && face <= 15)) {
                newPointX = f.getX() - NEW_ORIG_X;
                newPointY = f.getY() - NEW_ORIG_Y;
            } else {
                newPointX = (f.getX() + NEW_ORIG_X) * (-1);
                newPointY = (f.getY() - NEW_ORIG_Y) * (-1);
            }

            // # Rotate the axes, round down to nearest integer since addressing begins at 0
            // # Scale coordinates of all dimensions to match resolution of DGGS
            double origX = ((newPointX - ((1 / (Math.sqrt(3))) * newPointY)) / (NEW_ORIG_X * (-2))) * TOTAL_RANGE;
            double origY = ((newPointX + ((1 / (Math.sqrt(3))) * newPointY)) / (NEW_ORIG_Y * (-2))) * TOTAL_RANGE;
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

            return new Tuple4<Long,Long,Long,Integer>(intX, intY, intZ, face);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // TODO
    }

    /**
     * PD code (Point cloud DGGS code, DGGS Morton for point cloud) decoding to 3-dimensional coordinates (WGS 84 3D, EPSG 4979)
     *
     * @param pdCode Point cloud DGGS code, DGGS Morton for point cloud, except rhombus face number
     * @param resolution Target resolution of PD code
     * @return 3-dimensional coordinate (WGS 84 3D, EPSG 4979), form as Coordinate[x=latitude,y=longitude,z=height]
     */
    public static Coordinate convertFromMorton(String pdCode, int resolution) {
        // # Get face number and Morton code
        // # First number indicates rhombus face!
        int face = Integer.parseInt(pdCode.substring(0, 1));
        String morton = pdCode.substring(1);

        // # Compute the X, Y, and Z values on rhombus
        long[] coordinateOnRhombus = Morton3D.decode(morton, resolution);

        return convertMortonToLatLong3D(coordinateOnRhombus[0], coordinateOnRhombus[1], coordinateOnRhombus[2], face, resolution);
    }

    /**
     * PD code (Point cloud DGGS code, DGGS Morton for point cloud) decoding to 3-dimensional coordinates (WGS 84 3D, EPSG 4979)
     * Target resolution is MAX_RESOLUTION (=32)
     * @param pdCode Point cloud DGGS code, DGGS Morton for point cloud, except rhombus face number
     * @return 3-dimensional coordinate (WGS 84 3D, EPSG 4979), form as Coordinate[x=latitude,y=longitude,z=height]
     */
    public static Coordinate convertFromMorton(String pdCode) {
        return convertFromMorton(pdCode, MAX_XY_RESOLUTION);
    }

    public static Coordinate convertMortonToLatLong3D(
            long x, long y, long h, int face, int resolution) {
        final double TOTAL_RANGE = Math.pow(2, resolution);
        final double TOTAL_RANGE_Z = resolution < (MAX_XY_RESOLUTION - MAX_Z_RESOLUTION) ?
                0 : Math.pow(2, (resolution - (MAX_XY_RESOLUTION - MAX_Z_RESOLUTION)));
        // # Convert h/Z to height above/below ellipsoid
        double height = TOTAL_RANGE_Z == 0 ? 0 : (h * 2 * H_RANGE) / TOTAL_RANGE_Z - H_RANGE;
        // # Scale coordinates to scale of Cartesian system
        double scaledX = (x / TOTAL_RANGE) * (NEW_ORIG_X * -2);
        double scaledY = (y / TOTAL_RANGE) * (NEW_ORIG_Y * -2);
        // # Convert coordinates from skewed system to Cartesian system (origin at left)
        double[][] a = {{1, (-1 / Math.sqrt(3))}, {1, (1 / Math.sqrt(3))}};
        RealMatrix rma = MatrixUtils.createRealMatrix(a);
        RealMatrix rmai = MatrixUtils.blockInverse(rma, 0);
        double[] b = {scaledX, scaledY};
        // MatrixUtils.createColumnRealMatrix -- a columnData x 1 FieldMatrix
        RealMatrix rmb = MatrixUtils.createColumnRealMatrix(b);
        // MatrixUtils.createRowRealMatrix -- a 1 x rowData.length RealMatrix
        RealMatrix rmx = rmai.multiply(rmb);
        double xCoord = rmx.getData()[0][0];
        double yCoord = rmx.getData()[1][0];

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
        double xOrigin;
        double yOrigin;

        if ((face >= 1 && face <= 5) || (face >= 11 && face <= 15)) {
            xOrigin = xCoord + NEW_ORIG_X;
            yOrigin = yCoord + NEW_ORIG_Y;
        } else {
            xOrigin = (xCoord + NEW_ORIG_X) * (-1);
            yOrigin = -yCoord + NEW_ORIG_Y;
        }

        try {
            ISEAProjection p = new ISEAProjection();
            p.setOrientation(0,0);
            GeoCoordinates geodeticCoord = p.icosahedronToSphere(new FaceCoordinates(face - 1, xOrigin, yOrigin));

            return new Coordinate(geodeticCoord.getLat(), geodeticCoord.getLon(), height);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // TODO
    }
}
