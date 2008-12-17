package score;

import java.util.Timer;
import java.util.TimerTask;

public class Metronome {

	Score score;
	ThreadedPlayer threadedPlayer;
	Timer timer;
	
	public Metronome(Score score, ThreadedPlayer threadedPlayer) {
		this.score = score;
		this.threadedPlayer = threadedPlayer;
	}
	
	private class MetronomeTask extends TimerTask {

		Score score;
		ThreadedPlayer threadedPlayer;
		
		public MetronomeTask(Score score, ThreadedPlayer threadedPlayer) {
			this.score = score;
			this.threadedPlayer = threadedPlayer;
		}
		
		public void run() {
			threadedPlayer.playClick();
		}
		
	}
	
	public void start() {
		if (null != timer) {
			timer.cancel();
		}
		timer = new Timer();
		MetronomeTask metronomeTask = new MetronomeTask(score, threadedPlayer);
		long period = (long) (1000 * Score.noteDuration(Note.Length.QUARTER, score.tempo, score.getsTheBeat));
		System.out.println(period);
		timer.scheduleAtFixedRate(metronomeTask, 0, period);
	}

	public void stop() {
		timer.cancel();
	}
}
