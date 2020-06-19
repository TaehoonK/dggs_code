package jp.go.aist.dggs.geometry;

import jp.go.aist.dggs.DGGS;

public class ISEA4DFaceCoordinates {
    private final int _res;
    private final int _face;
    private final long _x;
    private final long _y;
    private final long _z;

    public ISEA4DFaceCoordinates(int face, long x, long y, long z, int resolution) {
        this._face = face;
        this._x = x;
        this._y = y;
        this._z = z;
        this._res = resolution;
    }

    public ISEA4DFaceCoordinates(int face, long x, long y, long z) {
        this(face, x, y, z, DGGS.MAX_XY_RESOLUTION);
    }

    public int getResolution() {
        return this._res;
    }

    public int getFace() {
        return this._face;
    }

    public long getX() {
        return this._x;
    }

    public long getY() {
        return this._y;
    }

    public long getZ() {
        return this._z;
    }

    public double getMaxX() {
        return Math.pow(2,_res);
    }

    public double getMaxY() {
        return Math.pow(2,_res);
    }

    public double getMaxZ() {
        return _res < (DGGS.MAX_XY_RESOLUTION - DGGS.MAX_Z_RESOLUTION) ?
                0 : Math.pow(2, (_res - (DGGS.MAX_XY_RESOLUTION - DGGS.MAX_Z_RESOLUTION)));
    }

    public Double distance3DTo(ISEA4DFaceCoordinates c) {
        return Math.sqrt(Math.pow(this._x - c.getX(), 2) + Math.pow(this._y - c.getY(), 2) + Math.pow(this._z - c.getZ(), 2));
    }
    @Override
    public String toString() {
        return String.format("res %d face %d x %d y %d z %d", this._res, this._face, this._x, this._y, this._z);
    }
}
