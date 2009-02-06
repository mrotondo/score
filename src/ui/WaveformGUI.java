package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class WaveformGUI extends JPanel {

	byte[] waveform;
	int xPadding = 10; int yPadding = 10;
	public WaveformGUI(byte[] waveform) {
		super();
		this.waveform = waveform;
	}
	
	public void setWaveform(byte[] waveform) {
		//this.waveform = waveform;
		byte[] newWaveform = new byte[this.waveform.length + waveform.length];
		for (int i = 0; i < this.waveform.length; i++) {
			newWaveform[i] = this.waveform[i];
		}
		for (int i = this.waveform.length; i < newWaveform.length; i++) {
			newWaveform[i] = waveform[i - this.waveform.length];
		}
		this.waveform = newWaveform;
		this.repaint();
	}

	// Adding a comment to test git. Now changing my comment.
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(Color.BLACK);
		double xScale = (getWidth() - xPadding * 2.0) / waveform.length;
		double yScale = (getHeight() - yPadding * 2.0) / 2.0 / Byte.MAX_VALUE;
		int yCenter = yPadding + (getHeight() - yPadding * 2) / 2;
		for (int i = 1; i < waveform.length; i++) {
			int prevAmplitude = (int) (waveform[i-1] * yScale);
			int amplitude = (int) (waveform[i] * yScale);
			g2.drawLine((int) (xPadding + (i - 1)  * xScale), yCenter + prevAmplitude, (int) (xPadding + i * xScale), yCenter + amplitude);
		}
	}
	
}
