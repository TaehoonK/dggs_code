package jp.go.aist.dggs.geometry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocalFaceCoordinatesTest {
    private final float delta = 0.0000001f;
    @Test
    public void toLocal_maxX() {
        ISEA4DFaceCoordinates faceCoordinates = new ISEA4DFaceCoordinates(9, (long) Math.pow(2,32)-1, (long) Math.pow(2,32)-1, 0L);
        String mortonCode = Morton3D.encode(faceCoordinates);
        assertEquals(mortonCode, "933333333333333333333333333333333");

        LocalFaceCoordinates localFaceCoordinates = faceCoordinates.toLocalize();
        assertEquals(localFaceCoordinates.getFace(), 0b10011111111111111111011);
        assertEquals(localFaceCoordinates.getX(), 0.8388607, delta);
        assertEquals(localFaceCoordinates.getY(), 0, delta);
        assertEquals(localFaceCoordinates.getZ(), 0, delta);
    }

    @Test
    public void toLocal_maxY() {
        ISEA4DFaceCoordinates faceCoordinates = new ISEA4DFaceCoordinates(9, 0L, (long) Math.pow(2,32)-1, 0L);
        String mortonCode = Morton3D.encode(faceCoordinates);
        assertEquals(mortonCode, "922222222222222222222222222222222");

        LocalFaceCoordinates localFaceCoordinates = faceCoordinates.toLocalize();
        assertEquals(localFaceCoordinates.getFace(), 0b10011010101010101010010);
        assertEquals(localFaceCoordinates.getX(), 0.4194303, delta);
        assertEquals(localFaceCoordinates.getY(), 0.7264746, delta);
        assertEquals(localFaceCoordinates.getZ(), 0, delta);
    }

    @Test
    public void toLocal_minY() {
        ISEA4DFaceCoordinates faceCoordinates = new ISEA4DFaceCoordinates(9, (long) Math.pow(2,32)-1, 0L, 0L);
        String mortonCode = Morton3D.encode(faceCoordinates);
        assertEquals(mortonCode, "911111111111111111111111111111111");

        LocalFaceCoordinates localFaceCoordinates = faceCoordinates.toLocalize();
        assertEquals(localFaceCoordinates.getFace(), 0b10010101010101010101001);
        assertEquals(localFaceCoordinates.getX(), 0.4194303, delta);
        assertEquals(localFaceCoordinates.getY(), -0.7264746, delta);
        assertEquals(localFaceCoordinates.getZ(), 0, delta);
    }

    @Test
    public void toLocal_maxZ() {
        ISEA4DFaceCoordinates faceCoordinates = new ISEA4DFaceCoordinates(9, 0L, 0L, (long) Math.pow(2,24)-1);
        String mortonCode = Morton3D.encode(faceCoordinates);
        assertEquals(mortonCode, "900000000444444444444444444444444");

        LocalFaceCoordinates localFaceCoordinates = faceCoordinates.toLocalize();
        assertEquals(localFaceCoordinates.getFace(), 0b10010000000000000000000);
        assertEquals(localFaceCoordinates.getX(), 0, delta);
        assertEquals(localFaceCoordinates.getY(), 0, delta);
        assertEquals(localFaceCoordinates.getZ(), 0.8388607, delta);
    }
}