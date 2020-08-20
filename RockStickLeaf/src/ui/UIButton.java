package ui;

import java.awt.*;

import javax.swing.*;

import rsl.*;

public class UIButton extends UIElement{

	private UIRectangle backing;
	private UIRectangle overlay;
	private UIText text;
	private boolean highlight;
	//private boolean pressed;
	private Color back, hlight;
	
	public UIButton(Game frame, int x, int y, int w, int h, Color backc, Color overc, boolean filloverlay, String t, Color tc, Font f) {
		super(frame,x,y);
		back = backc;
		hlight = overc;
		parts.add(backing = new UIRectangle(game,xPos-w/2,yPos-h/2,w,h,back,true));
		parts.add(overlay = new UIRectangle(game,xPos-w/2,yPos-h/2,w,h,hlight,filloverlay));
		parts.add(text = new UIText(game,xPos,yPos+f.getSize()/3,t,tc,f));
		update();
	}

	
	public boolean highlighted() {return highlight;}
	public String text() {return text.getText();}
	public Color buttonColor() {return back;}
	public void setColor(Color c) {
		back = c;
		backing.setColor(back);
	}
	
	@Override
	public void update() {
		text.center(xPos);
		Point b = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(b, game.draw);
		double x = b.getX();
		double y = b.getY();
		highlight = x>=backing.getxPos() && x<=backing.getxPos()+backing.getwLen() && y>=backing.getyPos() && y<=backing.getyPos()+backing.gethLen();
		if(highlight) { //color based on if mouse is hovered
			overlay.setColor(hlight); //show highlight
		}else {
			overlay.setColor(new Color(0,0,0,0)); //remove highlight
		}
	}
}