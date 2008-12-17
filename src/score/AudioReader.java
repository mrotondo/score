package score;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;

public class AudioReader implements Runnable {

	final int BUFFER_SIZE_SAMPLES = 1000;
	final float SAMPLES_PER_SECOND = 44100;
	byte[] ringbuffer;
	byte[] outputBuffer;
	AudioWriter writer;
	int readPosition, prevReadPosition;
	
	
	public AudioReader(byte[] ringbuffer, AudioWriter writer) {
		this.ringbuffer = ringbuffer;
		this.writer = writer;
		this.outputBuffer = new byte[44100];
	}
	
	public void run() {
		AudioFormat format = new AudioFormat(SAMPLES_PER_SECOND, 8, 1, true, true);
		DataLine.Info clipInfo = new DataLine.Info(Clip.class, format);
		Clip clip = null;
		try {
			clip = (Clip) AudioSystem.getLine(clipInfo);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		int outputBufferPos = 0;
		System.out.println("Hey!");
		while (!Thread.interrupted()) {
			readPosition = (writer.writePosition - BUFFER_SIZE_SAMPLES) % ringbuffer.length;
			if (readPosition < 0) {
				readPosition = ringbuffer.length + readPosition;
			}
			if (readPosition != prevReadPosition) {
				outputBuffer[outputBufferPos++] = ringbuffer[readPosition];
				
				if (outputBufferPos == outputBuffer.length) {
					clip.flush();
					clip.stop();
			        clip.close();
					try {
						clip.open(format, outputBuffer, 0, outputBuffer.length);
					} catch (LineUnavailableException e) {
						e.printStackTrace();
					}
					clip.start();

					// This is probably going to start playing a new clip before the old one finishes... Needs to be true streaming
					
					outputBufferPos = 0;
				}
				
				prevReadPosition = readPosition;
			}
		}
	}
}
