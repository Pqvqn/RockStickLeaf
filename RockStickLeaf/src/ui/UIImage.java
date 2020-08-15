package ui;

import java.awt.*;

import rsl.*;

public class UIImage extends UIElement{
	
	private double wid,hei;
	private Image img;
	
	public UIImage(Game frame, int x, int y, double w, double h, Image i) { //image size as set w and h values
		super(frame,x,y);
		xPos = x;
		yPos = y;
		img = i;
		wid = w;
		hei = h;
	}
	public UIImage(Game frame, int x, int y, double ratio, Image i) { //image size as ratio to regular size
		super(frame,x,y);
		xPos = x;
		yPos = y;
		img = i;
		wid = img.getWidth(null) * ratio;
		hei = img.getHeight(null) * ratio;
	}

	public void setImg(Image i) {
		img = i;
	}
	
	public void paint(Graphics g) {
		g.drawImage(img, xPos, yPos, (int)(.5+wid), (int)(.5+hei), null);
	}
	
	
}
