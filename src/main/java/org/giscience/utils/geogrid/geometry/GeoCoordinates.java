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

    /**
     * @param latitude latitude from WGS84 (UoM: degree)
     * @param longitude longitude from WGS84 (UoM: degree)
     * @param height ellipsoidal height (UoM: meter)
     */
    public GeoCoordinates(Double latitude, Double longitude, Double height) throws Exception {
        if (latitude < -90 || latitude > 90) throw new Exception("invalid latitude");
        longitude %= 360;
        if (longitude > 180) longitude -= 360;
        else if (longitude < -180) longitude += 360;
        this._lat = latitude;
        this._lon = longitude;
        this._height = height;
    }

    /**
     * @param latitude latitude from WGS84 (UoM: degree)
     * @param longitude longitude from WGS84 (UoM: degree)
     */
    public GeoCoordinates(Double latitude, Double longitude) throws Exception {
        this(latitude, longitude, 0d);
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
            return String.format("latitude %f longitude %f height %f", this._lat, this._lon, this._height);
        else
            return String.format("latitude %f longitude %f", this._lat, this._lon);
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
