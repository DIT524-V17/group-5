package se.gu.dit524.group5.bluetoothremote.Dijkstra;

/**
 * Implemented by julian.bock on 22.02.17. */
public class Heap<E extends Comparable<E>>{
    protected E[] arr;
    protected int size = 0;
    
    /** Creates a heap with an initial capacity of 8 **/
    @SuppressWarnings("unchecked")
    public Heap() {
        arr = (E[]) new Comparable[8];
    }

    @SuppressWarnings("unchecked")
    public Heap(int expectedCapacity) {
        arr = (E[]) new Comparable[expectedCapacity];
    }
    
    /** Finds the minimal element in the heap, should be O(1). 
     * @return The minimal element
     * **/
    public E findMin() {
        return arr[0];
    }
    
    /** Finds and removes the minimal element from the heap, should be O(log N).
     * @return The minimal element
     * **/
    public E removeMin() {
        if (this.size == 0) return null;
        E min = arr[0];
        if (this.size() > 1) {
            arr[0] = arr[--size];
            downHeap(0);            // float down from the new root node
        }
        else arr[--size] = null;
        return min;
    }

    /** Adds an element to the heap, should be O(log N). 
     * @param elem The element to be added.
     * **/
    public void add(E elem) {
        if (size >= arr.length) {
            @SuppressWarnings("unchecked")
            E[] newArr = (E[]) new Comparable[size * 2];
            for (int i = 0; i < arr.length; i++) newArr[i] = arr[i];
            arr = newArr;
        }

        arr[size] = elem;
        upHeap(size++);         // float up from the newly added node
    }

    // Rearrange (fix) the heap by floating up - starting at index i.
    protected void upHeap(int i) {
        if (this.size <= 1) return;
        int previous = i, current;
        while ((current = (previous -1) /2) >= 0) {
            if (arr[previous].compareTo(arr[current]) < 0) {
                E tmp = arr[previous];
                arr[previous] = arr[current];
                arr[current] = tmp;
                previous = current;
            }
            else break;
        }
    }

    // Rearrange (fix) the heap by floating down - starting at index i.
    protected void downHeap(int i) {
        if (this.size <= 1) return;
        int previous = i, current;
        while ((current = previous *2) <= size -2) {
            int min;
            if (arr[current +1] == null) break;
            else if (arr[current +2] == null) min = current +1;
            else min = arr[current + 1].compareTo(arr[current + 2]) <= 0 ? current + 1 : current + 2;
            if (arr[previous].compareTo(arr[min]) > 0) {
                E tmp = arr[previous];
                arr[previous] = arr[min];
                arr[min] = tmp;
                previous = min;
            }
            else break;
        }
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size <= 0;
    }
}