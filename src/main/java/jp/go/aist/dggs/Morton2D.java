package jp.go.aist.dggs;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author TaehoonKim AIST DPRT, Research Assistant
 * */
public class Morton2D {

    private static final long EIGHT_BIT_MASK = 0x000000FF;
    private static final int UNIT_SIZE = 4;
    private static final int DIMENSION = 2;
    private static final int LOOP_COUNT = Long.SIZE / (UNIT_SIZE * DIMENSION);
    private static final int MAX_RESOLUTION = Long.SIZE / DIMENSION;

    private static final long[] MortonTable256
            = {
            0x0000, 0x0001, 0x0004, 0x0005, 0x0010, 0x0011, 0x0014, 0x0015,
            0x0040, 0x0041, 0x0044, 0x0045, 0x0050, 0x0051, 0x0054, 0x0055,
            0x0100, 0x0101, 0x0104, 0x0105, 0x0110, 0x0111, 0x0114, 0x0115,
            0x0140, 0x0141, 0x0144, 0x0145, 0x0150, 0x0151, 0x0154, 0x0155,
            0x0400, 0x0401, 0x0404, 0x0405, 0x0410, 0x0411, 0x0414, 0x0415,
            0x0440, 0x0441, 0x0444, 0x0445, 0x0450, 0x0451, 0x0454, 0x0455,
            0x0500, 0x0501, 0x0504, 0x0505, 0x0510, 0x0511, 0x0514, 0x0515,
            0x0540, 0x0541, 0x0544, 0x0545, 0x0550, 0x0551, 0x0554, 0x0555,
            0x1000, 0x1001, 0x1004, 0x1005, 0x1010, 0x1011, 0x1014, 0x1015,
            0x1040, 0x1041, 0x1044, 0x1045, 0x1050, 0x1051, 0x1054, 0x1055,
            0x1100, 0x1101, 0x1104, 0x1105, 0x1110, 0x1111, 0x1114, 0x1115,
            0x1140, 0x1141, 0x1144, 0x1145, 0x1150, 0x1151, 0x1154, 0x1155,
            0x1400, 0x1401, 0x1404, 0x1405, 0x1410, 0x1411, 0x1414, 0x1415,
            0x1440, 0x1441, 0x1444, 0x1445, 0x1450, 0x1451, 0x1454, 0x1455,
            0x1500, 0x1501, 0x1504, 0x1505, 0x1510, 0x1511, 0x1514, 0x1515,
            0x1540, 0x1541, 0x1544, 0x1545, 0x1550, 0x1551, 0x1554, 0x1555,
            0x4000, 0x4001, 0x4004, 0x4005, 0x4010, 0x4011, 0x4014, 0x4015,
            0x4040, 0x4041, 0x4044, 0x4045, 0x4050, 0x4051, 0x4054, 0x4055,
            0x4100, 0x4101, 0x4104, 0x4105, 0x4110, 0x4111, 0x4114, 0x4115,
            0x4140, 0x4141, 0x4144, 0x4145, 0x4150, 0x4151, 0x4154, 0x4155,
            0x4400, 0x4401, 0x4404, 0x4405, 0x4410, 0x4411, 0x4414, 0x4415,
            0x4440, 0x4441, 0x4444, 0x4445, 0x4450, 0x4451, 0x4454, 0x4455,
            0x4500, 0x4501, 0x4504, 0x4505, 0x4510, 0x4511, 0x4514, 0x4515,
            0x4540, 0x4541, 0x4544, 0x4545, 0x4550, 0x4551, 0x4554, 0x4555,
            0x5000, 0x5001, 0x5004, 0x5005, 0x5010, 0x5011, 0x5014, 0x5015,
            0x5040, 0x5041, 0x5044, 0x5045, 0x5050, 0x5051, 0x5054, 0x5055,
            0x5100, 0x5101, 0x5104, 0x5105, 0x5110, 0x5111, 0x5114, 0x5115,
            0x5140, 0x5141, 0x5144, 0x5145, 0x5150, 0x5151, 0x5154, 0x5155,
            0x5400, 0x5401, 0x5404, 0x5405, 0x5410, 0x5411, 0x5414, 0x5415,
            0x5440, 0x5441, 0x5444, 0x5445, 0x5450, 0x5451, 0x5454, 0x5455,
            0x5500, 0x5501, 0x5504, 0x5505, 0x5510, 0x5511, 0x5514, 0x5515,
            0x5540, 0x5541, 0x5544, 0x5545, 0x5550, 0x5551, 0x5554, 0x5555
    };

