package ui;

import java.awt.*;
import java.awt.geom.*;

import rsl.*;

public class UISymbol extends UIElement{
	
	private String symbol; //text to base symbol off of
	private Color color_primary;
	private Color color_secondary;
	private int size;

	public UISymbol(Game frame, int x, int y, int relativesize, String character, Color prim, Color sec) {
		super(frame, x, y);
		symbol = character;
		xPos = x;
		yPos = y;
		color_primary = prim;
		color_secondary = sec;
		size = relativesize;
	}

	public String getSymbol() {return symbol;}
	public void setSymbol(String symbol) {this.symbol = symbol;}

	//public Color getColor() {return color;}
	//public void setColor(Color color) {this.color = color;}

	public void paint(Graphics g) {
		g.setColor(color_primary);
		Graphics2D g2 = (Graphics2D)g;
		AffineTransform origt = g2.getTransform(); //transformation to reset to
	
		if(symbol.equals("v") || symbol.equals("<") || symbol.equals(">")){ //for arrows
			g.setColor(color_secondary); //draw arrow
			g2.rotate(Math.PI/4,xPos,yPos);
			g2.drawRect(xPos-size/2, yPos-size/2, size, size); //draw outline behind arrow
			g.setColor(color_primary);
			switch(symbol) { //rotate depending on arrow needed
			case "v":
				g2.rotate(Math.PI,xPos,yPos);
				break;
			case "<":
				g2.rotate(3*Math.PI/2,xPos,yPos);
				break;
			case ">":
				g2.rotate(Math.PI/2,xPos,yPos);
				break;
			case "^":
				//g2.rotate(0,xPos,yPos);
				break;
			}
			g2.fillRect(xPos-size/2,yPos-size/2,size,size/3); //draw rectangles for arrow
			g2.fillRect(xPos-size/2,yPos-size/2,size/3,size);
			
		}
		
		
		g2.setTransform(origt);
	}
	
	
}
