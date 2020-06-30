package jp.go.aist.dggs.utils;

import jp.go.aist.dggs.common.DGGS;
import jp.go.aist.dggs.geometry.Morton3D;
import jp.go.aist.dggs.geometry.ISEA4DFaceCoordinates;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.giscience.utils.geogrid.geometry.FaceCoordinates;
import org.giscience.utils.geogrid.geometry.GeoCoordinates;
import org.giscience.utils.geogrid.projections.ISEAProjection;

import static jp.go.aist.dggs.common.DGGS.*;

/**
 * Utilizing methods for PD code (one of type of Morton code).
 * PD code is one of type of Morton code, especially DGGS Morton for the point cloud.
 * DGGS is discrete global grid system.
 *
 * @author TaehoonKim AIST DPRT, Research Assistant
 */

public class MortonUtils {
    /**
     * Searching a greatest common ancestor from given PD code list.
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
     * PD code (DGGS Morton for point cloud) encoding from 2-D (or 3-D) geodetic coordinates.
     *
     * @param geoCoordinates    Geodetic coordinate (WGS 84 2-D (EPSG:4326) or WGS 84 3-D (EPSG:4979))
     * @param resolution        Target resolution for PD code encoding
     * @return PD code (DGGS Morton for point cloud)
     */
    public static String toPDCode(GeoCoordinates geoCoordinates, int resolution) {
        ISEA4DFaceCoordinates faceCoordinates = toFaceCoordinate(geoCoordinates);

        assert faceCoordinates != null;
        return Morton3D.encode(faceCoordinates, resolution);
    }