    private static final long[] MortonTable256DecodeX = {
            0, 1, 0, 1, 2, 3, 2, 3, 0, 1, 0, 1, 2, 3, 2, 3,
            4, 5, 4, 5, 6, 7, 6, 7, 4, 5, 4, 5, 6, 7, 6, 7,
            0, 1, 0, 1, 2, 3, 2, 3, 0, 1, 0, 1, 2, 3, 2, 3,
            4, 5, 4, 5, 6, 7, 6, 7, 4, 5, 4, 5, 6, 7, 6, 7,
            8, 9, 8, 9, 10, 11, 10, 11, 8, 9, 8, 9, 10, 11, 10, 11,
            12, 13, 12, 13, 14, 15, 14, 15, 12, 13, 12, 13, 14, 15, 14, 15,
            8, 9, 8, 9, 10, 11, 10, 11, 8, 9, 8, 9, 10, 11, 10, 11,
            12, 13, 12, 13, 14, 15, 14, 15, 12, 13, 12, 13, 14, 15, 14, 15,
            0, 1, 0, 1, 2, 3, 2, 3, 0, 1, 0, 1, 2, 3, 2, 3,
            4, 5, 4, 5, 6, 7, 6, 7, 4, 5, 4, 5, 6, 7, 6, 7,
            0, 1, 0, 1, 2, 3, 2, 3, 0, 1, 0, 1, 2, 3, 2, 3,
            4, 5, 4, 5, 6, 7, 6, 7, 4, 5, 4, 5, 6, 7, 6, 7,
            8, 9, 8, 9, 10, 11, 10, 11, 8, 9, 8, 9, 10, 11, 10, 11,
            12, 13, 12, 13, 14, 15, 14, 15, 12, 13, 12, 13, 14, 15, 14, 15,
            8, 9, 8, 9, 10, 11, 10, 11, 8, 9, 8, 9, 10, 11, 10, 11,
            12, 13, 12, 13, 14, 15, 14, 15, 12, 13, 12, 13, 14, 15, 14, 15
    };

    private static final long[] MortonTable256DecodeY = {
            0, 0, 1, 1, 0, 0, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3,
            0, 0, 1, 1, 0, 0, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3,
            4, 4, 5, 5, 4, 4, 5, 5, 6, 6, 7, 7, 6, 6, 7, 7,
            4, 4, 5, 5, 4, 4, 5, 5, 6, 6, 7, 7, 6, 6, 7, 7,
            0, 0, 1, 1, 0, 0, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3,
            0, 0, 1, 1, 0, 0, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3,
            4, 4, 5, 5, 4, 4, 5, 5, 6, 6, 7, 7, 6, 6, 7, 7,
            4, 4, 5, 5, 4, 4, 5, 5, 6, 6, 7, 7, 6, 6, 7, 7,
            8, 8, 9, 9, 8, 8, 9, 9, 10, 10, 11, 11, 10, 10, 11, 11,
            8, 8, 9, 9, 8, 8, 9, 9, 10, 10, 11, 11, 10, 10, 11, 11,
            12, 12, 13, 13, 12, 12, 13, 13, 14, 14, 15, 15, 14, 14, 15, 15,
            12, 12, 13, 13, 12, 12, 13, 13, 14, 14, 15, 15, 14, 14, 15, 15,
            8, 8, 9, 9, 8, 8, 9, 9, 10, 10, 11, 11, 10, 10, 11, 11,
            8, 8, 9, 9, 8, 8, 9, 9, 10, 10, 11, 11, 10, 10, 11, 11,
            12, 12, 13, 13, 12, 12, 13, 13, 14, 14, 15, 15, 14, 14, 15, 15,
            12, 12, 13, 13, 12, 12, 13, 13, 14, 14, 15, 15, 14, 14, 15, 15
    };

