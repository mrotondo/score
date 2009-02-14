package audio;

import filters.Filter;

public interface AudioGenerator {
	public void fillBuffers();
	public double[] getBuffer();
	public void addFilter(Filter filter);
}
