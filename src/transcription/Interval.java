package transcription;

public enum Interval {
	PERFECT_UNISON(0, "unison"),
	MINOR_SECOND(1, "minor second"),
	MAJOR_SECOND(2, "major second"),
	MINOR_THIRD(3, "minor third"),
	MAJOR_THIRD(4, "major third"),
	PERFECT_FOURTH(5, "perfect fourth"),
	TRITONE(6, "tritone"),
	PERFECT_FIFTH(7, "perfect fifth"),
	MINOR_SIXTH(8, "minor sixth"),
	MAJOR_SIXTH(9, "major sixth"),
	MINOR_SEVENTH(10, "minor seventh"),
	MAJOR_SEVENTH(11, "major seventh"),
	PERFECT_OCTAVE(12, "octave");

	public String name;
	public int pitchDistance;
	private Interval(int pitchDistance, String name) {
		this.name = name;
		this.pitchDistance = pitchDistance;
	}
	
}