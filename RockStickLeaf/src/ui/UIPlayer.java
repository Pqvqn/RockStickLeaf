package ui;

import java.awt.*;

import rsl.*;

public class UIPlayer extends UIElement{

	private Player player;
	private int size; //physical size on screen
	public static final int RIGHT = 1, LEFT = -1;
	private int orientation;
	
	public UIPlayer(Game frame, int x, int y, int sz, int orient, Player p) {
		super(frame,x,y);
		player = p;
		size = sz;
		orient = orientation;
		parts.add(new UIText(game,xPos-player.name.length()/2*size,yPos-size*2,player.name,Color.WHITE,new Font("Arial",Font.BOLD,size*2)));
		update();
	}
	
	@Override
	public void update() {
		
	}
	
}
