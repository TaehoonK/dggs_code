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
    public void getGreatestCommonAncestor() {
        String[] pdCodeList = {"1023","1023"};
        Assert.assertEquals("1023", MortonUtils.getGreatestCommonAncestor(pdCodeList));
    }

    @Test
    public void convertToMorton() {
        String mortonCode = MortonUtils.convertToMorton(34.6400223819d, 135.454610442d, 1.1d, 32);
        System.out.println("Morton Code (32) : " + mortonCode);
        assertEquals("803213125333023101316260525667721", mortonCode);
        Coordinate coords = MortonUtils.convertFromMorton(mortonCode);
        assertEquals(34.6400224, Math.round(coords.getX()*10000000)/10000000.0, 0.00000001);
        assertEquals(135.4546104, Math.round(coords.getY()*10000000)/10000000.0, 0.00000001);
        assertEquals(1.1, Math.round(coords.getZ()*10000)/10000.0, 0.00001);
    }

    @Test
    public void convertToMorton2() {
        String mortonCode = MortonUtils.convertToMorton(34.6400223819d, 135.454610442d, 1.1d, 30);
        //System.out.println("Morton Code (31) : " + mortonCode);
        //assertEquals("80321312533302310131626052566772", mortonCode);
        Coordinate coords = MortonUtils.convertFromMorton(mortonCode);
        assertEquals(34.6400224, Math.round(coords.getX()*10000000)/10000000.0, 0.00000001);
        assertEquals(135.4546104, Math.round(coords.getY()*10000000)/10000000.0, 0.00000001);
        assertEquals(1.1, Math.round(coords.getZ()*10000)/10000.0, 0.00001);
    }
}
