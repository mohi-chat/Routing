package utils;

import java.io.Serializable;

/** Node to store adjacency info in the graph.*/
@SuppressWarnings("serial")
public class Node implements Serializable{
	public int vertex;
	public int weight;
	public Node(int vertex, int weight) {
		super();
		this.vertex = vertex;
		this.weight = weight;
	}
}
