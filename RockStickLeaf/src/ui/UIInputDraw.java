package ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import rsl.*;

public class UIInputDraw extends UIElement implements MouseListener{

	private UIText promptDisp;
	private UIImage disp;
	private Image drawing;
	private Image submittedDrawing;
	private int wid, hei;
	private int lastx, lasty;
	
	public UIInputDraw(Game frame, int x, int y) {
		super(frame,x,y);
		wid = 256; hei = 256;
		lastx = -1; lasty = -1;
		drawing = new BufferedImage(wid,hei,BufferedImage.TYPE_INT_ARGB);
		parts.add(new UIRectangle(game,xPos-wid/2,yPos-hei/2,wid,hei,new Color(255,255,255,255),true)); //background
		parts.add(disp = new UIImage(game,xPos-wid/2,yPos-wid/2,wid,hei,drawing));
		parts.add(promptDisp = new UIText(game,xPos,yPos-2*hei/3,"",new Color(255,255,255,255),new Font("Arial",Font.ITALIC,20)));
		update();
	}
	
	
	public void startImage(String prompt) {
		promptDisp.setText(prompt);
	}
	
	public Image getSubmission() {return submittedDrawing;}
	
	@Override
	public void update() {
		promptDisp.center(xPos);
		Graphics g = drawing.getGraphics();
		if(lastx>=0 && lasty>=0) { //if pressed down
			Point b = MouseInfo.getPointerInfo().getLocation();
			g.setColor(Color.black); //set color
			g.drawLine(lastx-xPos+wid/2, lasty-yPos+hei/2, (int)(.5+b.getX())-xPos+wid/2, (int)(.5+b.getY())-yPos+hei/2); //draw from last position to current one
			disp.setImg(drawing); //display new image
			if(lastx>=0 && lasty>=0) {
				lastx = (int)(.5+b.getX()); //set new location
				lasty = (int)(.5+b.getY());
			}
			
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		lastx = arg0.getXOnScreen();
		lasty = arg0.getYOnScreen();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		lastx = -1;
		lasty = -1;
	}

	
}
