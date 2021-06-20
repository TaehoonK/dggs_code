package jp.go.aist.dggs.utils;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.util.VincentyGeodesy;
import jp.go.aist.dggs.common.DGGS;
import jp.go.aist.dggs.geometry.ISEA4DFaceCoordinates;
import org.giscience.utils.geogrid.generic.Trigonometric;
import org.giscience.utils.geogrid.geometry.FaceCoordinates;
import org.giscience.utils.geogrid.geometry.GeoCoordinates;
import org.giscience.utils.geogrid.projections.ISEAProjection;
import org.junit.Test;

import java.util.Objects;
import static org.junit.Assert.*;

public class MortonUtilsTest {
    private final double _precision = 1e-7;
    private final double _precision_z = 1e-2;
    private final int _iterations = 1000000;

    @Test
    public void getCommonAncestor() {
        String[] pdCodeList = {"12343241532167","12342451","1234512983012308"};
        assertEquals("1234", MortonUtils.getGreatestCommonAncestor(pdCodeList));
    }

    @Test
    public void getCommonAncestor_NotEqualFace() {
        String[] pdCodeList = {"12343241532167","22342451","3234512983012308"};
        assertEquals("", MortonUtils.getGreatestCommonAncestor(pdCodeList));
    }

    @Test
    public void getCommonAncestor_Equal() {
        String[] pdCodeList = {"1023","1023"};
        assertEquals("1023", MortonUtils.getGreatestCommonAncestor(pdCodeList));
    }

    @Test
    public void convertToMorton() {
        String mortonCode = MortonUtils.toPDCode(new GeoCoordinates(34.6400223819d, 135.454610432d, 1.1d), 32);
        GeoCoordinates coords = MortonUtils.toGeoCoordinate(mortonCode);
        assertTrue(Math.abs(34.6400223819d - coords.getLat()) < this._precision);
        assertTrue(Math.abs(135.454610432d - coords.getLon()) < this._precision);
        assertTrue(Math.abs(1.1d - coords.getHeight()) < this._precision_z);
    }

    @Test
    public void convertToMorton_Random() {
        for (int i = 0; i < this._iterations; i++) {
            GeoCoordinates c = new GeoCoordinates(Math.random() * 179.99 - 89.995, Math.random() * 360.0 - 180.0, Math.random() * DGGS.H_RANGE * 2 - DGGS.H_RANGE);
            String mortonCode = MortonUtils.toPDCode(c, DGGS.MAX_XY_RESOLUTION);
            GeoCoordinates coords = MortonUtils.toGeoCoordinate(mortonCode);

            assertTrue(Math.abs(coords.getLat() - c.getLat()) < this._precision);
            assertTrue((Math.abs(coords.getLon() - c.getLon()) % 180) * Trigonometric.cos(c.getLat()) < this._precision);
            assertTrue(Math.abs(coords.getHeight() - c.getHeight()) < this._precision_z);
        }
    }

    @Test
    public void convertToMorton_resolution() {
        GeoCoordinates c = new GeoCoordinates(Math.random() * 179.99 - 89.995, Math.random() * 360.0 - 180.0);
        String mortonCode = MortonUtils.toPDCode(c, DGGS.MAX_XY_RESOLUTION);
        GeoCoordinates coords = MortonUtils.toGeoCoordinate(mortonCode, DGGS.MAX_XY_RESOLUTION);
        assertTrue(Math.abs(coords.getLat() - c.getLat()) < this._precision);
        assertTrue((Math.abs(coords.getLon() - c.getLon()) % 180) * Trigonometric.cos(c.getLat()) < this._precision);

        coords = MortonUtils.toGeoCoordinate(mortonCode, 25);
        assertTrue(Math.abs(coords.getLat() - c.getLat()) < this._precision * 100);
        assertTrue((Math.abs(coords.getLon() - c.getLon()) % 180) * Trigonometric.cos(c.getLat()) < this._precision * 100);

        coords = MortonUtils.toGeoCoordinate(mortonCode, 20);
        assertTrue(Math.abs(coords.getLat() - c.getLat()) < this._precision * 1000);
        assertTrue((Math.abs(coords.getLon() - c.getLon()) % 180) * Trigonometric.cos(c.getLat()) < this._precision * 1000);

        coords = MortonUtils.toGeoCoordinate(mortonCode, 15);
        assertTrue(Math.abs(coords.getLat() - c.getLat()) < this._precision * 20000);
        assertTrue((Math.abs(coords.getLon() - c.getLon()) % 180) * Trigonometric.cos(c.getLat()) < this._precision * 20000);
    }


