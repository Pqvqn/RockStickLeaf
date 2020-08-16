package ui;

import java.awt.*;
import java.io.IOException;

import javax.imageio.*;

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
		
		parts.add(new UIRectangle(game,xPos-size/4,yPos-3*size/5,size,size,Color.WHITE,true)); //image backing
		try {
			parts.add(new UIImage(game,xPos-size/4,yPos-3*size/5,size,size,ImageIO.read(unit.imgFile),true));//image
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parts.add(new UIText(game, xPos+size, yPos+size/3, unit.name, Color.black, new Font("Arial",Font.BOLD,size))); //unit name
		if(quantity>=0)parts.add(new UIText(game, xPos-size*2, yPos+size/3, quantity+"x", Color.black, new Font("Arial",Font.ITALIC,size))); //quantity
		for(int i=0; i<dirkeys.length(); i++) { //add all symbols for dir keys
			parts.add(new UISymbol(game,size*8+xPos+size*2*i,yPos,size,dirkeys.substring(i,i+1),Color.WHITE,Color.BLACK));
		}
	}
	
	
}
