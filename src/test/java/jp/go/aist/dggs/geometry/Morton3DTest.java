package jp.go.aist.dggs.geometry;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * @author TaehoonKim AIST DPRT, Research Assistant
 * */
public class Morton3DTest {
    private final int _iterations = 1000000;

    @Test
    public void encode() {
        assertEquals("036535360425151435157513012137203", Morton3D.encode(new ISEA4DFaceCoordinates(0,3158179513L,3594588765L,1789573128L, 32)));
        assertEquals("0365353604251514351575130121372", Morton3D.encode(new ISEA4DFaceCoordinates(0,3158179513L,3594588765L,1789573128L, 30)));
        assertEquals("0365353604251514351575", Morton3D.encode(new ISEA4DFaceCoordinates(0,3158179513L,3594588765L,1789573128L, 21)));

        assertEquals("100000000000545677476566767547475", Morton3D.encode(new ISEA4DFaceCoordinates(1,1419627,243658,2097151,32)));
        assertEquals("10000000000", Morton3D.encode(new ISEA4DFaceCoordinates(1,1419627,243658,2097151,10)));

        assertEquals("000000000000000000000000000003650", Morton3D.encode(new ISEA4DFaceCoordinates(0,10,12,6,32)));
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

    @Test
    public void encode_decode() {
        for(int i = 0; i < _iterations; i++) {
            long x = (long) (Math.random() * 4294967295L);
            long y = (long) (Math.random() * 4294967295L);
            long z = (long) (Math.random() * 16777215L);
            String pdCode = Morton3D.encode(new ISEA4DFaceCoordinates(1,x,y,z,32));
            ISEA4DFaceCoordinates result = Morton3D.decode(pdCode);
            assertEquals(x, result.getX());
            assertEquals(y, result.getY());
            assertEquals(z, result.getZ());
        }
    }
}