package score;

import javax.sound.sampled.LineUnavailableException;

public class ScoreTest {

	public static void main(String[] args) {
		ScoreTest test = new ScoreTest();
		
		System.out.println("About to test Tone");
		test.testTone();
		
		System.out.println("About to test Note");
		test.testNote();
		
		System.out.println("About to test Player");
		test.testPlayer();

		System.out.println("About to test Score");
		test.testScore();

		System.out.println("About to test ToneWriter");
		test.testToneWriter();
		
		System.out.println("Success!");
	}

	private void testScore() {

		System.out.println("Testing noteDuration");
		gAssEqDouble(Score.noteDuration(Note.Length.QUARTER, 60.0, Note.Length.QUARTER), 1.0);
		gAssEqDouble(Score.noteDuration(Note.Length.QUARTER, 120.0, Note.Length.QUARTER), 0.5);
		gAssEqDouble(Score.noteDuration(Note.Length.WHOLE, 240.0, Note.Length.WHOLE), 0.25);
		gAssEqDouble(Score.noteDuration(Note.Length.EIGHTH, 120.0, Note.Length.EIGHTH), 0.5);

		System.out.println("Testing beatsPerNote");
		gAssEqDouble(Score.beatsPerNote(Note.Length.WHOLE, Note.Length.QUARTER), 4.0);
		gAssEqDouble(Score.beatsPerNote(Note.Length.QUARTER, Note.Length.QUARTER), 1.0);
		gAssEqDouble(Score.beatsPerNote(Note.Length.EIGHTH, Note.Length.QUARTER), 0.5);
		gAssEqDouble(Score.beatsPerNote(Note.Length.EIGHTH, Note.Length.EIGHTH), 1.0);
		gAssEqDouble(Score.beatsPerNote(Note.Length.WHOLE, Note.Length.EIGHTH), 8.0);
		
		System.out.println("Testing getMeasures");
		Score score = new Score(120, Note.Length.QUARTER, 4, new StaffGUI());
		score.addNote(new Note(new Tone(51), Note.Length.QUARTER));
		score.addNote(new Note(new Tone(50), Note.Length.QUARTER));
		score.addNote(new Note(new Tone(48), Note.Length.QUARTER));
		score.addNote(new Note(new Tone(46), Note.Length.QUARTER));
		gAssEqInt(score.getMeasures().size(), 1);
		score.addNote(new Note(new Tone(48), Note.Length.HALF));
		score.addNote(new Note(new Tone(46), Note.Length.HALF));
		gAssEqInt(score.getMeasures().size(), 2);
		score.addNote(new Note(new Tone(46), Note.Length.EIGHTH));
		gAssEqInt(score.getMeasures().size(), 3);
		
		System.out.println("Testing getTimeSignature");
		score = new Score(120, Note.Length.QUARTER, 4, new StaffGUI());
		int[] timeSignature = score.getTimeSignature();
		gAssEqInt(timeSignature[0], 4);
		gAssEqInt(timeSignature[1], 4);
		score = new Score(120, Note.Length.QUARTER, 5, new StaffGUI());
		timeSignature = score.getTimeSignature();
		gAssEqInt(timeSignature[0], 5);
		gAssEqInt(timeSignature[1], 4);
		score = new Score(120, Note.Length.WHOLE, 1, new StaffGUI());
		timeSignature = score.getTimeSignature();
		gAssEqInt(timeSignature[0], 1);
		gAssEqInt(timeSignature[1], 1);
		score = new Score(120, Note.Length.EIGHTH, 6, new StaffGUI());
		timeSignature = score.getTimeSignature();
		gAssEqInt(timeSignature[0], 6);
		gAssEqInt(timeSignature[1], 8);
	}
	
