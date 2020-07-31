package jp.go.aist.dggs.geometry;

import jp.go.aist.dggs.common.DGGS;

public class LocalFaceCoordinates {
    private final int _local_resolution = 9;
    private final String _face;
    private final float _x;
    private final float _y;
    private final float _z;

    public LocalFaceCoordinates(ISEA4DFaceCoordinates faceCoordinates) {
        _face = Morton3D.encode(faceCoordinates).substring(0,_local_resolution + 1);
        StringBuilder sb = new StringBuilder(_face);
        for (int i = 0; i < DGGS.MAX_XY_RESOLUTION - _local_resolution; i++) {
            sb.append('0');
        }
        ISEA4DFaceCoordinates fixedFaceCoords = Morton3D.decode(sb.toString()).toOrthogonal();
        ISEA4DFaceCoordinates orthogonalCoords = faceCoordinates.toOrthogonal();

        _x = (float) (orthogonalCoords.getX() - fixedFaceCoords.getX()) / 10000000f;
        _y = (float) (orthogonalCoords.getY() - fixedFaceCoords.getY()) / 10000000f;
        _z = (float) (orthogonalCoords.getZ() - fixedFaceCoords.getZ()) / 10000000f;
    }

    public String getFace() {
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
        return String.format("face %s x %f y %f z %f", this._face, this._x, this._y, this._z);
    }


}
