package score;

import javax.sound.sampled.LineUnavailableException;

public class ThreadedPlayer {

	//Runnable reader;
	AudioWriter writer;
	Thread producer;
	
	public ThreadedPlayer(WaveformGUI waveform) {
		writer = null;
		try {
			writer = new AudioWriter(waveform);
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
			System.exit(0);
		}
		producer = new Thread(writer);
		producer.start();
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
		producer.interrupt();
		// TODO: Figure out how to close cleanly (without bzzt at the end if a note is playing when quit happens)
	}
}