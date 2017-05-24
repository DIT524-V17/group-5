/**
 * Created by Vin on 15/05/2017.
 */
public class Edge {
    final Node n1;
    final Node n2;
    final double weight;
    final double slope;

    public Edge(Node n1, Node n2){
        this.n1 = n1;
        this.n2 = n2;
        this.slope = (n2.y - n1.y)/(n2.x - n1.x);
        this.weight = Math.sqrt(
                      Math.pow(n1.y-n2.y,2)
                    + Math.pow(n1.x-n2.x,2)
                      );
    }

    public String toString(){
        return n1 + "---" + n2;
    }

}
