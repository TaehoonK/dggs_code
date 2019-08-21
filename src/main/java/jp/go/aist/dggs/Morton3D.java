package jp.go.aist.dggs;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author TaehoonKim AIST DPRT, Research Assistant
 * */
public class Morton3D {
    private static final long EIGHT_BIT_MASK = 0x000000FF;
    private static final long NINE_BIT_MASK = 0x000001FF;
    private static final int UNIT_SIZE = 3;
    private static final int DIMENSION = 3;
    private static final int MAX_RESOLUTION = 32;

    private static final int[] MortonTable3D256
            = {
            0x00000000,
            0x00000001, 0x00000008, 0x00000009, 0x00000040, 0x00000041, 0x00000048, 0x00000049, 0x00000200,
            0x00000201, 0x00000208, 0x00000209, 0x00000240, 0x00000241, 0x00000248, 0x00000249, 0x00001000,
            0x00001001, 0x00001008, 0x00001009, 0x00001040, 0x00001041, 0x00001048, 0x00001049, 0x00001200,
            0x00001201, 0x00001208, 0x00001209, 0x00001240, 0x00001241, 0x00001248, 0x00001249, 0x00008000,
            0x00008001, 0x00008008, 0x00008009, 0x00008040, 0x00008041, 0x00008048, 0x00008049, 0x00008200,
            0x00008201, 0x00008208, 0x00008209, 0x00008240, 0x00008241, 0x00008248, 0x00008249, 0x00009000,
            0x00009001, 0x00009008, 0x00009009, 0x00009040, 0x00009041, 0x00009048, 0x00009049, 0x00009200,
            0x00009201, 0x00009208, 0x00009209, 0x00009240, 0x00009241, 0x00009248, 0x00009249, 0x00040000,
            0x00040001, 0x00040008, 0x00040009, 0x00040040, 0x00040041, 0x00040048, 0x00040049, 0x00040200,
            0x00040201, 0x00040208, 0x00040209, 0x00040240, 0x00040241, 0x00040248, 0x00040249, 0x00041000,
            0x00041001, 0x00041008, 0x00041009, 0x00041040, 0x00041041, 0x00041048, 0x00041049, 0x00041200,
            0x00041201, 0x00041208, 0x00041209, 0x00041240, 0x00041241, 0x00041248, 0x00041249, 0x00048000,
            0x00048001, 0x00048008, 0x00048009, 0x00048040, 0x00048041, 0x00048048, 0x00048049, 0x00048200,
            0x00048201, 0x00048208, 0x00048209, 0x00048240, 0x00048241, 0x00048248, 0x00048249, 0x00049000,
            0x00049001, 0x00049008, 0x00049009, 0x00049040, 0x00049041, 0x00049048, 0x00049049, 0x00049200,
            0x00049201, 0x00049208, 0x00049209, 0x00049240, 0x00049241, 0x00049248, 0x00049249, 0x00200000,
            0x00200001, 0x00200008, 0x00200009, 0x00200040, 0x00200041, 0x00200048, 0x00200049, 0x00200200,
            0x00200201, 0x00200208, 0x00200209, 0x00200240, 0x00200241, 0x00200248, 0x00200249, 0x00201000,
            0x00201001, 0x00201008, 0x00201009, 0x00201040, 0x00201041, 0x00201048, 0x00201049, 0x00201200,
            0x00201201, 0x00201208, 0x00201209, 0x00201240, 0x00201241, 0x00201248, 0x00201249, 0x00208000,
            0x00208001, 0x00208008, 0x00208009, 0x00208040, 0x00208041, 0x00208048, 0x00208049, 0x00208200,
            0x00208201, 0x00208208, 0x00208209, 0x00208240, 0x00208241, 0x00208248, 0x00208249, 0x00209000,
            0x00209001, 0x00209008, 0x00209009, 0x00209040, 0x00209041, 0x00209048, 0x00209049, 0x00209200,
            0x00209201, 0x00209208, 0x00209209, 0x00209240, 0x00209241, 0x00209248, 0x00209249, 0x00240000,
            0x00240001, 0x00240008, 0x00240009, 0x00240040, 0x00240041, 0x00240048, 0x00240049, 0x00240200,
            0x00240201, 0x00240208, 0x00240209, 0x00240240, 0x00240241, 0x00240248, 0x00240249, 0x00241000,
            0x00241001, 0x00241008, 0x00241009, 0x00241040, 0x00241041, 0x00241048, 0x00241049, 0x00241200,
            0x00241201, 0x00241208, 0x00241209, 0x00241240, 0x00241241, 0x00241248, 0x00241249, 0x00248000,
            0x00248001, 0x00248008, 0x00248009, 0x00248040, 0x00248041, 0x00248048, 0x00248049, 0x00248200,
            0x00248201, 0x00248208, 0x00248209, 0x00248240, 0x00248241, 0x00248248, 0x00248249, 0x00249000,
            0x00249001, 0x00249008, 0x00249009, 0x00249040, 0x00249041, 0x00249048, 0x00249049, 0x00249200,
            0x00249201, 0x00249208, 0x00249209, 0x00249240, 0x00249241, 0x00249248, 0x00249249
    };

