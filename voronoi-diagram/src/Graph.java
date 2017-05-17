
import java.util.ArrayList;

public class Graph {
    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;

    public Graph() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
    }


    public void add(Vertex v1, Vertex v2) {
        Edge temp = findEdge(v1, v2);
        if (temp != null) {
            // Don't allow multiple edges, update cost.
            System.out.println("Edge " + v1.identifier+ "," + v2.identifier + " already exists.");
        } else {
            // this will also create the vertices
            Vertex v1Temp = findVertex(v1);
            Vertex v2Temp = findVertex(v2);
            if(v1Temp == null){
                this.vertices.add(v1);
            }

            if(v2Temp == null){
                this.vertices.add(v2);
            }

            this.edges.add(new Edge(v1, v2));

        }
    }

    private Vertex findVertex(Vertex v) {
        for (Vertex each : vertices) {
            if (each.identifier.equals(v.identifier))
                return each;
        }
        return null;
    }


    private Edge findEdge(Vertex v1, Vertex v2) {
        for (Edge each : edges) {
            if ((each.v1.identifier.equals(v1.identifier) && each.v2.identifier.equals(v2.identifier)) || (each.v1.identifier.equals(v2.identifier) && each.v2.identifier.equals(v1.identifier))) {
                return each;
            }
        }
        return null;
    }

}