package rsl;

import java.awt.event.*;

public class Controls implements KeyListener{

	private Game game;
	private Player player;
	//controls for each player added
	private final int[][] CONTROLSCHEMES = {{KeyEvent.VK_W,KeyEvent.VK_S,KeyEvent.VK_A,KeyEvent.VK_D},
			{KeyEvent.VK_UP,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT},
			{KeyEvent.VK_I,KeyEvent.VK_K,KeyEvent.VK_J,KeyEvent.VK_L},
			{KeyEvent.VK_NUMPAD8,KeyEvent.VK_NUMPAD5,KeyEvent.VK_NUMPAD4,KeyEvent.VK_NUMPAD6}};
	private final int UPKEY = 0, RIGHTKEY = 3, DOWNKEY = 1, LEFTKEY = 2;
	private int scheme;
	private String choosingsequence;
	public String sequence;
	public boolean awaiting;
	
	public Controls(Game g, Player body, int c) {
		game = g;
		player = body;
		scheme = c;
		choosingsequence = "";
		sequence = null;
		awaiting = false;
	}
	
	public void startSequence() {
		sequence = null;
		awaiting = true;
	}
	public void endSequence() {
		sequence = null;
		awaiting = false;
	}
	
	
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		game.draw.match.dispNotif("");
		if(!awaiting) {game.draw.repaint();return;} //if player doesn't need to input, don't get a sequence
		
		if(key == CONTROLSCHEMES[scheme][UPKEY]) {
			if(player.isTurn) { //build sequence for turn actions
				sequence = choosingsequence;
				choosingsequence = "";
				player.sendSequence(sequence);
			}else if(player.choice == null){ //build sequence to be thrown
				if(key == CONTROLSCHEMES[scheme][UPKEY]) {
					player.makeChoice(choosingsequence);
					choosingsequence = "";
				}
			}
		}else if(key == CONTROLSCHEMES[scheme][DOWNKEY]) {
			choosingsequence += "v";
		}else if(key == CONTROLSCHEMES[scheme][LEFTKEY]) {
			choosingsequence += "<";
		}else if(key == CONTROLSCHEMES[scheme][RIGHTKEY]) {
			choosingsequence += ">";
		}
		game.draw.repaint();
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}

}