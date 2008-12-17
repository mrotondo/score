package score;

import java.util.LinkedList;

import javax.sound.sampled.LineUnavailableException;

public class Score {

	public double tempo; // beats per minute
	public Note.Length getsTheBeat;
	public int beatsPerMeasure;
	public LinkedList<Note> notes;
	public StaffGUI staffGUI;
	
	public Score(double tempo, Note.Length getsTheBeat, int beatsPerMeasure, StaffGUI staffGUI) {
		this.tempo = tempo;
		this.getsTheBeat = getsTheBeat;
		this.beatsPerMeasure = beatsPerMeasure;
		this.staffGUI = staffGUI;
		this.staffGUI.setScore(this);
		notes = new LinkedList<Note>();
	}
	
	public void addNote(Note note) {
		notes.add(note);
	}
	
	public void playNotes() {
		for (Note note : notes) {
			try {
				Player.playTone(note.tone, noteDuration(note.duration, tempo, getsTheBeat));
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
	}
	
	public LinkedList<LinkedList<Note>> getMeasures() {
		LinkedList<LinkedList<Note>> measures = new LinkedList<LinkedList<Note>>();
		LinkedList<Note> measure = new LinkedList<Note>();
		double beatCounter = 0;
		for (Note note : notes) {
			measure.add(note);
			beatCounter += beatsPerNote(note.duration, getsTheBeat);
			if ((int)beatCounter == beatsPerMeasure && measure != null) {
				measures.add(measure);
				beatCounter = 0;
				measure = new LinkedList<Note>();
			}
		}
		if (!measure.isEmpty()) // Pick up a non-empty final measure that didn't quite fill up
			measures.add(measure);
		return measures;
	}
	
	public int[] getTimeSignature() {
		int[] timeSignature = new int[2];
		timeSignature[0] = beatsPerMeasure;
		timeSignature[1] = (int) beatsPerNote(Note.Length.WHOLE, getsTheBeat);
		return timeSignature;
	}
	
	public static double noteDuration(Note.Length length, double tempo, Note.Length getsTheBeat) {
		double beatsPerNote = Score.beatsPerNote(length, getsTheBeat);
		return beatDuration(tempo) * beatsPerNote;
	}
	
	public static double beatsPerNote(Note.Length length, Note.Length getsTheBeat) {
		// Use 2 ^ the difference in orders of magnitude to get the length of the note in fractions of beats
		return Math.pow(2, length.ordinal() - getsTheBeat.ordinal());
	}
	
	public static double beatDuration(double tempo) {
		return 60 / tempo;
	}
}