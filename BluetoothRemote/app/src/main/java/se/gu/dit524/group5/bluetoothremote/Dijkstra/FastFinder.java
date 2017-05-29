package se.gu.dit524.group5.bluetoothremote.Dijkstra;

import se.gu.dit524.group5.bluetoothremote.Voronoi.Edge;
import se.gu.dit524.group5.bluetoothremote.Voronoi.Graph;
import se.gu.dit524.group5.bluetoothremote.Voronoi.Node;

/**
 * Implemented by julian.bock on 22.02.17. (ad part of LAD)
 * Integrated into the RemoteControl on 24.05.17. */
public class FastFinder {
    /** Fast route finder using Dijkstras algorithm  (see TramFinder.findRoute) **/
    public static Node[] findRoute(Graph nw, Node from, Node to) {
        if (!nw.hasNode(from)) from = nw.addToGraph(from);
        else from = nw.findNode(from);
        if (!nw.hasNode(to)) to = nw.addToGraph(to);
        else to = nw.findNode(to);

        // Initialize a new heap to keep track of currently available connections
        // and an array holding information about visited nodes, routes and times.
        Heap availableConnections = new PriorityHeap(nw.getNodes().size());
        PriorityConnection[] fastest = new PriorityConnection[nw.getNodes().size()];

        // First define the starting point for this route, then (following Dijkstra) iterate
        // through all connected nodes within the network until the destination is reached:
        fastest[from.id()] = new PriorityConnection(from, new Edge(from, from, 0), 0);
        if (from.getNeighbours().size() == 0) return null;

        while (!from.equals(to)) {
            // Add all new edges (going out from the current node and leading to a unvisited endpoint) to the heap:
            for (Node node : from.getNeighbours().keySet())
                if (fastest[node.id()] == null) {
                    Edge edge = from.getNeighbours().get(node);
                    availableConnections.add(new PriorityConnection(from, edge, fastest[from.id()].weight()));
                }
            if (availableConnections.isEmpty()) return null;

            // Get the next 'shortest' connection off the heap and store any information about the path taken within
            // the 'fastest' array - which will automatically mark the new node as visited, since fastest[conn.to.id]
            // will no longer be equal to null.
            PriorityConnection shortest = (PriorityConnection)availableConnections.removeMin();
            fastest[shortest.to.id()] = shortest;

            // In case the destination is reached, it's time to return a sorted representation of the fastest route:
            if (shortest.to.equals(to)) {
                SortedListImpl<PriorityConnection> route = new SortedListImpl();
                PriorityConnection backtrack = fastest[shortest.to.id()];
                do route.add(backtrack);
                while (!(backtrack = fastest[backtrack.from.id()]).from.equals(backtrack.to));

                Node[] resultingRoute = new Node[route.size()];
                for (int i = 0; i < resultingRoute.length; i++) resultingRoute[i] = route.get(i).to;
                return resultingRoute;
            }
            else from = shortest.to;    // In any other case, continue the next iteration using the latest endpoint.
        }
        return null;
    }
}
