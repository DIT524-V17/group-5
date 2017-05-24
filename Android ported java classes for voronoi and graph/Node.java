package com.example.mghan.implementationvoronidijkstra;

public class Node {
    double x;
    double y;
    String identifier;

    public Node(double x, double y, String identifier)
    {
        this.x = x;
        this.y = y;
        this.identifier = identifier;
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

