package jp.go.aist.dggs.geometry;

import jp.go.aist.dggs.query.ISEA4DCellBoundary;
import jp.go.aist.dggs.utils.MortonUtils;
import org.giscience.utils.geogrid.geometry.GeoCoordinates;

import static jp.go.aist.dggs.common.DGGS.*;
/**
 * Handling PD code (one of type of Morton code) for 3-dimensional DGGS (Discrete Global Grid Systems) cell index
 * reference: Kim, Taehoon, et al. "Efficient Encoding and Decoding Extended Geocodes for Massive Point Cloud Data."
 *                          2019 IEEE International Conference on Big Data and Smart Computing (BigComp). IEEE, 2019.
 *
 * @author TaehoonKim AIST DPRT, Research Assistant
 */
public class Morton3D {
    final static int DIMENSION = 3;

    /**
     * PD code (Point cloud DGGS code, DGGS Morton for point cloud) encoding.
     *
     * @param faceCoordinates   ISEA4D face coordinate
     * @return PD code: Point cloud DGGS code, DGGS Morton for point cloud
     */
    public static String encode(ISEA4DFaceCoordinates faceCoordinates) {
        return encode(faceCoordinates, faceCoordinates.getResolution());
    }

    /**
     * PD code (Point cloud DGGS code, DGGS Morton for point cloud) encoding.
     *
     * @param faceCoordinates   ISEA4D face coordinate
     * @param resolution        Target resolution of PD code
     * @return PD code: Point cloud DGGS code, DGGS Morton for point cloud
     */
    public static String encode(ISEA4DFaceCoordinates faceCoordinates, int resolution) {
        long x = faceCoordinates.getX();
        long y = faceCoordinates.getY();
        long z = faceCoordinates.getZ();
        long mCode;
        StringBuilder pdCode = new StringBuilder(String.valueOf(faceCoordinates.getFace()));

        for (int i = 4; i > 0; --i) {
            StringBuilder sb = new StringBuilder();
            int shift = (i - 1) * 8; // A unit of bits of the bucket is 8-bits
            mCode = 0;
            mCode = mCode   // handling a 24-bits(3 dimension x 8 bits) bucket at once
                    | (Morton3DTable256Encode[(int) ((z >> shift) & EIGHT_BIT_MASK)] << 2)
                    | (Morton3DTable256Encode[(int) ((y >> shift) & EIGHT_BIT_MASK)] << 1)
                    | (Morton3DTable256Encode[(int) ((x >> shift) & EIGHT_BIT_MASK)]);

            if (mCode != 0) {
                boolean isSixBits = true;
                for (int j = 3; j > 0; --j) {
                    shift = (j - 1) * UNIT_SIZE_3D * DIMENSION;
                    String partialPDCode = PDCode3DTable512Encode[(int) ((mCode >> shift) & NINE_BIT_MASK)];
                    if (isSixBits) {
                        // Bucket size is 24-bits, it can be distinguished to 6-bits, 9-bits, 9-bits.
                        // So, the first one should be 6-bits.
                        partialPDCode = partialPDCode.substring(1);
                        isSixBits = false;
                    }
                    sb.append(partialPDCode);
                }
                pdCode.append(sb.toString());
            } else {  // mCode is 0
                pdCode.append("00000000");
            }

            if (pdCode.length() > resolution + 1)
                break;
        }

        pdCode = new StringBuilder(pdCode.length() > resolution + 1 ? pdCode.substring(0, resolution + 1) : pdCode.toString());

        return pdCode.toString();
    }

    /**
     * Convert PD code to face coordinate.
     *
     * @param pdCode    Point cloud DGGS code, DGGS Morton for point cloud
     * @return 3-dimensional coordinate on ISEA projection face
     */
    public static ISEA4DFaceCoordinates decode(String pdCode) {
        return decode(pdCode, MAX_XY_RESOLUTION);
    }

