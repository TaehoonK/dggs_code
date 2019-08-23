package jp.go.aist.dggs;

import static jp.go.aist.dggs.DGGS.*;
/**
 * Handling PD code (one of type of Morton code) for 3-dimensional DGGS (Discrete Global Grid Systems) cell index
 * reference: Kim, Taehoon, et al. "Efficient Encoding and Decoding Extended Geocodes for Massive Point Cloud Data."
 *                          2019 IEEE International Conference on Big Data and Smart Computing (BigComp). IEEE, 2019.
 *
 * @author TaehoonKim AIST DPRT, Research Assistant
 */
public class Morton3D {
    /**
     * PD code (Point cloud DGGS code, DGGS Morton for point cloud) encoding with Lookup Table
     *
     * @param x          range is from 0 to 4,294,967,295 (2^32 - 1)
     * @param y          range is from 0 to 4,294,967,295 (2^32 - 1)
     * @param z          range is from 0 to 16,777,215 (2^24 - 1)
     * @param face       Index of rhombuses
     * @param resolution Resolution of generate PD code
     * @return PD code
     */
    public static String encode(long x, long y, long z, int face, int resolution) {
        String pdCode = encode(x, y, z, resolution);
        return face + pdCode;
    }

    /**
     * PD code (Point cloud DGGS code, DGGS Morton for point cloud) encoding with Lookup Table
     *
     * @param x          range is from 0 to 4,294,967,295 (2^32 - 1)
     * @param y          range is from 0 to 4,294,967,295 (2^32 - 1)
     * @param z          range is from 0 to 16,777,215 (2^24 - 1)
     * @param resolution Resolution of generate PD code
     * @return PD code
     */
    private static String encode(long x, long y, long z, int resolution) {
        long mCode;
        StringBuilder pdCode = new StringBuilder();

        for (int i = 4; i > 0; --i) {
            StringBuilder sb = new StringBuilder();
            int shift = (i - 1) * 8; // A unit of bits of the bucket is 8-bits
            mCode = 0;
            mCode = mCode   // handling a 24-bits(3 dimension x 8 bits) bucket at once
                    | (MortonTable256Encode[(int) ((z >> shift) & EIGHT_BIT_MASK)] << 2)
                    | (MortonTable256Encode[(int) ((y >> shift) & EIGHT_BIT_MASK)] << 1)
                    | MortonTable256Encode[(int) ((x >> shift) & EIGHT_BIT_MASK)];

            if (mCode != 0) {
                boolean isSixBits = true;
                for (int j = 3; j > 0; --j) {
                    shift = (j - 1) * 9; // UNIT_SIZE * DIMENSION is 9
                    String partialPDCode = PDCodeTable512Encode[(int) ((mCode >> shift) & NINE_BIT_MASK)];
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

            if (pdCode.length() > resolution)
                break;
        }

        pdCode = new StringBuilder(pdCode.length() > resolution ? pdCode.substring(0, resolution) : pdCode.toString());

        return pdCode.toString();
    }

    /**
     * Convert PD code to coordinate using bit operation
     *
     * @param pdCode Point cloud DGGS code, DGGS Morton for point cloud, except rhombus face number
     * @return 3-dimensional coordinate within a rhombus cell, form as array[x,y,z]
     */
    public static long[] decode(String pdCode) {
        return decode(pdCode, MAX_XY_RESOLUTION);
    }

    /**
     * Convert PD code to coordinate using bit operation
     *
     * @param pdCode Point cloud DGGS code, DGGS Morton for point cloud, except rhombus face number
     * @param resolution Target resolution of PD code
     * @return 3-dimensional coordinate within a rhombus cell, form as array[x,y,z]
     */
    public static long[] decode(String pdCode, int resolution) {
        long[] coordinates = new long[3];

        int extraSize = resolution - pdCode.length();
        StringBuilder sb = new StringBuilder(pdCode);
        if(extraSize > 0) {
            for (int i = 0; i < extraSize; i++) {
                sb.append('0');
            }
            pdCode = sb.toString();
        }
        else {
            pdCode = sb.substring(0, resolution);
        }

        final int LOOP_COUNT = pdCode.length() / UNIT_SIZE;
        for (int i = 0; i < LOOP_COUNT; ++i) {
            int index = PDCodeTable512Decode.get(pdCode.substring(
                    (LOOP_COUNT - (i + 1)) * UNIT_SIZE,
                    (LOOP_COUNT - i) * UNIT_SIZE));
            coordinates[0] |= MortonTable512Decode[index] << (UNIT_SIZE * i);
            coordinates[1] |= MortonTable512Decode[index >> 1] << (UNIT_SIZE * i);
            coordinates[2] |= MortonTable512Decode[index >> 2] << (UNIT_SIZE * i);
        }

        if (pdCode.length() % UNIT_SIZE != 0) {
            int remainStringSize = pdCode.length() % UNIT_SIZE;
            String remainString = pdCode.substring(pdCode.length() - remainStringSize);
            sb = new StringBuilder(remainString);
            for (int i = 0; i < UNIT_SIZE - remainStringSize; i++) {
                sb.insert(0, '0');
            }
            String remainPDCode = sb.toString();
            int index = PDCodeTable512Decode.get(remainPDCode);
            coordinates[0] = coordinates[0] << remainStringSize | MortonTable512Decode[index];
            coordinates[1] = coordinates[1] << remainStringSize | MortonTable512Decode[index >> 1];
            coordinates[2] = coordinates[2] << remainStringSize | MortonTable512Decode[index >> 2];
        }

        return coordinates;
    }
}
