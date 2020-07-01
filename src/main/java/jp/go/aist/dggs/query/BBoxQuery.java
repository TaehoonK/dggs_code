package jp.go.aist.dggs.query;

import ch.ethz.globis.phtree.PhTree;
import jp.go.aist.dggs.geometry.ISEA4DFaceCoordinates;
import jp.go.aist.dggs.geometry.Morton3D;
import jp.go.aist.dggs.utils.MortonUtils;
import org.giscience.utils.geogrid.geometry.GeoCoordinates;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class BBoxQuery {

    /**
     * Returns an iterator of the PD codes list, which contained a given bounding box.
     *
     * @param minCoords Minimum geodetic coordinates of bounding cube (or box)
     * @param maxCoords Maximum geodetic coordinates of bounding cube (or box)
     * @param resolution Resolution of generate Morton code
     * @param phTree Index tree of point cloud dataset
     * @return An iterator of the PD codes list, which contained a given bounding box
     * */
    public static Iterator doBBoxQuery(GeoCoordinates minCoords, GeoCoordinates maxCoords, int resolution, PhTree phTree) {
        HashSet<String> result = getBBoxQueryResult(minCoords, maxCoords, resolution, phTree);

        return result.iterator();
    }

    public static HashSet<String> getBBoxQueryResult(GeoCoordinates minCoords, GeoCoordinates maxCoords, int resolution, PhTree phTree) {
        if(minCoords.getDimension() != maxCoords.getDimension() && phTree.getDim() != minCoords.getDimension()) {
            throw new IllegalArgumentException();
        }

        HashSet<String> result = new HashSet<>();
        ArrayList<QueryRange> queryList = translateQueryRange(minCoords, maxCoords, resolution);

        // Collect each query result from queryList
        for(QueryRange qr : queryList) {
            PhTree.PhQuery<Object> tempResult = phTree.query(qr.minPoint, qr.maxPoint);
            while (tempResult.hasNext()) {
                String pdCode = (String) tempResult.nextValue();
                result.add(pdCode);
            }
        }
        return result;
    }

    private static ArrayList<QueryRange> translateQueryRange(GeoCoordinates minCoords, GeoCoordinates maxCoords, int resolution) {
        ArrayList<QueryRange> queryList         = new ArrayList<>();
        ArrayList<Long> bottomBoundaryValueList = new ArrayList<>();
        ArrayList<Long> topBoundaryValueList    = new ArrayList<>();

        GeoCoordinates coordLL = minCoords;
        GeoCoordinates coordRU = maxCoords;
        GeoCoordinates coordLU = new GeoCoordinates(coordRU.getLat(), coordLL.getLon(), coordRU.getHeight());
        GeoCoordinates coordRL = new GeoCoordinates(coordLL.getLat(), coordRU.getLon(), coordLL.getHeight());

        QueryPoint queryLL = new QueryPoint(coordLL, resolution);
        QueryPoint queryRU = new QueryPoint(coordRU, resolution);
        QueryPoint queryLU = new QueryPoint(coordLU, resolution);
        QueryPoint queryRL = new QueryPoint(coordRL, resolution);

        long bottomBoundaryValue = queryLU._faceCoordinates.getY();
        long topBoundaryValue    = queryLU._faceCoordinates.getY();

        for(long xCoords = queryLU._faceCoordinates.getX(); xCoords <= queryLL._faceCoordinates.getX(); xCoords++) {
            // cell boundary check (Two exceptional cases)
            ISEA4DFaceCoordinates faceCoordinates   = new ISEA4DFaceCoordinates(queryLU._face, xCoords, bottomBoundaryValue, 0, resolution);
            GeoCoordinates centerP                  = Morton3D.getCenter(faceCoordinates);
            GeoCoordinates[] boundary               = Morton3D.getBoundary(faceCoordinates).toList();

            if(centerP.getLon() > minCoords.getLon() && bottomBoundaryValue > queryLL._faceCoordinates.getY()) {
                bottomBoundaryValue--;
            } else if(boundary[2].getLon() < minCoords.getLon()) {
                bottomBoundaryValue++;
            }
            bottomBoundaryValueList.add(bottomBoundaryValue);
            bottomBoundaryValue--;

            if(bottomBoundaryValue < queryLL._faceCoordinates.getY()) // LL point's Y value is minimum Y value of given range
                bottomBoundaryValue = queryLL._faceCoordinates.getY();
        }
        for(long xCoords = queryLL._faceCoordinates.getX() + 1; xCoords <= queryRL._faceCoordinates.getX(); xCoords++) {
            // cell boundary check (Two exceptional cases)
            ISEA4DFaceCoordinates faceCoordinates   = new ISEA4DFaceCoordinates(queryLL._face, xCoords, bottomBoundaryValue, 0, resolution);
            GeoCoordinates centerP                  = Morton3D.getCenter(faceCoordinates);
            GeoCoordinates[] boundary               = Morton3D.getBoundary(faceCoordinates).toList();

            if(centerP.getLat() > minCoords.getLat()) {
                bottomBoundaryValue--;
            } else if(boundary[3].getLat() < minCoords.getLat()) {
                bottomBoundaryValue++;
            }
            bottomBoundaryValueList.add(bottomBoundaryValue);
            bottomBoundaryValue++;
        }

        for(long xCoords = queryLU._faceCoordinates.getX(); xCoords <= queryRU._faceCoordinates.getX(); xCoords++) {
            // cell boundary check (Two exceptional cases)
            ISEA4DFaceCoordinates faceCoordinates   = new ISEA4DFaceCoordinates(queryLU._face, xCoords, topBoundaryValue, 0, resolution);
            GeoCoordinates centerP                  = Morton3D.getCenter(faceCoordinates);
            GeoCoordinates[] boundary               = Morton3D.getBoundary(faceCoordinates).toList();

            if(centerP.getLat() < maxCoords.getLat() && topBoundaryValue < queryRU._faceCoordinates.getY()) {
                topBoundaryValue++;
            } else if(boundary[1].getLat() > maxCoords.getLat()) {
                topBoundaryValue--;
            }
            topBoundaryValueList.add(topBoundaryValue);
            topBoundaryValue++;

            if(topBoundaryValue > queryRU._faceCoordinates.getY()) // RU point's Y value is maximum Y value of given range
                topBoundaryValue = queryRU._faceCoordinates.getY();
        }
        for(long xCoords = queryRU._faceCoordinates.getX() + 1; xCoords <= queryRL._faceCoordinates.getX(); xCoords++) {
            // cell boundary check (Two exceptional cases)
            ISEA4DFaceCoordinates faceCoordinates   = new ISEA4DFaceCoordinates(queryRU._face, xCoords, topBoundaryValue, 0, resolution);
            GeoCoordinates centerP                  = Morton3D.getCenter(faceCoordinates);
            GeoCoordinates[] boundary               = Morton3D.getBoundary(faceCoordinates).toList();

            if(centerP.getLon() < maxCoords.getLon()) {
                topBoundaryValue++;
            } else if(boundary[0].getLon() > maxCoords.getLon()) {
                topBoundaryValue--;
            }
            topBoundaryValueList.add(topBoundaryValue);
            topBoundaryValue--;
        }

        if(topBoundaryValueList.size() != bottomBoundaryValueList.size()) {
            throw new IllegalStateException();
        }

        long tempX = queryLU._faceCoordinates.getX(); // minimum X value of given range
        for(int i = 0; i < bottomBoundaryValueList.size(); i++) {
            long min = bottomBoundaryValueList.get(i);
            long max = topBoundaryValueList.get(i);
            long[] minPoint, maxPoint;

            if(minCoords.getDimension() == 2) {
                minPoint = new long[]{tempX, min};
                maxPoint = new long[]{tempX, max};
            } else {
                minPoint = new long[]{tempX, min, queryLL._faceCoordinates.getZ()};
                maxPoint = new long[]{tempX, max, queryRU._faceCoordinates.getZ()};
            }
            tempX++;

            queryList.add(new QueryRange(minPoint, maxPoint));
        }

        return queryList;
    }

    static class QueryPoint {
        int             _resolution;
        int             _face;
        String          _pdCode;
        GeoCoordinates  _geodeticCoords;
        ISEA4DFaceCoordinates _faceCoordinates;

        QueryPoint(GeoCoordinates queryPoint, int resolution) {
            _resolution = resolution;
            _geodeticCoords = queryPoint;
            _pdCode = MortonUtils.toPDCode(queryPoint, resolution);
            _faceCoordinates = Morton3D.decode(_pdCode, resolution);
            _face = _faceCoordinates.getFace();
        }
    }

    static class QueryRange {
        long[] minPoint;
        long[] maxPoint;

        QueryRange(long[] minPoint, long[] maxPoint) {
            this.minPoint = minPoint;
            this.maxPoint = maxPoint;
        }
    }
}
