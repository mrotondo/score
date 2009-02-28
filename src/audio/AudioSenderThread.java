package audio;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import filters.FilterFactory;

public class AudioSenderThread extends Thread {

	public static final int SAMPLES_PER_SECOND = 44100;
	public static final int BUFFER_LENGTH = SAMPLES_PER_SECOND / 400;
	static final int MAX_TONE_AMPLITUDE = 127;
	byte[] writeBuffer;
	int writePosition, signalCounter;
	AudioFormat format;
	SourceDataLine dataLine;
	
	private ConcurrentLinkedQueue<AudioGenerator> toneQueue;
	private ConcurrentLinkedQueue<Double> tonesToAdd;
	private ConcurrentLinkedQueue<Double> tonesToRemove;
	private ConcurrentLinkedQueue<AudioGenerator> generatorsToAdd;
	private ConcurrentLinkedQueue<AudioGenerator> generatorsToRemove;
	
	private static AudioSenderThread audioSenderThread;
	
	public static void startSendingAudio() {
		try {
			audioSenderThread = new AudioSenderThread();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		audioSenderThread.start();
	}
	
	public AudioSenderThread() throws LineUnavailableException {
		writeBuffer = new byte[BUFFER_LENGTH];  // Write to the SourceDataLine every nth of a second
		format = new AudioFormat((float) SAMPLES_PER_SECOND, 8, 1 , true, true);
		dataLine = AudioSystem.getSourceDataLine(format);
		toneQueue = new ConcurrentLinkedQueue<AudioGenerator>();
		tonesToAdd = new ConcurrentLinkedQueue<Double>();
		tonesToRemove = new ConcurrentLinkedQueue<Double>();
		generatorsToAdd = new ConcurrentLinkedQueue<AudioGenerator>();
		generatorsToRemove = new ConcurrentLinkedQueue<AudioGenerator>();
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
			
			while (!tonesToAdd.isEmpty()) {
				Double freq = tonesToAdd.remove();
				SineGenerator sineGenerator = new SineGenerator(freq, 1.0);
				FilterFactory.applyFilters(sineGenerator);
				
				sineGenerator.start();
				toneQueue.add(sineGenerator);
			}
			while (!generatorsToAdd.isEmpty()) {
				AudioGenerator generator = generatorsToAdd.remove();
				generator.start();
				toneQueue.add(generator);
			}
			while (!tonesToRemove.isEmpty()) {
				Double freq = tonesToRemove.remove();
				Iterator<AudioGenerator> toneIterator = toneQueue.iterator();
				while (toneIterator.hasNext()) {
					SineGenerator sine = (SineGenerator) toneIterator.next();
					if (sine.getFrequency() == freq) {
						toneIterator.remove();
					}
				}
			}
			while (!generatorsToRemove.isEmpty()) {
				AudioGenerator generator = generatorsToAdd.remove();
				toneQueue.remove(generator);
			}
			
			int perSignalAmplitude = 127;  // un-normalized
			if (!toneQueue.isEmpty()) {
				perSignalAmplitude = MAX_TONE_AMPLITUDE / toneQueue.size(); // normalize the volume of all the tones to produce exactly max volume

				// TODO: Possibly do something other than send all or send none?
				boolean shouldSendData = true;
				for (AudioGenerator audioGenerator : toneQueue) {
					if (!audioGenerator.shouldSendSamples(BUFFER_LENGTH)) {
						shouldSendData = false;
						break;
					}
				}
				for(int i = 0; i < BUFFER_LENGTH; i++) { writeBuffer[i] = 0; }
				if (shouldSendData) {
					for (AudioGenerator audioGenerator : toneQueue) {
						double[] toneBuffer = audioGenerator.getSamples(BUFFER_LENGTH); 
						for (int i = 0; i < BUFFER_LENGTH; i++) {
							writeBuffer[i] += (byte) (toneBuffer[i] * perSignalAmplitude);
						}
					}
					dataLine.write(writeBuffer, 0, BUFFER_LENGTH);
				}
			}
			
			/*try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
		}
		dataLine.stop();
		dataLine.flush();
	}
	
	
	public static void startTone(double freq) {
		audioSenderThread.tonesToAdd.add(freq);
	}
	public static void startTone(AudioGenerator audioGenerator) {
		audioSenderThread.generatorsToAdd.add(audioGenerator);
	}

	public static void stopTone(double freq) {
		audioSenderThread.tonesToRemove.add(freq);
	}
	public static void stopTone(AudioGenerator audioGenerator) {
		audioSenderThread.generatorsToRemove.add(audioGenerator);
	}

}