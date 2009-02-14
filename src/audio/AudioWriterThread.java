package audio;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import filters.FilterFactory;

public class AudioWriterThread extends Thread {

	private HashMap<Double, AudioGenerator> toneMap;
	private ConcurrentLinkedQueue<Double> tonesToAdd;
	private ConcurrentLinkedQueue<Double> tonesToRemove;
	
	private static AudioWriterThread audioWriterThread = new AudioWriterThread();
	
	public static void startWritingAudio() {
		audioWriterThread.start();
	}
	
	public AudioWriterThread() {
		super();
		toneMap = new HashMap<Double, AudioGenerator>();
		tonesToAdd = new ConcurrentLinkedQueue<Double>();
		tonesToRemove = new ConcurrentLinkedQueue<Double>();
	}
	
	public void run() {
		System.out.println("AudioWriterThread starting...");
		while (!isInterrupted()) {
			while (!tonesToAdd.isEmpty()) {
				Double freq = tonesToAdd.remove();
				SineGenerator sineWriter = new SineGenerator(freq, 1.0, AudioSenderThread.BUFFER_LENGTH);
				FilterFactory.applyFilters(sineWriter);
				synchronized(toneMap) {
					toneMap.put(freq, sineWriter);
				}
			}
			while (!tonesToRemove.isEmpty()) {
				Double freq = tonesToRemove.remove();
				synchronized(toneMap) {
					toneMap.remove(freq);
				}
			}
			for (AudioGenerator toneWriter : toneMap.values()) {
				toneWriter.fillBuffers();
			}
		}
	}
	
	public static AudioGenerator[] getAudioGenerators() {
		synchronized(audioWriterThread.toneMap) {
			AudioGenerator[] audioGenerators = new AudioGenerator[audioWriterThread.toneMap.size()]; 
			audioWriterThread.toneMap.values().toArray(audioGenerators);
			return audioGenerators;
		}
	}
	
	public static void startTone(double freq) {
		audioWriterThread.tonesToAdd.add(freq);
	}

	public static void stopTone(double freq) {
		audioWriterThread.tonesToRemove.add(freq);
	}
	
}