    /**
     * Convert PD code to face coordinate.
     *
     * @param pdCode        Point cloud DGGS code, DGGS Morton for point cloud
     * @param resolution    Target resolution of PD code
     * @return 3-dimensional coordinate on ISEA projection face
     */
    public static ISEA4DFaceCoordinates decode(String pdCode, int resolution) {
        int face = Integer.parseInt(pdCode.substring(0, 1));
        String morton = pdCode.substring(1);

        int extraSize = resolution - morton.length();
        StringBuilder sb = new StringBuilder(morton);
        if(extraSize > 0) {
            for (int i = 0; i < extraSize; i++) {
                sb.append('0');
            }
            morton = sb.toString();
        }
        else {
            morton = sb.substring(0, resolution);
        }

        long[] coordinates = new long[3];
        final int LOOP_COUNT = morton.length() / UNIT_SIZE_3D;
        for (int i = 0; i < LOOP_COUNT; ++i) {
            int index = PDCode3DTable512Decode.get(morton.substring(
                    (LOOP_COUNT - (i + 1)) * UNIT_SIZE_3D,
                    (LOOP_COUNT - i) * UNIT_SIZE_3D));
            coordinates[0] |= Morton3DTable512Decode[index] << (UNIT_SIZE_3D * i);
            coordinates[1] |= Morton3DTable512Decode[index >> 1] << (UNIT_SIZE_3D * i);
            coordinates[2] |= Morton3DTable512Decode[index >> 2] << (UNIT_SIZE_3D * i);
        }

        if (morton.length() % UNIT_SIZE_3D != 0) {
            int remainStringSize = morton.length() % UNIT_SIZE_3D;
            String remainString = morton.substring(morton.length() - remainStringSize);
            sb = new StringBuilder(remainString);
            for (int i = 0; i < UNIT_SIZE_3D - remainStringSize; i++) {
                sb.insert(0, '0');
            }
            String remainPDCode = sb.toString();
            int index = PDCode3DTable512Decode.get(remainPDCode);
            coordinates[0] = coordinates[0] << remainStringSize | Morton3DTable512Decode[index];
            coordinates[1] = coordinates[1] << remainStringSize | Morton3DTable512Decode[index >> 1];
            coordinates[2] = coordinates[2] << remainStringSize | Morton3DTable512Decode[index >> 2];
        }

        return new ISEA4DFaceCoordinates(face, coordinates[0], coordinates[1], coordinates[2], resolution);
    }

    /**
     * Get a center geodetic coordinates from the given PD code
     *
     * @param pdCode Target PD code for getting center geodetic coordinates
     * @return A geodetic coordinates
     * */
    public static GeoCoordinates getCenter(String pdCode) {
        return MortonUtils.toGeoCoordinate(pdCode);
    }

    /**
     * Get a center geodetic coordinate from the given face coordinates
     *
     * @param faceCoordinates Target face coordinates for getting center geodetic coordinates
     * @return A geodetic coordinates
     * */
    public static GeoCoordinates getCenter(ISEA4DFaceCoordinates faceCoordinates) {
        return MortonUtils.toGeoCoordinate(faceCoordinates);
    }

    /**
     * Calculate cell boundary coordinate for given input pdCode.
     *
     * @param pdCode Target PD code for getting boundary
     * @return ISEA4DCellBoundary instance
     * */
    public static ISEA4DCellBoundary getBoundary(String pdCode) {
        ISEA4DFaceCoordinates faceCoordinates = decode(pdCode);

        return getBoundary(faceCoordinates);
    }

    /**
     * Calculate cell boundary coordinate for given input pdCode.
     *
     * @param faceCoordinates Target ISEA4D face coordinate for getting boundary
     * @return ISEA4DCellBoundary instance
     * */
    public static ISEA4DCellBoundary getBoundary(ISEA4DFaceCoordinates faceCoordinates) {
        // TODO: extend to 3D
        return Morton2D.getBoundary(faceCoordinates);
    }
}