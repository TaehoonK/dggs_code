package jp.go.aist.dggs.geometry;

import jp.go.aist.dggs.utils.MortonUtils;
import org.giscience.utils.geogrid.geometry.FaceCoordinates;
import org.giscience.utils.geogrid.geometry.GeoCoordinates;
import org.giscience.utils.geogrid.projections.ISEAProjection;
import org.locationtech.proj4j.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class CRSCompareTest extends JPanel {
    private static final int canvas_width = 600;
    private static final int canvas_height = 600;
    Polygon p_on_WGS;
    Polygon p_on_UTM1;
    Polygon p_on_UTM2;
    Polygon p_on_UTM3;
    Polygon p_on_ISEA;
    Polygon p_on_ISEA_original;

    double[][] polygonWGSCoords;
    double[][] polygonUTM1Coords;
    double[][] polygonUTM2Coords;
    double[][] polygonUTM3Coords;
    double[][] polygonISEAOriginCoords;
    long[][] polygonISEACoords;

    public CRSCompareTest() throws Exception {
        randomGeometryCreation();
        p_on_WGS = new Polygon();
        p_on_UTM1 = new Polygon();
        p_on_UTM2 = new Polygon();
        p_on_UTM3 = new Polygon();
        p_on_ISEA = new Polygon();
        p_on_ISEA_original = new Polygon();

        double[] minWGS = {polygonWGSCoords[0][0], polygonWGSCoords[0][1]}, maxWGS = {polygonWGSCoords[0][0],polygonWGSCoords[0][1]};
        double[] minUTM1 = {polygonUTM1Coords[0][0], polygonUTM1Coords[0][1]}, maxUTM1 = {polygonUTM1Coords[0][0], polygonUTM1Coords[0][1]};
        double[] minUTM2 = {polygonUTM2Coords[0][0], polygonUTM2Coords[0][1]}, maxUTM2 = {polygonUTM2Coords[0][0], polygonUTM2Coords[0][1]};
        double[] minUTM3 = {polygonUTM3Coords[0][0], polygonUTM3Coords[0][1]}, maxUTM3 = {polygonUTM3Coords[0][0], polygonUTM3Coords[0][1]};
        double[] minISEA_Origin = {polygonISEAOriginCoords[0][0], polygonISEAOriginCoords[0][1]}, maxISEA_Origin = {polygonISEAOriginCoords[0][0], polygonISEAOriginCoords[0][1]};
        long[] minISEA = {polygonISEACoords[0][0], polygonISEACoords[0][1]}, maxISEA = {polygonISEACoords[0][0], polygonISEACoords[0][1]};

        for(double[] coords : polygonWGSCoords) {
            if(minWGS[0] > coords[0]) minWGS[0] = coords[0];
            if(minWGS[1] > coords[1]) minWGS[1] = coords[1];
            if(maxWGS[0] < coords[0]) maxWGS[0] = coords[0];
            if(maxWGS[1] < coords[1]) maxWGS[1] = coords[1];
        }

        boolean usedX = true;
        double boundaryWGS;
        if(Math.abs(maxWGS[0] - minWGS[0]) < Math.abs(maxWGS[1] - minWGS[1]))
            usedX = false;

        if(usedX)
            boundaryWGS = Math.abs(maxWGS[0] - minWGS[0]);
        else
            boundaryWGS = Math.abs(maxWGS[1] - minWGS[1]);

        for(double[] coords : polygonUTM1Coords) {
            if(minUTM1[0] > coords[0]) minUTM1[0] = coords[0];
            if(minUTM1[1] > coords[1]) minUTM1[1] = coords[1];
            if(maxUTM1[0] < coords[0]) maxUTM1[0] = coords[0];
            if(maxUTM1[1] < coords[1]) maxUTM1[1] = coords[1];
        }
        double boundaryUTM1;
        if(usedX)
            boundaryUTM1 = Math.abs(maxUTM1[0] - minUTM1[0]);
        else
            boundaryUTM1 = Math.abs(maxUTM1[1] - minUTM1[1]);

        for(double[] coords : polygonUTM2Coords) {
            if(minUTM2[0] > coords[0]) minUTM2[0] = coords[0];
            if(minUTM2[1] > coords[1]) minUTM2[1] = coords[1];
            if(maxUTM2[0] < coords[0]) maxUTM2[0] = coords[0];
            if(maxUTM2[1] < coords[1]) maxUTM2[1] = coords[1];
        }
        double boundaryUTM2;
        if(usedX)
            boundaryUTM2 = Math.abs(maxUTM2[0] - minUTM2[0]);
        else
            boundaryUTM2 = Math.abs(maxUTM2[1] - minUTM2[1]);

        for(double[] coords : polygonUTM3Coords) {
            if(minUTM3[0] > coords[0]) minUTM3[0] = coords[0];
            if(minUTM3[1] > coords[1]) minUTM3[1] = coords[1];
            if(maxUTM3[0] < coords[0]) maxUTM3[0] = coords[0];
            if(maxUTM3[1] < coords[1]) maxUTM3[1] = coords[1];
        }
        double boundaryUTM3;
        if(usedX)
            boundaryUTM3 = Math.abs(maxUTM3[0] - minUTM3[0]);
        else
            boundaryUTM3 = Math.abs(maxUTM3[1] - minUTM3[1]);

        for(long[] coords : polygonISEACoords) {
            if(minISEA[0] > coords[0]) minISEA[0] = coords[0];
            if(minISEA[1] > coords[1]) minISEA[1] = coords[1];
            if(maxISEA[0] < coords[0]) maxISEA[0] = coords[0];
            if(maxISEA[1] < coords[1]) maxISEA[1] = coords[1];
        }
        long boundaryISEA;
        if(usedX)
            boundaryISEA = Math.abs(maxISEA[0] - minISEA[0]);
        else
            boundaryISEA = Math.abs(maxISEA[1] - minISEA[1]);

        for(double[] coords : polygonISEAOriginCoords) {
            if(minISEA_Origin[0] > coords[0]) minISEA_Origin[0] = coords[0];
            if(minISEA_Origin[1] > coords[1]) minISEA_Origin[1] = coords[1];
            if(maxISEA_Origin[0] < coords[0]) maxISEA_Origin[0] = coords[0];
            if(maxISEA_Origin[1] < coords[1]) maxISEA_Origin[1] = coords[1];
        }
        double boundaryISEA_Origin;
        if(usedX)
            boundaryISEA_Origin = Math.abs(maxISEA_Origin[0] - minISEA_Origin[0]);
        else
            boundaryISEA_Origin = Math.abs(maxISEA_Origin[1] - minISEA_Origin[1]);

        for(int i = 0; i < polygonUTM1Coords.length; i++) {
            polygonWGSCoords[i][0] = (polygonWGSCoords[i][0] - minWGS[0]) / boundaryWGS * canvas_width + 50;
            polygonWGSCoords[i][1] = canvas_height - (polygonWGSCoords[i][1] - minWGS[1]) / boundaryWGS * canvas_height + 50;
            p_on_WGS.addPoint((int) polygonWGSCoords[i][0], (int) polygonWGSCoords[i][1]);

            polygonUTM1Coords[i][0] = (polygonUTM1Coords[i][0] - minUTM1[0]) / boundaryUTM1 * canvas_width + 50;
            polygonUTM1Coords[i][1] = canvas_height - (polygonUTM1Coords[i][1] - minUTM1[1]) / boundaryUTM1 * canvas_height + 50;
            p_on_UTM1.addPoint((int) polygonUTM1Coords[i][0], (int) polygonUTM1Coords[i][1]);

            polygonUTM2Coords[i][0] = (polygonUTM2Coords[i][0] - minUTM2[0]) / boundaryUTM2 * canvas_width + 50;
            polygonUTM2Coords[i][1] = canvas_height - (polygonUTM2Coords[i][1] - minUTM2[1]) / boundaryUTM2 * canvas_height + 50;
            p_on_UTM2.addPoint((int) polygonUTM2Coords[i][0], (int) polygonUTM2Coords[i][1]);

            polygonUTM3Coords[i][0] = (polygonUTM3Coords[i][0] - minUTM3[0]) / boundaryUTM3 * canvas_width + 50;
            polygonUTM3Coords[i][1] = canvas_height - (polygonUTM3Coords[i][1] - minUTM3[1]) / boundaryUTM3 * canvas_height + 50;
            p_on_UTM3.addPoint((int) polygonUTM3Coords[i][0], (int) polygonUTM3Coords[i][1]);

            polygonISEACoords[i][0] = (polygonISEACoords[i][0] - minISEA[0]) * canvas_width / boundaryISEA + 50;
            polygonISEACoords[i][1] = canvas_height - (polygonISEACoords[i][1] - minISEA[1]) * canvas_height / boundaryISEA + 50;
            p_on_ISEA.addPoint((int) polygonISEACoords[i][0], (int) polygonISEACoords[i][1]);

            polygonISEAOriginCoords[i][0] = (polygonISEAOriginCoords[i][0] - minISEA_Origin[0]) * canvas_width / boundaryISEA_Origin + 50;
            polygonISEAOriginCoords[i][1] = canvas_height - (polygonISEAOriginCoords[i][1] - minISEA_Origin[1]) * canvas_height / boundaryISEA_Origin + 50;
            p_on_ISEA_original.addPoint((int) polygonISEAOriginCoords[i][0], (int) polygonISEAOriginCoords[i][1]);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.drawPolygon(p_on_WGS);
        g.drawString("EPSG:4326", 10,10);
        g.setColor(Color.BLUE);
        g.drawPolygon(p_on_UTM1);
        g.drawString("EPSG:6674 (UTM)", 10,25);
        g.setColor(Color.GREEN);
        g.drawPolygon(p_on_UTM2);
        g.drawString("EPSG:3095 (UTM)", 10,40);
        g.setColor(Color.ORANGE);
        g.drawPolygon(p_on_UTM3);
        g.drawString("EPSG:32653 (UTM)", 10,55);
        g.setColor(Color.MAGENTA);
        g.drawPolygon(p_on_ISEA_original);
        g.drawString("ISEA_original", 10,70);
        g.setColor(Color.RED);
        g.drawPolygon(p_on_ISEA);
        g.drawString("ISEA4D", 10,85);

    }

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

    public void randomGeometryCreation() {
        final double random_boundary = 0.000001;

        // Reference EPSG:3095 (https://epsg.io/3095)
        GeoCoordinates c1 = new GeoCoordinates(Math.random() * random_boundary + 34, Math.random() * random_boundary + 135);
        GeoCoordinates c2 = new GeoCoordinates(Math.random() * random_boundary + 34, Math.random() * random_boundary + 135);
        GeoCoordinates c3 = new GeoCoordinates(Math.random() * random_boundary + 34, Math.random() * random_boundary + 135);

        polygonWGSCoords = new double[][]{{c1.getLon(), c1.getLat()}, {c2.getLon(), c2.getLat()}, {c3.getLon(), c3.getLat()}, {c1.getLon(), c1.getLat()}};

        String csNameFrom = "EPSG:4326";// https://epsg.io/4326
        String csNameTo1 = "EPSG:6674"; // https://epsg.io/6674
        String csNameTo2 = "EPSG:3095"; // https://epsg.io/3095
        String csNameTo3 = "EPSG:32653";// https://epsg.io/32653

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CRSFactory csFactory = new CRSFactory();
        CoordinateReferenceSystem crsFrom = csFactory.createFromName(csNameFrom);
        CoordinateReferenceSystem crsTo1 = csFactory.createFromName(csNameTo1);
        CoordinateTransform trans = ctFactory.createTransform(crsFrom, crsTo1);

        ProjCoordinate p1 = new ProjCoordinate(c1.getLon(), c1.getLat());
        ProjCoordinate p2 = new ProjCoordinate(c2.getLon(), c2.getLat());
        ProjCoordinate p3 = new ProjCoordinate(c3.getLon(), c3.getLat());

        ProjCoordinate p1_Prime = new ProjCoordinate();
        ProjCoordinate p2_Prime = new ProjCoordinate();
        ProjCoordinate p3_Prime = new ProjCoordinate();

        trans.transform(p1, p1_Prime);
        trans.transform(p2, p2_Prime);
        trans.transform(p3, p3_Prime);

        polygonUTM1Coords = new double[][]{{p1_Prime.x, p1_Prime.y}, {p2_Prime.x, p2_Prime.y}, {p3_Prime.x, p3_Prime.y}, {p1_Prime.x, p1_Prime.y}};

        CoordinateReferenceSystem crsTo2 = csFactory.createFromName(csNameTo2);
        trans = ctFactory.createTransform(crsFrom, crsTo2);
        p1_Prime = new ProjCoordinate();
        p2_Prime = new ProjCoordinate();
        p3_Prime = new ProjCoordinate();
        trans.transform(p1, p1_Prime);
        trans.transform(p2, p2_Prime);
        trans.transform(p3, p3_Prime);

        polygonUTM2Coords = new double[][]{{p1_Prime.x, p1_Prime.y}, {p2_Prime.x, p2_Prime.y}, {p3_Prime.x, p3_Prime.y}, {p1_Prime.x, p1_Prime.y}};

        CoordinateReferenceSystem crsTo3 = csFactory.createFromName(csNameTo3);
        trans = ctFactory.createTransform(crsFrom, crsTo3);
        p1_Prime = new ProjCoordinate();
        p2_Prime = new ProjCoordinate();
        p3_Prime = new ProjCoordinate();
        trans.transform(p1, p1_Prime);
        trans.transform(p2, p2_Prime);
        trans.transform(p3, p3_Prime);

        polygonUTM3Coords = new double[][]{{p1_Prime.x, p1_Prime.y}, {p2_Prime.x, p2_Prime.y}, {p3_Prime.x, p3_Prime.y}, {p1_Prime.x, p1_Prime.y}};

        ISEAProjection p = new ISEAProjection();
        p.setOrientation(0,0);
        FaceCoordinates orgin_f1 = p.sphereToIcosahedron(c1);
        FaceCoordinates orgin_f2 = p.sphereToIcosahedron(c2);
        FaceCoordinates orgin_f3 = p.sphereToIcosahedron(c3);

        polygonISEAOriginCoords = new double[][]{{orgin_f1.getX(), orgin_f1.getY()}, {orgin_f2.getX(), orgin_f2.getY()}, {orgin_f3.getX(), orgin_f3.getY()}, {orgin_f1.getX(), orgin_f1.getY()}};

        ISEA4DFaceCoordinates f1 = Objects.requireNonNull(MortonUtils.toFaceCoordinate(c1)).toOrthogonal();
        ISEA4DFaceCoordinates f2 = Objects.requireNonNull(MortonUtils.toFaceCoordinate(c2)).toOrthogonal();
        ISEA4DFaceCoordinates f3 = Objects.requireNonNull(MortonUtils.toFaceCoordinate(c3)).toOrthogonal();

        polygonISEACoords = new long[][]{{f1.getX(), f1.getY()}, {f2.getX(), f2.getY()}, {f3.getX(), f3.getY()}, {f1.getX(), f1.getY()}};
        System.out.println(f1.toString());
        System.out.println(f2.toString());
        System.out.println(f3.toString());

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
    }

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame();
        frame.setTitle("Canvas");
        frame.setSize(canvas_width + 100, canvas_height + 100);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        Container contentPane = frame.getContentPane();
        contentPane.add(new CRSCompareTest());
        frame.setVisible(true);
    }
}

