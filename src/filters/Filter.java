package filters;

public interface Filter {

	public void start();
	public void transformSamples(double[] samples);
	public boolean finished();
	
	public Filter clone();

}
