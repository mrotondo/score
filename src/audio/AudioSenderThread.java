package audio;

import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioSenderThread extends Thread {

	public static final float SAMPLES_PER_SECOND = 44100;
	public static final int BUFFER_LENGTH = (int) SAMPLES_PER_SECOND / 400;
	//static final int MAX_TONE_AMPLITUDE = 62;
	byte[] writeBuffer;
	int writePosition, signalCounter;
	AudioFormat format;
	SourceDataLine dataLine;
	HashMap<Double, AudioGenerator> toneMap;
	
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
		format = new AudioFormat(SAMPLES_PER_SECOND, 8, 1 , true, true);
		dataLine = AudioSystem.getSourceDataLine(format);
		toneMap = new HashMap<Double, AudioGenerator>();
	}
	
	public void run() {
		System.out.println("AudioSenderThread starting...");
		AudioWriterThread.startWritingAudio();
		
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
			
			AudioGenerator[] toneArray = AudioWriterThread.getAudioGenerators();
			if (toneArray.length > 0) {
				//perSignalAmplitude = MAX_TONE_AMPLITUDE / toneArray.length; // normalize the volume of all the tones to produce exactly max volume
				
				double amplitude = 0;
				boolean send = true;
				double[][] signals = new double[toneArray.length][writeBuffer.length];

				// TODO: Instead of checking to see if a tone can write n, writing n, and then re-writing n, might just check to see if it can write n
				//       and then write those during one pass for all tones that can do so (or just fail if any can't write n)
				for (int toneIndex = 0; toneIndex < toneArray.length; toneIndex++) {
					AudioGenerator toneWriter = toneArray[toneIndex];
					double[] toneAmplitudes = toneWriter.getBuffer();
					
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
							amplitude += signals[toneIndex][writePosition] * 127; //* perSignalAmplitude;
							writeBuffer[writePosition] = (byte) amplitude;
						}
					}
					
					dataLine.write(writeBuffer, 0, writePosition);
					writePosition = 0;
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
}