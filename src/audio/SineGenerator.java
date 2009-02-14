package audio;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import filters.Filter;

public class SineGenerator implements AudioGenerator {

	private double frequency;
	private double volume;
	
	private int positionInSamples;
	private int samplesSent;
	private long timeStartedMillis;
	
	private LinkedList<Filter> filters;
	
	private int writeBufferIndex;
	private int readBufferIndex;
	private double[][] buffers;
	boolean[] needsFilling;
	Lock[] bufferLocks;
	private final int bufferLength;
	
	public SineGenerator(double frequency, double volume, int bufferLengthInSamples) {
		this.frequency = frequency;
		this.volume = volume;
		this.bufferLength = bufferLengthInSamples;
		
		writeBufferIndex = 0;
		readBufferIndex = 0;
		buffers = new double[10][bufferLength];
		needsFilling = new boolean[buffers.length];
		for (int i = 0; i < needsFilling.length; i++) { needsFilling[i] = true; }
		bufferLocks = new Lock[buffers.length];
		for (int i = 0; i < bufferLocks.length; i++) { bufferLocks[i] = new ReentrantLock(); }
		
		timeStartedMillis = System.currentTimeMillis();
		samplesSent = 0;
		positionInSamples = 0;
		
		filters = new LinkedList<Filter>();
	
		fillBuffers();
	}
	
	// should be private
	public int millisToSamples(float millis) {
		return (int) (millis / 1000 * (int) AudioSenderThread.SAMPLES_PER_SECOND);
	}
	
	public boolean shouldSendData(int numSamples) {
		return millisToSamples(System.currentTimeMillis() - timeStartedMillis) - samplesSent <= numSamples;
	}
	
	public void fillBuffers() {
		System.out.println("fill");
		while (needsFilling[writeBufferIndex]) {
			synchronized(buffers[writeBufferIndex]) {
				if (needsFilling[writeBufferIndex]) {
					double[] amplitudes = buffers[writeBufferIndex];
					for (int i = 0; i < bufferLength; i++) {
						amplitudes[i] = Math.sin((((positionInSamples++ + i) * 2 * Math.PI) / AudioSenderThread.SAMPLES_PER_SECOND) * frequency) * volume;
					}
					filterAmplitudes(amplitudes);
					
					needsFilling[writeBufferIndex] = false;
					writeBufferIndex = (writeBufferIndex + 1) % buffers.length;
				}
			}
		}
	}
	
	public double[] getBuffer() {
		System.out.println("get");
		synchronized(buffers[readBufferIndex]) {
			if (!shouldSendData(bufferLength) || null == buffers[readBufferIndex]) return null;
			
			double[] bufferToReturn = buffers[readBufferIndex];
			needsFilling[readBufferIndex] = true;
			readBufferIndex = (readBufferIndex + 1) % buffers.length;
			
			samplesSent += bufferLength;
			return bufferToReturn;
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
