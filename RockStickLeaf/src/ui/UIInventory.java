package ui;

import java.util.*;
import java.awt.*;

import rsl.*;

public class UIInventory extends UIElement{

	private Inventory inv;
	private int size; //physical size on screen
	private ArrayList<UIUnit> unitlist;
	
	public UIInventory(Game frame, int x, int y, int sz, Player p) {
		super(frame,x,y);
		inv = p.getInventory();
		size = sz;
		unitlist = new ArrayList<UIUnit>();
		parts.add(new UIText(game,xPos,yPos-size*2,p.name,Color.WHITE,new Font("Arial",Font.BOLD,size*2)));
		update();
	}
	
	@Override
	public void update() {
		for(UIUnit ru : unitlist)parts.remove(ru); //reset unit list
		unitlist = new ArrayList<UIUnit>();
		for(int i=0; i<game.unitorder.size(); i++) { //check all items against inventory
			Unit u = game.unitorder.get(i);
			if(inv.numberOf(u)>0) { //add only if inventory contains some of the unit
				UIUnit n = new UIUnit(game, xPos, (int)(.5+yPos+unitlist.size()*size*1.5), size, u, game.encode(i), inv.numberOf(u));
				unitlist.add(n);
				parts.add(n);
			}
		}
	}
	
}
