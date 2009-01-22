package score;

public class SineWriter implements ToneWriter {

	private double frequency;
	private double volume;
	
	private float samplesPerSecond;
	
	private int samplesSent;
	private long timeStartedMillis;
	
	public SineWriter(double frequency, double volume, float samplesPerSecond) {
		this.frequency = frequency;
		this.volume = volume;
		this.samplesPerSecond = samplesPerSecond;
		
		timeStartedMillis = System.currentTimeMillis();
		samplesSent = 0;
	}
	
	// should be private
	public int millisInSamples(float millis) {
		return (int) (millis / 1000 * (int) samplesPerSecond);
	}
	
	private boolean haveSentEnough() {
		/*
		System.out.println("____ I am " + this + " ____");
		System.out.println("I was born at: " + timeStartedMillis);
		System.out.println("It is now: " + System.currentTimeMillis());
		System.out.println("I've been alive for: " + (System.currentTimeMillis() - timeStartedMillis) + " millis");
		System.out.println("I've been alive for: " + millisInSamples(System.currentTimeMillis() - timeStartedMillis) + " samples");
		System.out.println("I've sent: " + samplesSent + " samples");
		System.out.println("__________________");
		*/
		return samplesSent >= millisInSamples(System.currentTimeMillis() - timeStartedMillis);
	}
	
	// TODO: Data should probably be retrieved in chunks, not one sample at a time
	public Double getAmplitude(int positionInSamples) {
		if (!haveSentEnough()) {
			samplesSent++;
			//System.out.println("I've returned " + samplesSent + " samples at freq " + frequency);
			return Math.sin(((positionInSamples * 2 * Math.PI) / samplesPerSecond) * frequency) * volume;
		} else {
			return null;
		}
	}
	
}
