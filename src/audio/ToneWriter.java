package audio;

public interface ToneWriter {
	
	public Double getAmplitude(int positionInSamples);
	public double[] getAmplitudes(int positionInSamples, int numSamples);
	
	public void addFilter(Filter filter);
	
}