    private static final long[] MortonTable512Decode = {
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7,
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7,
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7,
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            0, 1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 2, 3,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7,
            4, 5, 4, 5, 4, 5, 4, 5, 6, 7, 6, 7, 6, 7, 6, 7
    };

    private static final String[] PDCodeTable512 = {
            "000","001","002","003","004","005","006","007",
            "010","011","012","013","014","015","016","017",
            "020","021","022","023","024","025","026","027",
            "030","031","032","033","034","035","036","037",
            "040","041","042","043","044","045","046","047",
            "050","051","052","053","054","055","056","057",
            "060","061","062","063","064","065","066","067",
            "070","071","072","073","074","075","076","077",
            "100","101","102","103","104","105","106","107",
            "110","111","112","113","114","115","116","117",
            "120","121","122","123","124","125","126","127",
            "130","131","132","133","134","135","136","137",
            "140","141","142","143","144","145","146","147",
            "150","151","152","153","154","155","156","157",
            "160","161","162","163","164","165","166","167",
            "170","171","172","173","174","175","176","177",
            "200","201","202","203","204","205","206","207",
            "210","211","212","213","214","215","216","217",
            "220","221","222","223","224","225","226","227",
            "230","231","232","233","234","235","236","237",
            "240","241","242","243","244","245","246","247",
            "250","251","252","253","254","255","256","257",
            "260","261","262","263","264","265","266","267",
            "270","271","272","273","274","275","276","277",
            "300","301","302","303","304","305","306","307",
            "310","311","312","313","314","315","316","317",
            "320","321","322","323","324","325","326","327",
            "330","331","332","333","334","335","336","337",
            "340","341","342","343","344","345","346","347",
            "350","351","352","353","354","355","356","357",
            "360","361","362","363","364","365","366","367",
            "370","371","372","373","374","375","376","377",
            "400","401","402","403","404","405","406","407",
            "410","411","412","413","414","415","416","417",
            "420","421","422","423","424","425","426","427",
            "430","431","432","433","434","435","436","437",
            "440","441","442","443","444","445","446","447",
            "450","451","452","453","454","455","456","457",
            "460","461","462","463","464","465","466","467",
            "470","471","472","473","474","475","476","477",
            "500","501","502","503","504","505","506","507",
            "510","511","512","513","514","515","516","517",
            "520","521","522","523","524","525","526","527",
            "530","531","532","533","534","535","536","537",
            "540","541","542","543","544","545","546","547",
            "550","551","552","553","554","555","556","557",
            "560","561","562","563","564","565","566","567",
            "570","571","572","573","574","575","576","577",
            "600","601","602","603","604","605","606","607",
            "610","611","612","613","614","615","616","617",
            "620","621","622","623","624","625","626","627",
            "630","631","632","633","634","635","636","637",
            "640","641","642","643","644","645","646","647",
            "650","651","652","653","654","655","656","657",
            "660","661","662","663","664","665","666","667",
            "670","671","672","673","674","675","676","677",
            "700","701","702","703","704","705","706","707",
            "710","711","712","713","714","715","716","717",
            "720","721","722","723","724","725","726","727",
            "730","731","732","733","734","735","736","737",
            "740","741","742","743","744","745","746","747",
            "750","751","752","753","754","755","756","757",
            "760","761","762","763","764","765","766","767",
            "770","771","772","773","774","775","776","777",
    };