	private void testPlayer() {
		System.out.println("Testing getNoteBytes");
		Tone tone = new Tone(48);
		double seconds = 1.4;
		byte[] bytes = Player.getToneBytes(tone, seconds);
		gAssEqInt(bytes.length, (int) (seconds * Player.SAMPLES_PER_SECOND));
		gAssEqByte(bytes[0], (byte) 0);
		// I could test more accurately whether the right bytes are showing up in the byte array...
		// But I'm not. Too much math, not right now, etc.
		
		try {
			//Note a4 = new Note(new Tone(48), Note.Length.QUARTER);
			//Note a0 = new Note(new Tone(0), Note.Length.QUARTER);
			//Player.playNote(a4, 1.0);
			//Player.playNote(a0, 0.05);
			Player.click(0.05);
		} catch (LineUnavailableException e) {
			throw new RuntimeException("Line unavailable: " + e.getMessage());
		}
	}
	
	private void testNote() {
		System.out.println("Note testing any bullshit");
	}

	private void testTone() {
		System.out.println("Testing pitchToFrequency");
		gAssEqDouble(Tone.pitchToFrequency(-12), 13.75);
		gAssEqDouble(Tone.pitchToFrequency(0), Tone.A0);
		gAssEqDouble(Tone.pitchToFrequency(12), 55);
		gAssEqDouble(Tone.pitchToFrequency(48), 440);
		gAssEqDouble(Tone.pitchToFrequency(50), 493.88330125612424);
		
		System.out.println("Testing pitchToOctave");
		gAssEqInt(Tone.pitchToOctave(-12), -1);
		gAssEqInt(Tone.pitchToOctave(0), 0);
		gAssEqInt(Tone.pitchToOctave(12), 1);
		gAssEqInt(Tone.pitchToOctave(48), 4);
		gAssEqInt(Tone.pitchToOctave(50), 4);
		
		System.out.println("Testing pitchToName");
		gAssEqString(Tone.pitchToName(-12), "A-1");
		gAssEqString(Tone.pitchToName(0), "A0");
		gAssEqString(Tone.pitchToName(12), "A1");
		gAssEqString(Tone.pitchToName(48), "A4");
		gAssEqString(Tone.pitchToName(49), "A#/Bb4");
		gAssEqString(Tone.pitchToName(50), "B4");
		gAssEqString(Tone.pitchToName(51), "C4");
		gAssEqString(Tone.pitchToName(55), "E4");
		gAssEqString(Tone.pitchToName(56), "F4");

		System.out.println("Testing pitchToNameObject");
		gAssEqString(Tone.pitchToNameObject(0).toString(), "A");
		gAssEqString(Tone.pitchToNameObject(1).toString(), "A");
		gAssEqString(Tone.pitchToNameObject(2).toString(), "B");
		gAssEqString(Tone.pitchToNameObject(3).toString(), "C");
		gAssEqString(Tone.pitchToNameObject(4).toString(), "C");
		gAssEqString(Tone.pitchToNameObject(5).toString(), "D");
		gAssEqString(Tone.pitchToNameObject(6).toString(), "D");
		gAssEqString(Tone.pitchToNameObject(7).toString(), "E");
		gAssEqString(Tone.pitchToNameObject(8).toString(), "F");
		gAssEqString(Tone.pitchToNameObject(9).toString(), "F");
		gAssEqString(Tone.pitchToNameObject(10).toString(), "G");
		gAssEqString(Tone.pitchToNameObject(11).toString(), "G");
		gAssEqString(Tone.pitchToNameObject(12).toString(), "A");
	}
	
	private void testToneWriter() {
		SineWriter sw = new SineWriter(440, 1.0, 44100);
		gAssEqInt(44100, sw.millisInSamples(1000));
		gAssEqInt(4410, sw.millisInSamples(100));
		gAssEqInt(441, sw.millisInSamples(10));
	}
	
	private void gAssEqDouble(double val1, double val2) {
		if (val1 != val2) {
			throw new RuntimeException(val1 + " does not equal " + val2);
		}
	}
	private void gAssEqInt(int val1, int val2) {
		if (val1 != val2) {
			throw new RuntimeException(val1 + " does not equal " + val2);
		}
	}
	private void gAssEqString(String val1, String val2) {
		if (!val1.equals(val2)) {
			throw new RuntimeException(val1 + " does not equal " + val2);
		}
	}
	private void gAssEqByte(byte val1, byte val2) {
		if (val1 != val2) {
			throw new RuntimeException(val1 + " does not equal " + val2);
		}
	}
}