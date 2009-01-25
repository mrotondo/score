package score;

public interface ToneWriter {
	
	public Double getAmplitude(int positionInSamples);
	public double[] getAmplitudes(int positionInSamples, int numSamples);
	
}
