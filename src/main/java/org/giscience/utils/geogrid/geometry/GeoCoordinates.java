package org.giscience.utils.geogrid.geometry;

import org.giscience.utils.geogrid.generic.Trigonometric;

/**
 * Geographic coordinates of a location on Earth.
 *
 * @author Franz-Benjamin Mocnik
 */
public class GeoCoordinates implements Comparable<GeoCoordinates> {
    private final Double _lat;
    private final Double _lon;
    private final Double _height;

    public GeoCoordinates(Double lat, Double lon, Double _height) throws Exception {
        if (lat < -90 || lat > 90) throw new Exception("invalid latitude");
        lon %= 360;
        if (lon > 180) lon -= 360;
        else if (lon < -180) lon += 360;
        this._lat = lat;
        this._lon = lon;
        this._height = _height;
    }

    public GeoCoordinates(Double lat, Double lon) throws Exception {
        this(lat, lon, 0d);
    }

    public Double getLat() {
        return this._lat;
    }

    public Double getLon() {
        return this._lon;
    }

    public Double getHeight(){
        return this._height;
    }

    public Double distanceTo2D(GeoCoordinates other) {
        return Trigonometric.acos(Trigonometric.sin(this.getLat()) * Trigonometric.sin(other.getLat()) + Trigonometric.cos(this.getLat()) * Trigonometric.cos(other.getLat()) * Trigonometric.cos(this.getLon() - other.getLon()));
    }

    @Override
    public String toString() {
        if(this._height != null)
            return String.format("lat %f lon %f height %f", this._lat, this._lon, this._height);
        else
            return String.format("lat %f lon %f", this._lat, this._lon);
    }

    @Override
    public int compareTo(GeoCoordinates o) {
        int d = 0;
        if(this._height != null && o._height != null)
            d = Double.compare(this._height, o._height);
        if (d != 0) return d;

        d = Double.compare(this._lat, o._lat);
        if (d != 0) return d;
        return Double.compare(this._lon, o._lon);
    }
}
