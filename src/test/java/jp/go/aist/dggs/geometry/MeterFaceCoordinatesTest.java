package jp.go.aist.dggs.geometry;

import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.util.VincentyGeodesy;
import jp.go.aist.dggs.utils.MortonUtils;
import org.giscience.utils.geogrid.geometry.GeoCoordinates;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertNotEquals;

public class MeterFaceCoordinatesTest {
    private final float delta = 0.0000001f;

    @Test
    public void toLocal_temp() {
        String mortonA = "803323032244674622345202112244267";
        String mortonB = "803323032600230766052631760355544";

        ISEA4DFaceCoordinates faceCoordinateA = Morton3D.decode(mortonA);
        ISEA4DFaceCoordinates faceCoordinateB = Morton3D.decode(mortonB);

        MeterFaceCoordinates meterFaceCoordA = faceCoordinateA.toMeterUnit();
        MeterFaceCoordinates meterFaceCoordB = faceCoordinateB.toMeterUnit();

        assertNotEquals(meterFaceCoordA.getFace(), meterFaceCoordB.getFace());
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
            System.out.println(point.getLongitude());
            WGS84Point nearPoint = VincentyGeodesy.moveInDirection(point, 0d, 1);
            System.out.println("WGS 1 meter: " + VincentyGeodesy.distanceInMeters(point, nearPoint));
            ISEA4DFaceCoordinates isea4DFaceCoordinateA = MortonUtils.toFaceCoordinate(Objects.requireNonNull(MortonUtils.toFaceCoordinate(new GeoCoordinates(point.getLatitude(), point.getLongitude()))), 32);
            ISEA4DFaceCoordinates isea4DFaceCoordinateB = MortonUtils.toFaceCoordinate(Objects.requireNonNull(MortonUtils.toFaceCoordinate(new GeoCoordinates(nearPoint.getLatitude(), nearPoint.getLongitude()))), 32);

            MeterFaceCoordinates meterFaceCoordA = isea4DFaceCoordinateA.toMeterUnit();
            MeterFaceCoordinates meterFaceCoordB = isea4DFaceCoordinateB.toMeterUnit();
            System.out.println("ISEA distance: " + meterFaceCoordA.distance2DTo(meterFaceCoordB));
        }
    }
}