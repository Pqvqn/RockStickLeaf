package ui;

import java.util.*;

import rsl.*;

public class UIMatch extends UIElement{

	private ArrayList<UIPlayer> playerUI;
	private int size; //physical size on screen
	
	public UIMatch(Game frame, int x, int y, int sz, ArrayList<Player> p) {
		super(frame,x,y);
		size = sz;
		
		playerUI = new ArrayList<UIPlayer>();
		for(int i=0; i<p.size(); i++) { //add players, alternating side that they are on
			UIPlayer np = new UIPlayer(game,xPos+((i%2==0)?-400:400),yPos,size,(i%2==0)?UIPlayer.RIGHT:UIPlayer.LEFT,p.get(i));
			parts.add(np);
			playerUI.add(np);
		}
		update();
	}
	
	@Override
	public void update() {
		for(int i=0; i<playerUI.size(); i++) {
			playerUI.get(i).update();
		}
	}
	
}
