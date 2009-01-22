package score;

import java.util.HashMap;
import java.util.LinkedList;

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
	HashMap<Double, ToneWriter> toneMap;
	LinkedList<Integer> clickPositions;
	WaveformGUI waveformGUI;
	
	public AudioWriter(WaveformGUI waveform) throws LineUnavailableException {
		writeBuffer = new byte[(int) (SAMPLES_PER_SECOND / 1000)];  // Write to the SourceDataLine every nth of a second
		format = new AudioFormat(SAMPLES_PER_SECOND, 8, 1 , true, true);
		dataLine = AudioSystem.getSourceDataLine(format);
		toneMap = new HashMap<Double, ToneWriter>();
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
		
		while(!Thread.interrupted()) {
			
			int perSignalAmplitude = 127;  // un-normalized
			
			Object[] toneArray = new Object[0];
			synchronized(toneMap) {
				toneArray = toneMap.values().toArray();
			}
			
			if (toneArray.length > 0) {
				//System.out.println("Looking at " + toneArray.length + " tones");
				// TODO: I should probably copy the toneMap here because the concurrent map can be non-empty on the line above this and empty on the line below
				perSignalAmplitude = MAX_TONE_AMPLITUDE / toneArray.length; // normalize the volume of all the tones to produce exactly max volume
				
				double amplitude = 0;
				boolean write = true;
				for (Object tone : toneArray) {
					ToneWriter toneWriter = (ToneWriter) tone;
					Double toneAmplitude = toneWriter.getAmplitude(signalCounter);
					if (toneAmplitude == null) {
						write = false;
						break;
					}
					amplitude += toneAmplitude * perSignalAmplitude;
				}
				if (write) {
					writeBuffer[writePosition++] = (byte) amplitude;
					signalCounter++;
				}
				
				if (writePosition == writeBuffer.length) {
					dataLine.write(writeBuffer, 0, writePosition);
					writePosition = 0;
				}
				
			} else {
				if (writePosition != 0) {
					dataLine.write(writeBuffer, 0, writePosition);
					writePosition = 0;
				}
			}
			
			
		
			/*
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
			}*/

			/*
			if (advanceWritePosition) {
				//writePosition++;
				i++;
				advanceWritePosition = false;
			}*/
			
			//if (writePosition == writeBuffer.length) { // || sendData) {
			//System.out.println(writePosition);
			//millisSent += writePosition * TIME CONVERSION;
			
			//waveformGUI.setWaveform(writeBuffer);
			//writePosition = 0;
			//sendData = false;
			//}

			/*
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		}
		dataLine.stop();
		dataLine.flush();
	}
	
	public void startTone(double freq) {
		synchronized(toneMap) {
			toneMap.put(freq, new SineWriter(freq, 1.0, SAMPLES_PER_SECOND));
		}
	}

	public void stopTone(double freq) {
		synchronized(toneMap) {
			toneMap.remove(freq);
		}
	}
	
	public void playClick() {
		synchronized(clickPositions) {
			clickPositions.add(0);
		}
	}
}