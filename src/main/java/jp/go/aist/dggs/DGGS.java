package jp.go.aist.dggs;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;


/**
 * static values for jp.go.aist.DGGS package
 *
 * @author TaehoonKim AIST DPRT, Research Assistant
 * */
public class DGGS {
    // For Morton3D encode and decode from rhombus index
    static final long EIGHT_BIT_MASK = 0x000000FF;
    static final long NINE_BIT_MASK = 0x000001FF;
    static final int UNIT_SIZE = 3;
    public static final int[] MortonTable256Encode
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

    static final long[] MortonTable512Decode = {
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

    static final String[] PDCodeTable512Encode = {
            "000", "001", "002", "003", "004", "005", "006", "007",
            "010", "011", "012", "013", "014", "015", "016", "017",
            "020", "021", "022", "023", "024", "025", "026", "027",
            "030", "031", "032", "033", "034", "035", "036", "037",
            "040", "041", "042", "043", "044", "045", "046", "047",
            "050", "051", "052", "053", "054", "055", "056", "057",
            "060", "061", "062", "063", "064", "065", "066", "067",
            "070", "071", "072", "073", "074", "075", "076", "077",
            "100", "101", "102", "103", "104", "105", "106", "107",
            "110", "111", "112", "113", "114", "115", "116", "117",
            "120", "121", "122", "123", "124", "125", "126", "127",
            "130", "131", "132", "133", "134", "135", "136", "137",
            "140", "141", "142", "143", "144", "145", "146", "147",
            "150", "151", "152", "153", "154", "155", "156", "157",
            "160", "161", "162", "163", "164", "165", "166", "167",
            "170", "171", "172", "173", "174", "175", "176", "177",
            "200", "201", "202", "203", "204", "205", "206", "207",
            "210", "211", "212", "213", "214", "215", "216", "217",
            "220", "221", "222", "223", "224", "225", "226", "227",
            "230", "231", "232", "233", "234", "235", "236", "237",
            "240", "241", "242", "243", "244", "245", "246", "247",
            "250", "251", "252", "253", "254", "255", "256", "257",
            "260", "261", "262", "263", "264", "265", "266", "267",
            "270", "271", "272", "273", "274", "275", "276", "277",
            "300", "301", "302", "303", "304", "305", "306", "307",
            "310", "311", "312", "313", "314", "315", "316", "317",
            "320", "321", "322", "323", "324", "325", "326", "327",
            "330", "331", "332", "333", "334", "335", "336", "337",
            "340", "341", "342", "343", "344", "345", "346", "347",
            "350", "351", "352", "353", "354", "355", "356", "357",
            "360", "361", "362", "363", "364", "365", "366", "367",
            "370", "371", "372", "373", "374", "375", "376", "377",
            "400", "401", "402", "403", "404", "405", "406", "407",
            "410", "411", "412", "413", "414", "415", "416", "417",
            "420", "421", "422", "423", "424", "425", "426", "427",
            "430", "431", "432", "433", "434", "435", "436", "437",
            "440", "441", "442", "443", "444", "445", "446", "447",
            "450", "451", "452", "453", "454", "455", "456", "457",
            "460", "461", "462", "463", "464", "465", "466", "467",
            "470", "471", "472", "473", "474", "475", "476", "477",
            "500", "501", "502", "503", "504", "505", "506", "507",
            "510", "511", "512", "513", "514", "515", "516", "517",
            "520", "521", "522", "523", "524", "525", "526", "527",
            "530", "531", "532", "533", "534", "535", "536", "537",
            "540", "541", "542", "543", "544", "545", "546", "547",
            "550", "551", "552", "553", "554", "555", "556", "557",
            "560", "561", "562", "563", "564", "565", "566", "567",
            "570", "571", "572", "573", "574", "575", "576", "577",
            "600", "601", "602", "603", "604", "605", "606", "607",
            "610", "611", "612", "613", "614", "615", "616", "617",
            "620", "621", "622", "623", "624", "625", "626", "627",
            "630", "631", "632", "633", "634", "635", "636", "637",
            "640", "641", "642", "643", "644", "645", "646", "647",
            "650", "651", "652", "653", "654", "655", "656", "657",
            "660", "661", "662", "663", "664", "665", "666", "667",
            "670", "671", "672", "673", "674", "675", "676", "677",
            "700", "701", "702", "703", "704", "705", "706", "707",
            "710", "711", "712", "713", "714", "715", "716", "717",
            "720", "721", "722", "723", "724", "725", "726", "727",
            "730", "731", "732", "733", "734", "735", "736", "737",
            "740", "741", "742", "743", "744", "745", "746", "747",
            "750", "751", "752", "753", "754", "755", "756", "757",
            "760", "761", "762", "763", "764", "765", "766", "767",
            "770", "771", "772", "773", "774", "775", "776", "777",
    };

    static final Map<String, Integer> PDCodeTable512Decode;
    static {
        PDCodeTable512Decode = new HashMap<>();
        for (int i = 0; i < 512; i++) {
            PDCodeTable512Decode.put(PDCodeTable512Encode[i], i);
        }
    }

    // For Morton3D encode and decode from WGS 84 coordinate (EPSG 4326 + altitude)
    public static final int MAX_XY_RESOLUTION = 32;
    static final int MAX_Z_RESOLUTION = 24;
    static final double M_PI = Math.PI;
    static final double NEW_ORIG_X = -0.6022955012659694; // # TABLE_G * (-1) #old:
    static final double NEW_ORIG_Y = -0.3477354703761901; // # TABLE_H * (-2) #old:
    static final double H_RANGE = 16000.0; // Unit = Meter
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

        return Math.atan2(
                Math.cos(v.getLatitude()) * Math.sin(v.getLongitude() - c.getLongitude()),
                Math.cos(c.getLatitude()) * Math.sin(v.getLatitude())
                        - Math.sin(c.getLatitude()) * Math.cos(v.getLatitude())
                        * Math.cos(v.getLongitude() - c.getLongitude()));
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
