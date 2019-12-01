package utils;

import java.util.Set;

import utils.Edge;

public class EdgeHeap {
	public int end;
	public Edge[] E;
	public EdgeHeap(Set<Edge> E) {
		super();
		this.E = new Edge[E.size()];
		int e = 0;
		for(Edge edge : E) {
			this.E[e] = edge;
			e++;
		}
	}
	private int leftChild(int index) { return 2*index; }
	private int rightChild(int index) { return 2*index + 1; }
	public Edge[] sort() {
		buildHeap();
		Edge temp;
		while(end>=1) {
			temp = E[1];
			E[1] = E[end];
			E[end] = temp;
			end--;
			fixHeap(1);
		}
		return E;
	}
	public void buildHeap() {
		end = E.length-1;
		for(int i = (E.length/2) ; i>=1; i--) fixHeap(i);
	}
	public void fixHeap(int index) {
		while(leftChild(index) <= end && rightChild(index) <= end &&
				E[index].weight > Math.min(E[leftChild(index)].weight, E[rightChild(index)].weight)) {
			int min_child_index = (E[leftChild(index)].weight < E[rightChild(index)].weight)? 
		 			   leftChild(index):rightChild(index);
			Edge temp = E[index];
			E[index] = E[min_child_index];
			E[min_child_index] = temp;
			index = min_child_index;
		}
	}
}