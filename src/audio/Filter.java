package audio;

public interface Filter {

	public void start();
	public void transformAmplitudes(double[] amplitudes);
	public boolean finished();
	
	public Filter clone();

}
