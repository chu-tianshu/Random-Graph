import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class RandomGraph {
	public static Map<Long, Set<Long>> constructGraph(long n, double p) {
	    Map<Long, Set<Long>> graph = new HashMap<>();
	    
	    long currSum = 0;
	    long currKey = 1;
	    while (true) {
	        long currGeoNum = (long) (Math.log(Math.random()) / Math.log(1 - p)) + 1;
	        currSum += currGeoNum;
	        
	        // System.out.println("sum: " + currSum);
	        
	        if (currSum > n * (n - 1) / 2) break;

	        while (currSum > (2 * n - currKey - 1) * currKey / 2) currKey++;
	        long currVal = currKey > 1 ? currSum - (2 * n - (currKey - 1) - 1) * (currKey - 1) / 2 + currKey : 1 + currSum;
	        
	        // System.out.println("key: " + currKey);
	        // System.out.println("val: " + currVal);
	        // System.out.println();
	        
	        if (!graph.containsKey(currKey)) graph.put(currKey, new HashSet<>());
	        Set<Long> currKeyNeighbors = graph.get(currKey);
	        currKeyNeighbors.add(currVal);
	        graph.put(currKey, currKeyNeighbors);
	        
	        if (!graph.containsKey(currVal)) graph.put(currVal, new HashSet<>());
	        Set<Long> currValNeighbors = graph.get(currVal);
	        currValNeighbors.add(currKey);
	        graph.put(currVal, currValNeighbors);
	    }
	    
	    return graph;
	}
	
	public static boolean checkConnectivityDfs(Map<Long, Set<Long>> graph) {
		Set<Long> visited = new HashSet<>();
		visit(graph, 1, visited);
		return (visited.size() == NUMBER_OF_NODES);
	}
	
	public static void visit(Map<Long, Set<Long>> graph, long key, Set<Long> visited) {
		if (!visited.add(key)) return;
		
		if (graph.containsKey(key))
			for (Long neighbor : graph.get(key)) visit(graph, neighbor, visited);
	}
	
	public static boolean checkConnectivityBfs(Map<Long, Set<Long>> graph) {
		Set<Long> visited = new HashSet<>();
		Queue<Long> queue = new LinkedList<>();
		
		queue.offer((long) 1);
		visited.add((long) 1);
		while (!queue.isEmpty()) {
			long currNode = queue.remove();
			visited.add(currNode);
			
			if (graph.containsKey(currNode)) {
				Set<Long> neighbors = graph.get(currNode);
				for (Long neighbor : neighbors)
					if (!visited.contains(neighbor)) queue.offer(neighbor);
			}
		}
		
		// System.out.println(visited.size());
		
		return (visited.size() == NUMBER_OF_NODES);
	}
	
	public static double calcProbability(int numIte, double c) {
		int connectingCount = 0;
		
		double p = c * Math.log(NUMBER_OF_NODES) / NUMBER_OF_NODES;
				
		for (int i = 0; i < numIte; i++) {
			Map<Long, Set<Long>> graph = constructGraph(NUMBER_OF_NODES, p);
			if (checkConnectivityBfs(graph)) {
				// System.out.println("conn");
				connectingCount++;
			}
		}
		
		return ((double) connectingCount / numIte);
	}
	
	public static double calcClusteringCoefficient(Map<Long, Set<Long>> graph) {
		double numerator = 0.0;
		double denominator = 0.0;
		
		for (Long x : graph.keySet()) {
			if (graph.get(x).size() < 2) continue;
			
			long dx = graph.get(x).size();
			
			// System.out.println("dx: " + dx);
			
			long bigGammaX = 0;
			
			Set<Long> neighborSet = graph.get(x);
			Set<Long> visited = new HashSet<>();
			Queue<Long> queue = new LinkedList<>();

			int i = 0;
			for (Long neighbor : neighborSet) {
				queue.offer(neighbor);
				if (i == 0) break;
			}
			
			while (!queue.isEmpty()) {
				long curr = queue.remove();
				if (visited.contains(curr)) continue;
				
				for (Long l : graph.get(curr)) {
					if (neighborSet.contains(l) && !visited.contains(l)) {
						bigGammaX++;
						queue.offer(l);
					}
				}
				
				visited.add(curr);
			}
			
			// System.out.println("Big gamma of x: " + bigGammaX);
			
			numerator += bigGammaX;
			denominator += dx * (dx - 1) / 2;
		}
		
		return numerator / denominator;
	}
	
	public static long calcApproxDiameter(Map<Long, Set<Long>> graph) {
		long diameter = 0;
		int numOfLoop = 0;
		for (long node : graph.keySet()) {
			if (numOfLoop++ > 10) break;
			diameter = Math.max(diameter, calcDepth(graph, node));
		}
		
		return diameter;
	}
	
	public static long calcDiameter(Map<Long, Set<Long>> graph) {
		long diameter = 0;
		for (long node : graph.keySet()) diameter = Math.max(diameter, calcDepth(graph, node));
		return diameter;
	}
	
	public static long calcDepth(Map<Long, Set<Long>> graph, long node) {
		long depth = 0;
		
		Queue<Long> queue = new LinkedList<>();
		Set<Long> visited = new HashSet<>();
		queue.offer(node);
		
		while (!queue.isEmpty()) {
			Long curr = queue.poll();
			visited.add(curr);
			
			boolean found = false;
			for (Long neighbor : graph.get(curr)) {
				if (visited.contains(neighbor)) continue;
				
				if (!found) {
					found = true;
					depth++;
				}
				
				queue.offer(neighbor);
			}
		}
		
		return depth;
	}
	
	public static void main(String[] args) {
		List<Long> diameters = new ArrayList<>();
		
		for (long n = 100; n <= 50000; n += 1000) {
			double p = 3 * Math.log(n) / n;
			Map<Long, Set<Long>> graph = constructGraph(n, p);
			long diameter = calcApproxDiameter(graph);
			diameters.add(diameter);
		}
		
		BufferedWriter bw = null;
		FileWriter fw = null;
		
		List<Double> probs = new ArrayList<>();
		
		try {
			fw = new FileWriter(OUTPUT_DIAM_PATH);
			bw = new BufferedWriter(fw);
			
			for (long diam : diameters) {
				bw.write(diam + "");
				bw.newLine();
			} 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		/*
		for (int n = 100; n <= 1000000; n *= 10) {
			double p = 3 * Math.log(n) / n;
			Map<Long, Set<Long>> graph = constructGraph(n, p);
			double clusteringCoefficient = calcClusteringCoefficient(graph);
			System.out.println("n = " + n);
			System.out.println(clusteringCoefficient);
		}
		*/
		
		/*
		double p = 1.5 * Math.log(NUMBER_OF_NODES) / NUMBER_OF_NODES;
		Map<Long, Set<Long>> graph = constructGraph(NUMBER_OF_NODES, p);
		boolean isConnected = checkConnectivityBfs(graph);
		System.out.println(isConnected);
		*/
		
		/*
		double prob = calcProbability(1000, 1.5);
		System.out.println(prob);
		*/
		
		/*
		BufferedWriter bw = null;
		FileWriter fw = null;
		
		List<Double> probs = new ArrayList<>();
		
		try {
			fw = new FileWriter(OUTPUT_FILE_PATH);
			bw = new BufferedWriter(fw);
			
			for (double c = 0.5; c <= 1.6; c += 0.05) {
				double prob = calcProbability(100, c);
				bw.write(prob + "");
				bw.newLine();
			} 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		*/
	}
	
	private static final long NUMBER_OF_NODES = 100;
	private static final String OUTPUT_PROB_PATH = "C:\\Users\\chutianshu\\Documents\\Courses\\NetworksAndDistributedProcessing\\Homeworks\\hw4\\Code\\JavaProject\\probabilities.txt";
	private static final String OUTPUT_DIAM_PATH = "C:\\Users\\chutianshu\\Documents\\Courses\\NetworksAndDistributedProcessing\\Homeworks\\hw4\\Code\\JavaProject\\diameters.txt";
}