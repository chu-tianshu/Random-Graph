public class Helpers {
	public static double calcConnProbability(int numIte, long n, double p) {
		int connectingCount = 0;
				
		for (int i = 0; i < numIte; i++) {
			Graph graph = new Graph(n, p);
			if (graph.checkConnectivity()) connectingCount++;
		}
		
		return ((double) connectingCount / numIte);
	}
	
	public static double calcConnProbability(int numIte, int n, double c, double alpha) {
		int connectingCount = 0;
		
		for (int i = 0; i < numIte; i++) {
			Graph graph = new Graph(n, c, alpha);
			if (graph.checkConnectivity()) connectingCount++;
		}
		
		return ((double) connectingCount / numIte);
	}
	
	public static double calcAverageMaxComponentSize(int numIte, int n, double c, double alpha) {
		int sum = 0;
		
		for (int i = 0; i < numIte; i++) {
			Graph graph = new Graph(n, c, alpha);
			sum += graph.calcMaxComponentSize();
		}
		
		return ((double) sum / numIte);
	}
	
	public static double generatePareto(double c, double alpha) {
		return (Math.pow(c / (1 - Math.random()), 1 / alpha));
	}
}