    /**
     * Face coordinate encoding from 2-D (or 3-D) geodetic coordinates.
     *
     * @param geoCoordinates    Geodetic coordinate (WGS 84 2-D (EPSG:4326) or WGS 84 3-D (EPSG:4979))
     * @return ISEA4D face coordinates
     */
    public static ISEA4DFaceCoordinates toFaceCoordinate(GeoCoordinates geoCoordinates) {
        ISEAProjection p = new ISEAProjection();
        try {
            // # out contains coordinates from center of triangle
            FaceCoordinates f = p.sphereToIcosahedron(geoCoordinates);
            int face = f.getFace();

            // # Find new coordinates of point from lower left/upper left origin
            double newPointX;
            double newPointY;
            newPointX = f.getX() - NEW_ORIG_X;
            if ((face >= 0 && face <= 4) || (face >= 10 && face <= 14)) {
                newPointY = f.getY() - NEW_ORIG_Y;
            } else {
                newPointY = f.getY() + NEW_ORIG_Y;
            }

            // # Rotate the axes, round down to nearest integer since addressing begins at 0
            // # Scale coordinates of all dimensions to match resolution of DGGS
            double origX = ((newPointX - ((1 / (Math.sqrt(3))) * newPointY)) / (NEW_ORIG_X * (-2))) * TOTAL_RANGE;
            double origY = ((newPointX + ((1 / (Math.sqrt(3))) * newPointY)) / (NEW_ORIG_X * (-2))) * TOTAL_RANGE;
            double origZ = 0;
            if(geoCoordinates.getHeight() != null)
                origZ = ((H_RANGE + geoCoordinates.getHeight()) / (H_RANGE * 2.0d)) * TOTAL_RANGE_Z;
            if(origX < 0 || origY < 0 || origZ < 0 || origX > TOTAL_RANGE || origY > TOTAL_RANGE || origZ > TOTAL_RANGE_Z)
                throw new IllegalArgumentException("new Point X (or Y) is not on the rhombus: X = " + origX + " || Y = " + origY  + " || Z = " + origZ);

            long intX = Double.valueOf(origX).longValue();
            long intY = Double.valueOf(origY).longValue();
            long intZ = Double.valueOf(origZ).longValue();

            // # Convert triangle face number to rhombus face number
            if (face == 0 || face == 5) {
                face = 0;
            } else if (face == 10 || face == 15) {
                face = 1;
            } else if (face == 1 || face == 6) {
                face = 2;
            } else if (face == 11 || face == 16) {
                face = 3;
            } else if (face == 2 || face == 7) {
                face = 4;
            } else if (face == 12 || face == 17) {
                face = 5;
            } else if (face == 3 || face == 8) {
                face = 6;
            } else if (face == 13 || face == 18) {
                face = 7;
            } else if (face == 4 || face == 9) {
                face = 8;
            } else {
                face = 9;
            }

            return new ISEA4DFaceCoordinates(face, intX, intY, intZ);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // TODO
    }

    /**
     * Get face coordinates according to a given resolution.
     * This function will work when target resolution is less than given ISEA4D face coordinates' resolution.
     *
     * @param faceCoordinates   Given ISEA4D face coordinates
     * @param resolution        Target resolution
     * @return ISEA4D face coordinates
     */
    public static ISEA4DFaceCoordinates toFaceCoordinate(ISEA4DFaceCoordinates faceCoordinates, int resolution) {
        if(faceCoordinates.getResolution() != resolution) {
            final double TOTAL_RANGE_Z_BY_RES = (resolution < (DGGS.MAX_XY_RESOLUTION - DGGS.MAX_Z_RESOLUTION) ? 0 : Math.pow(2, resolution - (DGGS.MAX_XY_RESOLUTION - DGGS.MAX_Z_RESOLUTION)));
            int face = faceCoordinates.getFace();
            long x = faceCoordinates.getX();
            long y = faceCoordinates.getY();
            long z = faceCoordinates.getZ();

            if(faceCoordinates.getResolution() > resolution) {
                x = (long) (faceCoordinates.getX() / faceCoordinates.getMaxX() * Math.pow(2, resolution));
                y = (long) (faceCoordinates.getY() / faceCoordinates.getMaxY() * Math.pow(2, resolution));
                z = (long) (faceCoordinates.getZ() / faceCoordinates.getMaxZ() * TOTAL_RANGE_Z_BY_RES);
            }
            else {
                // TODO
            }

            return new ISEA4DFaceCoordinates(face, x, y, z, resolution);
        }
        else {
            return faceCoordinates;
        }
    }

    /**
     * PD code (Point cloud DGGS code, DGGS Morton for point cloud) decoding to 3-dimensional coordinates (WGS 84 3D, EPSG:4979)
     *
     * @param pdCode     PD code, DGGS Morton for point cloud
     * @param resolution Target resolution for PD code decoding
     * @return 3-dimensional geodetic coordinate (WGS 84 3D, EPSG:4979)
     */
    public static GeoCoordinates toGeoCoordinate(String pdCode, int resolution) {
        // # Compute the X, Y, and Z values on rhombus and face index
        ISEA4DFaceCoordinates coordinateOnRhombus = Morton3D.decode(pdCode, resolution);

        return toGeoCoordinate(coordinateOnRhombus);
    }

    /**
     * PD code (Point cloud DGGS code, DGGS Morton for point cloud) decoding to 3-dimensional coordinates (WGS 84 3D, EPSG:4979)
     * Target resolution is MAX_RESOLUTION (=32)
     *
     * @param pdCode    DGGS Morton for point cloud
     * @return 3-dimensional geodetic coordinate (WGS 84 3D, EPSG:4979)
     */
    public static GeoCoordinates toGeoCoordinate(String pdCode) {
        return toGeoCoordinate(pdCode, MAX_XY_RESOLUTION);
    }


    /**
     * ISEA4D face coordinates decoding to 3-dimensional coordinates (WGS 84 3D, EPSG:4979)
     * This function uses a given ISEA4D face coordinates' resolution.
     *
     * @param faceCoordinates ISEA4D face coordinates
     * @return 3-dimensional geodetic coordinate (WGS 84 3D, EPSG:4979)
     */
    public static GeoCoordinates toGeoCoordinate(ISEA4DFaceCoordinates faceCoordinates) {
        double x = faceCoordinates.getX();
        double y = faceCoordinates.getY();
        double z = faceCoordinates.getZ();
        int face = faceCoordinates.getFace();

        // # Convert h/Z to height above/below ellipsoid
        double height = faceCoordinates.getMaxZ() <= 1 ? 0 : (z * 2.0d * H_RANGE) / faceCoordinates.getMaxZ() - H_RANGE;
        // # Scale coordinates to scale of Cartesian system
        double scaledX = (x / faceCoordinates.getMaxX()) * (NEW_ORIG_X * -2);
        double scaledY = (y / faceCoordinates.getMaxY()) * (NEW_ORIG_X * -2);
        double[] b = {scaledX, scaledY};
        RealMatrix rmb = MatrixUtils.createColumnRealMatrix(b); // MatrixUtils.createColumnRealMatrix -- a columnData x 1 FieldMatrix
        RealMatrix rmx = MATRIX_A_INVERSE.multiply(rmb); // MatrixUtils.createRowRealMatrix -- a 1 x rowData.length RealMatrix
        double xCoord = rmx.getData()[0][0];
        double yCoord = rmx.getData()[1][0];

        // # Get triangle face from rhombus face based on values of y.
        // # If y is negative, triangles will be downward oriented
        if (yCoord >= 0) {
            if (face == 0) {
                face = 0;
            } else if (face == 1) {
                face = 10;
            } else if (face == 2) {
                face = 1;
            } else if (face == 3) {
                face = 11;
            } else if (face == 4) {
                face = 2;
            } else if (face == 5) {
                face = 12;
            } else if (face == 6) {
                face = 3;
            } else if (face == 7) {
                face = 13;
            } else if (face == 8) {
                face = 4;
            } else if (face == 9) {
                face = 14;
            }
        } else {
            if (face == 0) {
                face = 5;
            } else if (face == 1) {
                face = 15;
            } else if (face == 2) {
                face = 6;
            } else if (face == 3) {
                face = 16;
            } else if (face == 4) {
                face = 7;
            } else if (face == 5) {
                face = 17;
            } else if (face == 6) {
                face = 8;
            } else if (face == 7) {
                face = 18;
            } else if (face == 8) {
                face = 9;
            } else if (face == 9) {
                face = 19;
            }
        }

        // # Translate coordinates to center (origin) of icosahedron triangle,
        // # taking into account triangle orientation
        double xOrigin;
        double yOrigin;

        xOrigin = xCoord + NEW_ORIG_X;
        if ((face >= 0 && face <= 4) || (face >= 10 && face <= 14)) {
            yOrigin = yCoord + NEW_ORIG_Y;
        } else {
            yOrigin = yCoord - NEW_ORIG_Y;
        }

        try {
            ISEAProjection p = new ISEAProjection();
            p.setOrientation(0,0);
            GeoCoordinates geodeticCoord = p.icosahedronToSphere(new FaceCoordinates(face, xOrigin, yOrigin));

            return new GeoCoordinates(geodeticCoord.getLat(), geodeticCoord.getLon(), height);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // TODO
    }

    /**
     * ISEA4D face coordinates decoding to 3-dimensional coordinates (WGS 84 3D, EPSG:4979)
     * This function uses a given target resolution.
     *
     * @param faceCoordinates ISEA4D face coordinates
     * @param resolution      Target resolution for face coordinates decoding
     * @return 3-dimensional geodetic coordinate (WGS 84 3D, EPSG:4979)
     */
    public static GeoCoordinates toGeoCoordinate(ISEA4DFaceCoordinates faceCoordinates, int resolution) {
        if(faceCoordinates.getResolution() != resolution) {
            return toGeoCoordinate(toFaceCoordinate(faceCoordinates, resolution));
        }
        return toGeoCoordinate(faceCoordinates);
    }
}
