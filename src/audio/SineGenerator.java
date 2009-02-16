package audio;

import java.util.LinkedList;

import filters.Filter;

public class SineGenerator implements AudioGenerator {
	private int samplesSent;
	private long timeStartedMillis;
	
	private LinkedList<Filter> filters;
	
	private double[] buffer;
	private int readIndex;
	
	public SineGenerator(double frequency, double volume) {
		buffer = new double[(int) (AudioSenderThread.SAMPLES_PER_SECOND / frequency)];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = Math.sin((i * 2 * Math.PI) / buffer.length);
		}
		readIndex = 0;
		
		timeStartedMillis = System.currentTimeMillis();
		samplesSent = 0;
		
		filters = new LinkedList<Filter>();
	}
	
	// should be private
	public int millisToSamples(float millis) {
		return (int) (millis / 1000 * AudioSenderThread.SAMPLES_PER_SECOND);
	}
	
	public boolean shouldSendSamples(int numSamples) {
		/*
		System.out.println("Should " + this + " send " + numSamples + " samples?");
		System.out.println("Samples alive: " + millisToSamples(System.currentTimeMillis() - timeStartedMillis));
		System.out.println("Samples sent: " + samplesSent);
		System.out.println("Samples I should send: " + (millisToSamples(System.currentTimeMillis() - timeStartedMillis) - samplesSent));
		*/
		boolean shouldSend = millisToSamples(System.currentTimeMillis() - timeStartedMillis) - samplesSent >= numSamples;
		//System.out.println(shouldSend);
		return shouldSend;
	}
	
	public double[] getSamples(int numSamples) {
		double[] returnBuffer = new double[numSamples];
		for (int i = 0; i < numSamples; i++) {
			returnBuffer[i] = buffer[readIndex];
			readIndex = (readIndex + 1) % buffer.length;
		}
		filterSamples(returnBuffer);
		samplesSent += numSamples;
		return returnBuffer;
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
