package ui;

import java.awt.*;
import java.awt.image.*;

import rsl.*;

public class UIImage extends UIElement{
	
	private double wid,hei;
	private Image img;
	
	public UIImage(Game frame, int x, int y, double w, double h, Image i, boolean avg) { //image size as set w and h values
		super(frame,x,y);
		xPos = x;
		yPos = y;
		img = i;
		wid = w;
		hei = h;
		if(avg && img!=null) {
	        ImageFilter filter = new AreaAveragingScaleFilter((int)(.5+wid),(int)(.5+hei));
	        ImageProducer producer = new FilteredImageSource(img.getSource(), filter);
	        img = game.draw.createImage(producer);
		}
	}
	public UIImage(Game frame, int x, int y, double ratio, Image i, boolean avg) { //image size as ratio to regular size
		super(frame,x,y);
		xPos = x;
		yPos = y;
		img = i;
		wid = img.getWidth(null) * ratio;
		hei = img.getHeight(null) * ratio;
		if(avg && img!=null) {
	        ImageFilter filter = new AreaAveragingScaleFilter((int)(.5+wid),(int)(.5+hei));
	        ImageProducer producer = new FilteredImageSource(img.getSource(), filter);
	        img = game.draw.createImage(producer);
		}
		
	}

	public void setImg(Image i) {
		img = i;
	}
	
	public void paint(Graphics g) {
		g.drawImage(img, xPos, yPos, (int)(.5+wid), (int)(.5+hei), null);
	}
	
	
}
