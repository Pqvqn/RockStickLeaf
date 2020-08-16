package ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;

import rsl.*;

public class UIInputDraw extends UIElement implements MouseListener{

	private UIText promptDisp;
	private UIImage disp;
	private Image drawing;
	private Image submittedDrawing;
	private int wid, hei;
	private int lastx, lasty;
	private UIButton submit;
	
	public UIInputDraw(Game frame, int x, int y) {
		super(frame,x,y);
		wid = 256; hei = 256;
		lastx = -1; lasty = -1;
		drawing = new BufferedImage(wid,hei,BufferedImage.TYPE_INT_ARGB);
		parts.add(new UIRectangle(game,xPos-wid/2,yPos-hei/2,wid,hei,new Color(255,255,255,255),true)); //background
		parts.add(disp = new UIImage(game,xPos-wid/2,yPos-wid/2,wid,hei,drawing,false));
		parts.add(promptDisp = new UIText(game,xPos,yPos-2*hei/3,"",new Color(255,255,255,255),new Font("Arial",Font.ITALIC,20)));
		parts.add(submit = new UIButton(game,xPos,yPos-230,100,60,Color.BLACK,Color.YELLOW,"Submit",Color.WHITE,new Font("Arial",Font.ITALIC,20)));
		update();
	}
	
	
	public void startImage(String prompt) {
		promptDisp.setText(prompt);
		submittedDrawing = null;
	}
	
	public Image getSubmission() {return submittedDrawing;}
	
	@Override
	public void update() {
		promptDisp.center(xPos);
		Graphics g = drawing.getGraphics();
		if(lastx>=0 && lasty>=0) { //if pressed down
			Point b = MouseInfo.getPointerInfo().getLocation();
			SwingUtilities.convertPointFromScreen(b, game.draw);
			g.setColor(Color.black); //set color
			g.drawLine(lastx-xPos+wid/2, lasty-yPos+hei/2, (int)(.5+b.getX())-xPos+wid/2, (int)(.5+b.getY())-yPos+hei/2); //draw from last position to current one
			disp.setImg(drawing); //display new image
			if(lastx>=0 && lasty>=0) {
				lastx = (int)(.5+b.getX()); //set new location
				lasty = (int)(.5+b.getY());
			}
			
		}
		submit.update();
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(submit.highlighted()) { //if clicked on submit button, submit image
			submittedDrawing = drawing;
		}
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
		lastx = arg0.getX();
		lasty = arg0.getY();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		lastx = -1;
		lasty = -1;
	}

	
}
