package se.gu.dit524.group5.bluetoothremote.Voronoi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

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
    public  Bitmap voronoiMap, siteMap;
    private LinearFunction linFun;
    private int nodeCounter;
    public  Graph voronoiGraph;
    private Bitmap inputMap;

    public Voronoi(Bitmap map){
        this.inputMap = map;
        this.MAP_HEIGHT = map.getHeight();
        this.MAP_WIDTH = map.getWidth();
        this.sites = new ArrayList<>();
        this.polygons = new ArrayList<>();
        this.builder = new VoronoiDiagramBuilder();
        this.envelope = new Envelope(new Coordinate(MAP_MIN,MAP_MIN), new Coordinate(MAP_WIDTH,MAP_HEIGHT));
        this.voronoiMap = Bitmap.createBitmap(MAP_WIDTH, MAP_HEIGHT, Bitmap.Config.ARGB_4444);
        this.siteMap = Bitmap.createBitmap(MAP_WIDTH, MAP_HEIGHT, Bitmap.Config.ARGB_4444);
        this.linFun = new LinearFunction(MAP_WIDTH, MAP_HEIGHT);
        this.voronoiGraph = new Graph(linFun);
        this.nodeCounter = 0;

    }

    private Coordinate[] getPolygonVertices(Polygon poly){
        Coordinate[] cs = null;
        try {
            cs = poly.getBoundary().getCoordinates();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return cs;
    }

    public void addSite(Coordinate site){
        for(Coordinate coor : this.sites) {
            if(Math.abs(coor.x - site.x) < 5.0 && Math.abs(coor.y - site.y) < 5.0){
                return;
            }
        }
        this.sites.add(site);
    }

    public void createVoronoi(){
        /*
        Canvas c = new Canvas(this.voronoiMap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2.0f);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE); */

        extractPolygonsFromGeometry();
        for(Polygon poly: this.polygons) {
            ArrayList tempIntCoords = new ArrayList();
            ArrayList tempIntCoords2 = new ArrayList();
            Coordinate[] cs = getPoylgonVertices(poly);
            if (cs == null) return;
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

            /*
            Path p = new Path();
            p.moveTo(xs[0], ys[0]);
            for(int i = 1; i < xs.length; i++){
                p.lineTo(xs[i], ys[i]);
            }
            c.drawPath(p, paint); */
        }
        // return this.voronoiMap;
    }

    private static final int NODE_COLOR = Color.argb(0xFF, 0x71, 0xB9, 0x60);
    private static final int EDGE_COLOR = Color.argb(0xFF, 0x71, 0xB9, 0xE5);
    private static final int SITE_COLOR = Color.argb(0xFF, 0xff, 0x40, 0x81);

    public Bitmap getVoronoiMap() {
        this.drawNodesAndEdges(null);
        return this.voronoiMap;
    }

    public void drawNodesAndEdges(Canvas c) {
        if (c == null) {
            this.voronoiMap = Bitmap.createBitmap(MAP_WIDTH, MAP_HEIGHT, Bitmap.Config.ARGB_4444);
            c = new Canvas(this.voronoiMap);
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1.5f);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        paint.setColor(NODE_COLOR);
        for (Node n : this.voronoiGraph.nodes)
            if (n.getNeighbours().size() > 0) c.drawCircle((float) n.x, (float) n.y, 2.0f, paint);

        paint.setColor(EDGE_COLOR);
        for (Edge e : this.voronoiGraph.edges) c.drawLine((float) e.v1.x, (float) e.v1.y, (float) e.v2.x, (float) e.v2.y, paint);
    }

    public Bitmap getSiteMap() {
        this.drawSiteMap(null);
        return this.siteMap;
    }

    public void drawSiteMap(Canvas c) {
        if (c == null) {
            this.siteMap = Bitmap.createBitmap(MAP_WIDTH, MAP_HEIGHT, Bitmap.Config.ARGB_4444);
            c = new Canvas(this.siteMap);
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1.5f);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(SITE_COLOR);

        for (Coordinate s : this.sites) c.drawCircle((float) s.x, (float) s.y, 2.0f, paint);
    }

    public Bitmap exportSiteMap() {
        Bitmap bmp = Bitmap.createBitmap(MAP_WIDTH, MAP_HEIGHT, Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(bmp);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1.5f);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(SITE_COLOR);

        for (Coordinate s : this.sites) c.drawPoint((float) s.x, (float) s.y, paint);
        return bmp;
    }

    public void extractVoronoiToGraph() {
        for (Polygon poly : this.polygons) {
            Coordinate[] cs = getPoylgonVertices(poly);
            if (cs == null) continue;
            for (int i = 0; i < cs.length; i++) {
                int j = i + 1;
                if (j < cs.length) {

                    Coordinate[] trimmedCoor = this.linFun.getTrimmedCoordinates(cs[i], cs[j]);
                    if (trimmedCoor == null) {
                        continue;
                    }
                    cs[i] = trimmedCoor[0];
                    cs[j] = trimmedCoor[1];

                    Node ver = new Node(cs[i].x, cs[i].y, this.nodeCounter);
                    if (this.voronoiGraph.addNode(ver)) {
                        this.nodeCounter++;
                    } else {
                        ver = this.voronoiGraph.findNode(ver);
                    }
                    Node ver2 = new Node(cs[j].x, cs[j].y, this.nodeCounter);
                    if (this.voronoiGraph.addNode(ver2)) {
                        this.nodeCounter++;
                    } else {
                        ver2 = this.voronoiGraph.findNode(ver2);
                    }

                    Edge e = this.voronoiGraph.addEdge(ver, ver2);
                    if (e != null) {
                        boolean isColliding = false;
                        double m = this.linFun.getSlope(ver, ver2);
                        double b = this.linFun.getB(ver, m);
                        double min = Math.min(ver.x, ver2.x);
                        double max = Math.max(ver.x, ver2.x);

                        if (min == max) {
                            double minY = Math.min(ver.y, ver2.y);
                            double maxY = Math.max(ver.y, ver2.y);
                            for (int y = (int) minY; y <= maxY; y++)
                                if ((inputMap.getPixel((int) min, y) &0xff) != 0xff) {
                                    isColliding = true;
                                    break;
                                }
                        }
                        else {
                            for (double k = min; k <= max; k++) {
                                int x = (int) k;
                                int y = (int) ((m * x) + b);

                                if (x < 0) x = 0;
                                else if (x >= inputMap.getWidth()) x = inputMap.getWidth() - 1;

                                if (y < 0) y = 0;
                                else if (y >= inputMap.getHeight()) y = inputMap.getHeight() - 1;

                                if ((inputMap.getPixel(x, y) & 0xff) != 0xff) {
                                    isColliding = true;
                                    break;
                                }

                                if (y + 1 < inputMap.getHeight())
                                    if ((inputMap.getPixel(x, y + 1) & 0xff) != 0xff) {
                                        isColliding = true;
                                        break;
                                    }

                                if (y - 1 >= 0)
                                    if ((inputMap.getPixel(x, y - 1) & 0xff) != 0xff) {
                                        isColliding = true;
                                        break;
                                    }
                            }
                        }
                        if (isColliding) this.voronoiGraph.edges.remove(e);
                        else {
                            ver.addNeighbour(ver2, e);
                            ver2.addNeighbour(ver, e);
                        }
                    }
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

    public ArrayList<Coordinate> getSites() {
        return this.sites;
    }

    public void parseSites(Bitmap siteMap) {
        for (int x = 0; x < siteMap.getWidth(); x++)
            for (int y = 0; y < siteMap.getHeight(); y++)
                if (siteMap.getPixel(x, y) != 0) this.addSite(new Coordinate(x, y));
    }
}
