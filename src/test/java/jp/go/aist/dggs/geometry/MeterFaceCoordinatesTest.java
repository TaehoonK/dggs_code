package jp.go.aist.dggs.geometry;

import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.util.VincentyGeodesy;
import jp.go.aist.dggs.utils.MortonUtils;
import org.giscience.utils.geogrid.geometry.GeoCoordinates;
import java.util.Objects;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MeterFaceCoordinatesTest {

    @Test
    public void toMeter_fromISEA4D() {
        String mortonA = "803323032244674622345202112244267";
        String mortonB = "803323032600230766052631760355544";

        ISEA4DFaceCoordinates faceCoordinateA = Morton3D.decode(mortonA);
        ISEA4DFaceCoordinates faceCoordinateB = Morton3D.decode(mortonB);

        MeterFaceCoordinates meterFaceCoordsA = faceCoordinateA.toMeterUnit();
        MeterFaceCoordinates meterFaceCoordsB = faceCoordinateB.toMeterUnit();

        System.out.println(meterFaceCoordsA.getFace() + "," + meterFaceCoordsB.getFace());
        assertEquals(meterFaceCoordsA.getFace(), meterFaceCoordsB.getFace());

        ISEA4DFaceCoordinates newFaceCoordsA = meterFaceCoordsA.toISEA4DFaceCoordinates();
        ISEA4DFaceCoordinates newFaceCoordsB = meterFaceCoordsB.toISEA4DFaceCoordinates();

        System.out.println(faceCoordinateA.distance3DTo(newFaceCoordsA));
        System.out.println(faceCoordinateB.distance3DTo(newFaceCoordsB));
        assertTrue(faceCoordinateA.distance3DTo(newFaceCoordsA) <= 0.01);
        assertTrue(faceCoordinateB.distance3DTo(newFaceCoordsB) <= 0.01);

        String newMortonA = Morton3D.encode(newFaceCoordsA);
        String newMortonB = Morton3D.encode(newFaceCoordsB);
        System.out.println(mortonA);
        System.out.println(newMortonA);
        System.out.println(mortonB);
        System.out.println(newMortonB);
//        System.out.println(faceCoordinateA);
//        System.out.println(newFaceCoordsA);
//        System.out.println(meterFaceCoordsA);
//        System.out.println(newFaceCoordsA.toMeterUnit());
//        System.out.println(mortonA);
//        System.out.println(newMortonA);
    }
    @Test
    public void toMeter_temp() {
        String mortonA = "803323032244674622345202112244267";
        ISEA4DFaceCoordinates faceCoordinateA = Morton3D.decode(mortonA);
        MeterFaceCoordinates meterFaceCoordsA = faceCoordinateA.toMeterUnit();
        MeterFaceCoordinates meterFaceCoordsB = new MeterFaceCoordinates(meterFaceCoordsA.getFace(),
                meterFaceCoordsA.getX() + 1, meterFaceCoordsA.getY() + 1, meterFaceCoordsA.getZ() + 1);

        ISEA4DFaceCoordinates newFaceCoordsA = meterFaceCoordsA.toISEA4DFaceCoordinates();
        ISEA4DFaceCoordinates newFaceCoordsB = meterFaceCoordsB.toISEA4DFaceCoordinates();
        System.out.println(newFaceCoordsA.distance3DTo(newFaceCoordsB));
        assertTrue(newFaceCoordsA.distance3DTo(newFaceCoordsB) <= 2);

        GeoCoordinates geoCoordsA = MortonUtils.toGeoCoordinate(newFaceCoordsA);
        GeoCoordinates geoCoordsB = MortonUtils.toGeoCoordinate(newFaceCoordsB);
        assert geoCoordsB != null;
        assert geoCoordsA != null;

        // To calculate distance between two geo-coordinates
        WGS84Point pointA = new WGS84Point(geoCoordsA.getLat(),geoCoordsA.getLon());
        WGS84Point pointB = new WGS84Point(geoCoordsB.getLat(),geoCoordsB.getLon());
        System.out.println(VincentyGeodesy.distanceInMeters(pointA, pointB));
        assertTrue(VincentyGeodesy.distanceInMeters(pointA, pointB) <= 2);
    }


    @Test
    public void compareUnit() {
        WGS84Point points[] = new WGS84Point[]{
                new WGS84Point(0, 0),
                new WGS84Point(10, 0),
                new WGS84Point(20, 0),
                new WGS84Point(30, 0),
                new WGS84Point(40, 0),
                new WGS84Point(50, 0),
                new WGS84Point(60, 0),
                new WGS84Point(70, 0),
                new WGS84Point(80, 0),
                new WGS84Point(89, 0),
                new WGS84Point(0, 0),
                new WGS84Point(0, 10),
                new WGS84Point(0, 20),
                new WGS84Point(0, 30),
                new WGS84Point(0, 40),
                new WGS84Point(0, 50),
                new WGS84Point(0, 60),
                new WGS84Point(0, 70),
                new WGS84Point(0, 80),
                new WGS84Point(0, 89),
                new WGS84Point(0, 100),
                new WGS84Point(0, 110),
                new WGS84Point(0, 120),
                new WGS84Point(0, 130),
                new WGS84Point(0, 140),
                new WGS84Point(0, 150),
                new WGS84Point(0, 160),
                new WGS84Point(0, 170),
                new WGS84Point(0, 179),
        };

        for (WGS84Point point : points) {
            System.out.println("(" + point.getLatitude() + "," + point.getLongitude() + ")");
            WGS84Point nearPoint = VincentyGeodesy.moveInDirection(point, 0d, 1);
            System.out.println("WGS 1 meter: " + VincentyGeodesy.distanceInMeters(point, nearPoint));
            ISEA4DFaceCoordinates isea4DFaceCoordinateA = MortonUtils.toFaceCoordinate(Objects.requireNonNull(MortonUtils.toFaceCoordinate(new GeoCoordinates(point.getLatitude(), point.getLongitude(), 10.0))), 32);
            ISEA4DFaceCoordinates isea4DFaceCoordinateB = MortonUtils.toFaceCoordinate(Objects.requireNonNull(MortonUtils.toFaceCoordinate(new GeoCoordinates(nearPoint.getLatitude(), nearPoint.getLongitude(), -10.0))), 32);

            MeterFaceCoordinates meterFaceCoordA = isea4DFaceCoordinateA.toMeterUnit();
            MeterFaceCoordinates meterFaceCoordB = isea4DFaceCoordinateB.toMeterUnit();
            System.out.println("ISEA distance: " + meterFaceCoordA.distance2DTo(meterFaceCoordB));
        }
    }
}