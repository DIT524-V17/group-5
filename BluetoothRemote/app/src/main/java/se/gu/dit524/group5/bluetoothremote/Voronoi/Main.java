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

        Graph g = new Graph(new LinearFunction(200,200));


        g.addNode(new Node(0,2, 0));
        g.addNode(new Node(1,6, 1));
        g.addNode(new Node(2,0, 2));
        g.addNode(new Node(3,4, 3));
        g.addNode(new Node(4,3, 4));
        g.addNode(new Node(5,1, 5));
        g.addNode(new Node(6,5, 6));

        g.addEdge(new Edge(g.nodes.get(0),g.nodes.get(2),g.linFunc));
        g.addEdge(new Edge(g.nodes.get(0),g.nodes.get(3),g.linFunc));
        g.addEdge(new Edge(g.nodes.get(2),g.nodes.get(1),g.linFunc));
        g.addEdge(new Edge(g.nodes.get(2),g.nodes.get(4),g.linFunc));
        g.addEdge(new Edge(g.nodes.get(1),g.nodes.get(6),g.linFunc));
        g.addEdge(new Edge(g.nodes.get(3),g.nodes.get(4),g.linFunc));
        g.addEdge(new Edge(g.nodes.get(3),g.nodes.get(5),g.linFunc));
        g.addEdge(new Edge(g.nodes.get(5),g.nodes.get(6),g.linFunc));

        //ACTUAL dijkstra calculation
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(g.nodes.get(3));
        LinkedList<Node> path = dijkstra.getPath(g.nodes.get(1));

        //print nodes
        System.out.println("EDGES");
        for(Node e : path){
            System.out.println(e);
        }
        System.out.println();

    }

}
