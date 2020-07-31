package jp.go.aist.dggs.geometry;

import jp.go.aist.dggs.common.DGGS;
import jp.go.aist.dggs.utils.MortonUtils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import static jp.go.aist.dggs.common.DGGS.MATRIX_A_INVERSE;

/**
 * Cartesian Coordinates of a location on a face of a platonic solid.
 * This class targeted ISEA4D projection.
 *
 * @author Taehoon Kim
 */
public class ISEA4DFaceCoordinates {
    private final int _res;
    private final int _face;
    private final long _x;
    private final long _y;
    private final long _z;
    private final boolean _isOrthogonal;

    ISEA4DFaceCoordinates(int face, long x, long y, long z, int resolution, boolean isOrthogonal) {
        this._face = face;
        this._x = x;
        this._y = y;
        this._z = z;
        this._res = resolution;
        this._isOrthogonal = isOrthogonal;
    }

    /**
     * @param face       Index of rhombuses (= diamond) from 0 to 9
     * @param x          range is from 0 to 4,294,967,295 (2^32 - 1)
     * @param y          range is from 0 to 4,294,967,295 (2^32 - 1)
     * @param z          range is from 0 to 16,777,215 (2^24 - 1)
     * @param resolution Resolution of generate face coordinates
     */
    public ISEA4DFaceCoordinates(int face, long x, long y, long z, int resolution) {
        this._face = face;
        this._x = x;
        this._y = y;
        this._z = z;
        this._res = resolution;
        this._isOrthogonal = false;
    }

    /**
     * @param face       Index of rhombuses (= diamond) from 0 to 9
     * @param x          range is from 0 to 4,294,967,295 (2^32 - 1)
     * @param y          range is from 0 to 4,294,967,295 (2^32 - 1)
     * @param resolution Resolution of generate face coordinates
     */
    public ISEA4DFaceCoordinates(int face, long x, long y, int resolution) {
        this(face, x, y, 0, resolution);
    }

    /**
     * @param face       Index of rhombuses (= diamond) from 0 to 9
     * @param x          range is from 0 to 4,294,967,295 (2^32 - 1)
     * @param y          range is from 0 to 4,294,967,295 (2^32 - 1)
     * @param z          range is from 0 to 16,777,215 (2^24 - 1)
     */
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

    public long getMaxX() {
        return Double.valueOf(Math.pow(2,_res) - 1).longValue();
    }

    public long getMaxY() {
        return Double.valueOf(Math.pow(2,_res) - 1).longValue();
    }

    public long getMaxZ() {
        return _res < (DGGS.MAX_XY_RESOLUTION - DGGS.MAX_Z_RESOLUTION) ?
                0 : Double.valueOf(Math.pow(2, (_res - (DGGS.MAX_XY_RESOLUTION - DGGS.MAX_Z_RESOLUTION))) - 1).longValue();
    }

    /**
     * 2-D distance calculation from this coordinate to another.
     *
     * @param another another coordinate
     * @return 2-D distance
     * */
    public double distance2DTo(ISEA4DFaceCoordinates another) {
        ISEA4DFaceCoordinates from = toOrthogonal();
        ISEA4DFaceCoordinates to = another.toOrthogonal();
        return Math.sqrt(Math.pow(from.getX() - to.getY(), 2)
                + Math.pow(from.getY() - to.getY(), 2));
    }

    /**
     * 3-D distance calculation from this coordinate to another.
     *
     * @param another another coordinate
     * @return 3-D distance
     * */
    public Double distance3DTo(ISEA4DFaceCoordinates another) {
        ISEA4DFaceCoordinates from = toOrthogonal();
        ISEA4DFaceCoordinates to = another.toOrthogonal();
        return Math.sqrt(Math.pow(from.getX() - to.getY(), 2)
                + Math.pow(from.getY() - to.getY(), 2)
                + Math.pow(from.getZ() - to.getZ(), 2));
    }

    /**
     * Generate the orthogonal coordinates from this FaceCoordinate.
     *
     * @return The orthogonal coordinates on ISEA face
     * */
    public ISEA4DFaceCoordinates toOrthogonal() {
        if(!_isOrthogonal) {
            double[] b = {this._x, this._y};
            RealMatrix matrix_B = MatrixUtils.createColumnRealMatrix(b);
            RealMatrix rmx = MATRIX_A_INVERSE.multiply(matrix_B);

            long orthogonal_x = Double.valueOf(rmx.getData()[0][0]).longValue();
            long orthogonal_y = Double.valueOf(rmx.getData()[1][0]).longValue();

            return new ISEA4DFaceCoordinates(_face, orthogonal_x, orthogonal_y, _z, _res, true);
        }

        return this;
    }

    /**
     * Generate the localized coordinate from this FaceCoordinate.
     *
     * @return The localized coordinate
     * */
    public LocalFaceCoordinates toLocalize() {
        return new LocalFaceCoordinates(this);
    }

    public long[] toList() {
        return new long[] {_x, _y, _z};
    }

    @Override
    public String toString() {
        return String.format("res %d face %d x %d y %d z %d", this._res, this._face, this._x, this._y, this._z);
    }
}
