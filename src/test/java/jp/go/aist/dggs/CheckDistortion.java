package jp.go.aist.dggs;

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
import java.util.ArrayList;
import java.util.Objects;

public class CheckDistortion {
    private final static String fileName = "LBJ_2019.ply";//"AIST_Waterfront_8F.ply";//

    public static void main(String[] args) throws Exception {
        ArrayList<Element> elements = new ArrayList<>();
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CRSFactory csFactory = new CRSFactory();
        CoordinateReferenceSystem crsFrom = csFactory.createFromName("EPSG:6676");
        CoordinateReferenceSystem crsTo = csFactory.createFromName("EPSG:4326");
        CoordinateTransform trans = ctFactory.createTransform(crsFrom, crsTo);

        Path path = Paths.get("C:\\Users\\dprt-taehoon\\IdeaProjects\\dggscode", fileName);
        PLY ply = PLY.load(path);
        PLYElementList vertices = ply.elements("vertex");
        ProjCoordinate geodeticCoords = new ProjCoordinate();

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
            trans.transform(orthogonalCoords, geodeticCoords);
            GeoCoordinates coordinates = new GeoCoordinates(geodeticCoords.y, geodeticCoords.x, (double) z[i]);
            ISEA4DFaceCoordinates faceCoordinates = Objects.requireNonNull(MortonUtils.toFaceCoordinate(coordinates).toOrthogonal());

            Element vertex = new Element(vertexType);
            vertex.setDouble("x", faceCoordinates.getX());
            vertex.setDouble("y", faceCoordinates.getY());
            vertex.setDouble("z", faceCoordinates.getZ());
            elements.add(vertex);
        }
        System.out.println("Registration finish : " + (System.currentTimeMillis() - registerStartTime) / 1000);
        System.out.println(elements.size());

        long writeStartTime = System.currentTimeMillis();
        PlyWriter.writePly(new File("dggs_" + fileName), elements);
        System.out.println("Write finish : " + (System.currentTimeMillis() - writeStartTime) / 1000);
    }
}