    private static final Map<String, Integer> PDCodeTableDecode512;
    static {
        PDCodeTableDecode512 = new HashMap<>();
        for(int i = 0; i < 512; i++) {
            PDCodeTableDecode512.put(PDCodeTable512[i],i);
        }
    }

    /**
     * Morton code encoding with Lookup Table
     *
     * @param x range is from 0 to 4,294,967,295 (2^32 - 1)
     * @param y range is from 0 to 4,294,967,295 (2^32 - 1)
     * @param z range is from 0 to 16,777,215 (2^24 - 1)
     * @param face Index of rhombuses
     * @param resolution Resolution of generate Morton code
     * @return Morton code based on DGGS for point cloud data
     */
    public static String encode(long x, long y, long z, int face, int resolution) {
        String pdCode = encode(x, y, z, resolution);
        return face + pdCode;
    }

    /**
     * Morton code encoding with Lookup Table
     *
     * @param x range is from 0 to 4,294,967,295 (2^32 - 1)
     * @param y range is from 0 to 4,294,967,295 (2^32 - 1)
     * @param z range is from 0 to 16,777,215 (2^24 - 1)
     * @param resolution Resolution of generate Morton code
     * @return Morton code based on DGGS for point cloud data except rhombuses index
     */
    private static String encode(long x, long y, long z, int resolution) {
        long mCode = 0;
        StringBuilder pdCode = new StringBuilder();

        for(int i = 4; i > 0; --i) {
            StringBuilder sb = new StringBuilder();
            int shift = (i - 1) * 8; // A unit of bits of the bucket is 8-bits
            mCode = 0;
            mCode = mCode   // handling a 24-bits(3 dimension x 8 bits) bucket at once
                    | (MortonTable3D256[(int) ((z >> shift) & EIGHT_BIT_MASK)] << 2)
                    | (MortonTable3D256[(int) ((y >> shift) & EIGHT_BIT_MASK)] << 1)
                    |  MortonTable3D256[(int) ((x >> shift) & EIGHT_BIT_MASK)];

            if(mCode != 0) {
                boolean isSixBits = true;
                for(int j = 3; j > 0; --j) {
                    shift = (j - 1) * 9; // UNIT_SIZE * DIMENSION is 9
                    String partialPDCode = PDCodeTable512[(int) ((mCode >> shift) & NINE_BIT_MASK)];
                    if (isSixBits) {
                        // Bucket size is 24-bits, it can be distinguished to 6-bits, 9-bits, 9-bits.
                        // So, the first one should be 6-bits.
                        partialPDCode = partialPDCode.substring(1);
                        isSixBits = false;
                    }
                    sb.append(partialPDCode);
                }
                pdCode.append(sb.toString());
            }
            else {  // mCode is 0
                pdCode.append("00000000");
            }

            if(pdCode.length() > resolution)
                break;
        }

        pdCode = new StringBuilder(pdCode.length() > resolution ? pdCode.substring(0, resolution) : pdCode.toString());

        if (pdCode.toString().equals("")) { // TODO ?? what is the purpose of this code?
            if (resolution != 0) {
                pdCode = new StringBuilder(Long.toString(mCode));
                System.out.println("Impossible!!");
            }
        }

        return pdCode.toString();
    }

