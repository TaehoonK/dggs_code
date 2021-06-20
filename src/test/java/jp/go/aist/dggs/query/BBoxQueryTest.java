package jp.go.aist.dggs.query;

import ch.ethz.globis.phtree.PhTree;
import jp.go.aist.dggs.geometry.Morton2D;
import org.giscience.utils.geogrid.geometry.GeoCoordinates;
import org.junit.Test;
import jp.go.aist.dggs.geometry.ISEA4DFaceCoordinates;
import jp.go.aist.dggs.geometry.Morton3D;
import jp.go.aist.dggs.utils.MortonUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class BBoxQueryTest {
    @Test
    public void translateQueryRange() {
        File file = new File("queryresult.txt");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.write("cell boundary\n");

            int resolution = 21;
            GeoCoordinates coordLL = new GeoCoordinates(35.23484897703, 129.08242161893, 0d);
            GeoCoordinates coordRU = new GeoCoordinates(35.2355165472, 129.08282061646, 0d);
            BBoxQuery.QueryPoint queryLL = new BBoxQuery.QueryPoint(coordLL, resolution);

            ArrayList<BBoxQuery.QueryRange> queryList = BBoxQuery.translateQueryRange(coordLL,coordRU,resolution);
            for(BBoxQuery.QueryRange qr : queryList) {
                for(long x = qr.minPoint[0]; x <= qr.maxPoint[0]; x++) {
                    for(long y = qr.minPoint[1]; y <= qr.maxPoint[1]; y++) {
                        ISEA4DFaceCoordinates faceCoordinates = new ISEA4DFaceCoordinates(queryLL._face, x, y,resolution);
                        ISEA4DCellBoundary cellBoundary = Morton2D.getBoundary(faceCoordinates);
                        printWriter.write(cellBoundary.toString() + "\n");
                    }
                }
            }
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBBoxQueryResults() {
        final int DIM = 3;
        final int NQ = 100;
        final int RES = 20;
        final double FIX_LAT = 34.64032095182;
        final double FIX_LON = 135.4546079423;
        final double INC_VAL = 0.0001;
        final Random R = new Random();

        long registerStartTime = System.currentTimeMillis();
        // Making dataset (3D coordinate)
        ArrayList<GeoCoordinates> rawDataset = new ArrayList<>();
        double lat = FIX_LAT;
        for (int i = 0; i < NQ;  i++) {
            double lon = FIX_LON;
            for (int j = 0; j < NQ;  j++) {
                rawDataset.add(new GeoCoordinates(lat,lon, R.nextDouble() * 10));
                lon += INC_VAL;
            }
            lat += INC_VAL;
        }

        // Generating PH-Tree index
        PhTree<String> phTree = PhTree.create(DIM);
        for(GeoCoordinates coordinates : rawDataset) {
            String pdCode = MortonUtils.toPDCode(coordinates, RES);
            ISEA4DFaceCoordinates faceCoordinates = Morton3D.decode(pdCode, RES);
            phTree.put(faceCoordinates.toList(), pdCode);
        }
        System.out.println("Registration finish : " + (System.currentTimeMillis() - registerStartTime) / 1000);

        // Making a bounding box
        double minBoundX = R.nextInt(NQ/4) * INC_VAL;
        double minBoundY = R.nextInt(NQ/4) * INC_VAL;
        double maxBoundX = minBoundX + R.nextInt(NQ/4 * 3) * INC_VAL;
        double maxBoundY = minBoundY + R.nextInt(NQ/4 * 3) * INC_VAL;
        GeoCoordinates minP = new GeoCoordinates(FIX_LAT + minBoundY, FIX_LON + minBoundX, 0D);
        GeoCoordinates maxP = new GeoCoordinates(FIX_LAT + maxBoundY, FIX_LON + maxBoundX, 10D);

        // Do BBox query
        HashSet<String> queryResults = BBoxQuery.getBBoxQueryResult(minP, maxP, RES, phTree);
        System.out.println("Num of query result cells (phTree) : " + queryResults.size());
        System.out.println("Min Bound : " + minBoundX + "," + minBoundY);
        System.out.println("Max Bound : " + maxBoundX + "," + maxBoundY);
        System.out.println("Min point : " + minP.getLon() + "," + minP.getLat() + "," + minP.getHeight());
        System.out.println("Max point : " + maxP.getLon() + "," + maxP.getLat() + "," + maxP.getHeight());
    }
}
