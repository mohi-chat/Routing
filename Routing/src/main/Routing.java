package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import utils.EdgeHeap;
import utils.FringeHeap;
import utils.Graph;
import utils.Edge;
import utils.Node;

/** This class implements Routing Algorithms to
 *  solve Maximum Bandwidth Path.*/
public class Routing {
	/** Finds Maximum Bandwidth Path from source s, to target t.
	 *  Uses modified Dijkstra's Algorithm. */
	public static Object[] MAX_BANDWIDTH_PATH_NO_HEAP(Graph G, int s, int t) {
		/* Start Time */
		long start = System.currentTimeMillis();
		/* Initialization step */
		int[] wt = new int[G.getNumVertices()];
		int[] dad = new int[G.getNumVertices()];
		Arrays.fill(wt, Integer.MAX_VALUE);
		String[] status = new String[G.getNumVertices()];
		List<List<Node>> adjList = G.getAdjacencyList();
		/* STEP 1: */
		for(int v = 0; v<G.getNumVertices(); v++) status[v] = "unseen";
		/* STEP 2: */
		status[s] = "in-tree";
		/* STEP 3: */
		for(Node w : adjList.get(s)) {
			if("unseen".equals(status[w.vertex])) {
				status[w.vertex] = "fringe";
				dad[w.vertex] = s;
				wt[w.vertex] = w.weight;	// wt[w] = weight(s,w)
			}
			else if("fringe".equals(status[w.vertex]) && wt[w.vertex] <  w.weight){
				wt[w.vertex] =  w.weight;
			}
		}
		/* STEP 4: */
		while(thereAreFringes(status)) {
			int v = getBestFringe(wt, status);
			status[v] = "in-tree";
			for(Node w : adjList.get(v)) {
				if("unseen".equals(status[w.vertex])) {
					status[w.vertex] = "fringe";
					dad[w.vertex] = v;
					wt[w.vertex] = Math.min(wt[v], w.weight);
				}
				else if("fringe".equals(status[w.vertex]) && wt[w.vertex] < Math.min(wt[v], w.weight)) {
					dad[w.vertex] = v;
					wt[w.vertex] = Math.min(wt[v], w.weight);
				}
			}
		}
		/* End Time */
		long end = System.currentTimeMillis();
		System.out.println("\tFound Maximum bandwidth path WITHOUT heap in "
							+ ((end-start)/1000F) + " seconds");
		/* STEP 5: */
		return new Object[]{dad, wt, new Float(((end-start)/1000F))};
	}

	/** Finds Maximum Bandwidth Pass from source s, to target t.
	 *  Uses modified Dijkstra's Algorithm and Heap Structure. */
	public static Object[] MAX_BANDWIDTH_PATH_WITH_HEAP(Graph G, int s, int t) {
		/* Start Time */
		long start = System.currentTimeMillis();
		/* Initialization step:
		   A frige heap Stores vertex in Heap and also weight array D.
		   Here D[] denotes the wt[] array. */
		FringeHeap heap = new FringeHeap(G.getNumVertices());
		int[] dad = new int[G.getNumVertices()];
		String[] status = new String[G.getNumVertices()];
		List<List<Node>> adjList = G.getAdjacencyList();
		/* STEP 1: */
		for(int v = 0; v<G.getNumVertices(); v++) status[v] = "unseen";
		/* STEP 2: */
		status[s] = "in-tree";
		/* STEP 3: */
		for(Node w : adjList.get(s)) {
			if("unseen".equals(status[w.vertex])) {
				status[w.vertex] = "fringe";
				dad[w.vertex] = s;
				heap.D[w.vertex] = w.weight;	// wt[w] = weight(s,w)
				heap.INSERT(w.vertex);
			}
			else if("fringe".equals(status[w.vertex]) && heap.D[w.vertex] <  w.weight){
				heap.D[w.vertex] =  w.weight;
				heap.fixHeap(heap.pos[w.vertex]);
			}
		}
		/* STEP 4: */
		while(heap.thereAreFringes()) {
			// Extract Max valued vertex from Heap.
			int v = heap.MAXIMUM();
			status[v] = "in-tree";
			for(Node w : adjList.get(v)) {
				if("unseen".equals(status[w.vertex])) {
					status[w.vertex] = "fringe";
					dad[w.vertex] = v;
					heap.D[w.vertex] = Math.min(heap.D[v], w.weight);
					heap.INSERT(w.vertex);
				}
				else if("fringe".equals(status[w.vertex]) && 
						heap.D[w.vertex] < Math.min(heap.D[v], w.weight)) {
					dad[w.vertex] = v;
					heap.D[w.vertex] = Math.min(heap.D[v], w.weight);
					heap.fixHeap(heap.pos[w.vertex]);
				}
			}
		}
		/* End Time */
		long end = System.currentTimeMillis();
		System.out.println("\tFound Maximum bandwidth path WITH heap in "
							+ ((end-start)/1000F) + " seconds");
		/* STEP 5: */
		return new Object[]{dad, heap.D, new Float(((end-start)/1000F))};
	}
	
