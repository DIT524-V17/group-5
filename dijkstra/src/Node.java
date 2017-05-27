import com.vividsolutions.jts.geom.Coordinate;

import java.util.ArrayList;

/**
 * Created by Vin on 15/05/2017.
 */
public class Node {
    private double x;
    private double y;
    private int id;

    public Node(double x, double y, int id){
        this.x = x;
        this.y = y;
        this.id = id;
    }

    @Override
    public String toString(){
        return "(x="+this.x+" y="+this.y+")";
    }
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Node))
            return false;
        Node n = (Node) obj;
        return this.id() == n.id() || Math.abs(n.x - this.x) <= 0.5f && Math.abs(n.y - this.y) <= 0.5f;
    }

    public int id() {
        return this.id;
    }

    public double x(){
        return this.x;
    }

    public double y(){
        return this.y;
    }

}
