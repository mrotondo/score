package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import transcription.Scale;
import audio.AudioSenderThread;
import audio.SineGenerator;
import audio.Tone;
import filters.FilterFactory;

@SuppressWarnings("serial")
public class ToneLinesUI extends JPanel implements MouseMotionListener, MouseListener {

	private SineGenerator sineGenerator;
	
	private Dimension screenDimension;

	private final Scale scale;
	private final Tone tonic;
	private final Tone[] scaleTones;
	private final double minFreq, maxFreq;
	
	public ToneLinesUI(Scale scale, Tone tonic) {
		this.scale = scale;
		this.tonic = tonic;
		scaleTones = scale.getTones(tonic);
		minFreq = scaleTones[0].getFrequency(); 
		maxFreq = scaleTones[scaleTones.length - 1].getFrequency(); 
			
		
		sineGenerator = new SineGenerator(tonic.getFrequency(), 1.0);
		FilterFactory.applyFilters(sineGenerator);
		AudioSenderThread.startTone(sineGenerator);
		
		screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.ORANGE);
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(2));
		for (int i = 0; i < scaleTones.length; i++) {
			double freq = scaleTones[i].getFrequency();
			int yPos = (int) ((freq - minFreq) / (maxFreq - minFreq) * this.getHeight());
			System.out.println("Painting f: " + freq + " @y: " + yPos);
			g2.drawLine(0, yPos, this.getWidth(), yPos);
		}
	}
	
	public void mouseDragged(MouseEvent e) {

	}

	public void mouseMoved(MouseEvent e) {
		sineGenerator.setFrequency((double) e.getY() / this.getHeight() * (maxFreq - minFreq) + minFreq);
		sineGenerator.setVolume(e.getX() / screenDimension.getWidth());
	}

	public void mouseClicked(MouseEvent e) {
		System.out.println("Freq: " + ((double) e.getY() / this.getHeight() * (maxFreq - minFreq) + minFreq));
		System.out.println("Y: " + e.getY());
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
