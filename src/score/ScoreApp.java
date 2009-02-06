package score;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import transcription.Note;
import transcription.Score;
import ui.StaffGUI;
import ui.ToneKeyboard;
import ui.UIController;
import ui.WaveformGUI;
import audio.FilterFactory;
import audio.LogFilter;
import audio.Metronome;
import audio.NoOpFilter;
import audio.Player;
import audio.ThreadedPlayer;
import audio.Tone;

public class ScoreApp {

	Score score;
	WaveformGUI waveformGUI;
	StaffGUI staffGUI;
	ToneKeyboard toneKeyboard;
	UIController uiController;
	Metronome metronome;
	
	public ScoreApp() {
		FilterFactory.addFilterPrototype(new NoOpFilter());
		//FilterFactory.addFilterPrototype(new NoiseFilter());
		FilterFactory.addFilterPrototype(new LogFilter(10000));
		
		staffGUI = new StaffGUI();
		waveformGUI = new WaveformGUI(Player.getToneBytes(new Tone(440), 1.0));
		toneKeyboard = new ToneKeyboard();
		
		initTestScore();
		
		metronome = new Metronome(score);
		
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
		score.play();
	}
	
	public static void main(String[] args) {
		
		final ScoreApp scoreApp = new ScoreApp();
		
		JFrame appFrame = new JFrame("Score");
		appFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { 
				ThreadedPlayer.instance.stop();
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

