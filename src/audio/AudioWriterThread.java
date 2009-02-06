package audio;

import java.util.HashMap;
import java.util.LinkedList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioWriterThread extends Thread {

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
	
	public AudioWriterThread() throws LineUnavailableException {
		writeBuffer = new byte[(int) (SAMPLES_PER_SECOND / 400)];  // Write to the SourceDataLine every nth of a second
		format = new AudioFormat(SAMPLES_PER_SECOND, 8, 1 , true, true);
		dataLine = AudioSystem.getSourceDataLine(format);
		toneMap = new HashMap<Double, ToneWriter>();
		clickPositions = new LinkedList<Integer>();
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
		
		while(!isInterrupted()) {
			
			int perSignalAmplitude = 127;  // un-normalized
			
			Object[] toneArray = new Object[0];
			synchronized(toneMap) {
				toneArray = toneMap.values().toArray();
			}
			
			if (toneArray.length > 0) {
				perSignalAmplitude = MAX_TONE_AMPLITUDE / toneArray.length; // normalize the volume of all the tones to produce exactly max volume
				
				double amplitude = 0;
				boolean send = true;
				double[][] signals = new double[toneArray.length][writeBuffer.length];

				// TODO: Instead of checking to see if a tone can write n, writing n, and then re-writing n, might just check to see if it can write n
				//       and then write those during one pass for all tones that can do so (or just fail if any can't write n)
				for (int toneIndex = 0; toneIndex < toneArray.length; toneIndex++) {
					ToneWriter toneWriter = (ToneWriter) toneArray[toneIndex];
					double[] toneAmplitudes = toneWriter.getAmplitudes(signalCounter, writeBuffer.length);
					
					if (toneAmplitudes == null) {
						send = false;
						break;
					}
					signals[toneIndex] = toneAmplitudes;
				}
				
				
				if (send) {
					for (writePosition = 0; writePosition < writeBuffer.length; writePosition++, signalCounter++) {
						amplitude = 0;
						for (int toneIndex = 0; toneIndex < toneArray.length; toneIndex++) {
							amplitude += signals[toneIndex][writePosition] * perSignalAmplitude;
							writeBuffer[writePosition] = (byte) amplitude;
						}
					}
					
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
		synchronized(toneMap) {
			SineWriter sineWriter = new SineWriter(freq, 1.0);
			FilterFactory.applyFilters(sineWriter);
			toneMap.put(freq, sineWriter);
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