package jp.go.aist.dggs.geometry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocalFaceCoordinatesTest {
    private final float delta = 0.000001f;
    @Test
    public void toLocal_maxX() {
        ISEA4DFaceCoordinates faceCoordinates = new ISEA4DFaceCoordinates(9, (long) Math.pow(2,32)-1, (long) Math.pow(2,32)-1, 0L);
        String mortonCode = Morton3D.encode(faceCoordinates);
        assertEquals(mortonCode, "933333333333333333333333333333333");

        LocalFaceCoordinates localFaceCoordinates = faceCoordinates.toLocalize();
        assertEquals(localFaceCoordinates.getFace(), "9333333333");
        assertEquals(localFaceCoordinates.getX(), 0.838861, delta);
        assertEquals(localFaceCoordinates.getY(), 0, delta);
        assertEquals(localFaceCoordinates.getZ(), 0, delta);
    }

    @Test
    public void toLocal_maxY() {
        ISEA4DFaceCoordinates faceCoordinates = new ISEA4DFaceCoordinates(9, 0L, (long) Math.pow(2,32)-1, 0L);
        String mortonCode = Morton3D.encode(faceCoordinates);
        assertEquals(mortonCode, "922222222222222222222222222222222");

        LocalFaceCoordinates localFaceCoordinates = faceCoordinates.toLocalize();
        assertEquals(localFaceCoordinates.getFace(), "9222222222");
        assertEquals(localFaceCoordinates.getX(), 0.419430, delta);
        assertEquals(localFaceCoordinates.getY(), 0.726475, delta);
        assertEquals(localFaceCoordinates.getZ(), 0, delta);
    }

    @Test
    public void toLocal_minY() {
        ISEA4DFaceCoordinates faceCoordinates = new ISEA4DFaceCoordinates(9, (long) Math.pow(2,32)-1, 0L, 0L);
        String mortonCode = Morton3D.encode(faceCoordinates);
        assertEquals(mortonCode, "911111111111111111111111111111111");

        LocalFaceCoordinates localFaceCoordinates = faceCoordinates.toLocalize();
        assertEquals(localFaceCoordinates.getFace(), "9111111111");
        assertEquals(localFaceCoordinates.getX(), 0.419430, delta);
        assertEquals(localFaceCoordinates.getY(), -0.726475, delta);
        assertEquals(localFaceCoordinates.getZ(), 0, delta);
    }

    @Test
    public void toLocal_maxZ() {
        ISEA4DFaceCoordinates faceCoordinates = new ISEA4DFaceCoordinates(9, 0L, 0L, (long) Math.pow(2,24)-1);
        String mortonCode = Morton3D.encode(faceCoordinates);
        assertEquals(mortonCode, "900000000444444444444444444444444");

        LocalFaceCoordinates localFaceCoordinates = faceCoordinates.toLocalize();
        assertEquals(localFaceCoordinates.getFace(), "9000000004");
        assertEquals(localFaceCoordinates.getX(), 0, delta);
        assertEquals(localFaceCoordinates.getY(), 0, delta);
        assertEquals(localFaceCoordinates.getZ(), 0.838861, delta);
    }
}