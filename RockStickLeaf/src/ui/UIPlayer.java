package ui;

import java.awt.*;

import rsl.*;

public class UIPlayer extends UIElement{

	private Player player;
	private int size; //physical size on screen
	public static final int RIGHT = 1, LEFT = -1;
	private int orientation;
	private UIText name;
	private UIText actions;
	
	public UIPlayer(Game frame, int x, int y, int sz, int orient, Player p) {
		super(frame,x,y);
		player = p;
		size = sz;
		orient = orientation;
		parts.add(name = new UIText(game,xPos-player.name.length()/2*size*2,yPos-size*2,player.name,Color.WHITE,new Font("Arial",Font.BOLD,size*2)));
		String actionstext = player.actionsTaken()+"/"+player.actionsCap()+" actions";
		parts.add(actions = new UIText(game,xPos-actionstext.length()/2*(size/2),yPos-size,actionstext,Color.WHITE,new Font("Arial",Font.PLAIN,size/2)));
		update();
	}
	
	@Override
	public void update() {
		if(player.isTurn) {
			name.setColor(Color.GREEN);
			String actionstext = player.actionsTaken()+"/"+player.actionsCap()+" actions";
			actions.setText(actionstext);
			actions.setxPos(xPos-actionstext.length()/2*(size/2));
		}else {
			name.setColor(Color.WHITE);
			actions.setText("");
		}
	}
	
}
