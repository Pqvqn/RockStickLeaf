package ui;

import java.awt.*;
import java.util.*;

import rsl.*;

public abstract class UIElement {
	
	protected ArrayList<UIElement> parts; //uielements within this one
	protected Game game;
	protected int xPos, yPos; //location

	public UIElement(Game frame, int x, int y) {
		parts = new ArrayList<UIElement>();
		game = frame;
		xPos = x; yPos = y;
	}
	
	public int getxPos() {return xPos;}
	public void setxPos(int xPos) {this.xPos = xPos;}
	public int getyPos() {return yPos;}
	public void setyPos(int yPos) {this.yPos = yPos;}

	
	public void paint(Graphics g) {
		for(int i=0; i<parts.size(); i++) { //paint all uielements within
			parts.get(i).paint(g);
		}
	}
}
