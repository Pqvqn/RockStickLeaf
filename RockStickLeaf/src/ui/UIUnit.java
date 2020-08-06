package ui;

import java.awt.*;

import rsl.*;

public class UIUnit extends UIElement{
	
	private Unit unit; //unit represented
	private String dirkeys; //directions pressed for this unit
	private int quantity; //quantity of unit (-1 for no quantity display)
	private int size; //size of ui element
	
	public UIUnit(Game frame, int x, int y, int sz, Unit u, String dirs, int quant) {
		super(frame, x, y);
		xPos = x;
		yPos = y;
		unit = u;
		dirkeys = dirs;
		quantity = quant;
		size = sz;
		parts.add(new UIText(game, xPos, yPos+size/3, unit.name, Color.black, new Font("Arial",Font.BOLD,size))); //unit name
		if(quantity>=0)parts.add(new UIText(game, xPos-size*2, yPos+size/3, quantity+"x", Color.black, new Font("Arial",Font.ITALIC,size))); //quantity
		for(int i=0; i<dirkeys.length(); i++) { //add all symbols for dir keys
			parts.add(new UISymbol(game,size*7+xPos+size*2*i,yPos,size,dirkeys.substring(i,i+1),Color.WHITE,Color.BLACK));
		}
	}
	
	
}
