package rsl;

import java.awt.event.*;

public class Controls implements KeyListener{

	private Game game;
	private Player player;
	//controls for each player added
	private final int[][] CONTROLSCHEMES = {{KeyEvent.VK_UP,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT},
			{KeyEvent.VK_W,KeyEvent.VK_S,KeyEvent.VK_A,KeyEvent.VK_D},
			{KeyEvent.VK_I,KeyEvent.VK_K,KeyEvent.VK_J,KeyEvent.VK_L},
			{KeyEvent.VK_NUMPAD8,KeyEvent.VK_NUMPAD5,KeyEvent.VK_NUMPAD4,KeyEvent.VK_NUMPAD6}};
	private final int UPKEY = 0, RIGHTKEY = 3, DOWNKEY = 1, LEFTKEY = 2;
	private int scheme;
	
	public Controls(Game g, Player body, int c) {
		game = g;
		player = body;
		scheme = c;
	}
	
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(player.choice == null) {
			if(key == CONTROLSCHEMES[scheme][UPKEY]) {
				player.completeDirKeys();
			}else if(key == CONTROLSCHEMES[scheme][DOWNKEY]) {
				player.addDirKey("v");
			}else if(key == CONTROLSCHEMES[scheme][LEFTKEY]) {
				player.addDirKey("<");
			}else if(key == CONTROLSCHEMES[scheme][RIGHTKEY]) {
				player.addDirKey(">");
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}

}
