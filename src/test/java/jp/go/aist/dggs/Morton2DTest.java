package jp.go.aist.dggs;

import jp.go.aist.dggs.geometry.ISEA4DFaceCoordinates;
import jp.go.aist.dggs.geometry.Morton2D;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author TaehoonKim AIST DPRT, Research Assistant
 * */
public class Morton2DTest {
    private final int _iterations = 1000000;

    @Test
    public void encode() {
        Assert.assertEquals("100", Morton2D.encode(new ISEA4DFaceCoordinates(1,10,12,2)));
        Assert.assertEquals("10000", Morton2D.encode(new ISEA4DFaceCoordinates(1,10,12,4)));
        Assert.assertEquals("100000000000000000000000000003210", Morton2D.encode(new ISEA4DFaceCoordinates(1,10,12,32)));
        Assert.assertEquals("132131320021111031113113012133203", Morton2D.encode(new ISEA4DFaceCoordinates(1,3158179513L,3594588765L,32)));
        Assert.assertEquals("132131320021111031113", Morton2D.encode(new ISEA4DFaceCoordinates(1,3158179513L,3594588765L,20)));
        Assert.assertEquals("13213132002", Morton2D.encode(new ISEA4DFaceCoordinates(1,3158179513L,3594588765L,10)));
    }

    @Test
    public void decode() {
        ISEA4DFaceCoordinates result = Morton2D.decode("13210", 4);
        assertEquals(10, result.getX());
        assertEquals(12, result.getY());

        result = Morton2D.decode("132131320021111031113113012133203", 32);
        assertEquals(3158179513L, result.getX());
        assertEquals(3594588765L, result.getY());


    }

    @Test
    public void encode_decode() {
        for(int i = 0; i < _iterations; i++) {
            long x = (long) (Math.random() * 4294967295L);
            long y = (long) (Math.random() * 4294967295L);
            String pdCode = Morton2D.encode(new ISEA4DFaceCoordinates(1,x,y,32));
            ISEA4DFaceCoordinates result = Morton2D.decode(pdCode);
            assertEquals(x, result.getX());
            assertEquals(y, result.getY());
        }
    }
}