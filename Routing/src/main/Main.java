package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import utils.Graph;

public class Main {
	public static void main(String[] argv) {
		
		int numPairsGraph = 5, numTestingPairs = 5;
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		println("1. Generate and save graph(for reference) - Program runs in 15 minutes approx.");
		println("2. Generate only - Progam runs in 7 minutes approx.");
		print("Enter your choice (1/2): ");
		int choice = input.nextInt();
		if(choice == 1) {
			generateGraphPairs(numPairsGraph);
			executeTesting(numPairsGraph, numTestingPairs, true);
		}else if(choice == 2){
			executeTesting(numPairsGraph, numTestingPairs, false);
		}
		
	}
	/** Generates specified pair of graphs using
	 *  average degree and percent neighbors. */
	private static void generateGraphPairs(int numPairsGraph) {
		println("GENERATING GRAPHS: ");
		String curr_dir_path = System.getProperty("user.dir")+"\\Graphs\\";
		File directory = new File(curr_dir_path);
		if (! directory.exists()){
	        directory.mkdir();
	    }
		for(int i = 1; i<=numPairsGraph; i++) {
			if(doesExists(curr_dir_path+"p"+i+"_graph1.ser")) {
				println("Pair "+i+" G1 already present. Nothing to do.\n");
			}
			else {
				print("Pair "+i+" G1 not found. Generating... ");
				/* Initialize G1. */
				int numVertices_G1 = 5000;
				int avgVertexDegree_G1 = 6;
				int maxWeightAllowed_G1 = 6000;
				Graph G1 = new Graph(numVertices_G1, maxWeightAllowed_G1);
				G1.generateWithAvgDegree(avgVertexDegree_G1);
				println("Done.");
				print("Storing graph in local memory... ");
				storeGraph(G1, curr_dir_path+"p"+i+"_graph1.ser");
				println("Done.");
				try {
					print("Printing graph in a text file... ");
					G1.printGraphToFile(curr_dir_path+"p"+i+"_graph1.txt");
					println("Done.\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if(doesExists(curr_dir_path+"p"+i+"_graph2.ser")) {
				println("Pair "+i+" G2 already present. Nothing to do.\n");
			}
			else {
				print("Pair "+i+" G2 not found. Generating... ");
				/* Initialize G2. */
				int numVertices_G2 = 5000;
				int percentNeighbors = 20;
				int maxWeightAllowed_G2 = 6000;
				Graph G2 = new Graph(numVertices_G2, maxWeightAllowed_G2);
				G2.generateWithAdjNeighbors(percentNeighbors);
				println("Done.");
				print("Storing graph in local memory... ");
				storeGraph(G2, curr_dir_path+"p"+i+"_graph2.ser");
				println("Done.");
				try {
					print("Printing graph in a text file... ");
					G2.printGraphToFile(curr_dir_path+"p"+i+"_graph2.txt");
					println("Done.\n");
				} catch (IOException e) {
					System.out.println("IOException Occured!");
					e.printStackTrace();
				}
			}
		}
	}
	
	/** Executes testing of MBP algorithms for each pair of graphs
	 *  on randomly chosen source and target vertices. */
	private static void executeTesting(int numPairsGraph, int numTestingPairs, boolean graphsSaved) {
		println("TESTING: ");
		String curr_dir_path = System.getProperty("user.dir")+"\\Graphs\\";
		Random rgen = new Random();
		int numVertices = 5000, maxWeightAllowed = 6000;
		int avgVertexDegree = 6, percentNeighbors = 20;
		float dijkstraWithoutHeapAvgTime = 0, dijkstraWithHeapAvgTime = 0, kruskalAvgTime = 0;
		float dWoHavgTime_g1 = 0, dWHavgTime_g1 = 0, kAvgTime_g1 = 0;
		float dWoHavgTime_g2 = 0, dWHavgTime_g2 = 0, kAvgTime_g2 = 0;
		for(int p = 1; p<=numPairsGraph; p++) {
			for(int i = 1; i<=2; i++) {
				dijkstraWithoutHeapAvgTime = 0;
				dijkstraWithHeapAvgTime = 0;
				kruskalAvgTime = 0;
				Graph G = null;
				if(graphsSaved) {
					print("Loading p"+p+"_graph"+i+"... ");
					G = loadGraph(curr_dir_path+"p"+p+"_graph"+i+".ser");
					println("Done.");
				} else {
					print("Generating p"+p+"_graph"+i+"... ");
					if(i==1) {
						G = new Graph(numVertices, maxWeightAllowed);
						G.generateWithAvgDegree(avgVertexDegree);
					}
					if(i==2) {
						G = new Graph(numVertices, maxWeightAllowed);
						G.generateWithAdjNeighbors(percentNeighbors);;
					}
					println("Done.");
				}
				int s,t;
				Object [] ans;
				for(int k = 0; k<numTestingPairs; k++) {
					/* Randomly generate source and target vertices. */
					s = rgen.nextInt(G.getNumVertices());
					t = rgen.nextInt(G.getNumVertices());
					println("Finding Maximum bandwidth path between source = "+s+" and target = "+t+".\n");
					/* MBP without heap. */
					ans = Routing.MAX_BANDWIDTH_PATH_NO_HEAP(G, s, t);
					printPathAndBandwidth(ans, s, t);
					dijkstraWithoutHeapAvgTime += (float)ans[2]/numTestingPairs;
					/* MBP with heap. */
					ans = Routing.MAX_BANDWIDTH_PATH_WITH_HEAP(G, s, t);
					printPathAndBandwidth(ans, s, t);
					dijkstraWithHeapAvgTime += (float)ans[2]/numTestingPairs;
					/* MBP using Kruskal's */
					G.convertToEdgeSet();
					ans = Routing.MAX_BANDWIDTH_PATH_KRUSKAL(G, s, t);
					printPathAndBandwidth(ans, s, t);
					kruskalAvgTime += (float)ans[2]/numTestingPairs;
					G.convertToAdjList();
				}
				println("EXECUTION SUMMARY: ");
				println("Average time taken by Dijkstra with no heap: "+ dijkstraWithoutHeapAvgTime);
				println("Average time taken by Dijkstra with heap: "+ dijkstraWithHeapAvgTime);
				println("Average time taken by Kruskal's Algorithm: "+ kruskalAvgTime);
				println("=================================================================================");
				println("\n");
				if(i==1) {
					dWoHavgTime_g1 += dijkstraWithoutHeapAvgTime/numPairsGraph;
					dWHavgTime_g1 += dijkstraWithHeapAvgTime/numPairsGraph;
					kAvgTime_g1 += kruskalAvgTime/numPairsGraph;
				}
				else {
					dWoHavgTime_g2 += dijkstraWithoutHeapAvgTime/numPairsGraph;
					dWHavgTime_g2 += dijkstraWithHeapAvgTime/numPairsGraph;
					kAvgTime_g2 += kruskalAvgTime/numPairsGraph;
				}
			} 
		}
		println("OVERALL SUMMARY: ");
		println("=================================================================================");
		println("Average time taken by Dijkstra with no heap for sparse graph: "+ dWoHavgTime_g1);
		println("Average time taken by Dijkstra with no heap for dense graph: "+ dWoHavgTime_g2+"\n");
		println("Average time taken by Dijkstra with heap for sparse graph: "+ dWHavgTime_g1);
		println("Average time taken by Dijkstra with heap for dense graph: "+ dWHavgTime_g2+"\n");
		println("Average time taken by Kruskal's Algorithm for sparse graph: "+ kAvgTime_g1);
		println("Average time taken by Kruskal's Algorithm for dense graph: "+ kAvgTime_g2);
		println("=================================================================================");
	}
	private static void print(String s) { System.out.print(s); }
	private static void println(String s) { System.out.println(s); }
	
	/** Helper method to print the path from source to vertex as contained
	 *  in dad[] array returned by MBP algorithms. */
	private static void printPathAndBandwidth(Object[] ans, int s, int t){
		int[] dad = (int[])ans[0];
		int[] wt = (int[])ans[1];
		Stack<Integer> path = new Stack<>();
		int v = dad[t];
		while(v!=s) {
			path.push(v);
			v = dad[v];
		}
		println("\tNo. of Hops: "+Integer.toString(path.size()+1));
		print("\tPath: "+s+" -> ");
		while(!path.isEmpty()) print(path.pop() + " -> ");
		println(Integer.toString(t));
		println("\tMaximum Bandwidth: "+ wt[t]+"\n");
	}
	
	/** Helper method to store graph object into local memory. */
	private static void storeGraph(Graph G, String fileName) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(G);
			oos.close();
		} catch (IOException e) {
		}
	}
	/** Helper method to load graph object into local memory. */
	private static Graph loadGraph(String fileName) {
		Graph G = null;
		try {
			FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			G = (Graph) ois.readObject();
			ois.close();

		} catch (FileNotFoundException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); } 
		catch (ClassNotFoundException e) { e.printStackTrace(); }
		return G;
	}
	/** Checks if a file exists by the given name. */
	private static boolean doesExists(String fileName) {
        File f = new File(fileName); 
        return f.exists();
	}
}