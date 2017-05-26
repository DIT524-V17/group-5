package se.gu.dit524.group5.bluetoothremote.Dijkstra;

/**
 * Created by julian.bock on 23.02.17. */
public class PriorityHeap<E extends Comparable<E>> extends Heap<E> {

    // Instance variable to keep track of paths that have already been stored within this heap.
    // Assuming that all nodes can be identified using a unique ID (ranging from 0 to the number
    // of nodes -1), this array holds the index of available paths (which are stored in this.arr)
    // per node +1. E.g.: pathToNode[node.id] contains '0' in case no path to 'node' is stored
    // within this heap, or 'i+1' representing its index increased by one.
    private int[] pathToNode;

    // Since this specialized Heap won't hold more than one path to any node of the network, its
    // size can be initialized (and limited) to the number of nodes within a given network.
    @SuppressWarnings("unchecked")
    public PriorityHeap(int nwSize) {
        super(nwSize);
        this.pathToNode = new int[nwSize];
    }

    @Override
    public E removeMin() {
        if (this.size == 0) return null;
        E min = arr[0];
        if (min instanceof PriorityConnection) {
            pathToNode[((PriorityConnection) min).to.id()] = 0;
            if (this.size() > 1) {
                arr[0] = arr[--size];
                arr[size] = null;
                pathToNode[((PriorityConnection) arr[0]).to.id()] = 1;
                downHeap(0);
            }
            else arr[--size] = null;
            return min;
        }
        else return super.removeMin();
    }

    @Override
    public void add(E elem) {
        if (elem instanceof PriorityConnection) {
            PriorityConnection e = (PriorityConnection) elem;
            if (pathToNode[e.to.id()] > 0 && elem.compareTo(arr[pathToNode[e.to.id()] -1]) < 0) {
                arr[pathToNode[e.to.id()] -1] = elem;
                upHeap(pathToNode[e.to.id()] -1);
            }
            else if (pathToNode[e.to.id()] == 0) {
                arr[size] = elem;
                pathToNode[e.to.id()] = size +1;
                upHeap(size++);
            }
        }
        else super.add(elem);
    }

    @Override
    protected void upHeap(int i) {
        if (this.size <= 1) return;
        if (arr[i] instanceof PriorityConnection) {
            int previous = i, current;
            while ((current = (previous - 1) / 2) >= 0) {
                if (arr[previous].compareTo(arr[current]) < 0) {
                    E tmp = arr[previous];
                    arr[previous] = arr[current];
                    pathToNode[((PriorityConnection) arr[previous]).to.id()] = previous +1;
                    arr[current] = tmp;
                    pathToNode[((PriorityConnection) arr[current]).to.id()] = current +1;
                    previous = current;
                } else break;
            }
        }
        else super.upHeap(i);
    }

    @Override
    protected void downHeap(int i) {
        if (this.size <= 1) return;
        if (arr[i] instanceof PriorityConnection) {
            int previous = i, current;
            while ((current = previous * 2) <= size - 2) {
                int min;
                if (arr[current +1] == null) break;
                else if (arr[current +2] == null) min = current +1;
                else min = arr[current + 1].compareTo(arr[current + 2]) <= 0 ? current + 1 : current + 2;
                if (arr[previous].compareTo(arr[min]) > 0) {
                    E tmp = arr[previous];
                    arr[previous] = arr[min];
                    pathToNode[((PriorityConnection) arr[previous]).to.id()] = previous +1;
                    arr[min] = tmp;
                    pathToNode[((PriorityConnection) arr[min]).to.id()] = min +1;
                    previous = min;
                } else break;
            }
        }
        else super.downHeap(i);
    }
}
