package audio;

import java.util.Timer;
import java.util.TimerTask;


import transcription.Note;
import transcription.Score;

public class Metronome {

	Score score;
	Timer timer;
	
	public Metronome(Score score) {
		this.score = score;
	}
	
	private class MetronomeTask extends TimerTask {

		Score score;
		
		public MetronomeTask(Score score) {
			this.score = score;
		}
		
		public void run() {
			ThreadedPlayer.instance.playClick();
		}
		
	}
	
	public void start() {
		if (null != timer) {
			timer.cancel();
		}
		timer = new Timer();
		MetronomeTask metronomeTask = new MetronomeTask(score);
		long period = (long) (1000 * Score.noteDuration(Note.Length.QUARTER, score.tempo, score.getsTheBeat));
		System.out.println(period);
		timer.scheduleAtFixedRate(metronomeTask, 0, period);
	}

	public void stop() {
		timer.cancel();
	}
}
