/**
 * Created by mghan on 2017-05-17.
 */
public class Edge {

    Vertex v1;
    Vertex v2;
    int distance;


    public Edge(Vertex v1, Vertex v2, int distance) {
        this.v1 = v1;
        this.v2 = v2;
        this.distance = distance;
    }

    public Edge(Vertex v1, Vertex v2){
        this.v1 = v1;
        this.v2 = v2;
        this.distance = (int)Math.round((Math.sqrt(Math.pow((v2.x - v1.x) ,2) + Math.pow((v2.y - v1.y) , 2))));

    }

    /**
     * @return Edge as string
     */
    @Override
    public String toString()
    {
        return "Edge From: " + v1.identifier + " to: " + v2.identifier+ " distance: " + distance;
    }
}
