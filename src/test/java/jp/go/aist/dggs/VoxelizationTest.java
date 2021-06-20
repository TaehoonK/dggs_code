package jp.go.aist.dggs;

import jp.go.aist.dggs.common.DGGS;
import jp.go.aist.dggs.geometry.ISEA4DFaceCoordinates;
import jp.go.aist.dggs.utils.MortonUtils;
import org.giscience.utils.geogrid.geometry.GeoCoordinates;
import org.jengineering.sjmply.PLY;
import org.jengineering.sjmply.PLYElementList;
import org.jengineering.sjmply.PLYType;
import org.locationtech.proj4j.*;
import org.smurn.jply.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class VoxelizationTest {
    private final static String fileName = "original_LBJ_2019.ply";//"AIST_Waterfront_8F.ply";//

    public static void main(String[] args) throws Exception {
        final int target_resolution = 27;
        final int threshold_max_count = 10;
        Set<String> voxelizedSet = new HashSet<>();
        Map<String, Integer> filteredMap = new HashMap<>();

        ArrayList<Element> elements = new ArrayList<>();
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CRSFactory csFactory = new CRSFactory();
        CoordinateReferenceSystem crsFrom = csFactory.createFromName("EPSG:2448");
        CoordinateReferenceSystem crsTo = csFactory.createFromName("EPSG:4326");
        CoordinateTransform transA = ctFactory.createTransform(crsFrom, crsTo);
        CoordinateTransform transB = ctFactory.createTransform(crsTo, crsFrom);
        ProjCoordinate geodeticCoords = new ProjCoordinate();
        ProjCoordinate orthogonalCoords2 = new ProjCoordinate();

        String rootPath = System.getProperty("user.dir");;
        Path path = Paths.get(rootPath, fileName);
        PLY ply = PLY.load(path);
        PLYElementList vertices = ply.elements("vertex");
        ElementType vertexType = new ElementType("vertex",
                new Property("x", DataType.FLOAT),
                new Property("y", DataType.FLOAT),
                new Property("z", DataType.FLOAT));
        float[] x = vertices.property(PLYType.FLOAT32, "x");
        float[] y = vertices.property(PLYType.FLOAT32, "y");
        float[] z = vertices.property(PLYType.FLOAT32, "z");

        long registerStartTime = System.currentTimeMillis();
        for(int i = 0; i < vertices.size; i++) {
            ProjCoordinate orthogonalCoords = new ProjCoordinate(x[i], y[i]);
            transA.transform(orthogonalCoords, geodeticCoords);
            GeoCoordinates coordinates = new GeoCoordinates(geodeticCoords.y, geodeticCoords.x, (double) z[i]);
            //ISEA4DFaceCoordinates faceCoordinates = Objects.requireNonNull(MortonUtils.toFaceCoordinate(coordinates).toOrthogonal());

            String pcCode = MortonUtils.toPDCode(coordinates, DGGS.MAX_XY_RESOLUTION);
            String targetCode = pcCode.substring(0,target_resolution);
            if(!voxelizedSet.contains(targetCode)) {
                voxelizedSet.add(targetCode);
//                GeoCoordinates voxel_coordinate = MortonUtils.toGeoCoordinate(pcCode, target_resolution);
//                ProjCoordinate geodeticCoords2 = new ProjCoordinate(voxel_coordinate.getLon(),voxel_coordinate.getLat());
//                transB.transform(geodeticCoords2, orthogonalCoords2);
//
//                Element vertex = new Element(vertexType);
//                vertex.setDouble("x", orthogonalCoords2.x);
//                vertex.setDouble("y", orthogonalCoords2.y);
//                vertex.setDouble("z", voxel_coordinate.getHeight());
//                elements.add(vertex);
            } else {
                if(filteredMap.containsKey(targetCode)) {
                    int count = filteredMap.get(targetCode);
                    count += 1;
                    filteredMap.replace(targetCode, count);
                }
                else {
                    filteredMap.put(targetCode, 2);
                }
            }
        }

        for(int threshold_value = 2; threshold_value <= threshold_max_count; threshold_value++) {
            for(Map.Entry<String, Integer> entry : filteredMap.entrySet()) {
                if(entry.getValue() > threshold_value) {
                    GeoCoordinates voxel_coordinate = MortonUtils.toGeoCoordinate(entry.getKey(), target_resolution);
                    ProjCoordinate geodeticCoords2 = new ProjCoordinate(voxel_coordinate.getLon(),voxel_coordinate.getLat());
                    transB.transform(geodeticCoords2, orthogonalCoords2);

                    Element vertex = new Element(vertexType);
                    vertex.setDouble("x", orthogonalCoords2.x);
                    vertex.setDouble("y", orthogonalCoords2.y);
                    vertex.setDouble("z", voxel_coordinate.getHeight());
                    elements.add(vertex);
                }
            }

            System.out.println("Registration (" + target_resolution + "," + threshold_value + ")");
            System.out.println("Original size : " + vertices.size);
            System.out.println("Filtered counts : " + filteredMap.size());
            System.out.println("Registration finish : " + (System.currentTimeMillis() - registerStartTime) / 1000 + "." + (System.currentTimeMillis() - registerStartTime) % 1000);
            System.out.println("Voxelized size : " + elements.size());

            long writeStartTime = System.currentTimeMillis();
            PlyWriter.writePly(new File("dggs_" + target_resolution + "_" + threshold_value + "_" + fileName), elements);
            System.out.println("Write finish : " + (System.currentTimeMillis() - writeStartTime) / 1000 + "." + (System.currentTimeMillis() - writeStartTime) % 1000);
            elements.clear();
        }
    }
}
