package audio;

import filters.Filter;

public interface AudioGenerator {
	public boolean shouldSendSamples(int numSamples);
	public double[] getSamples(int numSamples);
	public void addFilter(Filter filter);
}