    private static final String[] PDCodeTable256 = {
            "0000","0001","0002","0003","0010","0011","0012","0013","0020","0021","0022","0023","0030","0031","0032","0033",
            "0100","0101","0102","0103","0110","0111","0112","0113","0120","0121","0122","0123","0130","0131","0132","0133",
            "0200","0201","0202","0203","0210","0211","0212","0213","0220","0221","0222","0223","0230","0231","0232","0233",
            "0300","0301","0302","0303","0310","0311","0312","0313","0320","0321","0322","0323","0330","0331","0332","0333",
            "1000","1001","1002","1003","1010","1011","1012","1013","1020","1021","1022","1023","1030","1031","1032","1033",
            "1100","1101","1102","1103","1110","1111","1112","1113","1120","1121","1122","1123","1130","1131","1132","1133",
            "1200","1201","1202","1203","1210","1211","1212","1213","1220","1221","1222","1223","1230","1231","1232","1233",
            "1300","1301","1302","1303","1310","1311","1312","1313","1320","1321","1322","1323","1330","1331","1332","1333",
            "2000","2001","2002","1003","2010","2011","2012","2013","2020","2021","2022","2023","2030","2031","2032","2033",
            "2100","2101","2102","1103","2110","2111","2112","2113","2120","2121","2122","2123","2130","2131","2132","2133",
            "2200","2201","2202","1203","2210","2211","2212","2213","2220","2221","2222","2223","2230","2231","2232","2233",
            "2300","2301","2302","1303","2310","2311","2312","2313","2320","2321","2322","2323","2330","2331","2332","2333",
            "3000","3001","3002","3003","3010","3011","3012","3013","3020","3021","3022","3023","3030","3031","3032","3033",
            "3100","3101","3102","3103","3110","3111","3112","3113","3120","3121","3122","3123","3130","3131","3132","3133",
            "3200","3201","3202","3203","3210","3211","3212","3213","3220","3221","3222","3223","3230","3231","3232","3233",
            "3300","3301","3302","3303","3310","3311","3312","3313","3320","3321","3322","3323","3330","3331","3332","3333"
    };

    private static final Map<String, Integer> PDCodeTableDecode256;
    static {
        PDCodeTableDecode256 = new HashMap<>();
        for(int i = 0; i < 256; i++) {
            PDCodeTableDecode256.put(PDCodeTable256[i],i);
        }
    }

    /**
     * Convert coordinates to Morton codes
     *
     * @param x x coordinate within an rhombus
     * @param y y coordinate within an rhombus
     * @param face Index of rhombuses
     * @param resolution Resolution of generate Morton code
     * @return Morton code based on DGGS for point cloud data
     */
    public static String encode(long x, long y, int face, int resolution) {
        String pdCode;
        String[] binaryList = new String[2];
        binaryList[0] = Long.toBinaryString(x);
        binaryList[1] = Long.toBinaryString(y);
        String[] fixedLengthBinaryList = MortonUtils.applyResolution(resolution, binaryList);

        int mY = 2;
        StringBuilder sb = new StringBuilder();
        sb.append(face);
        for(int i = 0; i < resolution; i++) {
            int tempX = Integer.valueOf(fixedLengthBinaryList[0].substring(i, i+1));
            int tempY = Integer.valueOf(fixedLengthBinaryList[1].substring(i, i+1)) * mY;

            int tempM = tempX + tempY;
            sb.append(tempM);
        }
        pdCode = sb.toString();

        return pdCode;
    }

