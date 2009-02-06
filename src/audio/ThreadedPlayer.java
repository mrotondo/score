package audio;

import javax.sound.sampled.LineUnavailableException;

import transcription.Note;

public class ThreadedPlayer {

	AudioWriterThread writer;
	public static ThreadedPlayer instance = new ThreadedPlayer();
	
	private ThreadedPlayer() {
		writer = null;
		try {
			writer = new AudioWriterThread();
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
			System.exit(0);
		}
		writer.start();
	}
	
	public void playNote(Note note) {
		
	}
	
	public void playMetronome() {
		
	}
	
	public void playClick() {
		writer.playClick();
	}
	
	public void startTone(double freq) {
		writer.startTone(freq);
	}

	public void stopTone(double freq) {
		writer.stopTone(freq);
	}
	
	public void stop() {
		writer.interrupt();
		// TODO: Figure out how to close cleanly (without bzzt at the end if a note is playing when quit happens)
	}
}