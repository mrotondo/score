package filters;

public class LogFilter implements Filter {
	
	private final double denominator;
	private int counter;
	
	public LogFilter(double denominator) {
		this.denominator = denominator;
	}
	
	public boolean finished() {
		return true;
	}

	public void start() {
		counter = 0;
	}

	public void transformSamples(double[] samples) {
		for (int i = 0; i < samples.length; i++) {
			samples[i] *= Math.min(1.0, Math.log((counter) / this.denominator + 1));
			counter++;
		}
	}

	public Filter clone() {
		return new LogFilter(this.denominator);
	}
	
}
