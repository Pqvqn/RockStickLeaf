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
	private String sequence; //keys pressed to choose unit
	private final int STANDBY = -1, THROW = 0, CRAFT = 1, CAPTURE = 2, PICKRECIPE = 3;
	private int purpose; //what unit is being selected for
	private Unit chosen;
	
	public Controls(Game g, Player body, int c) {
		game = g;
		player = body;
		scheme = c;
		sequence = "";
		purpose = STANDBY;
	}
	
	public void completeSequence() {
		int num = game.decode(sequence);
		sequence = "";
		if (num<0 || (purpose != CRAFT && num>=game.unitorder.size()))return;
		if(purpose == THROW && !player.has(game.unitorder.get(num)))return;
		
		switch(purpose) {
		case THROW:
			chosen = game.unitorder.get(num);
			game.playerThrows(player,chosen);
			purpose = STANDBY;
			break;
		case CRAFT:
			//new unit/recipe if needed
			if(num>=game.unitorder.size()){//make new unit
				System.out.println("NEW UNIT NAME: ");
				Unit nu = new Unit(game,game.getConsoleInput());
				game.addNewUnit(nu);
				chosen = nu;
			}else {
				chosen = game.unitorder.get(num);
			}
			
			purpose = PICKRECIPE;
			break;
		case CAPTURE:
			chosen = game.unitorder.get(num);
			player.capture(game.otherPlayer(player),chosen);
			break;
		case PICKRECIPE:
			if(num==0) {

			}else if(num==1) {
				System.out.println("RECIPE: ");
				Recipe craftr = new Recipe(game,game.getConsoleInput());
				game.recipes.add(craftr);
				num = chosen.recipes.indexOf(craftr)+2;
			}
			
			if(num>=2) {
				player.craft(chosen.recipes.get(num-2));
			}
			purpose = STANDBY;
			break;
		}
	}
	
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(player.canThrow) {
			purpose = THROW;
		}
		if(player.isTurn) {
			player.isTurn = player.canAct();
			if(key == CONTROLSCHEMES[scheme][UPKEY]) { //end turn = ^
				player.isTurn = false;
				purpose = STANDBY;
			}else if(key == CONTROLSCHEMES[scheme][DOWNKEY]) { //craft = v
				purpose = CRAFT;
			}else if(key == CONTROLSCHEMES[scheme][RIGHTKEY]) { //capture = >
				purpose = CAPTURE;
			}
		}
		if(purpose != STANDBY) {
			if(key == CONTROLSCHEMES[scheme][UPKEY]) {
				completeSequence();
			}else if(key == CONTROLSCHEMES[scheme][DOWNKEY]) {
				sequence += "v";
			}else if(key == CONTROLSCHEMES[scheme][LEFTKEY]) {
				sequence += "<";
			}else if(key == CONTROLSCHEMES[scheme][RIGHTKEY]) {
				sequence += ">";
			}
		}
	}
	
	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}

}
