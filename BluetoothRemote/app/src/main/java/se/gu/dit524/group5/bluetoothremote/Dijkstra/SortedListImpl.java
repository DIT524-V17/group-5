package se.gu.dit524.group5.bluetoothremote.Dijkstra;

/**
 * Group5 / Assignment2.Task2
 * Created by julian.bock on 06.02.17.
 * -----------------------------------
 * Challenge 2A: 79 lines of code - including 8 '@Override' keywords, not counting empty lines or comments.
 * Challenge 2D: Solved with a complexity of O(N * log M) - see below.
 * */
public class SortedListImpl<E extends Comparable<E>> implements SortedList<E> {
    // Constants used as return values from within compareElemAt():
    private static final int LARGER   = -2;
    private static final int SMALLER  = -3;

    // Instance variable, holding the list itself:
    private E[] arr;

    // Adds a given element 'elem' to the current array of elements, retaining its
    // characteristics. Complexity: O(N).
    @Override
    public void add(E elem) {
        E[] tmp = (E[]) new Comparable[this.size() +1];
        int insertAt = this.firstIndex(elem);
        for (int i = 0; i < tmp.length; i++) {
            if (i == insertAt) tmp[i] = elem;
            else tmp[i] = i < insertAt ? this.get(i) : this.get(i -1);
        }
        this.arr = tmp;
    }

    // Adds all elements of a given array 'arr' at their appropriate positions to the
    // current array of elements, thus retaining its characteristics. Complexity: O(N + M),
    // where N is equal to this.size() and M corresponds to the length of the supplied array of elements.
    @Override
    public void addSortedArray(E[] arr) {
        E[] tmp = (E[]) new Comparable[this.size() +arr.length];
        int thisPos = 0, arrPos = 0, tmpPos = 0;

        while (thisPos < this.size() && arrPos < arr.length)
            tmp[tmpPos++] = this.get(thisPos).compareTo(arr[arrPos]) <= 0 ? this.get(thisPos++) : arr[arrPos++];

        while (thisPos < this.size()) tmp[tmpPos++] = this.get(thisPos++);
        while (arrPos < arr.length) tmp[tmpPos++] = arr[arrPos++];
        this.arr = tmp;
    }

    // Returns the element at index 'ix' within an instance of SortedListImpl.
    // Complexity: O(1).
    @Override
    public E get(int ix) {
        return (0 <= ix && ix < this.size()) ? this.arr[ix] : null;
    }

    // Uses logarithmic search to find the first occurrence of a given element 'elem'
    // within this.arr and returns either its index, or its potential position.
    // Complexity: O(log N) - inherited from findLog().
    @Override
    public int firstIndex(E elem) {
        return findLog(elem, true);
    }

    // Uses logarithmic search to find the last occurrence of a given element 'elem'
    // within this.arr and returns either its index, or its potential position.
    // Complexity: O(log N) - inherited from findLog().
    @Override
    public int lastIndex(E elem) {
        return findLog(elem, false);
    }

    // Returns a boolean indicating the existence of a given element 'elem' within
    // this.arr. Complexity: O(log N) - inherited from findLog().
    @Override
    public boolean contains(E elem) {
        int pos = this.findLog(elem, true);
        return elem.equals(this.get(pos -1)) || elem.equals(this.get(pos));
    }

    // Returns the count of elements between the first occurrence of 'lo' and the
    // last occurrence of 'hi' - respectively their possible positions in this.arr.
    // Complexity: O(log N) - inherited from firstIndex() and lastIndex().
    @Override
    public int countBetween(E lo, E hi) {
        int h = this.lastIndex(hi), l = this.firstIndex(lo);
        return (h == -1 || l == this.size()) ? 0 : h -l +1;
    }

    // Returns the size/ length of a list represented as instance of SortedListImpl.
    // Complexity: O(1).
    @Override
    public int size() {
        if (this.arr == null) return 0;
        else return this.arr.length;
    }

    // A small method to handle the logarithmic search, its indices and possible return
    // values. Complexity: At most O(log N).
    private int findLog(E elem, boolean firstIndex) {
        int min = 0, max = this.size() -1, result, mid;
        while (max >= min) {
            switch (result = compareElemAt(mid = (min +max) /2, elem, firstIndex)) {
                case LARGER: max = mid -1; break;
                case SMALLER: min = mid +1; break;
                default: return result;
            }
        }
        return firstIndex ? this.size() : -1;
    }

    // Compares a given element 'elem' with elements at and around the index 'index'.
    // Depending on 'firstIndex', used as indicator whether to find the first or last
    // occurrence of the given element within this.arr, this method returns either the
    // actual index of an element (or position in the array) matching the criteria, or
    // one of the constants LARGER/SMALLER, telling the search algorithm to continue
    // looking for a larger, respectively smaller element within this.arr.
    // Complexity: O(1).
    private int compareElemAt(int index, E elem, boolean firstIndex) {
        E preElem = this.get(index -1), postElem = this.get(index +1);

        int currToElem = this.get(index).compareTo(elem);
        int preToElem = (preElem == null ? 0 : preElem.compareTo(elem));
        int postToElem = (postElem == null ? 0 : postElem.compareTo(elem));

        if (firstIndex) {
            if (currToElem >= 0 && (preElem == null || preToElem < 0)) return index;
            else if (currToElem < 0 && postElem != null && postToElem > 0) return index +1;
        } else {
            if (currToElem <= 0 && (postElem == null || postToElem > 0)) return index;
            else if (currToElem > 0 && preElem != null && preToElem < 0) return index -1;
        }
        if (currToElem == 0) return firstIndex ? LARGER : SMALLER;
        else return currToElem > 0 ? LARGER : SMALLER;
    }

    // Challenge 2D:
    // Removes all elements from this.arr which can not be found within the given 'list'.
    // Complexity: O(N * log M), where N is equal to this.size() and M is the size of
    // the supplied 'list'.
    @Override
    public void intersection(SortedList<E> list) {
        int validItemCount = 0;
        int[] validItemIds = new int[this.size()];
        for (int i = 0; i < this.size(); i++)
            if (list.contains(this.get(i))) validItemIds[validItemCount++] = i;

        E[] tmp = (E[]) new Comparable[validItemCount];
        for (int i = 0; i < validItemCount; i++) tmp[i] = this.get(validItemIds[i]);
        this.arr = tmp;
    }
}