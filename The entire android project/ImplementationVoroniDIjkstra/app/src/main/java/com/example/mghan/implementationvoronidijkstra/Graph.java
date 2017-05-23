package com.example.mghan.implementationvoronidijkstra;
import java.util.ArrayList;

public class Graph {
    protected ArrayList<Node> nodes;
    protected ArrayList<Edge> edges;

    public Graph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }


    public void addEdge(Node v1, Node v2) {
        if(v1.equals(v2) || ((v1.x == 0.0 && v1.y == 0) && (v2.x == 0.0 && v2.y == 0))){return;}
        Edge temp = findEdge(new Edge(v1, v2));
        if (temp != null) {
            // Don't allow multiple edges, update cost.
            System.out.println("Edge " + v1.identifier+ "," + v2.identifier + " already exists.");
        } else {
            this.edges.add(new Edge(v1, v2));
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
            System.out.println("Node " + v.identifier + " already exists in the graph");
            return false;
        }
    }

}