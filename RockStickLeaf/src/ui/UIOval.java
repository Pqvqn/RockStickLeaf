package ui;

import java.awt.*;

import rsl.*;

public class UIOval extends UIElement{
	
	private int wRad, hRad;
	private Color color;
	private boolean filled;

	public UIOval(Game frame, int x, int y, int w, int h, Color c, boolean fill) {
		super(frame,x,y);
		xPos = x;
		yPos = y;
		wRad = w;
		hRad = h;
		color = c;
		filled = fill;
	}
	
	public int getwRad() {return wRad;}
	public void setwRad(int wRad) {this.wRad = wRad;}
	
	public int gethRad() {return hRad;}
	public void sethRad(int hRad) {this.hRad = hRad;}

	public Color getColor() {return color;}
	public void setColor(Color color) {this.color = color;}

	public void paint(Graphics g) {
		g.setColor(color);
		if(filled) {
			g.fillOval(xPos-wRad/2,yPos-hRad/2,wRad,hRad);
		}else {
			g.drawOval(xPos-wRad/2,yPos-hRad/2,wRad,hRad);
		}
		
	}
	
	
}
