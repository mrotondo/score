package score;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import transcription.Note;
import transcription.Scale;
import transcription.Score;
import ui.StaffGUI;
import ui.ToneKeyboard;
import ui.ToneLinesUI;
import ui.UIController;
import ui.WaveformGUI;
import audio.AudioSenderThread;
import audio.Metronome;
import audio.SineGenerator;
import audio.Tone;
import filters.FilterFactory;
import filters.LogFilter;

public class ScoreApp {

	Score score;
	WaveformGUI waveformGUI;
	StaffGUI staffGUI;
	ToneKeyboard toneKeyboard;
	UIController uiController;
	ToneLinesUI toneLines;
	Metronome metronome;
	
	public ScoreApp() {
		AudioSenderThread.startSendingAudio();
		
		//FilterFactory.addFilterPrototype(new NoOpFilter());
		//FilterFactory.addFilterPrototype(new NoiseFilter());
		FilterFactory.addFilterPrototype(new LogFilter(10000));
		
		staffGUI = new StaffGUI();
		
		SineGenerator sine = new SineGenerator(440, 1.0);
		FilterFactory.applyFilters(sine);
		double[] buffer = sine.getSamples(AudioSenderThread.SAMPLES_PER_SECOND);
		System.out.println(buffer[buffer.length / 4]);
		byte[] bytes = new byte[buffer.length];
		for (int i = 0; i < buffer.length; i++) {
			bytes[i] = (byte) (buffer[i] * 127);
		}
		
		//byte[] bytes = new byte[1];
		waveformGUI = new WaveformGUI(bytes);
		toneLines = new ToneLinesUI(Scale.melodicMinorScale, new Tone(51));
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
		//score.play();
	}
	
	public static void main(String[] args) {
		
		final ScoreApp scoreApp = new ScoreApp();
		
		JFrame appFrame = new JFrame("Score");
		appFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { 
				//ThreadedPlayer.instance.stop();
				System.exit(0); }
		});
				
		Container contentPane = appFrame.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		scoreApp.staffGUI.setPreferredSize(new Dimension(1400, 100));
		scoreApp.waveformGUI.setPreferredSize(new Dimension(1400, 100));
		scoreApp.toneLines.setPreferredSize(new Dimension(1400, 600));
		contentPane.add(scoreApp.staffGUI);
		contentPane.add(scoreApp.waveformGUI);
		contentPane.add(scoreApp.toneLines);
		appFrame.addKeyListener(scoreApp.toneKeyboard);
		appFrame.addKeyListener(scoreApp.uiController);
		scoreApp.toneLines.addMouseListener(scoreApp.toneLines);
		scoreApp.toneLines.addMouseMotionListener(scoreApp.toneLines);
		//appFrame.setResizable(false);
		//appFrame.pack();
		//appFrame.setSize(new Dimension(1400, 800));
		appFrame.setUndecorated(true);
		appFrame.setVisible(true);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		gd.setFullScreenWindow(appFrame);
		
	}
}

