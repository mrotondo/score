package filters;

import java.util.Random;

public class NoiseFilter implements Filter {

	Random random;
	
	public boolean finished() {
		return true;
	}

	public void start() {
		random = new Random();
	}

	public void transformSamples(double[] samples) {
		for (int i = 0; i < samples.length; i++) {
			samples[i] = random.nextDouble();
		}
	}

	public Filter clone() {
		return new NoiseFilter();
	}
	
}
