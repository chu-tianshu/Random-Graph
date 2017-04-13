public class RandomGraph {	
	public static void main(String[] args) {
		double c = 1.5;
		double alpha = 2;
		
		for (int n = 100; n <= 10000; n += 100) {
			Graph graph = new Graph(n, c, alpha);
			double clusteringCoefficient = 0.0;
			
			for (int i = 0; i < 20000 / n; i++) {
				clusteringCoefficient += graph.calcClusteringCoefficient();
			}
			
			System.out.println(clusteringCoefficient / 10);
		}
	}
}