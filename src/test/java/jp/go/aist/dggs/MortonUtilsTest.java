package jp.go.aist.dggs;

import org.giscience.utils.geogrid.generic.Trigonometric;
import org.giscience.utils.geogrid.geometry.GeoCoordinates;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MortonUtilsTest {
    private final double _precision = 1e-5;
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
        assertEquals("821233112402000000211006037330223", mortonCode);

        Coordinate coords = MortonUtils.convertFromMorton(mortonCode);
        assertTrue(Math.abs(34.6400223819d - coords.getX()) < this._precision);
        assertTrue(Math.abs(135.454610432d - coords.getY()) < this._precision);
        assertTrue(Math.abs(1.1d - coords.getZ()) < this._precision_z);
    }


    @Test
    public void convertToMorton_Except1() throws Exception {
        GeoCoordinates c = new GeoCoordinates(2.273799d,-66.926215d);
        String mortonCode = MortonUtils.convertToMorton(c.getLat(), c.getLon(),0d, 32);
        Coordinate coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println(c.toString());
        System.out.println(coords.toString());
        assertTrue(Math.abs(coords.getX() - c.getLat()) < this._precision);
        assertTrue((Math.abs(coords.getY() - c.getLon()) % 180) * Trigonometric.cos(c.getLat()) < this._precision);
    }

    @Test
    public void convertToMorton_Except2() throws Exception {
        GeoCoordinates c = new GeoCoordinates(-45.670508d,-2.473789d);
        String mortonCode = MortonUtils.convertToMorton(c.getLat(), c.getLon(),0d, 32);
        Coordinate coords = MortonUtils.convertFromMorton(mortonCode);
        System.out.println(c.toString());
        System.out.println(coords.toString());
        assertTrue(Math.abs(coords.getX() - c.getLat()) < this._precision);
        assertTrue((Math.abs(coords.getY() - c.getLon()) % 180) * Trigonometric.cos(c.getLat()) < this._precision);
    }

    @Test
    public void convertToMorton_Random() throws Exception {
        for (int i = 0; i < this._iterations; i++) {
            GeoCoordinates c = new GeoCoordinates(Math.random() * 180 - 90, Math.random() * 360 - 180);
            String mortonCode = MortonUtils.convertToMorton(c.getLat(), c.getLon(), 0, DGGS.MAX_XY_RESOLUTION);
            Coordinate coords = MortonUtils.convertFromMorton(mortonCode);

            System.out.println(c.toString());
            System.out.println(coords.toString());

            assertTrue(Math.abs(coords.getX() - c.getLat()) < this._precision);
            assertTrue((Math.abs(coords.getY() - c.getLon()) % 180) * Trigonometric.cos(c.getLat()) < this._precision);
        }
    }

    @Test
    public void convertToMorton_resolution() {
        String mortonCode = MortonUtils.convertToMorton(34.6400223819d, 135.454610432d, 20d, 30);
        Coordinate coords = MortonUtils.convertFromMorton(mortonCode, 25);
        System.out.println(coords.toString());
        coords = MortonUtils.convertFromMorton(mortonCode, 25);
        System.out.println(coords.toString());
        coords = MortonUtils.convertFromMorton(mortonCode, 20);
        System.out.println(coords.toString());

        coords = MortonUtils.convertFromMorton(mortonCode, 15);
        System.out.println(coords.toString());
        assertEquals(34.64, Math.round(coords.getX()*100)/100.0, 0.001);
        assertEquals(135.45, Math.round(coords.getY()*100)/100.0, 0.001);
        assertEquals(0, Math.round(coords.getZ()*10000)/10000.0, 0.01);
    }
}
