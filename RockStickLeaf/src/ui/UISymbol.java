package ui;

import java.awt.*;
import java.awt.geom.*;

import rsl.*;

public class UISymbol extends UIElement{
	
	private String symbol; //text to base symbol off of
	private Color color;
	private int size;

	public UISymbol(Game frame, int x, int y, int relativesize, String character, Color c) {
		super(frame, x, y);
		symbol = character;
		xPos = x;
		yPos = y;
		color = c;
		size = relativesize;
	}

	public String getSymbol() {return symbol;}
	public void setSymbol(String symbol) {this.symbol = symbol;}

	public Color getColor() {return color;}
	public void setColor(Color color) {this.color = color;}

	public void paint(Graphics g) {
		g.setColor(color);
		Graphics2D g2 = (Graphics2D)g;
		AffineTransform origt = g2.getTransform();
	
		if(symbol.equals("v") || symbol.equals("<") || symbol.equals(">")){
			g2.rotate(Math.PI/4,xPos,yPos);
			g2.drawRect(xPos-size/2, yPos-size/2, size, size);
			switch(symbol) {
			case "v":
				g2.rotate(Math.PI,xPos,yPos);
				break;
			case "<":
				g2.rotate(3*Math.PI/2,xPos,yPos);
				break;
			case ">":
				g2.rotate(Math.PI/2,xPos,yPos);
				break;
			}
			g2.fillRect(xPos-size/2,yPos-size/2,size,size/3);
			g2.fillRect(xPos-size/2,yPos-size/2,size/3,size);
			
		}
		
		
		g2.setTransform(origt);
	}
	
	
}
