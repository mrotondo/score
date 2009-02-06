package audio;

public class Tone {

	public enum Name {
		A(0), B(2), C(3), D(5), E(7), F(8), G(10);
		
		public int pitchPerOctave;
		private Name(int pitchPerOctave) {
			this.pitchPerOctave = pitchPerOctave;
		}
	}
	
	public static final double A0 = 27.5;
	public int pitch; // A0 = 0, 1 per halftone
	
	public Tone(int pitch) {
		this.pitch = pitch;
	}
	
	public int getOctave() {
		return pitchToOctave(this.pitch);
	}
	
	public double getFrequency() {
		return pitchToFrequency(this.pitch);
	}
	
	public String getName() {
		return pitchToName(this.pitch);
	}
	
	public Tone.Name getNameObject() {
		return pitchToNameObject(this.pitch);
	}

	public String toString() {
		return getName();
	}

	public static double pitchToFrequency(int pitch) {
		// pitch is actually the difference between the current pitch and A0
		return A0 * Math.pow(2, pitch / 12.0);
	}
	
	public static int pitchToOctave(int pitch) {
		return pitch / 12;
	}
	
	public static String pitchToName(int pitch) {
		int octave = pitchToOctave(pitch);
		int pitchMod = pitch % 12;
		for (int i = 0; i < Tone.Name.values().length; i++) {
			Tone.Name name = Tone.Name.values()[i];
			Tone.Name nextName = Tone.Name.values()[(i+1) % Tone.Name.values().length];
			if (pitchMod == name.pitchPerOctave) {
				return name + "" + octave;
			} else if (pitchMod == name.pitchPerOctave + 1 && // Grab incidentals that fall above this name
					   nextName.pitchPerOctave != name.pitchPerOctave + 1) {  // Don't want to call C "B#"
				return name + "#/" + nextName + "b" + octave;
			}
		}
		return "FAIL (pitch: " + pitch + ")";
	}
	
	public static Tone.Name pitchToNameObject(int pitch) {
		int pitchMod = pitch % 12;
		for (int i = 0; i < Tone.Name.values().length; i++) {
			Tone.Name name = Tone.Name.values()[i];
			Tone.Name nextName = Tone.Name.values()[(i+1) % Tone.Name.values().length];
			if (pitchMod == name.pitchPerOctave ||
				(pitchMod == name.pitchPerOctave + 1 &&  // Grab incidentals that fall above this name
				 nextName.pitchPerOctave != name.pitchPerOctave + 1)) {  // Don't want to call C "B#"
				return name;
			}
		}
		return null;
	}
		
}
