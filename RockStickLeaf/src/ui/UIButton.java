package ui;

import java.awt.*;

import javax.swing.*;

import rsl.*;

public class UIButton extends UIElement{

	private UIRectangle backing;
	private UIText text;
	private boolean highlight;
	//private boolean pressed;
	private Color back, hlight;
	
	public UIButton(Game frame, int x, int y, int w, int h, Color bc, Color bch, String t, Color tc, Font f) {
		super(frame,x,y);
		back = bc;
		hlight = bch;
		parts.add(backing = new UIRectangle(game,xPos-w/2,yPos-h/2,w,h,bc,true));
		parts.add(text = new UIText(game,xPos,yPos+f.getSize()/3,t,tc,f));
		update();
	}

	
	public boolean highlighted() {return highlight;}
	
	@Override
	public void update() {
		text.center(xPos);
		Point b = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(b, game.draw);
		double x = b.getX();
		double y = b.getY();
		highlight = x>=backing.getxPos() && x<=backing.getxPos()+backing.getwLen() && y>=backing.getyPos() && y<=backing.getyPos()+backing.gethLen();
		if(highlight) { //color based on if mouse is hovered
			backing.setColor(hlight);
		}else {
			backing.setColor(back);
		}
	}
}