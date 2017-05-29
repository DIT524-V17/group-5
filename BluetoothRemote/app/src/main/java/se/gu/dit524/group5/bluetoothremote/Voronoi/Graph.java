package se.gu.dit524.group5.bluetoothremote.Voronoi;
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
        if (temp == null){
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
        if (temp == null){
            Edge edge = new Edge(e.n1(), e.n2(),linFunc);
            this.edges.add(edge);
            return edge;
        }
        return null;
    }

    public Node findNode(Node n){
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

    public boolean hasNode(Node n){
        return this.nodes.contains(n);
    }

    public Node addToGraph(Node n){
        List<Edge> edges = this.edges;
        List<Edge> newEdges = new ArrayList<>();
        List<Edge> connectedEdges = new ArrayList<>();
        LinearFunction f = this.linFunc;

        for(Edge edge : edges){          //collects edges, perpendicular to every edge already added
            //and stemming from n
            //get slope for the perpendicular line
            double a = -(1 / edge.slope());
            System.out.println(edge.slope());
            System.out.println(a + "slopeeeeeeeeeee");


            //find b for the perpendicular function
            //y = ax+b
            //n.y = a*n.x + b
            //-b = a*n.x - n.y
            //b = n.y - a*n.x
            double b = n.y() - (a * n.x());

            //find b for edge's function
            double bEdge = edge.n1().y() - (edge.slope() * edge.n1().x());

            //find intersection point m for edge and ax+b=y (n)
            //ax+b = cx+d (x1=x2, y1=y2)

            //x = (d-b)/(a-c)
            double x = (bEdge-b) / (a - edge.slope());
            double y = a * x + b;
            System.out.println("x- "+x+"  y- "+y);

            //check if x,y lie within the scope of the map sort of
            boolean lies = true;
            lies = x>0 && lies;
            lies = x<this.linFunc.getWidth() && lies;
            lies = y>0 && lies;
            lies = y<this.linFunc.getHeight() && lies;

            if (lies) {
                newEdges.add(new Edge(new Node(x, y, 10000), n, f));
                connectedEdges.add(edge);
            }
        }

        //find the shortest of the newly drawn edges
        Edge shortest = new Edge(n,n,f);
        Edge closest = new Edge(n,n,f);
        System.out.println(shortest);

        for(int i = 0; i<newEdges.size(); i++) {
            if (shortest.n1()==shortest.n2() || shortest.distance() > newEdges.get(i).distance()) {
                System.out.println("executes");
                shortest = newEdges.get(i);
                closest = connectedEdges.get(i);
            }
        }
        System.out.println(shortest+ "      shortest");
        System.out.println(closest + "      closest");

        //at this point you have the edge to draw and the edge to cut/extend in order to create a node at the
        //appropriate place
        //shortest.n1 is the intersection point

        //check if shortest.n1 lies on closest
        Edge e1,e2;

        //at this point create properly initialized nodes to add later
        n = new Node(n.x(), n.y(), this.nodes.size());
        Node intersection = new Node(shortest.n1().x(), shortest.n1().y(), this.nodes.size() +1);

        if(closest.containsPoint(intersection.x(),intersection.y())) { //intersection on closest

            if(n.equals(intersection)) {
                closest.n1().removeNeighbour(closest.n2());
                closest.n2().removeNeighbour(closest.n1());
                e1 = new Edge(closest.n1(), n, f);

                closest.n1().addNeighbour(n, e1);
                n.addNeighbour(closest.n1(), e1);

                e2 = new Edge(n, closest.n2(), f);
                closest.n2().addNeighbour(n, e2);
                n.addNeighbour(closest.n2(), e2);

                this.edges.add(e1);
                this.edges.add(e2);
                this.nodes.add(n);
            }
            else {
                //break apart edge
                closest.n1().removeNeighbour(closest.n2());
                closest.n2().removeNeighbour(closest.n1());

                e1 = new Edge(closest.n1(), intersection, f);
                closest.n1().addNeighbour(intersection, e1);
                intersection.addNeighbour(closest.n1(), e1);

                e2 = new Edge(intersection, closest.n2(), f);
                closest.n2().addNeighbour(intersection, e2);
                intersection.addNeighbour(closest.n2(), e2);

                //reinitialize shortest to fit clean nodes
                shortest = new Edge(n, intersection, this.linFunc);
                intersection.addNeighbour(n, shortest);
                n.addNeighbour(intersection, shortest);


                this.edges.add(e1);
                this.edges.add(e2);
                this.edges.add(shortest);
                this.nodes.add(n);
                this.nodes.add(intersection);
            }

        }
        else if(intersection.x()>closest.n1().x()){ //intersection on n1 side of closest
            if(n.equals(intersection)) {
                closest.n1().removeNeighbour(closest.n2());
                closest.n2().removeNeighbour(closest.n1());

                e1 = new Edge(closest.n1(), n, f);
                closest.n1().addNeighbour(n, e1);
                n.addNeighbour(closest.n1(), e1);


                this.edges.add(e1);
                this.nodes.add(n);
            }
            else {
                e1 = new Edge(closest.n1(), intersection, f);
                closest.n1().addNeighbour(intersection, e1);
                intersection.addNeighbour(closest.n1(), e1);

                //reinitialize shortest to fit clean nodes
                shortest = new Edge(n, intersection, this.linFunc);
                intersection.addNeighbour(n, shortest);
                n.addNeighbour(intersection, shortest);

                this.edges.add(e1);
                this.edges.add(shortest);
                this.nodes.add(intersection);
                this.nodes.add(n);
            }
        }
        else{   //intersection on n2 side of closest

            if(n.equals(intersection)) {
                closest.n1().removeNeighbour(closest.n2());
                closest.n2().removeNeighbour(closest.n1());

                e2 = new Edge(n, closest.n2(), f);
                closest.n2().addNeighbour(n, e2);
                n.addNeighbour(closest.n2(), e2);

                this.edges.add(e2);
                this.nodes.add(n);
            }
            else{
                e1 = new Edge(closest.n2(),intersection,f);
                closest.n2().addNeighbour(intersection,e1);
                intersection.addNeighbour(closest.n2(),e1);

                //reinitialize shortest to fit clean nodes
                shortest = new Edge(n,intersection,this.linFunc);
                intersection.addNeighbour(n, shortest);
                n.addNeighbour(intersection, shortest);

                this.edges.add(e1);
                this.edges.add(shortest);
                this.nodes.add(intersection);
                this.nodes.add(n);
            }
        }
        return n;
    }

    public List<Node> getNodes(){
        return this.nodes;
    }

    public List<Edge> getEdges() {
        return this.edges;
    }
}

