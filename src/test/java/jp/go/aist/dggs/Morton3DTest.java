package jp.go.aist.dggs;

import jp.go.aist.dggs.geometry.ISEA4DFaceCoordinates;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * @author TaehoonKim AIST DPRT, Research Assistant
 * */
public class Morton3DTest {

    @Test
    public void encode() {
        Assert.assertEquals("036535360425151435157513012137203", Morton3D.encode(3158179513L,3594588765L,1789573128L, 0,32));
        Assert.assertEquals("0365353604251514351575130121372", Morton3D.encode(3158179513L,3594588765L,1789573128L,0, 30));
        Assert.assertEquals("0365353604251514351575", Morton3D.encode(3158179513L,3594588765L,1789573128L, 0,21));

        Assert.assertEquals("100000000000545677476566767547475", Morton3D.encode(1419627,243658,2097151, 1,32));
        Assert.assertEquals("10000000000", Morton3D.encode(1419627,243658,2097151, 1,10));

        Assert.assertEquals("000000000000000000000000000003650", Morton3D.encode(10,12,6, 0,32));
    }

    @Test
    public void decode() {
        ISEA4DFaceCoordinates result = Morton3D.decode("836535360425151435157513012137203");
        assertEquals(3158179513L, result.getX());
        assertEquals(3594588765L, result.getY());
        assertEquals(1789573128L, result.getZ());
        assertEquals(32, result.getResolution());

        ISEA4DFaceCoordinates result2 = Morton3D.decode("83650", 4);
        assertEquals(10, result2.getX());
        assertEquals(12, result2.getY());
        assertEquals(6, result2.getZ());
        assertEquals(4, result2.getResolution());

        ISEA4DFaceCoordinates result3 = Morton3D.decode("800000000000000000000000000003650");
        assertEquals(10L, result3.getX());
        assertEquals(12L, result3.getY());
        assertEquals(6L, result3.getZ());
        assertEquals(32, result3.getResolution());

        String code = "0545677476566767547475";
        ISEA4DFaceCoordinates result4 = Morton3D.decode(code, code.length() - 1);
        assertEquals(1419627L, result4.getX());
        assertEquals(243658L, result4.getY());
        assertEquals(2097151L, result4.getZ());
        assertEquals(code.length() - 1, result4.getResolution());
    }
}