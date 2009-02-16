package filters;

public class NoOpFilter implements Filter {

	public boolean finished() {
		return true;
	}

	public void start() {

	}

	public Filter clone() {
		return new NoOpFilter();
	}

	public void transformSamples(double[] samples) {
		
	}
	
}
