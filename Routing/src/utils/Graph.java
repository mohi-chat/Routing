package utils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Graph implements Serializable{
	private static final long serialVersionUID = 1L;
	private int numVertices;
	private int maxWeightAllowed;
	private List<List<Node>> adjacencyList;
	private Set<Edge> edgeSet;
	private Random rgen;
	
	public Graph(int numVertices, int maxWeightAllowed) {
		super();
		this.numVertices = numVertices;
		this.maxWeightAllowed = maxWeightAllowed;
		this.adjacencyList = new ArrayList<>();
		this.edgeSet = new LinkedHashSet<>();
		// Making edge set 1 indexed for simplicity
		edgeSet.add(new Edge(-1,-1,-1));
		this.rgen = new Random();
	}
	public int getNumVertices() {
		return numVertices;
	}
	public List<List<Node>> getAdjacencyList() {
		return adjacencyList;
	}
	public Set<Edge> getEdgeSet() {
		return edgeSet;
	}
	/** Generates a random graph with given number of vertices
	 * and average degree of vertex. It replaces the previous graph,
	 * if called multiple times.*/
	public void generateWithAvgDegree(int avgVertexDegree) {
		/* No of edges required to maintain given average vertex
		 * degree.*/
		int edgesRequired = (avgVertexDegree*numVertices)/2;
		initializeConnectedGraph(); // Adds #edges equal to numVertices 
		edgesRequired = edgesRequired - numVertices;
		/* Step 2: Randomly add rest of the edges. */
		int vertex_a, vertex_b, weight;
		Node v;
		while(edgesRequired > 0) {
			weight = getRandomWeight();
			vertex_a = getRandomVertex();
			vertex_b = getRandomVertex();
			final int dest = vertex_b;
			if(!adjacencyList.get(vertex_a).stream().anyMatch(o -> o.vertex == dest)) {
				// Edge a -> b
				v = new Node(vertex_a, weight);
				adjacencyList.get(vertex_b).add(v);
				// Edge b -> a
				v = new Node(vertex_b, weight);
				adjacencyList.get(vertex_a).add(v);
				edgesRequired--;
			}
		} 
	}
	
	/** Generates a random graph with percentage of neighbors each
	 *  vertex can have. It replaces the previous graph,
	 *  if called multiple times.*/
	public void generateWithAdjNeighbors(int percentNeighbors) {
		/* Range of edges required to maintain given percent of
		 * Neighbors.*/
		int percentOffset = 2;
		int minEdges = ((percentNeighbors-percentOffset)*numVertices)/100;
		int maxEdges = ((percentNeighbors+percentOffset)*numVertices)/100;
		int[] numEdges = new int[numVertices];
		long start, end;
		boolean allVertexSaturated = false;
		int randVertex, weight, neighbors, neighborsLeft;
		Node v;
		initializeConnectedGraph(); // Adds #edges equal to numVertices	
		for(int i = 0; i<numVertices; i++) {
			neighbors = getRandomNeighborsCount(minEdges, maxEdges);
			numEdges[i] = neighbors;
		}
		/* Step 3: Randomly add rest of the edges for each vertex. */
		for(int i = 0; i<numVertices; i++) {
			neighborsLeft = numEdges[i] - adjacencyList.get(i).size();
			start = System.currentTimeMillis();
			while(neighborsLeft>0) {
				weight = getRandomWeight();
				// Get a random vertex that is not yet saturated
				randVertex = getRandomVertex();
				while(!allVertexSaturated && adjacencyList.get(randVertex).size() >= numEdges[randVertex]) randVertex = getRandomVertex();
				final int dest = randVertex;
				if(!adjacencyList.get(i).stream().anyMatch(o -> o.vertex == dest)) {
					// Edge a -> b
					v = new Node(randVertex, weight);
					adjacencyList.get(i).add(v);
					// Edge b -> a
					v = new Node(i, weight);
					adjacencyList.get(randVertex).add(v);
					neighborsLeft--;
				}
				end = System.currentTimeMillis();
				if((end-start)/1000F > 3) {
					allVertexSaturated = true;
					break;
				}
			}
			if(allVertexSaturated) break;
		} 
	}
	
	/** Initializes a connected graph with all the vertices. */
	public void initializeConnectedGraph() {
		/* Start with clearing out previous graph instance. */
		adjacencyList.clear();
		
		/* STEP 1: Initialize graph with empty list for each 
		 * vertex. */
		for(int i = 0;i<numVertices; i++) {
			adjacencyList.add(new LinkedList<Node>());
		}
		/* STEP 2: To make sure graph is connected start with a
		 * random order of vertices and connect vertices in
		 * circle. For example if, numVertices = 10, then
		 * a random order could be [2 5 6 7 9 8 3 1 10 4]. Then
		 * we connect vertices 2->5->6->7->9->8->3->1->10->4->2. 
		 * Now at this point the graph is connected, we can add
		 * more random edges to meet vertex degree requirement.*/
		Integer[] temp = new Integer[numVertices] ;
		int weight;
		Node v;
		for(int i = 0; i<numVertices; i++) temp[i] = i;
		List<Integer> randomOrderedVertices = Arrays.asList(temp);
		Collections.shuffle(randomOrderedVertices);
		for(int i = 0; i<(numVertices-1); i++) {
			weight = getRandomWeight();
			// Add edge a -> b
			v = new Node(randomOrderedVertices.get(i+1),weight);
			adjacencyList.get(randomOrderedVertices.get(i)).add(v);
			// Add edge b -> a
			v = new Node(randomOrderedVertices.get(i),weight);
			adjacencyList.get(randomOrderedVertices.get(i+1)).add(v);
		}
		weight = getRandomWeight();
		v = new Node(randomOrderedVertices.get(0),weight);
		adjacencyList.get(randomOrderedVertices.get(numVertices-1)).add(v);
		v = new Node(randomOrderedVertices.get(numVertices-1),weight);
		adjacencyList.get(randomOrderedVertices.get(0)).add(v);
	}
	/** Populates edge set by traversing the adjacency list,
	 *  and clears the adjacency list( To signal ready for
	 *  garbage collection ).*/
	public void convertToEdgeSet() {
		for(int v = 0; v<numVertices; v++) {
			for(Node e : adjacencyList.get(v)) {
				if(e.vertex >= v) edgeSet.add(new Edge(v, e.vertex, e.weight));
			}
			adjacencyList.get(v).clear();
		}
	}
	/** Creates adjacency list by traversing the Edge list,
	 *  and clears the Edge list( To signal ready for
	 *  garbage collection ).*/
	public void convertToAdjList() {
		for(Edge e: edgeSet) {
			if(e.u == -1) continue;
			Node q = new Node(e.v,e.weight);
			adjacencyList.get(e.u).add(q);
			// Add edge b -> a
			q = new Node(e.u,e.weight);
			adjacencyList.get(e.v).add(q);
		}
		edgeSet.clear();
	}

	private int getRandomVertex() {
		return rgen.nextInt(numVertices);
	}
	private int getRandomWeight() {
		return rgen.nextInt(maxWeightAllowed)+1;
	}
	private int getRandomNeighborsCount(int min, int max) {
		return rgen.nextInt((max - min) + 1) + min;
	}
	public void printGraphToFile(String fileName) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		writer.write("V : List of <v,w>");
		writer.newLine();
		writer.write("=================");
		writer.newLine();
		for(int i = 0; i<numVertices; i++) {
			String line = Integer.toString(i) + " : ";
			List<Node> entry = adjacencyList.get(i);
			for(int j = 0; j<entry.size()-1; j++) {
				line = line + Integer.toString(((Node)entry.get(j)).vertex) + "|"
					   + Integer.toString(((Node)entry.get(j)).weight) + " -> ";
			}
			line = line + Integer.toString(((Node)entry.get(entry.size()-1)).vertex) + "|"
				   + Integer.toString(((Node)entry.get(entry.size()-1)).weight);
			writer.write(line);
			writer.newLine();
		}
	    writer.close();
	}
}