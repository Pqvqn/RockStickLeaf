package ui;

import java.awt.Color;
import java.awt.Font;
import java.util.*;

import rsl.*;

public class UIMatch extends UIElement{

	private ArrayList<UIPlayer> playerUI;
	private int size; //physical size on screen
	private UIText countdown;
	private UIText notif;
	
	public UIMatch(Game frame, int x, int y, int sz, ArrayList<Player> p) {
		super(frame,x,y);
		size = sz;
		parts.add(countdown = new UIText(game,xPos,yPos-size*2,"",Color.WHITE,new Font("Arial",Font.BOLD,size*4)));
		parts.add(notif = new UIText(game,xPos,yPos-size*4,"",Color.WHITE,new Font("Arial",Font.BOLD,size/2)));
		playerUI = new ArrayList<UIPlayer>();
		for(int i=0; i<p.size(); i++) { //add players, alternating side that they are on
			UIPlayer np = new UIPlayer(game,xPos+((i%2==0)?-400:400),yPos,size,(i%2==0)?UIPlayer.RIGHT:UIPlayer.LEFT,p.get(i));
			parts.add(np);
			playerUI.add(np);
		}
		update();
	}
	
	public void count(int c,boolean display) {
		if(!display) {
			countdown.setText("");
		}else {
			countdown.setText(c+"");
			countdown.center(xPos);
			countdown.setColor(new Color(255,Math.min((255/4)*c,255),0));
		}
		game.draw.repaint();
	}
	public void dispNotif(String n) {
		notif.setText(n);
		notif.center(game.X_RESOL/2);
	}
	
	
	public void setMenu(Player p, String prompt, int[] nums, String[] strings, boolean display) {
		for(int i=0; i<playerUI.size(); i++) {
			if(playerUI.get(i).isPlayer(p)) {
				playerUI.get(i).setMenu(prompt, nums, strings, display);
			}
		}
		if(display)game.draw.repaint();
	}
	
	@Override
	public void update() {
		for(int i=0; i<playerUI.size(); i++) {
			playerUI.get(i).update();
		}
	}
	
}
