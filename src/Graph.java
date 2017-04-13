import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Graph {
	public Graph(long n, double p) { // G(n, p) model
	    map = new HashMap<>();
	    numberOfNodes = n;
	    
	    long currSum = 0;
	    long currKey = 1;
	    while (true) {
	        long currGeoNum = (long) (Math.log(Math.random()) / Math.log(1 - p)) + 1;
	        currSum += currGeoNum;
	        
	        if (currSum > n * (n - 1) / 2) break;

	        while (currSum > (2 * n - currKey - 1) * currKey / 2) currKey++;
	        long currVal = currKey > 1 ? currSum - (2 * n - (currKey - 1) - 1) * (currKey - 1) / 2 + currKey : 1 + currSum;
	        
	        if (!map.containsKey(currKey)) map.put(currKey, new HashSet<>());
	        Set<Long> currKeyNeighbors = map.get(currKey);
	        currKeyNeighbors.add(currVal);
	        map.put(currKey, currKeyNeighbors);
	        
	        if (!map.containsKey(currVal)) map.put(currVal, new HashSet<>());
	        Set<Long> currValNeighbors = map.get(currVal);
	        currValNeighbors.add(currKey);
	        map.put(currVal, currValNeighbors);
	    }
	}
	
	public Graph(int n, double c, double alpha) { // GED model, wi generated based on Pareto(c, alpha)
		map = new HashMap<>();
		numberOfNodes = n;
		
		for (long i = 0; i < n; i++) map.put(i, new HashSet<>());
		
		double[] weights = new double[n];
		double weightSum = 0.0;
		for (int i = 0; i < n; i++) {
			weights[i] = Helpers.generatePareto(c, alpha);
			weightSum += weights[i];
		}
		
		for (long i = 0; i < n - 1; i++) {
			for (long j = i + 1; j < n; j++) {
				if (Math.random() < Math.min(weights[(int) i] * weights[(int) j] / weightSum, 1)) {
					map.get(i).add((long) j);
					map.get(j).add((long) i);
				}
			}
		}
	}
	
	public Graph(int n, int m0, int m) { // BA model
		map = new HashMap<>();
		numberOfNodes = n;
		
		for (long i = 1; i <= m0; i++) {
			Set<Long> currNeighbors = new HashSet<>();
			for (long j = 1; j <= m0; j++)
				if (i != j) currNeighbors.add(j);
			
			map.put(i, currNeighbors);
		}
		
		int currDegreeSum = m0 * (m0 - 1);
		
		for (long i = m0 + 1; i <= n; i++) {
			
		}
	}
	
	public boolean checkConnectivity() {
		Set<Long> visited = new HashSet<>();
		Queue<Long> queue = new LinkedList<>();
		
		queue.offer((long) 1);
		visited.add((long) 1);
		while (!queue.isEmpty()) {
			long currNode = queue.remove();
			visited.add(currNode);
			
			if (map.containsKey(currNode)) {
				Set<Long> neighbors = map.get(currNode);
				for (Long neighbor : neighbors)
					if (!visited.contains(neighbor)) queue.offer(neighbor);
			}
		}
		
		return (visited.size() == numberOfNodes);
	}
	
	public List<Integer> calcComponentSizes() {
		List<Integer> result = new ArrayList<>();
		
		Set<Long> visited = new HashSet<>();
		for (long i = 0; i < numberOfNodes; i++) {
			if (!visited.contains(i)) {
				Set<Long> currVisited = new HashSet<>();
				Queue<Long> queue = new LinkedList<>();
				
				queue.add(i);
				while (!queue.isEmpty()) {
					long currNode = queue.remove();
					visited.add(currNode);
					currVisited.add(currNode);
					
					Set<Long> neighbors = map.get(currNode);
					for (Long neighbor : neighbors) 
						if (!currVisited.contains(neighbor)) queue.offer(neighbor);
				}
				
				result.add(currVisited.size());
			}
		}
		
		return result;
	}
	
	public int calcMaxComponentSize() {
		int result = 0;
		
		Set<Long> visited = new HashSet<>();
		for (long i = 0; i < numberOfNodes; i++) {
			if (!visited.contains(i)) {
				Set<Long> currVisited = new HashSet<>();
				Queue<Long> queue = new LinkedList<>();
				
				queue.add(i);
				while (!queue.isEmpty()) {
					long currNode = queue.remove();
					visited.add(currNode);
					currVisited.add(currNode);
					
					Set<Long> neighbors = map.get(currNode);
					for (Long neighbor : neighbors) 
						if (!currVisited.contains(neighbor)) queue.offer(neighbor);
				}
				
				if (currVisited.size() > numberOfNodes / 2) return currVisited.size();
				result = Math.max(result, currVisited.size());
			}
		}
		
		return result;
	}
	
	public int[] getDegrees() {
		int[] degrees = new int[(int) numberOfNodes];
		for (long i = 0; i < numberOfNodes; i++) degrees[(int) i] = map.get(i).size();
		return degrees;
	}
	
	public double calcClusteringCoefficient() {
		double numerator = 0.0;
		double denominator = 0.0;
		
		for (Long x : map.keySet()) {
			if (map.get(x).size() < 2) continue;
			
			long dx = map.get(x).size();
			
			long bigGammaX = 0;
			
			Set<Long> neighborSet = map.get(x);
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
				
				for (Long l : map.get(curr)) {
					if (neighborSet.contains(l) && !visited.contains(l)) {
						bigGammaX++;
						queue.offer(l);
					}
				}
				
				visited.add(curr);
			}
			
			numerator += bigGammaX;
			denominator += dx * (dx - 1) / 2;
		}
		
		return numerator / denominator;
	}
	
	public long calcDiameter() {
		long diameter = 0;
		for (long node : map.keySet()) diameter = Math.max(diameter, calcDepth(node));
		return diameter;
	}
	
	public long calcApproxDiameter() {
		long diameter = 0;
		int numOfLoop = 0;
		
		for (long node : map.keySet()) {
			if (numOfLoop++ > 10) break;
			diameter = Math.max(diameter, calcDepth(node));
		}
		
		return diameter;
	}
	
	private long calcDepth(long node) {
		long depth = 0;
		
		Queue<Long> queue = new LinkedList<>();
		Set<Long> visited = new HashSet<>();
		queue.offer(node);
		
		while (!queue.isEmpty()) {
			Long curr = queue.poll();
			visited.add(curr);
			
			boolean found = false;
			for (Long neighbor : map.get(curr)) {
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
	
	public long getNodeCount() {
		return numberOfNodes;
	}
	
	public int getDegree(long node) {
		return map.containsKey(node) ? map.get(node).size() : 0;
	}
	
	private Map<Long, Set<Long>> map;
	private long numberOfNodes;
}
