package utils;

import java.util.Arrays;

/** Implements a Max Heap structure to store fringes. */
public class FringeHeap {
	private int numVertex;
	public int end;
	public int[] H;
	public int[] D;
	public int[] pos;
	public FringeHeap(int numVertex) {
		super();
		this.numVertex = numVertex;
		// Maintaining 1-indexed array for simplicity.
		this.H = new int[numVertex+1];
		this.pos = new int[numVertex+1];
		this.H[0] = -1;
		this.pos[0] = -1;
		this.end = 1;
		for(int i = 1; i<=numVertex; i++) H[i] = i-1;
		// Initialize the Distance array with INF for all vertices
		this.D = new int[numVertex];
		Arrays.fill(this.D, Integer.MAX_VALUE);
	}
	public int getNumVertex() {
		return numVertex;
	}
	private int parent(int index) { return index/2; }
	private int leftChild(int index) { return 2*index; }
	private int rightChild(int index) { return 2*index + 1; }
	private boolean isLeaf(int index) {
		if(index >= end/2 && index <= end) return true;
		else return false;
	}
	public boolean thereAreFringes() {
		return end>1;
	}
	public void fixHeap(int index) {
		while( !isLeaf(index) && D[H[index]]< Math.max(D[H[leftChild(index)]], D[H[rightChild(index)]])){
			 int max_child_index = (D[H[leftChild(index)]] > D[H[rightChild(index)]])?leftChild(index):rightChild(index);
			 int temp = H[index];
			 pos[H[index]] = max_child_index;
			 H[index] = H[max_child_index];
			 pos[H[max_child_index]] = index;
			 H[max_child_index] = temp;
			 index = max_child_index;
		}
		while( index>=2 && D[H[index]] > D[H[parent(index)]]) {
			 int temp = H[index];
			 pos[H[index]] = parent(index);
			 H[index] = H[parent(index)];
			 pos[H[parent(index)]] = index;
			 H[parent(index)] = temp;
			 index = parent(index);
		}
	}
	public int MAXIMUM() {
		int max_val = H[1];
		DELETE(1);
		return max_val;
	}
	public void DELETE(int index) {
		H[index] = H[end-1];
		pos[H[end-1]] = index;
		end--;
		fixHeap(index);
	}
	public void INSERT(int vertex) {
		H[end] = vertex;
		int index = end;
		pos[vertex] = index;
		end++;
		fixHeap(index);
	}
}