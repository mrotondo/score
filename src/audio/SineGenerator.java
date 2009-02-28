package audio;

import java.util.LinkedList;

import filters.Filter;

public class SineGenerator implements AudioGenerator {
	private int samplesSent;
	private long timeStartedMillis;
	
	private LinkedList<Filter> filters;
	
	private double[] buffer;
	private int readIndex;
	private double frequency;
	private double nextFrequency;
	private double volume;
	private double nextVolume;
	
	public SineGenerator(double frequency, double volume) {
		this.frequency = frequency;
		this.nextFrequency = frequency;
		
		this.volume = volume;
		this.nextVolume = volume;
		
		buffer = new double[AudioSenderThread.SAMPLES_PER_SECOND];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = Math.sin((i * 2 * Math.PI) / buffer.length);
		}
		readIndex = 0;
		samplesSent = 0;
		
		filters = new LinkedList<Filter>();
	}
	
	public void start() {
		timeStartedMillis = System.currentTimeMillis();
	}
	
	// should be private
	public int millisToSamples(float millis) {
		return (int) (millis / 1000 * AudioSenderThread.SAMPLES_PER_SECOND);
	}
	
	public boolean shouldSendSamples(int numSamples) {
		boolean shouldSend = millisToSamples(System.currentTimeMillis() - timeStartedMillis) - samplesSent >= numSamples;
		return shouldSend;
	}
	
	public double[] getSamples(int numSamples) {
		frequency = nextFrequency;
		volume = nextVolume;
		double[] returnBuffer = new double[numSamples];
		for (int i = 0; i < numSamples; i++) {
			returnBuffer[i] = buffer[readIndex] * volume;
			readIndex = (readIndex + (int) frequency) % buffer.length;
		}
		filterSamples(returnBuffer);
		samplesSent += numSamples;
		return returnBuffer;
	}

	public void setFrequency(double frequency) { 
		nextFrequency = Math.max(0, frequency);
	}
	public double getFrequency() {
		return frequency;
	}
	public void setVolume(double volume) { 
		nextVolume = volume;
	}
	public double getVolume() {
		return volume;
	}
	
	private void filterSamples(double[] samples) {
		for (Filter filter : filters) {
			filter.transformSamples(samples);
		}
	}
	
	public void addFilter(Filter filter) {
		filters.add(filter);
	}
	
}
