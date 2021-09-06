package jp.go.aist.dggs.geometry;

import jp.go.aist.dggs.common.DGGS;

import static jp.go.aist.dggs.common.DGGS.*;
import static jp.go.aist.dggs.common.DGGS.H_RANGE;

public class MeterFaceCoordinates {
    private final double _meter_unit = 0.00166282891483159;
    private final int _local_resolution = 8;
    private final int _face;
    private final float _x;
    private final float _y;
    private final float _z;

    public MeterFaceCoordinates(ISEA4DFaceCoordinates faceCoordinates) {
        String localMorton = Morton3D.encode(faceCoordinates).substring(0, _local_resolution + 1);
        _face = getIntFace(localMorton);

        StringBuilder sb = new StringBuilder(localMorton);
        for (int i = 0; i < DGGS.MAX_XY_RESOLUTION - _local_resolution; i++) {
            sb.append('0');
        }
        ISEA4DFaceCoordinates fixedFaceCoords = Morton3D.decode(sb.toString()).toOrthogonal();
        ISEA4DFaceCoordinates orthogonalCoords = faceCoordinates.toOrthogonal();

        _x = (float) ((orthogonalCoords.getX() - fixedFaceCoords.getX()) * _meter_unit);
        _y = (float) ((orthogonalCoords.getY() - fixedFaceCoords.getY()) * _meter_unit);
        _z = (float) (faceCoordinates.getMaxZ() <= 1 ? 0 : (orthogonalCoords.getZ() * 2.0d * H_RANGE) / faceCoordinates.getMaxZ() - H_RANGE);
    }

    public MeterFaceCoordinates(int localFace, float x, float y, float z) {
        _face = localFace;
        _x = x;
        _y = y;
        _z = z;
    }

    public ISEA4DFaceCoordinates toISEA4DFaceCoordinates() {
        String localMorton = String.valueOf(this.getFace());
        StringBuilder sb = new StringBuilder(localMorton);
        for (int i = 0; i < DGGS.MAX_XY_RESOLUTION - _local_resolution; i++) {
            sb.append('0');
        }
        ISEA4DFaceCoordinates fixedFaceCoords = Morton3D.decode(sb.toString()).toOrthogonal();

        long orthogonal_x = (long) (this.getX() / _meter_unit + fixedFaceCoords.getX());
        long orthogonal_y = (long) (this.getY() / _meter_unit + fixedFaceCoords.getY());
        long orthogonal_z = (long) ((this.getZ() + H_RANGE) * fixedFaceCoords.getMaxZ() / (H_RANGE * 2.0d));
        int face = Integer.parseInt(localMorton.substring(0,1));
        ISEA4DFaceCoordinates newOrthogonalFaceCoords = new ISEA4DFaceCoordinates(face, orthogonal_x, orthogonal_y, orthogonal_z, MAX_XY_RESOLUTION, true);

        return  newOrthogonalFaceCoords.fromOrthogonalToDGGS();
    }

    private int getIntFace(String localMorton) {
        int _int_face = 0;
        if(localMorton.length() <= 9) {
            _int_face = Integer.parseInt(localMorton);
        }
        else {
            // TODO: Currently, not necessary
            System.out.println("face index value is over than Integer.MAX_VALUE");

            ISEA4DFaceCoordinates localFace = Morton3D.decode(localMorton, _local_resolution);
            int x = (int) localFace.getX();
            int y = (int) localFace.getY();
            int z = (int) localFace.getZ();
            _int_face = _int_face
                    | localFace.getFace() << 19
                    | Morton2DTable256Encode[(int) (y & EIGHT_BIT_MASK)] << 4
                    | Morton2DTable256Encode[(int) (x & EIGHT_BIT_MASK)] << 3
                    | (z & 0x00000001) << 2
                    | (y & 0x00000001) << 1
                    | (x & 0x00000001);
        }

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

    /**
     * 2-D distance calculation from this coordinate to another.
     *
     * @param another another coordinate
     * @return 2-D distance
     * */
    public double distance2DTo(MeterFaceCoordinates another) {
        return Math.sqrt(Math.pow(this.getX() - another.getX(), 2) + Math.pow(this.getY() - another.getY(), 2));
    }

    public double distance3DTo(MeterFaceCoordinates another) {
        return Math.sqrt(Math.pow(this.getX() - another.getX(), 2) + Math.pow(this.getY() - another.getY(), 2) + Math.pow(this.getZ() - another.getZ(), 2));
    }

    @Override
    public String toString() {
        return String.format("face %d x %f y %f z %f", this._face, this._x, this._y, this._z);
    }
}