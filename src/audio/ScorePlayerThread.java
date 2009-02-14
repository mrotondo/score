package audio;

import java.util.ListIterator;

import transcription.Note;
import transcription.Score;

public class ScorePlayerThread extends Thread {

	private Score score;
	
	public ScorePlayerThread(Score score) {
		this.score = score;
	}
	
	public void run() {
	
		ListIterator<Note> noteIterator = score.notes.listIterator();

		Note note = noteIterator.next();
		double durationSeconds = Score.noteDuration(note.duration, score.tempo, score.getsTheBeat) * 1000;
		Long startTime = System.currentTimeMillis();
		//ThreadedPlayer.instance.startTone(note.tone.getFrequency());
		AudioWriterThread.startTone(note.tone.getFrequency());
		
		while (!isInterrupted()) {
			if (System.currentTimeMillis() - startTime > durationSeconds) {
				if (noteIterator.hasNext()) {
					//ThreadedPlayer.instance.stopTone(note.tone.getFrequency());
					note = noteIterator.next();
					durationSeconds = Score.noteDuration(note.duration, score.tempo, score.getsTheBeat) * 1000;
					startTime = System.currentTimeMillis();
					//ThreadedPlayer.instance.startTone(note.tone.getFrequency());
					AudioWriterThread.startTone(note.tone.getFrequency());
				} else {
					//ThreadedPlayer.instance.stopTone(note.tone.getFrequency());
					AudioWriterThread.stopTone(note.tone.getFrequency());
					score.stop();
				}
			} else {
				try {
					sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		

	}

}
