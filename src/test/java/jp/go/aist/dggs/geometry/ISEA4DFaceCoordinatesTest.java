package jp.go.aist.dggs.geometry;

import jp.go.aist.dggs.common.DGGS;
import jp.go.aist.dggs.utils.MortonUtils;
import org.giscience.utils.geogrid.geometry.FaceCoordinates;
import org.giscience.utils.geogrid.geometry.GeoCoordinates;
import org.giscience.utils.geogrid.projections.ISEAProjection;
import org.junit.Test;

import static jp.go.aist.dggs.common.DGGS.*;
import static org.junit.Assert.*;

public class ISEA4DFaceCoordinatesTest {
    private final int _iterations = 1000000;

    @Test
    public void orthogonal_Random() {
        for (int i = 0; i < this._iterations; i++) {
            GeoCoordinates c = new GeoCoordinates(Math.random() * 179.99 - 89.995, Math.random() * 360.0 - 180.0, Math.random() * DGGS.H_RANGE * 2 - DGGS.H_RANGE);
            ISEA4DFaceCoordinates faceCoordinates = MortonUtils.toFaceCoordinate(c).toOrthogonal();

            ISEAProjection p = new ISEAProjection();
            FaceCoordinates f = p.sphereToIcosahedron(c);
            int face = f.getFace();
            double newPointX;
            double newPointY;
            newPointX = f.getX() - NEW_ORIG_X;
            if ((face >= 0 && face <= 4) || (face >= 10 && face <= 14)) {
                newPointY = f.getY() - NEW_ORIG_Y;
            } else {
                newPointY = f.getY() + NEW_ORIG_Y;
            }
            double origX = newPointX / (-NEW_ORIG_X * 2) * TOTAL_RANGE;
            double origY = newPointY / (-NEW_ORIG_X * 2) * TOTAL_RANGE;
            double origZ = ((H_RANGE + c.getHeight()) / (H_RANGE * 2)) * TOTAL_RANGE_Z;

            assertTrue(Math.abs(faceCoordinates.getX() - Double.valueOf(origX).longValue()) <= 1);
            assertTrue(Math.abs(faceCoordinates.getY() - Double.valueOf(origY).longValue()) <= 1);
            assertTrue(Math.abs(faceCoordinates.getZ() - Double.valueOf(origZ).longValue()) <= 1);
        }
    }
}