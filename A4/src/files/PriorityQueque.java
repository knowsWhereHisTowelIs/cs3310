/**
 * CS3310 - A4
 * Author: Caleb Slater
 */
package files;

import java.util.ArrayList;

/**
 * A max heap
 */
public class PriorityQueque {
    private ArrayList<HeapEntity> maxHeap = new ArrayList();
    private int N = 0;
    
    public PriorityQueque() {
        
    }
    
    /**
     * add entity
     * @param name
     * @param population 
     */
    public void add(String name, int population) {
        heapInstert(name, population);
    }
    
    /**
     * Add to heap
     * @param name
     * @param population 
     */
    private void heapInstert(String name, int population) {
        HeapEntity heapEntity = new HeapEntity(name, population);
        maxHeap.add(N, heapEntity);
        walkUp(N);
        N++;
    }
    
    /**
     * Always delete root ptr
     * return object
     * @return 
     */
    public HeapEntity delete() {
        HeapEntity max = null;
        if( N > 0 ) {
            max = maxHeap.get(0);
            N--;
            heapDelete();
        }

        return max;
    }
    
    /**
     * delete element from heap
     */
    private void heapDelete() {
        maxHeap.set(0, maxHeap.get(N));
        maxHeap.remove(N);
        walkDown(0);
    }
    
    /**
     * walk up heap and move max values up
     * @param currentIndex 
     */
    private void walkUp(int currentIndex) {
        int parentIndex = ( currentIndex - 1) / 2;
        
        while ( currentIndex > 0 ) {
            HeapEntity current = maxHeap.get(currentIndex);
            HeapEntity parent = maxHeap.get(parentIndex);
            if( current.getPopulation() > parent.getPopulation() ) {
                heapSwap(currentIndex, parentIndex);
                currentIndex = parentIndex;
                parentIndex = ( currentIndex - 1) / 2;
            } else {
                break;
            }
        }
    }
    
    /**
     * walk down heap and move max values up
     * @param currentIndex 
     */
    private void walkDown(int currentIndex) {
        int leftIndex = (2*currentIndex) + 1;
        int rightIndex = (2*currentIndex) + 2;
        int childIndex;
                
        while ( leftIndex < (N - 1) ) {
            HeapEntity current = maxHeap.get(currentIndex);
            leftIndex = (2*currentIndex) + 1;
            rightIndex = (2*currentIndex) + 2;
            HeapEntity left = leftIndex < (N-1) ? maxHeap.get(leftIndex) : null;
            HeapEntity right = leftIndex < (N-1) ? maxHeap.get(rightIndex) : null;
            
            int childVal;
            int leftVal = left == null ? -1 : left.getPopulation();
            int rightVal = right == null ? -1 : right.getPopulation();
            
            if ( rightIndex >= (N - 1) || leftVal > rightVal ) {
                //right child out of bounds
                childVal = leftVal;
                childIndex = leftIndex;
            } else {
                childVal = rightVal;
                childIndex = rightIndex;
            }
            if( childIndex < (N-1) && current.getPopulation() < childVal ) {
                //prepare for next iteration
                heapSwap(childIndex, currentIndex);
                currentIndex = childIndex;
            } else {
                break;
            }
        }
    }
    
    /**
     * swap values in array
     * @param a
     * @param b 
     */
    private void heapSwap(int a, int b) {
        HeapEntity tmpA = maxHeap.get(a);
        HeapEntity tmpB = maxHeap.get(b);
        maxHeap.set(a, tmpB);
        maxHeap.set(b, tmpA);
    }
    
    public HeapEntity getHeapEntity(int position) {
        HeapEntity tmp = null;
        if( position < N ) {
            tmp = maxHeap.get(position);
        }
        return tmp;
    }

    public ArrayList<HeapEntity> getMaxHeap() {
        return maxHeap;
    }

    public void setMaxHeap(ArrayList<HeapEntity> maxHeap) {
        this.maxHeap = maxHeap;
    }

    public int getN() {
        return N;
    }

    public void setN(int N) {
        this.N = N;
    }
}
