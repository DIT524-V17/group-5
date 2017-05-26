package se.gu.dit524.group5.bluetoothremote.Dijkstra;

import se.gu.dit524.group5.bluetoothremote.Voronoi.Edge;
import se.gu.dit524.group5.bluetoothremote.Voronoi.Node;

/**
 * Created by julian.bock on 22.02.17. */
public class PriorityConnection implements Comparable<PriorityConnection> {

    // Instance variables to represent a comparable TramConnection variant with an
    // optional time parameter - e.g. to add the weight of a previous path to this one.
    public final int additionalWeight;
    public final Node from, to;
    public final Edge edge;

    public PriorityConnection(Node from, Edge edge, int additionalWeight) {
        this.edge =             edge;
        this.from =             from;
        this.to =               edge.destination(from);

        // Holds whatever distance it took to reach this edge:
        this.additionalWeight = additionalWeight;
    }

    public int weight() {
        return this.edge.distance() +this.additionalWeight;
    }

    // Compare the total "weight" of this edge (the time it takes to use this path)
    // with the weight of another edge - return their difference.
    @Override
    public int compareTo(PriorityConnection o) {
        return (this.edge.distance() +this.additionalWeight)
                -(o.edge.distance() +o.additionalWeight);
    }
}
