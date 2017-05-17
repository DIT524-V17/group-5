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
    int MAP_MIN = 0;
    int POLYCOUNTER = 1;
    public voronoi() {

        VoronoiDiagramBuilder vornoiBuilder = new VoronoiDiagramBuilder();
        //Array list that stores all the sites
        ArrayList<Coordinate> coordinates = new ArrayList();
        coordinates.add(new Coordinate(10, 20));
        coordinates.add(new Coordinate(50, 60));
        coordinates.add(new Coordinate(25, 65));
        coordinates.add(new Coordinate(123, 123));
        coordinates.add(new Coordinate(180, 160));
        coordinates.add(new Coordinate(78, 100));

        //Assigning those sites to the previously initialized voronoi builder which will do magic
        vornoiBuilder.setSites(coordinates);

       //Setting an envelope which is like a wrapper around the area independent of some kind of panel or bitmap image
        Envelope envelope = new Envelope(new Coordinate(0.0,0.0), new Coordinate(MAP_SIZE,MAP_SIZE));
        vornoiBuilder.setClipEnvelope(envelope);

        //Extracting the result as some form of geometry
        Geometry geo = vornoiBuilder.getDiagram(new GeometryFactory());

       //Extracting all the polygons in that geometry to then extract the coordinates of the vertices of those polygons
        ArrayList<Polygon> polygons = new ArrayList<Polygon>();
        for(int i = 0; i < geo.getNumGeometries(); i++) {
         polygons.add((Polygon) geo.getGeometryN(i));
        }

        Coordinate[] polygonVertices = vornoiBuilder.getDiagram(new GeometryFactory()).getCoordinates();

        //Array list that allows you to not be so strict about size storing all vertices coordinates such that they are
        //within the boundaries of the map
        ArrayList<Coordinate> polygonVerticesTempArrayList = new ArrayList<Coordinate>();
        for (Coordinate coor: polygonVertices){
            if( ((coor.x <= MAP_SIZE) && (coor.x >= MAP_MIN)) && ((coor.y <= MAP_SIZE)&&(coor.y >= MAP_MIN)) ){
                polygonVerticesTempArrayList.add(coor);
            }
        }
        //Bound vertices is just the array that will be accessed to get coordinates for vertices
        Coordinate[] boundVertices = new Coordinate[polygonVerticesTempArrayList.size()];
        for(int i = 0; i < boundVertices.length; i++){
            boundVertices[i] = polygonVerticesTempArrayList.get(i);
        }
        //Printing the results just to see what kind of data i ended up with

        /*for (Coordinate coor: boundVertices) {
            System.out.println(coor.x + " : " + coor.y);
        }*/

        //Uncomment the following if you wish to see delauny triangulation on the diagram

       /* DelaunayTriangulationBuilder delauny = new DelaunayTriangulationBuilder();
        delauny.setSites(coordinates);
        GeometryCollection triangles = (GeometryCollection) delauny.getTriangles(new GeometryFactory());
        ArrayList<Polygon> neighbors = new ArrayList<Polygon>();
        for(int i = 0; i < triangles.getNumGeometries(); i++){
            neighbors.add((Polygon) triangles.getGeometryN(i));
        } */

        //The output method chosen is a buffered image because why not
        BufferedImage img = new BufferedImage(MAP_SIZE,MAP_SIZE,BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.createGraphics();
        g.clearRect(0,0,MAP_SIZE,MAP_SIZE);

        //Ok this is a big for loop but the summary goes as follows
        //FOr every polygon that we extracted get the coordinates for the boundaries (a.k.a) vertices.
        //Take the x coordinate of those vertices and round them to int and add them to an integer array
        //Take the y coordinates of those vertices and round them to int then add them to another array
        //Now use graphics to draw the polygon from its set of vertices.
        //Repeat the process above for every polygon until the diagram is over.

        //STUFF I HAVE ADDED TO THE PROCESS
        //For every vertex on the diagram if it is within the boundaries of the map then add a blue circle at that point
        //TODO: Use vertex pairs to get functions of lines "Edges" that you can later on add nodes to at will.
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

            System.out.println("Polygon number: " + POLYCOUNTER);
            for(int i = 0; i< xs.length; i++){
                System.out.println(xs[i] + " : " + ys[i]);
            }
            POLYCOUNTER++;
        }

            g.setColor(Color.BLUE);
            for(Coordinate coor: polygonVertices){
                drawCenteredCircle(g, (int)coor.x, (int)coor.y, 5);
            }

            //Uncomment below if you wish to see the delauny triangulation.

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

    public void drawCenteredCircle(Graphics g, int x, int y, int r) {
        x = x-(r/2);
        y = y-(r/2);
        g.fillOval(x,y,r,r);
    }

    public static void main(String[] args){
        voronoi vor = new voronoi();
    }

}