	/** Finds Maximum Bandwidth Pass from source s, to target t.
	 *  Uses Kruskal's Algorithm. */
	public static Object[] MAX_BANDWIDTH_PATH_KRUSKAL(Graph G, int s, int t) {
		/* Initialize a Min heap for sorting edges in decreasing order. */
		EdgeHeap H = new EdgeHeap(G.getEdgeSet());
		/* Start Time */
		long start = System.currentTimeMillis();
		Edge[] sortedEdges = H.sort();
		/* Initialize an empty spanning tree. */
		List<List<Node>> T = new ArrayList<>();
		for(int i = 0;i<G.getNumVertices(); i++) {
			T.add(new LinkedList<Node>());
		}
		MAKESET_UNION_FIND op = new MAKESET_UNION_FIND(G.getNumVertices());
		for(int v = 0; v<G.getNumVertices(); v++) op.makeSet(v);
		int r1,r2;
		for(Edge e : sortedEdges) {
			/* Skip dummy value. This dummy value is used to maintain 1-indexed Edge array. */
			if(e.u == -1) continue;
			r1 = op.find(e.u);
			r2 = op.find(e.v);
			if( r1 != r2) {
				op.union(r1, r2);
				addEdgeToTree(T, e);
			}
		}
		/* Use DFS to find the path from s to t in the maximum
		 * spanning tree obtained from kruskal's. */
		Object[] result =  DEPTH_FIRST_SEARCH(T, s, t);
		/* End Time */
		long end = System.currentTimeMillis();
		System.out.println("\tFound Maximum bandwidth path using Kruskal's Algorithm in "
							+ ((end-start)/1000F) + " seconds");
		
		return new Object[]{result[0], result[1], new Float(((end-start)/1000F))};
	}
	
	/* Helper Methods for MBP without heap implementation. */
	/** Returns if there are any more fringes left to be explored.
	 *  Running time: O(n) */
	private static boolean thereAreFringes(String[] status) {
		for(String s : status) {
			if("fringe".equals(s)) return true;
		}
		return false;
	}
	/** Returns the next best fringe with largest weight.
	 *  Running time: O(n) */
	private static int getBestFringe(int[] wt, String[] status) {
		int bestFringe = 0, maxWeight = Integer.MIN_VALUE;
		for(int v = 0; v<status.length; v++) {
			if("fringe".equals(status[v]) && wt[v] > maxWeight) {
				bestFringe = v;
				maxWeight = wt[v];
			}
		}
		return bestFringe;
	}
	
	/* Helper methods for MBP using Kruskal's Algorithm. */
	/** Performs a Depth first search on maximum spanning tree, T and
	 *  returns the path and maximum weight of the path from s to t. */
	private static Object[] DEPTH_FIRST_SEARCH(List<List<Node>> T, int s, int t) {
		String[] color = new String[T.size()];
		int[] dad = new int[T.size()];
		int[] wt = new int[T.size()];
		Arrays.fill(wt, Integer.MAX_VALUE);
		for(int v = 0; v<T.size(); v++) color[v] = "white";
		DFS(T, s, t, color, dad, wt);
		return new Object[]{dad, wt};
	}
	
	/** Recursive method to explore nodes in Depth first manner. */
	private static void DFS(List<List<Node>> T, int s, int t, String[] color, int[] dad, int[] wt) {
		color[s] = "grey";
		for(Node w : T.get(s)) {
			if(color[w.vertex] == "white") {
				dad[w.vertex] = s;
				wt[w.vertex] = Math.min(wt[s], w.weight);
				if(w.vertex == t) return;
				DFS(T, w.vertex, t, color, dad, wt);
			}
		}
		color[s] = "black";
	}
	
	/** Adds an edge into a adjacency list of Tree, T. */
	private static void addEdgeToTree(List<List<Node>> T, Edge e) {
		// Add edge a -> b
		Node q = new Node(e.v,e.weight);
		T.get(e.u).add(q);
		// Add edge b -> a
		q = new Node(e.u,e.weight);
		T.get(e.v).add(q);
	}
	
	/** Implements Makeset-Union-Find operations for
	 * 	Kruskal's Algorithm. */
	private static class MAKESET_UNION_FIND{
		public int[] dad;
		public int[] rank;
		public MAKESET_UNION_FIND(int numVertices) {
			super();
			dad = new int[numVertices];
			rank = new int[numVertices];
		}
		public void makeSet(int vertex) {
			dad[vertex] = 0;
			rank[vertex] = 0;
		}
		public void union(int r1, int r2) {
			if(rank[r1] > rank[r2]) dad[r2] = r1;
			else if(rank[r1] < rank[r2]) dad[r1] = r2;
			else {
				dad[r1] = r2;
				rank[r2]++;
			}
		}
		public int find(int vertex) {
			int w = vertex;
			while(dad[w]!=0) w = dad[w];
			return w;
		}
	}
}