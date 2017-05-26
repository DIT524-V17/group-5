package se.gu.dit524.group5.bluetoothremote.Dijkstra;

/**
 * Models sorted (a.k.a. ordered) lists. 
 * Any number of elements can be added to the list (only limited by the memory of the machine), and when added 
 * they are automatically placed in order (the element class must implements the Comparable interface).
 *
 * There are also some methods for querying the list, particularly there is a method that counts how many values are 
 * in an interval (the interval are specified by two values). 
 *
 * @param <E> The type of elements stored in the list. E needs to be a subclass of Comparable, so the elements can be ordered
 */
public interface SortedList<E extends Comparable<E>> {
// Hint: See TestList.java for some examples of usage and expected results (use it to test your code).
// Hint: Comparable: https://docs.oracle.com/javase/8/docs/api/java/lang/Comparable.html
// Hint: You need to have a type parameter <E extends Comparable<E>> in your implementing class, and implement SortedList<E>.
// Hint: To create an array of type E[], use E[] arr = (E[]) new Comparable[arraysize];

    /**
     * Add an element to the sorted list. (Making sure that the list remains sorted)
     * Complexity at most O(N)
     **/
    public void add(E elem);
    // Hint: for array-based solutions, you need to replace the array if it is full
    // Challenge 2E: can you have a log N add, if you are also allowed to have a log N get?
    
    /**
     * Add all elements of a given array to the list
     * Complexity at most O(N+M), where N is size() and M is arr.length
     * @param arr A sorted array of elements
     **/
    public void addSortedArray(E[] arr);
    // Hint: Look for 'merge algorithms'
    
    /**
     * Complexity: At most O(log N), preferably O(1)
     * @param ix An index in the list
     * @return The element at index ix of the list for 0 <= ix < size(),
     *         null if the index is out of bounds
     */
    public E get(int ix);
    
    /**
     * If elem is in the list, return the first (smallest) index it occupies (there may be multiple copies)
     * If elem is not in the list, return the index of the first element larger than elem
     * If there are no elements larger than elem, return size()
     * @param elem An element
     * @return The smallest index i, such that get(i) >= elem, or size() if there is no such i
     */
    public int firstIndex(E elem);
    // Hint: look in the TestList.java file to see an example of how this is expected to work
    
    /**
     * If elem is in the list, return the last (largest) index it occupies (there may be multiple copies)
     * If elem is not in the list, return the index of the last element smaller than elem
     * If there are no elements larger than elem, return -1
     * @param elem An element
     * @return The largest index i, such that get(i) <= elem, or -1 if there is no such i
     */
    public int lastIndex(E elem);
    
    /**
     * Complexity: At most O(log N), where N is size()
     * @param elem The element to search for
     * @return true iff elem is in the list
     */
    public boolean contains(E elem);
    //Hint: Use the other methods to implement this
    
    /**
     * Complexity: At most O(log N), where N is size()
     * @return The number of elements x such that lo <= x <= hi
     **/
    public int countBetween(E lo, E hi);
    //Hint: Use the other methods to implement this
    
    /**
     * Complexity: O(1)
     * @return The number of elements
     */
    public int size();

    // Challenge 2D:
    public void intersection(SortedList<E> list);
}