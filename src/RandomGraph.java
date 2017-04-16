public class RandomGraph {	
	public static void main(String[] args) {
		int m0 = 3;
		int m = 3;
		double q = 0.5;
		
		for (int n = 100; n <= 10000; n += 100) {
			Graph graph = new Graph(n, m0, m, q);
			double c = graph.calcClusteringCoefficient();
			System.out.println(c);
		}
	}
}