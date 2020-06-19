package org.giscience.utils.geogrid.geometry;

/**
 * Cartesian Coordinates of a location on a face of a platonic solid.
 *
 * @author Franz-Benjamin Mocnik
 */
public class FaceCoordinates {
    private final Integer _face;
    private final Double _x;
    private final Double _y;
    private final Double _z;

    public FaceCoordinates(Integer face, Double x, Double y, Double z) {
        this._face = face;
        this._x = x;
        this._y = y;
        this._z = z;
    }

    public FaceCoordinates(Integer face, Double x, Double y) {
        this(face,x,y,null);
    }

    public Integer getFace() {
        return this._face;
    }

    public Double getX() {
        return this._x;
    }

    public Double getY() {
        return this._y;
    }

    public Double getZ() {
        return this._z;
    }

    public Double distanceTo(FaceCoordinates c) {
        return Math.sqrt(Math.pow(this._x - c.getX(), 2) + Math.pow(this._y - c.getY(), 2));
    }
    @Override
    public String toString() {
        if(this._z != null)
            return String.format("face %d x %f y %f z %f", this._face, this._x, this._y, this._z);
        else
            return String.format("face %d x %f y %f", this._face, this._x, this._y);
    }
}
