package score;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.ConcurrentHashMap;

public class UIController implements KeyListener, MouseListener {

	final ConcurrentHashMap<Integer, Boolean> keyPressedMap = new ConcurrentHashMap<Integer, Boolean>();
	ScoreApp scoreApp;
	
	public void initKeyPressedMap() {
		keyPressedMap.put(90, false);		
		keyPressedMap.put(88, false);
		keyPressedMap.put(67, false);
	}
	
	public UIController(ScoreApp scoreApp) {
		super();
		
		initKeyPressedMap();
		this.scoreApp = scoreApp;
	}
	
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch(e.getKeyCode()) {
		case 88:
			scoreApp.startMetronome();
			break;
		case 67:
			scoreApp.stopMetronome();
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
