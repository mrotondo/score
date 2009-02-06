package audio;

import java.util.Random;

public class NoiseFilter implements Filter {

	Random random;
	
	public boolean finished() {
		return true;
	}

	public void start() {
		random = new Random();
	}

	public void transformAmplitudes(double[] amplitudes) {
		for (int i = 0; i < amplitudes.length; i++) {
			amplitudes[i] = random.nextDouble();
		}
	}

	public Filter clone() {
		return new NoiseFilter();
	}
	
}
