package jp.go.aist.dggs;

import jp.go.aist.dggs.geometry.ISEA4DFaceCoordinates;
import org.giscience.utils.geogrid.geometry.FaceCoordinates;
import org.giscience.utils.geogrid.geometry.GeoCoordinates;
import org.giscience.utils.geogrid.projections.ISEAProjection;
import org.junit.Test;
import org.locationtech.proj4j.*;

import java.util.Objects;

import static org.junit.Assert.assertTrue;

public class ISEA4DFaceCoordinatesTest {

    public Double distance2DTo(ProjCoordinate startP, ProjCoordinate endP) {
        return Math.sqrt(Math.pow(startP.x - endP.x, 2) + Math.pow(startP.y - endP.y, 2));
    }

    public double angleBetweenTwoLine(double point1X, double point1Y,
                                      double point2X, double point2Y,
                                      double fixedX, double fixedY) {
        double angle1 = Math.atan2(point1Y - fixedY, point1X - fixedX);
        double angle2 = Math.atan2(point2Y - fixedY, point2X - fixedX);

        double innerAngle = angle1 - angle2;
        while(innerAngle < 0 || innerAngle > Math.PI) {
            if(innerAngle < 0) innerAngle += Math.PI*2;
            else if(innerAngle > Math.PI) innerAngle = Math.PI*2 - innerAngle;
        }

        return Math.toDegrees(innerAngle);
    }