    /**
     * Convert coordinates to Morton codes
     *
     * @param x x coordinate within an rhombus
     * @param y y coordinate within an rhombus
     * @param resolution Resolution of generate Morton code
     * @return Morton code based on DGGS for point cloud data
     */
    public static String encode(long x, long y, int resolution) {
        String pdCode;
        String[] binaryList = new String[2];
        binaryList[0] = Long.toBinaryString(x);
        binaryList[1] = Long.toBinaryString(y);
        String[] fixedLengthBinaryList = MortonUtils.applyResolution(resolution, binaryList);

        int mY = 2;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < resolution; i++) {
            int tempX = Integer.valueOf(fixedLengthBinaryList[0].substring(i, i+1));
            int tempY = Integer.valueOf(fixedLengthBinaryList[1].substring(i, i+1)) * mY;

            int tempM = tempX + tempY;
            sb.append(tempM);
        }
        pdCode = sb.toString();

        return pdCode;
    }

    /**
     * Convert Morton codes to coordinate
     *
     * @param pdCode Morton code based on DGGS for point cloud data
     * @return 2-dimension coordinate within an rhombus, array[x,y]
     */
    public static long[] decode(String pdCode) {
        long[] coordinates = new long[2];

        StringBuilder sbX = new StringBuilder();
        StringBuilder sbY = new StringBuilder();
        for(int i = 0; i < pdCode.length(); i++) {
            int tempM = Integer.valueOf(pdCode.substring(i, i+1));

            switch(tempM){
                case 0:
                    sbX.append(0);
                    sbY.append(0);
                    break;
                case 1:
                    sbX.append(1);
                    sbY.append(0);
                    break;
                case 2:
                    sbX.append(0);
                    sbY.append(1);
                    break;
                case 3:
                    sbX.append(1);
                    sbY.append(1);
                    break;
            }
        }
        coordinates[0] = Long.valueOf(sbX.toString(),2);
        coordinates[1] = Long.valueOf(sbY.toString(),2);

        return coordinates;
    }

    /**
     * Morton code encoding with Lookup Table
     *
     * @param x range is from 0 to ‭4,294,967,295‬ (2^32 - 1)
     * @param y range is from 0 to ‭4,294,967,295‬ (2^32 - 1)
     * @param face Index of rhombuses
     * @param resolution Resolution of generate Morton code
     * @return Morton code based on DGGS for point cloud data
     */
    public static String encodeBitOp(long x, long y, int face, int resolution) {
        long mCode = 0;
        for(int i = 4; i > 0; --i) {
            int shift = (i - 1) * 8;
            mCode = mCode << 16
                    | (MortonTable256[(int) ((y >> shift) & EIGHT_BIT_MASK)] << 1)
                    | MortonTable256[(int) ((x >> shift) & EIGHT_BIT_MASK)];
        }

        String pdCode = getPDCode(mCode);
        pdCode = pdCode.length() > resolution?pdCode.substring(0,resolution):MortonUtils.getFixedLengthCode(pdCode, resolution);
        return Integer.toString(face) + pdCode;
    }

    /**
     * Morton code encoding with Lookup Table
     *
     * @param x range is from 0 to ‭4,294,967,295‬ (2^32 - 1)
     * @param y range is from 0 to ‭4,294,967,295‬ (2^32 - 1)
     * @param resolution Resolution of generate Morton code
     * @return Morton code based on DGGS for point cloud data except rhombuses index
     */
    public static String encodeBitOp(long x, long y, int resolution) {
        long mCode = 0;
        for(int i = 4; i > 0; --i) {
            int shift = (i - 1) * 8;
            mCode = mCode << 16
                    | (MortonTable256[(int) ((y >> shift) & EIGHT_BIT_MASK)] << 1)
                    | MortonTable256[(int) ((x >> shift) & EIGHT_BIT_MASK)];
        }

        String pdCode = getPDCode(mCode);
        pdCode = pdCode.length() > resolution?pdCode.substring(0,resolution):MortonUtils.getFixedLengthCode(pdCode, resolution);
        return pdCode;
    }

    /**
     * Convert Morton codes to coordinate using bit operation
     *
     * @param pdCode Morton code based on DGGS for point cloud data except rhombuses index
     * @return	2-dimension coordinate within an rhombus, array[x,y]
     */
    public static long[] decodeBitOp(String pdCode) {
        long[] coordinates = new long[2];
        BigInteger mCode = getMCode(pdCode);
        coordinates[0] = decodeHelper(mCode, MortonTable256DecodeX);
        coordinates[1] = decodeHelper(mCode, MortonTable256DecodeY);

        return coordinates;
    }

    /**
     * Helper Method for LookUp Table based decoding
     *
     * @param mCode Morton code up to 64 bits
     * @param LUT LookUp Table for decoding Morton code
     *
     * @return decoded value
     */
    private static long decodeHelper(long mCode, final int LUT[]) {
        long coordinate = 0;
        for (int i = 0; i < LOOP_COUNT; ++i) {
            coordinate |= (LUT[(int) ((mCode >> (i * 8)) & EIGHT_BIT_MASK)] << (4 * i));
        }

        return coordinate;
    }

    /**
     * Helper Method for LookUp Table based decoding
     *
     * @param mCode Morton code
     * @param LUT LookUp Table for decoding Morton code
     *
     * @return decoded value
     */
    private static long decodeHelper(BigInteger mCode, final long LUT[]) {
        long coordinate = 0;
        final int LOOP_COUNT =  (int) Math.ceil((double)mCode.bitLength() / (UNIT_SIZE * DIMENSION));

        for (int i = 0; i < LOOP_COUNT; ++i) {
            coordinate |= LUT[mCode.shiftRight(i * 8).and(BigInteger.valueOf(EIGHT_BIT_MASK)).intValue()] << (4 * i);
        }

        return coordinate;
    }

    /**
     * Convert normal Morton code to PD-Morton code (Morton code based on DGGS for point cloud data)
     *
     * @param mCode Normal Morton code (made by bit interleaving)
     * @return Morton code based on DGGS for point cloud data (32 level of resolution). It doesn't have an index of face
     */
    public static String getPDCode(long mCode) {
        return MortonUtils.getPDCode(mCode, UNIT_SIZE, DIMENSION, EIGHT_BIT_MASK, PDCodeTable256);
    }

    /**
     * Convert normal Morton code to PD-Morton code (Morton code based on DGGS for point cloud data)
     *
     * @param mCode Normal Morton code (made by bit interleaving and BigInteger)
     * @return Morton code based on DGGS for point cloud data. It doesn't have an index of face
     */
    public static String getPDCode(BigInteger mCode) {
        return MortonUtils.getPDCode(mCode, UNIT_SIZE, DIMENSION, EIGHT_BIT_MASK, PDCodeTable256);
    }

    /**
     * Convert PD-Morton code (Morton code based on DGGS for point cloud data) to normal Morton code
     *
     * @param pdCode Morton code based on DGGS for point cloud data (up to 31 level of resolution). It doesn't have an index of face
     * @return Normal Morton code
     */
    public static long getMCode_old(String pdCode) throws IllegalArgumentException {
        return MortonUtils.getMCode_old(pdCode, MAX_RESOLUTION, UNIT_SIZE, DIMENSION, PDCodeTableDecode256);
    }

    /**
     * Convert PD-Morton code (Morton code based on DGGS for point cloud data) to normal Morton code
     *
     * @param pdCode Morton code based on DGGS for point cloud data (up to 31 level of resolution). It doesn't have an index of face
     * @return Normal Morton code
     */
    public static BigInteger getMCode(String pdCode) throws IllegalArgumentException {
        return MortonUtils.getMCode(pdCode, UNIT_SIZE, DIMENSION, PDCodeTableDecode256);
    }
}
