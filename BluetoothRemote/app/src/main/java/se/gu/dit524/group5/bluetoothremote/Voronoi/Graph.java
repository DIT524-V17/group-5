import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vin on 15/05/2017.
 */
public class Graph {
    protected List<Edge> edges;
    protected List<Node> nodes;
    protected LinearFunction linFunc;

    public Graph(LinearFunction linearFunction){
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.linFunc = linearFunction;
    }

    public boolean addNode(Node n){
        if(findNode(n) != null)
            return false;
        this.nodes.add(n);
        return true;
    }

    public Edge addEdge(Node n1, Node n2){
        if(n1.equals(n2) || ((n1.x() == 0.0 && n1.y() == 0.0) && (n2.x() == 0.0 && n2.y() == 0)))
            return null;
        Edge temp = findEdge(new Edge(n1,n2,linFunc));
        if (temp != null){
            Edge e = new Edge(n1,n2,linFunc);
            this.edges.add(e);
            return e;
        }
        return null;
    }

    public Edge addEdge(Edge e){
        if(e.n1().equals(e.n2()) || ((e.n1().x() == 0.0 && e.n1().y() == 0.0) && (e.n2().x() == 0.0 && e.n2().y() == 0)))
            return null;
        Edge temp = findEdge(new Edge(e.n1(), e.n2(),linFunc));
        if (temp != null){
            Edge edge = new Edge(e.n1(), e.n2(),linFunc);
            this.edges.add(edge);
            return edge;
        }
        return null;
    }

    protected Node findNode(Node n){
        for (Node each : nodes){
            if (each.equals(n))
                return each;
        }
        return null;
    }

    private Edge findEdge(Edge e){
        for (Edge each : this.edges){
            if(each.equals(e))
                return each;
        }
        return null;
    }

    public void addToGraph(Node n){
        //TODO:find nearest edge
        //get slope of edge
        //get slope of connection
        //get intersection
        //cut at node and intersection
        List<Edge> edges = this.edges;
        List<Edge> newEdges = new ArrayList<>();
        List<Edge> connectedEdges = new ArrayList<>();


        for(Edge edge : edges){                     //collects edges, perpendicular to every edge already added
            //and stemming from n
            //get slope for the perpendicular line
            double a = - (1 / edge.slope());

            //find b for the perpendicular function
            //a * x + b = y
            //b = y /(a * x)
            double b = n.y() / (a * n.x());

            //find b for edge's function
            double bEdge = edge.n1().y() / (edge.slope() * edge.n1().x());

            //find intersection point m for edge and ax+b=y (n)
            //ax+b = cx+d (x1=x2, y1=y2)
            //x = (d-b)/(a-c)
            double x = (bEdge - b) / (a - edge.slope());
            double y = a * x + b;
            System.out.println("x- "+x+"  y-"+y);

            //check if x,y lie on edge
            boolean lies = true;
            lies = x>0 && lies;
            lies = x<400 && lies;
            lies = y>0 && lies;
            lies = y<400 && lies;

            if (lies)
                newEdges.add(new Edge(new Node(x,y,0),n,null));
            connectedEdges.add(edge);
        }

        //find the shortest of the newly drawn edges
        Edge shortest = new Edge(n,n,null);
        Edge closest = new Edge(n,n,null);
        System.out.println(shortest);

        for(int i = 0; i<newEdges.size(); i++) {
            if (shortest.n1()==shortest.n2() || shortest.distance() > newEdges.get(i).distance()) {
                shortest = newEdges.get(i);
                closest = connectedEdges.get(i);
            }
        }
        System.out.println(shortest);
        System.out.println(closest);

        //at this point you have the edge to draw and the edge to cut in order to create a node at the
        //appropriate place

        Edge e1 = new Edge(closest.n1(), shortest.n1(),null);
        Edge e2 = new Edge(shortest.n1(), closest.n2(), null);

        if(this.edges.remove(closest)){
            this.edges.add(e1);
            this.edges.add(e2);
            this.edges.add(shortest);
            this.nodes.add(n);
            this.nodes.add(shortest.n1());
        }


    }

    private int intersectAdd(Edge e){
        return edges.indexOf(e);

    }
}

