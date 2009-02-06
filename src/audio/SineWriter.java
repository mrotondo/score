package audio;

import java.util.LinkedList;

public class SineWriter implements ToneWriter {

	private double frequency;
	private double volume;
	
	private int samplesSent;
	private long timeStartedMillis;
	
	private LinkedList<Filter> filters;
	
	public SineWriter(double frequency, double volume) {
		this.frequency = frequency;
		this.volume = volume;
		
		timeStartedMillis = System.currentTimeMillis();
		samplesSent = 0;
		
		filters = new LinkedList<Filter>();
	}
	
	// should be private
	public int millisInSamples(float millis) {
		return (int) (millis / 1000 * (int) AudioWriterThread.SAMPLES_PER_SECOND);
	}
	
	private boolean haveSentEnough() {
		return samplesSent >= millisInSamples(System.currentTimeMillis() - timeStartedMillis);
	}
	
	public Double getAmplitude(int positionInSamples) {
		if (!haveSentEnough()) {
			samplesSent++;
			return Math.sin(((positionInSamples * 2 * Math.PI) / AudioWriterThread.SAMPLES_PER_SECOND) * frequency) * volume;
		} else {
			return null;
		}
	}

	public double[] getAmplitudes(int positionInSamples, int numSamples) {
		if (millisInSamples(System.currentTimeMillis() - timeStartedMillis) - samplesSent <= numSamples) {
			return null;
		} else {
			double[] amplitudes = new double[numSamples];
			for (int i = 0; i < numSamples; i++) {
				amplitudes[i] = Math.sin((((positionInSamples + i) * 2 * Math.PI) / AudioWriterThread.SAMPLES_PER_SECOND) * frequency) * volume;
			}
			filterAmplitudes(amplitudes);
			samplesSent += numSamples;
			return amplitudes;
		}
	}

	private void filterAmplitudes(double[] amplitudes) {
		for (Filter filter : filters) {
			filter.transformAmplitudes(amplitudes);
		}
	}
	
	public void addFilter(Filter filter) {
		filters.add(filter);
	}
	
}
