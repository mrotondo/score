package transcription;

import audio.Tone;

public class Scale {

	public static Scale majorScale = new Scale(new Interval[]{ Interval.MAJOR_SECOND,
															   Interval.MAJOR_SECOND,
															   Interval.MINOR_SECOND,
															   Interval.MAJOR_SECOND,
															   Interval.MAJOR_SECOND,
															   Interval.MAJOR_SECOND,
															   Interval.MINOR_SECOND
															  }, "Major");
	
	public static Scale naturalMinorScale = new Scale(new Interval[]{ Interval.MAJOR_SECOND,
																	  Interval.MINOR_SECOND,
																	  Interval.MAJOR_SECOND,
																	  Interval.MAJOR_SECOND,
																	  Interval.MINOR_SECOND,
																	  Interval.MAJOR_SECOND,
																	  Interval.MAJOR_SECOND
																	 }, "Natural Minor");
	
	public static Scale harmonicMinorScale = new Scale(new Interval[]{ Interval.MAJOR_SECOND,
																	   Interval.MINOR_SECOND,
																	   Interval.MAJOR_SECOND,
																	   Interval.MAJOR_SECOND,
																	   Interval.MINOR_SECOND,
																	   Interval.MINOR_THIRD,
																	   Interval.MAJOR_SECOND
																	  }, "Harmonic Minor");
	
	public static Scale melodicMinorScale = new Scale(new Interval[]{ Interval.MAJOR_SECOND,
																	  Interval.MINOR_SECOND,
																	  Interval.MAJOR_SECOND,
																	  Interval.MAJOR_SECOND,
																	  Interval.MAJOR_SECOND,
																	  Interval.MAJOR_THIRD,
																	  Interval.MINOR_SECOND
																	 }, "Melodic Minor");

	
	private Interval[] intervals;
	private final String name;

	public Scale(Interval[] intervals, String name) {
		this.intervals = intervals;
		this.name = name;
	}

	public static Tone[] getTones(Scale scale, Tone tonic) {
		Tone[] tones = new Tone[scale.intervals.length + 1];
		for (int i = 0; i < scale.intervals.length; i++) {
			tones[i] = Scale.getTone(scale, tonic, i);
		}	
		return tones;
	}
	public static Tone getTone(Scale scale, Tone tonic, int degree) {
		int pitch = tonic.pitch;
		if (0 == degree) return new Tone(pitch);
		for (int i = 0; i < degree; i++) {
			pitch += scale.intervals[i % scale.intervals.length].pitchDistance;
		}
		return new Tone(pitch);
	}
}