    public void convertToMorton_TriangleCheck() {
        double _goldenRatio = (1 + Math.sqrt(5)) / 2.;
        double F_DEG = Math.toDegrees(Math.atan(1 / (2 * Math.pow(_goldenRatio, 2))));
        double _g = F_DEG + 2 * Math.toDegrees(Math.atan(_goldenRatio)) - 90;
        double E_DEG = 90 - _g;
        System.out.println(E_DEG + " & " + F_DEG);

        String mortonCode = MortonUtils.toPDCode(new GeoCoordinates(E_DEG,-144d, 0.0d), 32);
        GeoCoordinates coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("1:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(E_DEG,-72d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("2:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(E_DEG,0d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("3:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(E_DEG,72d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("4:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(E_DEG,144d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("5:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(F_DEG,-144d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("6:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(F_DEG,-72d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("7:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(F_DEG,0d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("8:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(F_DEG,72d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("9:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(F_DEG,144d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("10:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(-F_DEG,-108d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("11:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(-F_DEG,-36d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("12:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(-F_DEG,36d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("13:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(-F_DEG,108d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("14:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(-F_DEG,180d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("15:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(-E_DEG,-108d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("16:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(-E_DEG,-36d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("17:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(-E_DEG,36d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("18:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(-E_DEG,108d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("19:" + coords.toString());
        mortonCode = MortonUtils.toPDCode(new GeoCoordinates(-E_DEG,180d, 0.0d), 32);
        coords = MortonUtils.toGeoCoordinate(mortonCode);
        System.out.println("20:" + coords.toString());
    }

    public void convertToMorton_Check_MIN() {
        double epsilon = 0.00000000000001;
        double _V_lat = 26.56505120675770;//26.565051177;
        double _V_lon = -180d + 0.0000001;
        ISEAProjection p = new ISEAProjection();
        p.setOrientation(0,0);
        GeoCoordinates point = new GeoCoordinates(_V_lat, _V_lon);
        FaceCoordinates f = p.sphereToIcosahedron(point);

        double MIN_X = f.getX();
        int init_face = f.getFace();
        System.out.println("initial face : " + init_face);
        while(f.getFace() == init_face) {
            _V_lat += epsilon;
            point = new GeoCoordinates(_V_lat, _V_lon);
            f = p.sphereToIcosahedron(point);
            //System.out.println("X : " + f.getX());
            if(MIN_X > f.getX()) MIN_X = f.getX();
        }
        System.out.println("new V_lat : " + _V_lat);
        System.out.println("changed face : " + f.getFace());
        System.out.println("Min_X : " + f.getX());
        System.out.println("Min_Y : " + f.getY());
    }

    public void convertMortonToLatLong3D() {
        for (int resolution = 0; resolution <= 32; resolution++) {
            double latitude = 35d;
            double longitude = 135d;
            double height = 100d;

            ISEA4DFaceCoordinates morton = MortonUtils.toFaceCoordinate(new GeoCoordinates(latitude, longitude, height));
            assert morton != null;
            GeoCoordinates coordinate = MortonUtils.toGeoCoordinate(morton, resolution);

            assert coordinate != null;
            System.out.println("lat:" + latitude +", long:" + longitude + ", height:" + height + ", r:" + resolution +
                    ", F_coord.x:" + morton.getX() + ", F_coord.y:" + morton.getY() + ", F_coord.z:" + morton.getZ() + ", f:" + morton.getFace() +
                    ", c.lat:" + coordinate.getLat() + ", c.lon:" + coordinate.getLon() + ", c.height:" + coordinate.getHeight());
        }
    }

    @Test
    public void compareWithGeohash() {
        WGS84Point points[] = new WGS84Point[] {
                new WGS84Point(0, 0),
                new WGS84Point(10,0),
                new WGS84Point(20,0),
                new WGS84Point(30,0),
                new WGS84Point(40,0),
                new WGS84Point(50,0),
                new WGS84Point(60,0),
                new WGS84Point(70,0),
                new WGS84Point(80,0),
                new WGS84Point(89,0)
//                new WGS84Point(0, 0),
//                new WGS84Point(0, 10),
//                new WGS84Point(0, 20),
//                new WGS84Point(0, 30),
//                new WGS84Point(0, 40),
//                new WGS84Point(0, 50),
//                new WGS84Point(0, 60),
//                new WGS84Point(0, 70),
//                new WGS84Point(0, 80),
//                new WGS84Point(0, 89),
//                new WGS84Point(0, 100),
//                new WGS84Point(0, 110),
//                new WGS84Point(0, 120),
//                new WGS84Point(0, 130),
//                new WGS84Point(0, 140),
//                new WGS84Point(0, 150),
//                new WGS84Point(0, 160),
//                new WGS84Point(0, 170),
//                new WGS84Point(0, 179)
        };

        for (WGS84Point point : points) {
            System.out.println(point.getLongitude());
            WGS84Point nearPoint = VincentyGeodesy.moveInDirection(point, 0d, 1);
            System.out.println("WGS 1 meter: " + VincentyGeodesy.distanceInMeters(point, nearPoint));
            ISEA4DFaceCoordinates isea4DFaceCoordinateA = MortonUtils.toFaceCoordinate(Objects.requireNonNull(MortonUtils.toFaceCoordinate(new GeoCoordinates(point.getLatitude(), point.getLongitude()))),32);
            ISEA4DFaceCoordinates isea4DFaceCoordinateB = MortonUtils.toFaceCoordinate(Objects.requireNonNull(MortonUtils.toFaceCoordinate(new GeoCoordinates(nearPoint.getLatitude(), nearPoint.getLongitude()))),32);
            GeoCoordinates geocoords_from = MortonUtils.toGeoCoordinate(isea4DFaceCoordinateA);
            GeoCoordinates geocoords_to = MortonUtils.toGeoCoordinate(isea4DFaceCoordinateB);
            System.out.println("ISEA 1 meter: " + VincentyGeodesy.distanceInMeters(new WGS84Point(geocoords_from.getLat(), geocoords_from.getLon()), new WGS84Point(geocoords_to.getLat(), geocoords_to.getLon())));
            System.out.println("ISEA distance: " + isea4DFaceCoordinateA.distance2DTo(isea4DFaceCoordinateB) * 0.001662828);
        }

        for (WGS84Point point : points) {
            GeoHash geoHash = GeoHash.withCharacterPrecision(point.getLatitude(), point.getLongitude(), 12);
            GeoHash geoHash_plus_1bit = geoHash.getAdjacent()[1];
            WGS84Point from = geoHash.getBoundingBoxCenter();
            WGS84Point to = geoHash_plus_1bit.getBoundingBoxCenter();

            System.out.println("Geohash: " + VincentyGeodesy.distanceInMeters(from, to));
            //System.out.println("Geohash: " + DistanceUtils.distVincentyRAD(from.getLatitude(),from.getLongitude(),to.getLatitude(),to.getLongitude()));


            ISEA4DFaceCoordinates isea4DFaceCoordinates = MortonUtils.toFaceCoordinate(Objects.requireNonNull(MortonUtils.toFaceCoordinate(new GeoCoordinates(point.getLatitude(), point.getLongitude()))),28);
            ISEA4DFaceCoordinates isea4DFaceCoordinates_plus_1 = new ISEA4DFaceCoordinates(isea4DFaceCoordinates.getFace(), isea4DFaceCoordinates.getX() + 1, isea4DFaceCoordinates.getY(), isea4DFaceCoordinates.getZ(), isea4DFaceCoordinates.getResolution());
            GeoCoordinates geocoords_from = MortonUtils.toGeoCoordinate(isea4DFaceCoordinates);
            GeoCoordinates geocoords_to = MortonUtils.toGeoCoordinate(isea4DFaceCoordinates_plus_1);
            System.out.println("ISEA: " + VincentyGeodesy.distanceInMeters(new WGS84Point(geocoords_from.getLat(), geocoords_from.getLon()), new WGS84Point(geocoords_to.getLat(), geocoords_to.getLon())));
        }
    }
}
