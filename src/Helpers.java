public class Helpers {
	public static double calcProbability(int numIte, long n, double p) {
		int connectingCount = 0;
				
		for (int i = 0; i < numIte; i++) {
			Graph graph = new Graph(n, p);
			if (graph.checkConnectivity()) connectingCount++;
		}
		
		return ((double) connectingCount / numIte);
	}
}
