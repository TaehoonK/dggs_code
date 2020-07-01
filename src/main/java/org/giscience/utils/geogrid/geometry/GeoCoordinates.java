package org.giscience.utils.geogrid.geometry;

import org.locationtech.spatial4j.distance.DistanceUtils;

/**
 * Geographic coordinates of a location on Earth.
 *
 * @author Franz-Benjamin Mocnik
 */
public class GeoCoordinates implements Comparable<GeoCoordinates>, Cloneable {
    private final Double _lat;
    private final Double _lon;
    private final Double _height;

    /**
     * @param latitude  latitude from WGS84 (UoM: degree)
     * @param longitude longitude from WGS84 (UoM: degree)
     * @param height    ellipsoidal height (UoM: meter)
     */
    public GeoCoordinates(Double latitude, Double longitude, Double height) throws IllegalArgumentException {
        if (latitude < -90 || latitude > 90) throw new IllegalArgumentException("invalid latitude");
        longitude %= 360;
        if (longitude > 180) longitude -= 360;
        else if (longitude < -180) longitude += 360;
        this._lat = latitude;
        this._lon = longitude;
        this._height = height;
    }

    /**
     * @param latitude  latitude from WGS84 (UoM: degree)
     * @param longitude longitude from WGS84 (UoM: degree)
     */
    public GeoCoordinates(Double latitude, Double longitude) throws IllegalArgumentException {
        this(latitude, longitude, null);
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

    /**
     * 2-D distance calculation from this coordinate to another.
     *
     * @param another another coordinate
     * @return 2-D distance
     * */
    public Double distanceTo2D(GeoCoordinates another) {
        return DistanceUtils.EARTH_EQUATORIAL_RADIUS_KM * 1000 * DistanceUtils.distHaversineRAD(this.getLat(), this.getLon(), another.getLat(), another.getLon()); // in meter.
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
        int d;
        d = Double.compare(this._lat, o._lat);
        if (d != 0) return d;
        d = Double.compare(this._lon, o._lon);
        if (d != 0) return d;
        if(this._height != null && o._height != null)
            d = Double.compare(this._height, o._height);
        return d;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
