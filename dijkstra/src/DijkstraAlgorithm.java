import java.util.*;

/**
 * Created by Vin on 15/05/2017.
 */
public class DijkstraAlgorithm {
    private final List<Node> nodes;
    private final List<Edge> edges;
    private Set<Node> settledNodes;
    private Set<Node> unSettledNodes;
    private Map<Node,Node> predecessors;
    private Map<Node,Double> distance;

    public DijkstraAlgorithm(Graph g){
        this.nodes = new ArrayList<Node>(g.nodes);
        this.edges = new ArrayList<Edge>(g.edges);
    }

    public void execute(Node n){
        settledNodes = new HashSet<Node>();
        unSettledNodes = new HashSet<Node>();
        distance = new HashMap<Node, Double>();
        predecessors = new HashMap<Node, Node>();
        distance.put(n,0.0);
        unSettledNodes.add(n);
        while(unSettledNodes.size()>0){
            Node node = getMin(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinDistances(node);
        }

    }

    private List<Node> getNeighbours(Node node){
        List<Node> neighbours = new ArrayList<Node>();
        for (Edge e : edges) {
            if (e.n1.equals(node) && !this.settledNodes.contains(e.n2)){
                neighbours.add(e.n2);
            }
            if (e.n2.equals(node) && !this.settledNodes.contains(e.n1)){
                neighbours.add(e.n1);
            }
        }
        return neighbours;
    }

    private Node getMin(Set<Node> nodes){
        Node min = null;
        for (Node n : nodes){
            if (min == null){
                min = n;
            }
            else{
                if (getShortestDistance(n) < getShortestDistance(min)){
                    min = n;
                }
            }
        }
        return min;
    }

    private void findMinDistances(Node n1){
        List<Node> adjacentNodes = getNeighbours(n1);
        for (Node n2 : adjacentNodes){
            if (getShortestDistance(n2) > getShortestDistance(n1) + getDistance(n1,n2)){
                distance.put(n2, getShortestDistance(n1)+getDistance(n2,n1));
                predecessors.put(n2,n1);
                unSettledNodes.add(n2);
            }
        }
    }

    private double getShortestDistance(Node n){
        Double d = distance.get(n);
        if(d == null) {
            return Integer.MAX_VALUE;
        }
        else{
            return d;
        }
    }

    public LinkedList<Node> getPath(Node n){
        LinkedList<Node> path = new LinkedList<Node>();
        Node step = n;
        if (predecessors.get(step) == null)
            return null;
        path.add(step);
        while(predecessors.get(step)!=null){
            step = predecessors.get(step);
            path.add(step);
        }
        Collections.reverse(path);
        return path;
    }

    private double getDistance(Node n1, Node n2){
        for (Edge e : edges){
            if(e.n1.equals(n1) && e.n2.equals(n2))
                return e.distance;
            if(e.n1.equals(n2) && e.n2.equals(n1))
                return e.distance;

        }
        throw new RuntimeException("you done messed up");
    }
}
