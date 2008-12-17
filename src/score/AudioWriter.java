package score;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioWriter implements Runnable {

	static final float SAMPLES_PER_SECOND = 44100;
	static final int MAX_TONE_AMPLITUDE = 62;
	static final int MAX_CLICK_AMPLITUDE = 80;
	static final int CLICK_LENGTH_FRAMES = 4410;
	byte[] writeBuffer;
	int writePosition, signalCounter;
	AudioFormat format;
	SourceDataLine dataLine;
	HashSet<Double> toneSet;
	LinkedList<Integer> clickPositions;
	WaveformGUI waveformGUI;
	
	public AudioWriter(WaveformGUI waveform) throws LineUnavailableException {
		writeBuffer = new byte[(int) (SAMPLES_PER_SECOND / 1000 * 5)];  // Write to the SourceDataLine every nth of a second
		System.out.println(writeBuffer.length);
		format = new AudioFormat(SAMPLES_PER_SECOND, 8, 1 , true, true);
		dataLine = AudioSystem.getSourceDataLine(format);
		toneSet = new HashSet<Double>();
		clickPositions = new LinkedList<Integer>();
		this.waveformGUI = waveform;
	}
	
	public void run() {
		try {
			dataLine.open(format);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		dataLine.start();

		writePosition = 0; // iterates through writeBuffer, looping
		signalCounter = 0; // counts up from 0 over time, gives position in the sine wave
		
		long framesSent = 0; // number of frames sent since keys were pressed
		long framesHeld = 0; // amount of time that keys have been held, in frames
		
		boolean keyPreviouslyPressed = false; // were keys pressed down on the previous loop iteration
		boolean sendData = false; // used to decide whether to send a partially full writeBuffer (true if keys were released mid-buffer)
		boolean advanceWritePosition = false;
		
		while(!Thread.interrupted()) {
			writeBuffer[writePosition] = 0;

			int perSignalAmplitude = 127;
			for (int i = 0; i < writeBuffer.length; i++) {
				synchronized(toneSet) {
					
					if (!toneSet.isEmpty()) {
						perSignalAmplitude = MAX_TONE_AMPLITUDE / toneSet.size(); // normalize the volume of all the tones to produce exactly max volume
					}
					
					if (framesSent < framesHeld - writeBuffer.length) {
						for (Double freq : toneSet) {
							// find the value of the signals that are being produced by the currerently pressed keys
							byte amplitude = (byte) (Math.sin(((signalCounter * 2 * Math.PI) / SAMPLES_PER_SECOND) * freq) * perSignalAmplitude);
							//writeBuffer[writePosition] += amplitude;
							writeBuffer[i] += amplitude;
						}
						if (!toneSet.isEmpty()) {
							signalCounter++;
							advanceWritePosition = true;
						}
					}
					
					if (!toneSet.isEmpty()) {
						if (!keyPreviouslyPressed) {
							// keys were just pressed, start counting how long they've been held for
							framesHeld = 0;
							framesSent = 0;
						}
						framesHeld++;
	
						keyPreviouslyPressed = true;	
					
					} else if (keyPreviouslyPressed) {
						keyPreviouslyPressed = false;
						sendData = true;
					}
				}
				
				
				synchronized(clickPositions) {
					int numClicks = clickPositions.size();
					for (int c = 0; c < numClicks; c++ ) {
						int clickPosition = clickPositions.removeFirst();
						byte amplitude = (byte) (Math.sin(((clickPosition * 2 * Math.PI) / SAMPLES_PER_SECOND) * 500.0) * MAX_CLICK_AMPLITUDE);
						amplitude = (byte) (amplitude * (1.0 / Math.pow((double) clickPosition / CLICK_LENGTH_FRAMES + 1, 30)));
						
						//writeBuffer[writePosition] += amplitude;
						writeBuffer[i] += amplitude;
						advanceWritePosition = true;
						if (clickPosition < CLICK_LENGTH_FRAMES) {
							clickPositions.addLast(clickPosition + 1);
						}
					}
				}
				
				if (advanceWritePosition) {
					//writePosition++;
					i++;
					advanceWritePosition = false;
				}
			}
			
			dataLine.write(writeBuffer, 0, writePosition);
			
			if (writePosition == writeBuffer.length) { // || sendData) {
				dataLine.write(writeBuffer, 0, writePosition);
				framesSent += writePosition;
				
				//waveformGUI.setWaveform(writeBuffer);
				writePosition = 0;
				sendData = false;
			}

			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dataLine.stop();
		dataLine.flush();
	}
	
	public void startTone(double freq) {
		//dataLine.flush();
		synchronized(toneSet) {
			toneSet.add(freq);
		}
	}

	public void stopTone(double freq) {
		//dataLine.flush();
		synchronized(toneSet) {
			toneSet.remove(freq);
		}
	}
	
	public void playClick() {
		synchronized(clickPositions) {
			clickPositions.add(0);
		}
	}
}