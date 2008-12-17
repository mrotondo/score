package score;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

public class ScoreApp {

	ThreadedPlayer threadedPlayer;
	Score score;
	WaveformGUI waveformGUI;
	StaffGUI staffGUI;
	ToneKeyboard toneKeyboard;
	UIController uiController;
	Metronome metronome;
	
	public ScoreApp() {
		staffGUI = new StaffGUI();
		waveformGUI = new WaveformGUI(Player.getClickBytes(1.0));
		threadedPlayer = new ThreadedPlayer(waveformGUI);
		toneKeyboard = new ToneKeyboard(threadedPlayer);
		
		initTestScore();
		
		metronome = new Metronome(score, threadedPlayer);
		
		uiController = new UIController(this);
	}
	
	public void startMetronome() {
		metronome.start();
	}
	public void stopMetronome() {
		metronome.stop();
	}

	private void initTestScore() {
		score = new Score(240, Note.Length.QUARTER, 4, staffGUI);
		
		score.addNote(new Note(new Tone(50), Note.Length.QUARTER));
		score.addNote(new Note(new Tone(48), Note.Length.QUARTER));
		score.addNote(new Note(new Tone(46), Note.Length.QUARTER));
		score.addNote(new Note(new Tone(48), Note.Length.QUARTER));

		score.addNote(new Note(new Tone(50), Note.Length.QUARTER));
		score.addNote(new Note(new Tone(50), Note.Length.QUARTER));
		score.addNote(new Note(new Tone(50), Note.Length.HALF));
				
		//score.addNote(new Note(new Tone(3), Note.Length.WHOLE));
		//score.addNote(new Note(new Tone(2), Note.Length.WHOLE));
		//score.addNote(new Note(new Tone(0), Note.Length.WHOLE));
		//score.addNote(new Note(new Tone(-1), Note.Length.WHOLE));
		//score.addNote(new Note(new Tone(-2), Note.Length.WHOLE)); // Why broken??!

		//score.playNotes();
	}
	
	public static void main(String[] args) {
		
		final ScoreApp scoreApp = new ScoreApp();
		
		JFrame appFrame = new JFrame("Score");
		appFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { 
				scoreApp.threadedPlayer.stop();
				System.exit(0); }
		});
				
		Container contentPane = appFrame.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.add(scoreApp.staffGUI);
		contentPane.add(scoreApp.waveformGUI);
		appFrame.addKeyListener(scoreApp.toneKeyboard);
		appFrame.addKeyListener(scoreApp.uiController);
		appFrame.addMouseListener(scoreApp.uiController);
		appFrame.setResizable(false);
		appFrame.pack();
		appFrame.setSize(new Dimension(1400, 400));
		appFrame.setVisible(true);
	}
}

