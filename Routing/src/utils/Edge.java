package utils;

import java.io.Serializable;

/** Stores an edge of the graph. */
@SuppressWarnings("serial")
public class Edge implements Serializable{
	public int u;
	public int v;
	public int weight;
	public Edge(int u, int v, int weight) {
		super();
		this.u = u;
		this.v = v;
		this.weight = weight;
	}
}
