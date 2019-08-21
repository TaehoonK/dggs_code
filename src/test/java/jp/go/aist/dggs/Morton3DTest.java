package jp.go.aist.dggs;
import org.junit.Assert;
import org.junit.Test;
import java.math.BigInteger;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author TaehoonKim AIST DPRT, Research Assistant
 * */
public class Morton3DTest {

    @Test
    public void encode() {
        Assert.assertEquals("03650", Morton3D.encode(10,12,6, 0,4));
        Assert.assertEquals("3650", Morton3D.encode(10,12,6, 4));
    }

    @Test
    public void encodeLong() {
        Assert.assertEquals("036535360425151435157513012137203", Morton3D.encode(3158179513L,3594588765L,1789573128L, 0,32));
        Assert.assertEquals("36535360425151435157513012137203", Morton3D.encode(3158179513L,3594588765L,1789573128L,32));
        Assert.assertEquals("0365353604251514351575", Morton3D.encode(3158179513L,3594588765L,1789573128L, 0,21));
    }

    @Test
    public void decode() {
        long[] result = Morton3D.decode("3650");
        assertEquals(10, result[0]);
        assertEquals(12, result[1]);
        assertEquals(6, result[2]);
    }

    @Test
    public void decodeLong() {
        long[] result = Morton3D.decode("36535360425151435157513012137203");
        assertEquals(3158179513L, result[0]);
        assertEquals(3594588765L, result[1]);
        assertEquals(1789573128L, result[2]);
    }

    @Test
    public void getPDCode() {
        assertEquals("3650", Morton3D.getPDCode(1960L));
        assertEquals("365353604251514351575", Morton3D.getPDCode(4421822648190882685L));
        assertEquals("3650", Morton3D.getPDCode(new BigInteger("1960")));
        assertEquals("365353604251514351575", Morton3D.getPDCode(new BigInteger("4421822648190882685")));
        assertEquals("36535360425151435157513012137203", Morton3D.getPDCode(new BigInteger("37983167325383909396374404739")));
    }

    @Test
    public void getMCode() {
        assertEquals(1960L, Morton3D.getMCode_old("000000000000000003650"));
        assertEquals(1960L, Morton3D.getMCode_old("3650"));
        assertEquals(4421822648190882685L, Morton3D.getMCode_old("365353604251514351575"));
        assertEquals(new BigInteger("1960"), Morton3D.getMCode("000000000000000003650"));
        assertEquals(new BigInteger("1960"), Morton3D.getMCode("3650"));
        assertEquals(new BigInteger("4421822648190882685"), Morton3D.getMCode("365353604251514351575"));
        assertEquals(new BigInteger("37983167325383909396374404739"), Morton3D.getMCode("36535360425151435157513012137203"));
    }

    @Test
    public void encodeBitOp() {
        Assert.assertEquals("000000000000000000000000000003650", Morton3D.encode(10,12,6, 0,32));
        Assert.assertEquals("00000000000000000000000000003650", Morton3D.encode(10,12,6,32));
    }

    @Test
    public void encodeBitOpLong() {
        Assert.assertEquals("000000000000545677476566767547475", Morton3D.encode(1419627,243658,2097151, 0,32));
        Assert.assertEquals("00000000000545677476566767547475", Morton3D.encode(1419627,243658,2097151,32));
        Assert.assertEquals("00000000000", Morton3D.encode(1419627,243658,2097151, 0,10));
        Assert.assertEquals("0000000000", Morton3D.encode(1419627,243658,2097151,10));

        Assert.assertEquals("36535360425151435157513012137203", Morton3D.encode(3158179513L,3594588765L,1789573128L,32));
        Assert.assertEquals("365353604251514351575130121372", Morton3D.encode(3158179513L,3594588765L,1789573128L,30));
        long[] result2 = Morton3D.decode("36535360425151435157513012137203");
        assertEquals(3158179513L, result2[0]);
        assertEquals(3594588765L, result2[1]);
        assertEquals(1789573128L, result2[2]);
    }

    @Test
    public void decodeBitOp() {
        long[] result = Morton3D.decode("3650");
        assertEquals(10L, result[0]);
        assertEquals(12L, result[1]);
        assertEquals(6L, result[2]);

        long[] result2 = Morton3D.decode("000000000000000003650");
        assertEquals(10L, result2[0]);
        assertEquals(12L, result2[1]);
        assertEquals(6L, result2[2]);
    }

    @Test
    public void decodeBitOpLong() {
        long[] result = Morton3D.decode("545677476566767547475");
        assertEquals(1419627L, result[0]);
        assertEquals(243658L, result[1]);
        assertEquals(2097151L, result[2]);

        long[] result2 = Morton3D.decode("36535360425151435157513012137203");
        assertEquals(3158179513L, result2[0]);
        assertEquals(3594588765L, result2[1]);
        assertEquals(1789573128L, result2[2]);
    }
}