package ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import audio.AudioWriterThread;
import audio.Tone;


public class ToneKeyboard implements KeyListener {

	final HashMap<Integer, Integer> keyToneMap = new HashMap<Integer, Integer>();
	final ConcurrentHashMap<Integer, Boolean> keyPressedMap = new ConcurrentHashMap<Integer, Boolean>();
	
	public ToneKeyboard() {
		super();
		initKeyToneMap();
	}
	
	// TODO(mrotondo): Figure out a better way to create these maps
	public void initKeyToneMap() {
		keyToneMap.put(65, 51);
		keyPressedMap.put(65, false);
		keyToneMap.put(83, 53);
		keyPressedMap.put(83, false);
		keyToneMap.put(68, 55);
		keyPressedMap.put(68, false);
		keyToneMap.put(70, 56);
		keyPressedMap.put(70, false);
		keyToneMap.put(71, 58);
		keyPressedMap.put(71, false);
		keyToneMap.put(72, 60);
		keyPressedMap.put(72, false);
		keyToneMap.put(74, 62);
		keyPressedMap.put(74, false);
		keyToneMap.put(75, 63);
		keyPressedMap.put(75, false);
	}
	
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyToneMap.containsKey(keyCode) && !keyPressedMap.get(keyCode)) {
			Tone tone = new Tone(keyToneMap.get(keyCode));
			//ThreadedPlayer.instance.startTone(tone.getFrequency());
			AudioWriterThread.startTone(tone.getFrequency());
			keyPressedMap.put(keyCode, true);
		}
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyToneMap.containsKey(keyCode)) {
			Tone tone = new Tone(keyToneMap.get(keyCode));
			//ThreadedPlayer.instance.stopTone(tone.getFrequency());
			AudioWriterThread.stopTone(tone.getFrequency());
			keyPressedMap.put(keyCode, false);
		}
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
