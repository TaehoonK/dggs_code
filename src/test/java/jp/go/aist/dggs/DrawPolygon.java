package jp.go.aist.dggs;

import jp.go.aist.dggs.geometry.ISEA4DFaceCoordinates;
import org.giscience.utils.geogrid.geometry.FaceCoordinates;
import org.giscience.utils.geogrid.geometry.GeoCoordinates;
import org.giscience.utils.geogrid.projections.ISEAProjection;
import org.junit.Test;
import org.locationtech.proj4j.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class DrawPolygon extends JPanel {
    private static final int canvas_width = 800;
    private static final int canvas_height = 800;
    Polygon p_on_WGS;
    Polygon p_on_UTM;
    Polygon p_on_ISEA;
    Polygon p_on_ISEA2;

    double[][] polygonWGSCoords;
    double[][] polygonUTMCoords;
    double[][] polygonISEACoords2;
    long[][] polygonISEACoords;

    public DrawPolygon() throws Exception {
        geometryCreation();
        p_on_WGS = new Polygon();
        p_on_UTM = new Polygon();
        p_on_ISEA = new Polygon();
        p_on_ISEA2 = new Polygon();

        double[] minWGS = {polygonWGSCoords[0][0], polygonWGSCoords[0][1]}, maxWGS = {polygonWGSCoords[0][0],polygonWGSCoords[0][1]};
        double[] minUTM = {polygonUTMCoords[0][0], polygonUTMCoords[0][1]}, maxUTM = {polygonUTMCoords[0][0],polygonUTMCoords[0][1]};
        long[] minISEA = {polygonISEACoords[0][0], polygonISEACoords[0][1]}, maxISEA = {polygonISEACoords[0][0], polygonISEACoords[0][1]};
        double[] minISEA2 = {polygonISEACoords2[0][0], polygonISEACoords2[0][1]}, maxISEA2 = {polygonISEACoords2[0][0], polygonISEACoords2[0][1]};

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

        for(double[] coords : polygonUTMCoords) {
            if(minUTM[0] > coords[0]) minUTM[0] = coords[0];
            if(minUTM[1] > coords[1]) minUTM[1] = coords[1];
            if(maxUTM[0] < coords[0]) maxUTM[0] = coords[0];
            if(maxUTM[1] < coords[1]) maxUTM[1] = coords[1];
        }
        double boundaryUTM;
        if(usedX)
            boundaryUTM = Math.abs(maxUTM[0] - minUTM[0]);
        else
            boundaryUTM = Math.abs(maxUTM[1] - minUTM[1]);

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

        for(double[] coords : polygonISEACoords2) {
            if(minISEA2[0] > coords[0]) minISEA2[0] = coords[0];
            if(minISEA2[1] > coords[1]) minISEA2[1] = coords[1];
            if(maxISEA2[0] < coords[0]) maxISEA2[0] = coords[0];
            if(maxISEA2[1] < coords[1]) maxISEA2[1] = coords[1];
        }
        double boundaryISEA2;
        if(usedX)
            boundaryISEA2 = Math.abs(maxISEA2[0] - minISEA2[0]);
        else
            boundaryISEA2 = Math.abs(maxISEA2[1] - minISEA2[1]);

        for(int i = 0; i < polygonUTMCoords.length; i++) {
            polygonWGSCoords[i][0] = (polygonWGSCoords[i][0] - minWGS[0]) / boundaryWGS * canvas_width + 50;
            polygonWGSCoords[i][1] = canvas_height - (polygonWGSCoords[i][1] - minWGS[1]) / boundaryWGS * canvas_height + 50;
            p_on_WGS.addPoint((int) polygonWGSCoords[i][0], (int) polygonWGSCoords[i][1]);

            polygonUTMCoords[i][0] = (polygonUTMCoords[i][0] - minUTM[0]) / boundaryUTM * canvas_width + 50;
            polygonUTMCoords[i][1] = canvas_height - (polygonUTMCoords[i][1] - minUTM[1]) / boundaryUTM * canvas_height + 50;
            p_on_UTM.addPoint((int) polygonUTMCoords[i][0], (int) polygonUTMCoords[i][1]);

            polygonISEACoords[i][0] = (polygonISEACoords[i][0] - minISEA[0]) * canvas_width / boundaryISEA + 50;
            polygonISEACoords[i][1] = canvas_height - (polygonISEACoords[i][1] - minISEA[1]) * canvas_height / boundaryISEA + 50;
            p_on_ISEA.addPoint((int) polygonISEACoords[i][0], (int) polygonISEACoords[i][1]);

            polygonISEACoords2[i][0] = (polygonISEACoords2[i][0] - minISEA2[0]) * canvas_width / boundaryISEA2 + 50;
            polygonISEACoords2[i][1] = canvas_height - (polygonISEACoords2[i][1] - minISEA2[1]) * canvas_height / boundaryISEA2 + 50;
            p_on_ISEA2.addPoint((int) polygonISEACoords2[i][0], (int) polygonISEACoords2[i][1]);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.drawPolygon(p_on_WGS);
        g.setColor(Color.BLUE);
        g.drawPolygon(p_on_UTM);
        g.setColor(Color.RED);
        g.drawPolygon(p_on_ISEA);
        g.setColor(Color.MAGENTA);
        g.drawPolygon(p_on_ISEA2);
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

    public void geometryCreation() throws Exception {
        final double boundary = 0.000001;

        // Reference EPSG:3095 (https://epsg.io/3095)
        GeoCoordinates c1 = new GeoCoordinates(Math.random() * boundary + 30.37, Math.random() * boundary + 132.83);
        GeoCoordinates c2 = new GeoCoordinates(Math.random() * boundary + 30.37, Math.random() * boundary + 132.83);
        GeoCoordinates c3 = new GeoCoordinates(Math.random() * boundary + 30.37, Math.random() * boundary + 132.83);

        polygonWGSCoords = new double[][]{{c1.getLon(), c1.getLat()}, {c2.getLon(), c2.getLat()}, {c3.getLon(), c3.getLat()}, {c1.getLon(), c1.getLat()}};

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

        polygonUTMCoords = new double[][]{{p1_Prime.x, p1_Prime.y}, {p2_Prime.x, p2_Prime.y}, {p3_Prime.x, p3_Prime.y}, {p1_Prime.x, p1_Prime.y}};

        ISEAProjection p = new ISEAProjection();
        p.setOrientation(0,0);
        FaceCoordinates orgin_f1 = p.sphereToIcosahedron(c1);
        FaceCoordinates orgin_f2 = p.sphereToIcosahedron(c2);
        FaceCoordinates orgin_f3 = p.sphereToIcosahedron(c3);

        polygonISEACoords2 = new double[][]{{orgin_f1.getX(), orgin_f1.getY()}, {orgin_f2.getX(), orgin_f2.getY()}, {orgin_f3.getX(), orgin_f3.getY()}, {orgin_f1.getX(), orgin_f1.getY()}};

        ISEA4DFaceCoordinates f1 = Objects.requireNonNull(MortonUtils.toFaceCoordinate(c1)).toOrthogonal();
        ISEA4DFaceCoordinates f2 = Objects.requireNonNull(MortonUtils.toFaceCoordinate(c2)).toOrthogonal();
        ISEA4DFaceCoordinates f3 = Objects.requireNonNull(MortonUtils.toFaceCoordinate(c3)).toOrthogonal();

        polygonISEACoords = new long[][]{{f1.getX(), f1.getY()}, {f2.getX(), f2.getY()}, {f3.getX(), f3.getY()}, {f1.getX(), f1.getY()}};

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
        frame.setTitle("Polygon");
        frame.setSize(canvas_width + 100, canvas_height + 100);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        Container contentPane = frame.getContentPane();
        contentPane.add(new DrawPolygon());
        frame.setVisible(true);
    }
}

