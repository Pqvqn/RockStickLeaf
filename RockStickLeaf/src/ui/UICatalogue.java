package ui;

import java.util.*;

import rsl.*;

public class UICatalogue extends UIElement{

	private int size; //physical size on screen
	private int column_size; //how many units fit in one column
	private ArrayList<UIUnit> unitlist;
	
	public UICatalogue(Game frame, int x, int y, int sz, int clmsz) {
		super(frame,x,y);
		size = sz;
		column_size = clmsz;
		unitlist = new ArrayList<UIUnit>();
		update();
	}
	
	@Override
	public void update() {
		for(UIUnit ru : unitlist)parts.remove(ru); //reset unit list
		unitlist = new ArrayList<UIUnit>();
		for(int i=0; i<game.unitorder.size(); i++) { //add all units to be displayed
			Unit u = game.unitorder.get(i);
			UIUnit n = new UIUnit(game, xPos+(unitlist.size()/column_size)*size*15, (int)(.5+yPos+(unitlist.size()%column_size)*size*1.5), size, u, game.encode(i), -1);
			unitlist.add(n);
			parts.add(n);
		}
	}
	
}
