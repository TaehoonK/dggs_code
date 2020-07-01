package jp.go.aist.dggs.geometry;

import org.giscience.utils.geogrid.geometry.GeoCoordinates;
import java.util.Arrays;

public class ISEA4DCellBoundary {
    public BoundaryElement[] boundaryElements;
    public GeoCoordinates baryCenter;

    public ISEA4DCellBoundary(GeoCoordinates[] boundary) {
        double diffX = Math.abs((boundary[1].getLon() + boundary[0].getLon())/2 - boundary[0].getLon());
        double diffY = Math.abs((boundary[1].getLat() + boundary[0].getLat())/2 - boundary[0].getLat());

        baryCenter = new GeoCoordinates((boundary[0].getLat() + boundary[1].getLat() + boundary[2].getLat() + boundary[3].getLat())/4 - diffY,
                (boundary[0].getLon() + boundary[1].getLon() + boundary[2].getLon() + boundary[3].getLon())/4 - diffX);

        boundaryElements = new BoundaryElement[4];
        for (int i = 0; i < boundary.length; i++) {
            boundaryElements[i] = new BoundaryElement(new GeoCoordinates(boundary[i].getLat() - diffY, boundary[i].getLon() - diffX), baryCenter);
        }
        Arrays.sort(boundaryElements);
    }

    public GeoCoordinates[] toList() {
        GeoCoordinates[] boundary = new GeoCoordinates[4];

        for (int i = 0; i < boundaryElements.length; i++) {
            boundary[i] = boundaryElements[i].coordinate;
        }

        return boundary;
    }

    static class BoundaryElement implements Comparable<BoundaryElement> {
        GeoCoordinates coordinate;
        double angle;

        BoundaryElement(GeoCoordinates coordinate, GeoCoordinates baryCenter) {
            this.coordinate = new GeoCoordinates(coordinate.getLat(), coordinate.getLon(), coordinate.getHeight());
            angle = Math.atan2(coordinate.getLat() - baryCenter.getLat(), coordinate.getLon() - baryCenter.getLon());
        }

        @Override
        public int compareTo(BoundaryElement o) {
            if(angle > o.angle) {
                return 1;
            } else if(angle < o.angle) {
                return -1;
            }
            return 0;
        }
    }
}
