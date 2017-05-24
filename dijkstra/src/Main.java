import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Vin on 15/05/2017.
 */
public class Main {

    //testing the validity of the algorithm

    public static void main(String[] args){

        //temp fix for graph import
        //TODO: import proper graph from the voronoi
        List<Node> nodes = new ArrayList<Node>();
        List<Edge> edges = new ArrayList<Edge>();
        nodes.add(new Node(0,2));
        nodes.add(new Node(1,6));
        nodes.add(new Node(2,0));
        nodes.add(new Node(3,4));
        nodes.add(new Node(4,3));
        nodes.add(new Node(5,1));
        nodes.add(new Node(6,5));

        edges.add(new Edge(nodes.get(0),nodes.get(2)));
        edges.add(new Edge(nodes.get(0),nodes.get(3)));
        edges.add(new Edge(nodes.get(2),nodes.get(1)));
        edges.add(new Edge(nodes.get(2),nodes.get(4)));
        edges.add(new Edge(nodes.get(1),nodes.get(6)));
        edges.add(new Edge(nodes.get(3),nodes.get(4)));
        edges.add(new Edge(nodes.get(3),nodes.get(5)));
        edges.add(new Edge(nodes.get(5),nodes.get(6)));

        //ACTUAL dijkstra calculation
        Graph graph = new Graph(nodes,edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(nodes.get(3));
        LinkedList<Node> path = dijkstra.getPath(nodes.get(1));

        //print nodes
        System.out.println("EDGES");
        for(Node e : path){
            System.out.println(e);
        }
        System.out.println();

    }

}
