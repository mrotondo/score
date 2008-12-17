package score;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class StaffGUI extends JPanel {

	enum Symbol {
		TREBLE_CLEF("A"),
		BASS_CLEF("B"),
		FLAT("C"),
		SHARP("D"),
		NATURAL("E"),
		WHOLE_NOTE("F"),
		HALF_NOTE_UP("G"),
		HALF_NOTE_DOWN("H"),
		QUARTER_NOTE_UP("I"),
		QUARTER_NOTE_DOWN("J"),
		EIGHTH_NOTE_UP("K"),
		EIGHT_NOTE_DOWN("L"),
		SIXTEENTH_NOTE_UP("M"),
		SIXTEENTH_NOTE_DOWN("N"),
		THIRTYSECOND_NOTE_UP("O"),
		THIRTYSECOND_NOTE_DOWN("P"),
		WHOLE_REST("a"),
		HALF_REST("b"),
		QUARTER_REST("c"),
		EIGTH_REST("d"),
		SIXTEENTH_REST("e"),
		THIRTYSECOND_REST("f");
		
		public String character;
		private Symbol(String character) {
			this.character = character;
		}
	}
	
	final int Y_OFFSET = 40;
	final int LINE_HEIGHT = 10;
	final int X_NOTE_DISTANCE = 30;
	final int X_OFFSET = 40;
	final HashMap<Note.Length, Symbol> noteSymbolMap = new HashMap<Note.Length, Symbol>();
	Font symbolFont, numberFont;
	Score score;
	
	public StaffGUI() {
		super();
		symbolFont = new Font("Easy", Font.PLAIN, 40);
		numberFont = new Font("Easy", Font.PLAIN, 20);
		initSymbolMap();
	}
	
	public void setScore(Score score) {
		this.score = score;
	}
	
	public void initSymbolMap() {
		noteSymbolMap.put(Note.Length.WHOLE, Symbol.WHOLE_NOTE);
		noteSymbolMap.put(Note.Length.HALF, Symbol.HALF_NOTE_UP);
		noteSymbolMap.put(Note.Length.QUARTER, Symbol.QUARTER_NOTE_UP);
		noteSymbolMap.put(Note.Length.EIGHTH, Symbol.EIGHTH_NOTE_UP);
		noteSymbolMap.put(Note.Length.SIXTEENTH, Symbol.SIXTEENTH_NOTE_UP);
		noteSymbolMap.put(Note.Length.THIRTYSECOND, Symbol.THIRTYSECOND_NOTE_UP);		
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.BLACK);
		for (int i = 0; i < 5; i++) {
			int yPos = i * LINE_HEIGHT + Y_OFFSET;
			g2.drawLine(0, yPos, getWidth(), yPos);
		}
		g2.setFont(symbolFont);
		drawClef(g2, Symbol.TREBLE_CLEF);
		drawTimeSignature(g2);
		drawNotes(g2);
	}
	
	public void drawClef(Graphics2D g2, Symbol clefSymbol) {
		if (clefSymbol == Symbol.TREBLE_CLEF) {
			g2.drawString(clefSymbol.character, 10, Y_OFFSET + LINE_HEIGHT * 3);
		} else if (clefSymbol == Symbol.BASS_CLEF) {
			g2.drawString(clefSymbol.character, 10, Y_OFFSET + LINE_HEIGHT * 1);
		}
	}
	
	public void drawTimeSignature(Graphics2D g2) {
		int[] timeSignature = score.getTimeSignature();
		g2.drawString("" + timeSignature[0], 40, Y_OFFSET + LINE_HEIGHT * 1);
		g2.drawString("" + timeSignature[1], 40, Y_OFFSET + LINE_HEIGHT * 3);
	}
	
	public void drawNotes(Graphics2D g2) {
		LinkedList<LinkedList<Note>> measures = score.getMeasures();
		int xPos = X_OFFSET;
		for (LinkedList<Note> measure : measures) {
			for (Note note : measure) {
				drawNote(g2, xPos += X_NOTE_DISTANCE, note);
			}
			xPos += X_NOTE_DISTANCE;
			g2.drawLine(xPos, Y_OFFSET, xPos, Y_OFFSET + LINE_HEIGHT * 4);
			// Account for the fact that symbols have their origin at the left edge, and so there is less visible space between them than after measure bars
			xPos -= (int)symbolFont.getStringBounds(Symbol.HALF_NOTE_UP.character, g2.getFontRenderContext()).getWidth();
		}
	}
	
	public void drawNote(Graphics2D g2, int xPos, Note note) {
		Symbol noteSymbol = noteSymbolMap.get(note.duration);
		// THIS IS TREBLE-CLEF ONLY
		// 33 (A0 is 33 lines & spaces from the top F line) - 7 lines & spaces per octave - a line/space per letter tone in the scale
		int distanceFromTopLine = 33 - (note.tone.getOctave() * 7 + note.tone.getNameObject().ordinal());
		// LINE_HEIGHT / 2 because LINE_HEIGHT represents a jump of a line and a space
		g2.drawString(noteSymbol.character, xPos, (int) (Y_OFFSET + (LINE_HEIGHT / 2.0) * distanceFromTopLine));
	}
}