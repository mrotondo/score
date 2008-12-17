package score;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ToneKeyboard implements KeyListener {

	final HashMap<Integer, Integer> keyToneMap = new HashMap<Integer, Integer>();
	final ConcurrentHashMap<Integer, Boolean> keyPressedMap = new ConcurrentHashMap<Integer, Boolean>();
	ThreadedPlayer threadedPlayer;
	
	public ToneKeyboard(ThreadedPlayer threadedPlayer) {
		super();
		
		initKeyToneMap();
		this.threadedPlayer = threadedPlayer;
	}
	
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
			int pitch = keyToneMap.get(keyCode);			
			threadedPlayer.startTone(new Tone(pitch).getFrequency());
			keyPressedMap.put(keyCode, true);
		}
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyToneMap.containsKey(keyCode)) {
			threadedPlayer.stopTone(new Tone(keyToneMap.get(keyCode)).getFrequency());
			keyPressedMap.put(keyCode, false);
		}
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
