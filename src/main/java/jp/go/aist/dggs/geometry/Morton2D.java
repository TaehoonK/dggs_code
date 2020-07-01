package jp.go.aist.dggs.geometry;

import static jp.go.aist.dggs.common.DGGS.*;

public class Morton2D {
    final static int DIMENSION = 2;

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

        long mCode;
        StringBuilder pdCode = new StringBuilder(String.valueOf(faceCoordinates.getFace()));

        for (int i = 4; i > 0; --i) {
            StringBuilder sb = new StringBuilder();
            int shift = (i - 1) * 8; // A unit of bits of the bucket is 8-bits
            mCode = 0;
            mCode = mCode << 16
                    | (Morton2DTable256Encode[(int) ((y >> shift) & EIGHT_BIT_MASK)] << 1)
                    | (Morton2DTable256Encode[(int) ((x >> shift) & EIGHT_BIT_MASK)]);

            if (mCode != 0) {
                for(int j = 2; j > 0; --j) {
                    shift = (j - 1) * UNIT_SIZE_2D * DIMENSION;
                    sb.append(PDCode2DTable256Encode[(int) ((mCode >> shift) & EIGHT_BIT_MASK)]);
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

        long[] coordinates = new long[2];
        final int LOOP_COUNT = morton.length() / UNIT_SIZE_2D;
        for (int i = 0; i < LOOP_COUNT; ++i) {
            int index = PDCode2DTable256Decode.get(morton.substring(
                    (LOOP_COUNT - (i + 1)) * UNIT_SIZE_2D,
                    (LOOP_COUNT - i) * UNIT_SIZE_2D));
            coordinates[0] |= Morton2DTable256Decode[index] << (UNIT_SIZE_2D * i);
            coordinates[1] |= Morton2DTable256Decode[index >> 1] << (UNIT_SIZE_2D * i);
        }

        if (morton.length() % UNIT_SIZE_2D != 0) {
            int remainStringSize = morton.length() % UNIT_SIZE_2D;
            String remainString = morton.substring(morton.length() - remainStringSize);
            sb = new StringBuilder(remainString);
            for (int i = 0; i < UNIT_SIZE_2D - remainStringSize; i++) {
                sb.insert(0, '0');
            }
            String remainPDCode = sb.toString();
            int index = PDCode2DTable256Decode.get(remainPDCode);
            coordinates[0] = coordinates[0] << remainStringSize | Morton2DTable256Decode[index];
            coordinates[1] = coordinates[1] << remainStringSize | Morton2DTable256Decode[index >> 1];
        }

        return new ISEA4DFaceCoordinates(face, coordinates[0], coordinates[1], resolution);
    }
}
