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
	private UIMenu menu;
	
	public UIPlayer(Game frame, int x, int y, int sz, int orient, Player p) {
		super(frame,x,y);
		player = p;
		size = sz;
		orient = orientation;
		parts.add(name = new UIText(game,xPos-player.name.length()/2*size*2,yPos-size*2,player.name,Color.WHITE,new Font("Arial",Font.BOLD,size*2)));
		name.center(xPos);
		String actionstext = player.actionsTaken()+"/"+player.actionsCap()+" actions";
		parts.add(actions = new UIText(game,xPos-actionstext.length()/2*(size/2),yPos-size,actionstext,Color.WHITE,new Font("Arial",Font.PLAIN,size/2)));
		parts.add(menu = new UIMenu(game,xPos,yPos-500));
		update();
	}
	
	public void setMenu(String prompt, int[] nums, String[] strings, boolean display) {
		menu.setMenu(prompt, nums, strings, display);
	}
	
	public boolean isPlayer(Player p) {
		return player.equals(p);
	}
	
	@Override
	public void update() {
		if(player.isTurn) {
			name.setColor(Color.GREEN);
			String actionstext = player.actionsTaken()+"/"+player.actionsCap()+" actions";
			actions.setText(actionstext);
			actions.center(xPos);
		}else if(player.choice!=null) {
			name.setColor(Color.CYAN);
			actions.setText("");
		}else {
			name.setColor(Color.WHITE);
			actions.setText("");
		}
	}
	
}
