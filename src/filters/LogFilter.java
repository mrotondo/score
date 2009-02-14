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

	public void transformAmplitudes(double[] amplitudes) {
		for (int i = 0; i < amplitudes.length; i++) {
			amplitudes[i] *= Math.min(1.0, Math.log((counter + 1) / this.denominator));
			counter++;
		}
	}

	public Filter clone() {
		return new LogFilter(this.denominator);
	}
	
}
