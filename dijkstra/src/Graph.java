import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vin on 15/05/2017.
 */
public class Graph {
    List<Edge> edges;
    List<Node> nodes;

    public Graph(List<Node> nodes, List<Edge> edges){
        this.nodes = nodes;
        this.edges = edges;
    }
}
