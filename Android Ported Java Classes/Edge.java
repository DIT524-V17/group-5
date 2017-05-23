package com.example.mghan.implementationvoronidijkstra;

public class Edge {

    Node v1;
    Node v2;
    int distance;
    String function;
    LinearFunction functioner = new LinearFunction();

    public Edge(Node v1, Node v2, int distance) {
        this.v1 = v1;
        this.v2 = v2;
        this.distance = distance;
    }

    public Edge(Node v1, Node v2){
        this.v1 = v1;
        this.v2 = v2;
        this.distance = (int)Math.round((Math.sqrt(Math.pow((v2.x - v1.x) ,2) + Math.pow((v2.y - v1.y) , 2))));
        this.function = functioner.getLinearFunction(v1, v2);

    }

    /**
     * @return Edge as string
     */
    @Override
    public String toString()
    {
        return "Edge From: " + v1.identifier + " to: " + v2.identifier+ " distance: " + distance;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Edge){
            Edge e = (Edge) obj;

            return (e.v1.equals(this.v1) && e.v2.equals(this.v2)) || e.v1.equals(this.v2) && e.v2.equals(this.v1);
        } else {
            return false;
        }
    }
}