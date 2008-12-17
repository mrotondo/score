package score;

public class Note {

	enum Length {
		THIRTYSECOND,
		SIXTEENTH,
		EIGHTH,
		QUARTER,
		HALF,
		WHOLE
	}
	
	public Tone tone;
	public Length duration;
	
	public Note(Tone tone, Length duration) {
		this.tone = tone;
		this.duration = duration;
	}
	
	public String toString() {
		return tone + ", " + duration;
	}
}
