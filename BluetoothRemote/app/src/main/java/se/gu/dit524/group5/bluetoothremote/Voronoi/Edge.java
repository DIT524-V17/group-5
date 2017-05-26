package se.gu.dit524.group5.bluetoothremote.Voronoi;

public class Edge {

    Node v1;
    Node v2;
    int distance;
    String function;

    public Edge(Node v1, Node v2, int distance) {
        this.v1 = v1;
        this.v2 = v2;
        this.distance = distance;
    }

    public Edge(Node v1, Node v2, LinearFunction func){
        this.v1 = v1;
        this.v2 = v2;
        this.distance = (int)Math.round((Math.sqrt(Math.pow((v2.x - v1.x) ,2) + Math.pow((v2.y - v1.y) , 2))));
        this.function = func.getLinearFunction(v1, v2);
    }

    /**
     * @return Edge as string
     */
    @Override
    public String toString()
    {
        return "Edge From: " + v1.id + " to: " + v2.id + " distance: " + distance;
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

    public Node n1() {
        return this.v1;
    }

    public Node n2() {
        return this.v2;
    }

    public Node destination(Node start) {
        if (!start.equals(v1) && !start.equals(v2)) return null;
        else return this.v1.equals(start) ? v2 : v1;
    }

    public int distance() {
        return distance;
    }
}