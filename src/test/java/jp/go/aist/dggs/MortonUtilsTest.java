package jp.go.aist.dggs;

import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import static org.junit.Assert.assertEquals;

public class MortonUtilsTest {
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
        assertEquals("803213121733023101312224125223321", mortonCode);

        Coordinate coords = MortonUtils.convertFromMorton(mortonCode);
        assertEquals(34.64002238, Math.round(coords.getX()*10000000)/10000000.0, 0.0000001);
        assertEquals(135.45461043, Math.round(coords.getY()*10000000)/10000000.0, 0.0000001);
        assertEquals(1.1, Math.round(coords.getZ()*10000)/10000.0, 0.01);
    }

    @Test
    public void convertToMorton2() {
        String mortonCode = MortonUtils.convertToMorton(34.6400223819d, 135.454610432d, 1.1d, 30);

        assertEquals("8032131217330231013122241252233", mortonCode);
        Coordinate coords = MortonUtils.convertFromMorton(mortonCode);
        assertEquals(34.6400224, Math.round(coords.getX()*10000000)/10000000.0, 0.00000001);
        assertEquals(135.4546104, Math.round(coords.getY()*10000000)/10000000.0, 0.00000001);
        assertEquals(1.1, Math.round(coords.getZ()*10000)/10000.0, 0.01);
    }

    @Test
    public void convertToMorton_resolution() {
        String mortonCode = MortonUtils.convertToMorton(34.6400223819d, 135.454610432d, 20d, 30);
        Coordinate coords = MortonUtils.convertFromMorton(mortonCode, 16);
        System.out.println(coords.getX());
        System.out.println(coords.getY());
        System.out.println(coords.getZ());
        assertEquals(34.64, Math.round(coords.getX()*100)/100.0, 0.001);
        assertEquals(135.45, Math.round(coords.getY()*100)/100.0, 0.001);
        assertEquals(0, Math.round(coords.getZ()*10000)/10000.0, 0.01);
    }
}