    @Test
    public void geometryOperationValidation() throws Exception {
        final double boundary = 0.00001;

        // Reference EPSG:3095 (https://epsg.io/3095)
        GeoCoordinates c1 = new GeoCoordinates(Math.random() * boundary + 30.37, Math.random() * boundary + 132.83);
        GeoCoordinates c2 = new GeoCoordinates(Math.random() * boundary + 30.37, Math.random() * boundary + 132.83);
        GeoCoordinates c3 = new GeoCoordinates(Math.random() * boundary + 30.37, Math.random() * boundary + 132.83);

        //System.out.printf("[%f,%f],[%f,%f],[%f,%f],[%f,%f]\n", c1.getLon(), c1.getLat(), c2.getLon(), c2.getLat(), c3.getLon(), c3.getLat(), c1.getLon(),c1.getLat());
        System.out.printf("double[][] polygonWGSCoords = {{%f,%f},{%f,%f},{%f,%f},{%f,%f}};\n", c1.getLon(), c1.getLat(), c2.getLon(), c2.getLat(), c3.getLon(), c3.getLat(), c1.getLon(), c1.getLat());

        String csNameFrom = "EPSG:4326";
        String csNameTo = "EPSG:3095";

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CRSFactory csFactory = new CRSFactory();
        CoordinateReferenceSystem crsFrom = csFactory.createFromName(csNameFrom);
        CoordinateReferenceSystem crsTo = csFactory.createFromName(csNameTo);
        CoordinateTransform trans = ctFactory.createTransform(crsFrom, crsTo);

        ProjCoordinate p1 = new ProjCoordinate(c1.getLon(), c1.getLat());
        ProjCoordinate p2 = new ProjCoordinate(c2.getLon(), c2.getLat());
        ProjCoordinate p3 = new ProjCoordinate(c3.getLon(), c3.getLat());

        ProjCoordinate p1_Prime = new ProjCoordinate();
        ProjCoordinate p2_Prime = new ProjCoordinate();
        ProjCoordinate p3_Prime = new ProjCoordinate();

        trans.transform(p1, p1_Prime);
        trans.transform(p2, p2_Prime);
        trans.transform(p3, p3_Prime);

        System.out.printf("double[][] polygonUTMCoords = {{%f,%f},{%f,%f},{%f,%f},{%f,%f}};\n", p1_Prime.x, p1_Prime.y, p2_Prime.x, p2_Prime.y, p3_Prime.x, p3_Prime.y, p1_Prime.x, p1_Prime.y);

        ISEAProjection p = new ISEAProjection();
        p.setOrientation(0,0);
        FaceCoordinates orgin_f1 = p.sphereToIcosahedron(c1);
        FaceCoordinates orgin_f2 = p.sphereToIcosahedron(c2);
        FaceCoordinates orgin_f3 = p.sphereToIcosahedron(c3);

        System.out.printf("double[][] polygonISEACoords2 = {{%f,%f},{%f,%f},{%f,%f},{%f,%f}};\n", orgin_f1.getX(), orgin_f1.getY(), orgin_f2.getX(), orgin_f2.getY(), orgin_f3.getX(), orgin_f3.getY(), orgin_f1.getX(), orgin_f1.getY());

        ISEA4DFaceCoordinates f1 = Objects.requireNonNull(MortonUtils.toFaceCoordinate(c1)).toOrthogonal();
        ISEA4DFaceCoordinates f2 = Objects.requireNonNull(MortonUtils.toFaceCoordinate(c2)).toOrthogonal();
        ISEA4DFaceCoordinates f3 = Objects.requireNonNull(MortonUtils.toFaceCoordinate(c3)).toOrthogonal();

        System.out.printf("long[][] polygonISEACoords = {{%d,%d},{%d,%d},{%d,%d},{%d,%d}};\n", f1.getX(), f1.getY(), f2.getX(), f2.getY(), f3.getX(), f3.getY(), f1.getX(), f1.getY());

        System.out.println(f1.distance2DTo(f2) / f1.distance2DTo(f3));
        System.out.println(distance2DTo(p1_Prime,p2_Prime) / distance2DTo(p1_Prime,p3_Prime));
        System.out.println(f1.distance2DTo(f3) / f3.distance2DTo(f2));
        System.out.println(distance2DTo(p1_Prime,p3_Prime) / distance2DTo(p3_Prime,p2_Prime));
        System.out.println(f1.distance2DTo(f2) / f2.distance2DTo(f3));
        System.out.println(distance2DTo(p1_Prime,p2_Prime) / distance2DTo(p2_Prime,p3_Prime));

        double angle_f1f3f2 = angleBetweenTwoLine(f1.getX(), f1.getY(), f2.getX(), f2.getY(), f3.getX(), f3.getY());
        double angle_p1p3p2 = angleBetweenTwoLine(p1_Prime.x, p1_Prime.y, p2_Prime.x, p2_Prime.y, p3_Prime.x, p3_Prime.y);
        double angle_f1f2f3 = angleBetweenTwoLine(f1.getX(), f1.getY(), f3.getX(), f3.getY(), f2.getX(), f2.getY());
        double angle_p1p2p3 = angleBetweenTwoLine(p1_Prime.x, p1_Prime.y, p3_Prime.x, p3_Prime.y, p2_Prime.x, p2_Prime.y);
        double angle_f2f1f3 = angleBetweenTwoLine(f3.getX(), f3.getY(), f2.getX(), f2.getY(), f1.getX(), f1.getY());
        double angle_p2p1p3 = angleBetweenTwoLine(p3_Prime.x, p3_Prime.y, p2_Prime.x, p2_Prime.y, p1_Prime.x, p1_Prime.y);

        System.out.println("Angle between f1-f3 and f2-f3: " + angle_f1f3f2);
        System.out.println("Angle between p1-p3 and p2-p3: " + angle_p1p3p2);
        System.out.println("Angle between f1-f2 and f2-f3: " + angle_f1f2f3);
        System.out.println("Angle between p1-p2 and p2-p3: " + angle_p1p2p3);
        System.out.println("Angle between f1-f2 and f1-f3: " + angle_f2f1f3);
        System.out.println("Angle between p1-p2 and p1-p3: " + angle_p2p1p3);

        //assertTrue(Math.abs(angle_f1f3f2 - angle_p1p3p2) < 7);
        //assertTrue(Math.abs(angle_f1f2f3 - angle_p1p2p3) < 7);
        //assertTrue(Math.abs(angle_f2f1f3 - angle_p2p1p3) < 7);
    }
}
