package jp.go.aist.dggs.geometry;

import jp.go.aist.dggs.common.DGGS;

import static jp.go.aist.dggs.common.DGGS.EIGHT_BIT_MASK;
import static jp.go.aist.dggs.common.DGGS.Morton2DTable256Encode;

public class LocalFaceCoordinates {
    private final static int _local_resolution = 9;
    private final static int _base_z = (int) Math.pow(2,23); // -> orignal data z value = 0
    private final int _face;
    private final float _x;
    private final float _y;
    private final float _z;

    public LocalFaceCoordinates(ISEA4DFaceCoordinates faceCoordinates) {
        String localMorton = Morton3D.encode(faceCoordinates).substring(0, _local_resolution + 1);
        _face = getIntFace(localMorton);

        StringBuilder sb = new StringBuilder(localMorton);
        for (int i = 0; i < DGGS.MAX_XY_RESOLUTION - _local_resolution; i++) {
            sb.append('0');
        }
        ISEA4DFaceCoordinates fixedFaceCoords = Morton3D.decode(sb.toString()).toOrthogonal();
        ISEA4DFaceCoordinates orthogonalCoords = faceCoordinates.toOrthogonal();

        _x = (float) (orthogonalCoords.getX() - fixedFaceCoords.getX()) / 1000000f;
        _y = (float) (orthogonalCoords.getY() - fixedFaceCoords.getY()) / 1000000f;
        _z = (float) (orthogonalCoords.getZ() - _base_z) / 1000000f;
    }

    private int getIntFace(String localMorton) {
        ISEA4DFaceCoordinates localFace = Morton3D.decode(localMorton, _local_resolution);
        int x = (int) localFace.getX();
        int y = (int) localFace.getY();

        int _int_face = 0;
        _int_face = _int_face
                | localFace.getFace() << 18
                | Morton2DTable256Encode[(int) (y & EIGHT_BIT_MASK)] << 3
                | Morton2DTable256Encode[(int) (x & EIGHT_BIT_MASK)] << 2
                | (y & 0x00000001) << 1
                | (x & 0x00000001);
        return _int_face;
    }

    public int getFace() {
        return this._face;
    }

    public float getX() {
        return this._x;
    }

    public float getY() {
        return this._y;
    }

    public float getZ() {
        return this._z;
    }

    public float[] toList() {
        return new float[] {_x, _y, _z};
    }

    @Override
    public String toString() {
        return String.format("face %d x %f y %f z %f", this._face, this._x, this._y, this._z);
    }


}
