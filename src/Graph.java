import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
	
	private Map<Long, Set<Long>> map;
	private long numberOfNodes;
}
