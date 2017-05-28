/**
 * Created by Vin on 15/05/2017.
 */

public class Edge {
    private final Node n1;
    private final Node n2;
    private final double distance;
    final LinearFunction function;

    public Edge(Node n1, Node n2, LinearFunction func){
        this.n1 = n1;
        this.n2 = n2;
        this.function = func;
        this.distance = Math.sqrt(
                      Math.pow(n1.y()-n2.y(),2)
                    + Math.pow(n1.x()-n2.x(),2)
                      );
    }

    @Override
    public String toString(){
        return n1 + "---" + n2;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Edge){
            Edge e = (Edge) obj;

            return (e.n1.equals(this.n1) && e.n2.equals(this.n2)) || e.n1.equals(this.n2) && e.n2.equals(this.n1);
        } else {
            return false;
        }
    }

    public boolean hasNode(Node n){
        return n.equals(this.n1) || n.equals(this.n2);
    }

    public Node destination(Node n){
        //@return other end of edge, assuming n is an end of the edge
        //FIXME: make sure this doesnt return nullpointers
        return this.n1.equals(n) ? this.n2 : this.n1;
    }

    public Node n1(){
        return this.n1;
    }

    public Node n2(){
        return this.n2;
    }

    public boolean containsPoint(double x, double y){
        boolean contains = true;
        contains = contains && ((x<this.n1().x() && x>this.n2().x()) ||
                                 x>this.n1().x() && x<this.n2().x());
        contains = contains && ((y<this.n1().y() && y>this.n2().y()) ||
                                 y>this.n1().y() && y<this.n2().y());
        return contains;

    }

    public double distance(){
        return distance;
    }

    public double slope(){
        return this.function.getSlope(this.n1,this.n2);
    }

    //TODO: bugtest/bugfix the algorithm

}
