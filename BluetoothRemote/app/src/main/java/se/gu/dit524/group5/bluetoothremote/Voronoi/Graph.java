package se.gu.dit524.group5.bluetoothremote.Voronoi;
import java.util.ArrayList;

public class Graph {
    protected ArrayList<Node> nodes;
    protected ArrayList<Edge> edges;
    protected LinearFunction linFunc;

    public Graph(LinearFunction linearFunction) {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        this.linFunc = linearFunction;
    }

    public Edge addEdge(Node v1, Node v2) {
        if(v1.equals(v2) || ((v1.x == 0.0 && v1.y == 0) && (v2.x == 0.0 && v2.y == 0))) return null;
        Edge temp = findEdge(new Edge(v1, v2, linFunc));
        if (temp != null) {
            // Don't allow multiple edges, update cost.
            // System.out.println("Edge " + v1.id + "," + v2.id + " already exists.");
            return null;
        } else {
            Edge e = new Edge(v1, v2, linFunc);
            this.edges.add(e);
            return e;
        }
    }

    public Edge addEdge(Edge e){
        if(e.v1.equals(e.v2) || ((e.v1.x == 0.0 && e.v1.y == 0) && (e.v2.x == 0.0 && e.v2.y == 0))) return null;
        Edge temp = findEdge(new Edge(e.v1, e.v2, linFunc));
        if (temp != null) {
            // Don't allow multiple edges, update cost.
            // System.out.println("Edge " + v1.id + "," + v2.id + " already exists.");
            return null;
        } else {
            Edge edge = new Edge(e.v1, e.v2, linFunc);
            this.edges.add(edge);
            return edge;
        }
    }

    protected Node findNode(Node v) {
        for (Node each : nodes) {
            if (each.equals(v))
                return each;
        }
        return null;
    }

    private Edge findEdge(Edge e) {
        for (Edge each : edges) {
            if (each.equals(e)) {
                return each;
            }
        }
        return null;
    }

    public boolean addNode(Node v){
        if(findNode(v) == null){
            nodes.add(v);
            return true;
        } else{
            // System.out.println("Node " + v.id + " already exists in the graph");
            return false;
        }
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }
}