    /**
     * Convert Morton codes to coordinate using bit operation
     *
     * @param pdCode Morton code based on DGGS for point cloud data except rhombuses index
     * @return	3-dimension coordinate within an rhombus, array[x,y,z]
     */
    public static long[] decode(String pdCode) {
        return decode(pdCode, MAX_RESOLUTION);
    }

    public static long[] decode(String pdCode, int resolution) {
        long[] coordinates = new long[3];

        int extraSize = resolution - pdCode.length();
        StringBuilder sb = new StringBuilder(pdCode);
        for (int i = 0; i < extraSize; i++) {
            sb.append('0');
        }
        pdCode = sb.toString();

        final int LOOP_COUNT = pdCode.length() / UNIT_SIZE;
        for (int i = 0; i < LOOP_COUNT; ++i) {
            int index = PDCodeTableDecode512.get(pdCode.substring(
                    (LOOP_COUNT - (i + 1)) * UNIT_SIZE,
                    (LOOP_COUNT - i) * UNIT_SIZE));
            coordinates[0] |= MortonTable512Decode[index] << (UNIT_SIZE * i);
            coordinates[1] |= MortonTable512Decode[index >> 1] << (UNIT_SIZE * i);
            coordinates[2] |= MortonTable512Decode[index >> 2] << (UNIT_SIZE * i);
        }

        if(pdCode.length() % UNIT_SIZE != 0) {
            int remainStringSize = pdCode.length() % UNIT_SIZE;
            String remainString = pdCode.substring(pdCode.length() - remainStringSize);
            sb = new StringBuilder(remainString);
            for (int i = 0; i < UNIT_SIZE - remainStringSize; i++) {
                sb.insert(0, '0');
            }
            String remainPDCode = sb.toString();
            int index = PDCodeTableDecode512.get(remainPDCode);
            coordinates[0] = coordinates[0] << remainStringSize | MortonTable512Decode[index];
            coordinates[1] = coordinates[1] << remainStringSize | MortonTable512Decode[index >> 1];
            coordinates[2] = coordinates[2] << remainStringSize | MortonTable512Decode[index >> 2];
        }

        return coordinates;
    }

    /**
     * Convert normal Morton code to PD-Morton code (Morton code based on DGGS for point cloud data)
     *
     * @param mCode Normal Morton code (made by bit interleaving)
     * @return Morton code based on DGGS for point cloud data (up to 21 level of resolution). It doesn't have an index of face
     */
    public static String getPDCode(long mCode) {
        return MortonUtils.getPDCode(mCode, UNIT_SIZE, DIMENSION, NINE_BIT_MASK, PDCodeTable512);
    }

    /**
     * Convert normal Morton code to PD-Morton code (Morton code based on DGGS for point cloud data)
     *
     * @param mCode Normal Morton code (made by bit interleaving and BigInteger)
     * @return Morton code based on DGGS for point cloud data. It doesn't have an index of face
     */
    public static String getPDCode(BigInteger mCode) {
        return MortonUtils.getPDCode(mCode, UNIT_SIZE, DIMENSION, NINE_BIT_MASK, PDCodeTable512);
    }

    /**
     * Convert PD-Morton code (Morton code based on DGGS for point cloud data) to normal Morton code
     *
     * @param pdCode Morton code based on DGGS for point cloud data (up to 21 level of resolution). It doesn't have an index of face
     * @return Normal Morton code
     */
    public static long getMCode_old(String pdCode) throws IllegalArgumentException {
        return MortonUtils.getMCode_old(pdCode, MAX_RESOLUTION, UNIT_SIZE, DIMENSION, PDCodeTableDecode512);
    }

    /**
     * Convert PD-Morton code (Morton code based on DGGS for point cloud data) to normal Morton code
     *
     * @param pdCode Morton code based on DGGS for point cloud data. It doesn't have an index of face
     * @return Normal Morton code
     */
    public static BigInteger getMCode(String pdCode) throws IllegalArgumentException {
        return MortonUtils.getMCode(pdCode, UNIT_SIZE, DIMENSION, PDCodeTableDecode512);
    }
}