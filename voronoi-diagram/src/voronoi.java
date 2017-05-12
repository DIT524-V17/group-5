/**
 * Created by mghan on 2017-05-10.
 */
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.*;
import com.vividsolutions.jts.geom.*;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.awt.image.BufferedImage;
import java.math.*;
import java.io.File;
import javax.imageio.ImageIO;

public class voronoi {

    int MAP_SIZE = 200;
    public voronoi() {
        VoronoiDiagramBuilder vornoiBuilder = new VoronoiDiagramBuilder();
        ArrayList<Coordinate> coordinates = new ArrayList();
        coordinates.add(new Coordinate(10, 20));
        coordinates.add(new Coordinate(50, 60));
        coordinates.add(new Coordinate(25, 65));
        coordinates.add(new Coordinate(123, 123));
        coordinates.add(new Coordinate(180, 160));
        vornoiBuilder.setSites(coordinates);
        Envelope envelope = new Envelope(new Coordinate(0.0,0.0), new Coordinate(MAP_SIZE,MAP_SIZE));
        vornoiBuilder.setClipEnvelope(envelope);
        Geometry geo = vornoiBuilder.getDiagram(new GeometryFactory());
        ArrayList<Polygon> polygons = new ArrayList<Polygon>();
        for(int i = 0; i < geo.getNumGeometries(); i++) {
         polygons.add((Polygon) geo.getGeometryN(i));
        }

       /* DelaunayTriangulationBuilder delauny = new DelaunayTriangulationBuilder();
        delauny.setSites(coordinates);
        GeometryCollection triangles = (GeometryCollection) delauny.getTriangles(new GeometryFactory());
        ArrayList<Polygon> neighbors = new ArrayList<Polygon>();
        for(int i = 0; i < triangles.getNumGeometries(); i++){
            neighbors.add((Polygon) triangles.getGeometryN(i));
        } */

        BufferedImage img = new BufferedImage(MAP_SIZE,MAP_SIZE,BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.createGraphics();
        g.clearRect(0,0,MAP_SIZE,MAP_SIZE);
        for(Polygon poly: polygons){
            ArrayList tempIntCoords = new ArrayList();
            ArrayList tempIntCoords2 = new ArrayList();
            Coordinate[] cs = poly.getBoundary().getCoordinates();
            for(Coordinate coor: cs){
              tempIntCoords.add( (int) Math.round(coor.x));
            }
            int[] xs = new int[tempIntCoords.size()];
            for(int i = 0; i < xs.length; i++){
                xs[i] = (int) tempIntCoords.get(i);
            }

            for(Coordinate coor: cs){
                tempIntCoords2.add( (int) Math.round(coor.y));
            }
            int[] ys = new int[tempIntCoords2.size()];
            for(int i = 0; i < ys.length; i++){
                ys[i] = (int) tempIntCoords2.get(i);
            }
            g.setColor(java.awt.Color.RED);
            g.drawPolygon(xs, ys, xs.length);
        }

       /* for(Polygon triangle: neighbors){
            ArrayList tempIntCoords = new ArrayList();
            ArrayList tempIntCoords2 = new ArrayList();
            Coordinate[] cs = triangle.getBoundary().getCoordinates();
            for(Coordinate coor: cs){
                tempIntCoords.add ((int) Math.round(coor.x));
            }
            int[] xs = new int[tempIntCoords.size()];
            for(int i = 0; i < xs.length; i++){
                xs[i] = (int) tempIntCoords.get(i);
            }

            for(Coordinate coor: cs){
                tempIntCoords2.add((int) Math.round(coor.y));
            }
            int[] ys = new int[tempIntCoords2.size()];
            for(int i = 0; i < ys.length; i++){
                ys[i] = (int) tempIntCoords2.get(i);
            }
            g.setColor(java.awt.Color.YELLOW);
            g.drawPolygon(xs, ys, xs.length);
        } */

        File output = new File("/Users/mghan/Desktop/image-voronoi.png");
        output.getAbsolutePath();
        try {
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args){
        voronoi vor = new voronoi();
    }

}
