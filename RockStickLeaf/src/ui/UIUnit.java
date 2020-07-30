package ui;

import java.awt.*;

import rsl.*;

public class UIUnit extends UIElement{
	
	private Unit unit; //unit represented
	private String dirkeys; //directions pressed for this unit
	private int quantity; //quantity of unit (-1 for no quantity display)
	
	public UIUnit(Game frame, int x, int y, Unit u, String dirs, int quant) {
		super(frame, x, y);
		xPos = x;
		yPos = y;
		unit = u;
		dirkeys = dirs;
		quantity = quant;
		parts.add(new UIText(game, xPos, yPos+10, unit.name, Color.black, new Font("Arial",Font.ITALIC,30))); //unit name
		for(int i=0; i<dirkeys.length(); i++) {
			parts.add(new UISymbol(game,120+xPos+50*i,yPos,30,dirkeys.substring(i,i+1),Color.WHITE));
		}
	}
	
	
}
