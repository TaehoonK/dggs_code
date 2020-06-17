package jp.go.aist.dggs;

import org.giscience.utils.geogrid.generic.Trigonometric;
import org.giscience.utils.geogrid.geometry.FaceCoordinates;
import org.giscience.utils.geogrid.geometry.GeoCoordinates;
import org.giscience.utils.geogrid.projections.ISEAProjection;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MortonUtilsTest {
    private final double _precision = 1e-7;
    private final double _precision_z = 1e-2;
    private final int _iterations = 1000000;

    @Test
    public void getCommonAncestor() {
        String[] pdCodeList = {"12343241532167","12342451","1234512983012308"};
        Assert.assertEquals("1234", MortonUtils.getGreatestCommonAncestor(pdCodeList));
    }

    @Test
    public void getCommonAncestor_NotEqualFace() {
        String[] pdCodeList = {"12343241532167","22342451","3234512983012308"};
        assertEquals("", MortonUtils.getGreatestCommonAncestor(pdCodeList));
    }

    @Test
    public void getCommonAncestor_Equal() {
        String[] pdCodeList = {"1023","1023"};
        Assert.assertEquals("1023", MortonUtils.getGreatestCommonAncestor(pdCodeList));
    }

    @Test
    public void convertToMorton() {
        String mortonCode = MortonUtils.convertToMorton(34.6400223819d, 135.454610432d, 1.1d, 32);
        Coordinate coords = MortonUtils.convertFromMorton(mortonCode);
        assertTrue(Math.abs(34.6400223819d - coords.getX()) < this._precision);
        assertTrue(Math.abs(135.454610432d - coords.getY()) < this._precision);
        assertTrue(Math.abs(1.1d - coords.getZ()) < this._precision_z);
    }


    public void convertToMorton_TriangleCheck() {
        double _goldenRatio = (1 + Math.sqrt(5)) / 2.;
        double F_DEG = Math.toDegrees(Math.atan(1 / (2 * Math.pow(_goldenRatio, 2))));
        double _g = F_DEG + 2 * Math.toDegrees(Math.atan(_goldenRatio)) - 90;
        double E_DEG = 90 - _g;
        System.out.println(E_DEG + " & " + F_DEG);

        String mortonCode = MortonUtils.convertToMorton(E_DEG,-144d, 0.0d, 32);
        Coordinate coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("1:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(E_DEG,-72d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("2:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(E_DEG,0, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("3:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(E_DEG,72d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("4:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(E_DEG,144d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("5:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(F_DEG,-144d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("6:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(F_DEG,-72d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("7:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(F_DEG,0, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("8:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(F_DEG,72d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("9:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(F_DEG,144d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("10:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(-F_DEG,-108d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("11:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(-F_DEG,-36d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("12:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(-F_DEG,36d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("13:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(-F_DEG,108d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("14:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(-F_DEG,180d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("15:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(-E_DEG,-108d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("16:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(-E_DEG,-36d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("17:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(-E_DEG,36d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("18:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(-E_DEG,108d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("19:" + coords.toString());
        mortonCode = MortonUtils.convertToMorton(-E_DEG,180d, 0.0d, 32);
        coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println("20:" + coords.toString());
    }

    public void convertToMorton_Check_MIN() throws Exception {
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

    @Test
    public void convertToMorton_Random() throws Exception {
        for (int i = 0; i < this._iterations; i++) {
            GeoCoordinates c = new GeoCoordinates(Math.random() * 179.99 - 89.995, Math.random() * 360.0 - 180.0);
            double height = Math.random() * DGGS.H_RANGE * 2 - DGGS.H_RANGE;
            String mortonCode = MortonUtils.convertToMorton(c.getLat(), c.getLon(), height, DGGS.MAX_XY_RESOLUTION);
            Coordinate coords = MortonUtils.convertFromMorton(mortonCode);

            assertTrue(Math.abs(coords.getX() - c.getLat()) < this._precision);
            assertTrue((Math.abs(coords.getY() - c.getLon()) % 180) * Trigonometric.cos(c.getLat()) < this._precision);
            assertTrue(Math.abs(coords.getZ() - height) < this._precision_z);
        }
    }

    @Test
    public void convertToMorton_resolution() throws Exception {
        GeoCoordinates c = new GeoCoordinates(34.6400223819d, 135.454610432d);
        String mortonCode = MortonUtils.convertToMorton(c.getLat(), c.getLon(), 0d, DGGS.MAX_XY_RESOLUTION);
        Coordinate coords = MortonUtils.convertFromMorton(mortonCode, DGGS.MAX_XY_RESOLUTION);
        assertTrue(Math.abs(coords.getX() - c.getLat()) < this._precision);
        assertTrue((Math.abs(coords.getY() - c.getLon()) % 180) * Trigonometric.cos(c.getLat()) < this._precision);

        coords = MortonUtils.convertFromMorton(mortonCode, 25);
        assertTrue(Math.abs(coords.getX() - c.getLat()) < this._precision * 100);
        assertTrue((Math.abs(coords.getY() - c.getLon()) % 180) * Trigonometric.cos(c.getLat()) < this._precision * 100);

        coords = MortonUtils.convertFromMorton(mortonCode, 20);
        assertTrue(Math.abs(coords.getX() - c.getLat()) < this._precision * 1000);
        assertTrue((Math.abs(coords.getY() - c.getLon()) % 180) * Trigonometric.cos(c.getLat()) < this._precision * 1000);

        coords = MortonUtils.convertFromMorton(mortonCode, 15);
        assertTrue(Math.abs(coords.getX() - c.getLat()) < this._precision * 20000);
        assertTrue((Math.abs(coords.getY() - c.getLon()) % 180) * Trigonometric.cos(c.getLat()) < this._precision * 20000);
    }

    @Test
    public void convertToMorton_resolution2() {
        String mortonCode = MortonUtils.convertToMorton(51.335624458573875d, 171.32047771529926d, 9986.692580252009d, 30);
        Coordinate coords = MortonUtils.convertFromMorton(mortonCode, 25);
        System.out.println(coords.toString());
        coords = MortonUtils.convertFromMorton(mortonCode, 25);
        System.out.println(coords.toString());
        coords = MortonUtils.convertFromMorton(mortonCode, 20);
        System.out.println(coords.toString());
    }
}
