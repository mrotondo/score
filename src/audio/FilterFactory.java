package audio;

import java.util.LinkedList;

public class FilterFactory {

	private static LinkedList<Filter> filters = new LinkedList<Filter>();
	
	public static void addFilterPrototype(Filter filter) {
		filters.add(filter);
	}
	
	public static void applyFilters(ToneWriter toneWriter) {
		for (Filter filter : filters) {
			Filter clone = filter.clone();
			clone.start();
			toneWriter.addFilter(clone);
		}
	}
	
}
