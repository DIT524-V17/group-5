import java.util.ArrayList;

/**
 * Created by Vin on 15/05/2017.
 */
public class Node {
    double x;
    double y;

    public Node(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString(){
        return "(x="+this.x+" y="+this.y+")";
    }
}
