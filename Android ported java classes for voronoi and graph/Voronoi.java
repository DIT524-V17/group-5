package com.example.mghan.implementationvoronidijkstra;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;

import java.util.ArrayList;

public class Voronoi {
    private final int MAP_HEIGHT;
    private final int MAP_WIDTH;
    private final int MAP_MIN = 0;
    private ArrayList<Coordinate> sites;
    private ArrayList<Polygon> polygons;
    private VoronoiDiagramBuilder builder;
    private Envelope envelope;
    private Bitmap voronoiMap;
    private LinearFunction linFun;
    private int nodeCounter;
    public Graph voronoiGraph;

    public Voronoi(int mapHeight, int mapWidth){
        this.MAP_HEIGHT = mapHeight;
        this.MAP_WIDTH = mapWidth;
        this.sites = new ArrayList<>();
        this.polygons = new ArrayList<>();
        this.builder = new VoronoiDiagramBuilder();
        this.envelope = new Envelope(new Coordinate(MAP_MIN,MAP_MIN), new Coordinate(MAP_WIDTH,MAP_HEIGHT));
        this.voronoiMap = Bitmap.createBitmap(MAP_WIDTH, MAP_HEIGHT, Bitmap.Config.ARGB_4444);
        this.linFun = new LinearFunction();
        this.nodeCounter = 0;
        this.voronoiGraph = new Graph();
    }

    private Coordinate[] getPoylgonVertices(Polygon poly){

        Coordinate[] cs = poly.getBoundary().getCoordinates();
        return  cs;
    }

    public void addSite(Coordinate site){
        for(Coordinate coor : this.sites) {
            if(Math.abs(coor.x - site.x) < 5.0 && Math.abs(coor.y - site.y) < 5.0){
                return;
            }
        }
        this.sites.add(site);
    }

    public Bitmap createVoronoi(){
        extractPolygonsFromGeometry();
        for(Polygon poly: this.polygons) {
            ArrayList tempIntCoords = new ArrayList();
            ArrayList tempIntCoords2 = new ArrayList();
            Coordinate[] cs = getPoylgonVertices(poly);
            for (Coordinate coor : cs) {
                tempIntCoords.add((int) Math.round(coor.x));
                tempIntCoords2.add((int) Math.round(coor.y));
            }

            int[] xs = new int[tempIntCoords.size()];
            int[] ys = new int[tempIntCoords2.size()];
            for (int i = 0; i < xs.length; i++) {
                xs[i] = (int) tempIntCoords.get(i);
                ys[i] = (int) tempIntCoords2.get(i);
            }

            Canvas c = new Canvas(this.voronoiMap);
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            Path p = new Path();
            p.moveTo(xs[0], ys[0]);

            for(int i = 1; i < xs.length; i++){
                p.lineTo(xs[i], ys[i]);
            }

            c.drawPath(p, paint);

        }

        return this.voronoiMap;
    }

    public void extractVoronoiToGraph(){

        for(Polygon poly : this.polygons) {
            Coordinate[] cs = getPoylgonVertices(poly);
            for (int i = 0; i < cs.length; i++) {
                int j = i + 1;
                if (j < cs.length) {

                    Coordinate[] trimmedCoor = this.linFun.getTrimmedCoordinates(cs[i], cs[j]);
                    if (trimmedCoor == null) {
                        continue;
                    }
                    cs[i] = trimmedCoor[0];
                    cs[j] = trimmedCoor[1];

                    Node ver = new Node(cs[i].x, cs[i].y, "Node " + this.nodeCounter);
                    if (this.voronoiGraph.addNode(ver)) {
                        this.nodeCounter++;
                    } else {
                        ver = this.voronoiGraph.findNode(ver);
                    }
                    Node ver2 = new Node(cs[j].x, cs[j].y, "Node " + this.nodeCounter);
                    if (this.voronoiGraph.addNode(ver2)) {
                        this.nodeCounter++;
                    } else {
                        ver2 = this.voronoiGraph.findNode(ver2);
                    }

                    this.voronoiGraph.addEdge(ver, ver2);
                }
            }
        }
    }

    private void extractPolygonsFromGeometry(){
        this.builder.setSites(this.sites);
        this.builder.setClipEnvelope(this.envelope);
        Geometry geo = builder.getDiagram(new GeometryFactory());
        for(int i = 0; i < geo.getNumGeometries(); i++){
            addPolygon((Polygon)geo.getGeometryN(i));
        }
    }

    private void addPolygon(Polygon poly){
        this.polygons.add(poly);
    }
}
