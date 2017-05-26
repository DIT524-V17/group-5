package se.gu.dit524.group5.bluetoothremote.Voronoi;

import java.util.HashMap;

public class Node {
    double x;
    double y;
    int id;
    private HashMap<Node, Edge> neighbours;

    public Node(double x, double y, int id)
    {
        this.x = x;
        this.y = y;
        this.id = id;
        this.neighbours = new HashMap<>();
    }

    public void addNeighbour(Node n, Edge e) {
        this.neighbours.put(n, e);
    }

    public HashMap<Node, Edge> getNeighbours() {
        return this.neighbours;
    }

    public int id() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Node){
            Node v = (Node) obj;
            return Math.abs(v.x - this.x) < 0.5 && Math.abs(v.y - this.y) < 0.5;
        }else{
            return false;
        }
    }
}

