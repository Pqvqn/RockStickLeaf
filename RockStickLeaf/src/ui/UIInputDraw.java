package ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

import javax.swing.*;

import rsl.*;

public class UIInputDraw extends UIElement implements MouseListener{

	private UIText promptDisp;
	private UIImage disp;
	private Image drawing;
	private Image submittedDrawing;
	private int wid, hei;
	private UIText penWid; //size of pen
	private int lastx, lasty; //last cursor x/y position
	private ArrayList<UIButton> buttons;
	
	public UIInputDraw(Game frame, int x, int y) {
		super(frame,x,y);
		wid = 256; hei = 256;
		lastx = -1; lasty = -1;
		
		
		drawing = new BufferedImage(wid,hei,BufferedImage.TYPE_INT_ARGB);
		parts.add(new UIRectangle(game,xPos-wid/2,yPos-hei/2,wid,hei,new Color(255,255,255,255),true)); //background
		parts.add(disp = new UIImage(game,xPos-wid/2,yPos-wid/2,wid,hei,drawing,false));
		parts.add(promptDisp = new UIText(game,xPos,yPos-4*hei/7,"",new Color(255,255,255,255),new Font("Arial",Font.ITALIC,20)));
		parts.add(penWid = new UIText(game,xPos,yPos+180,"15",Color.BLACK,new Font("Arial",Font.BOLD,30)));
		
		//create all buttons
		buttons = new ArrayList<UIButton>();
		buttons.add(new UIButton(game,xPos,yPos-230,100,60,Color.BLACK,Color.YELLOW,"Submit",Color.WHITE,new Font("Arial",Font.BOLD,20))); //submit
		buttons.add(new UIButton(game,xPos-wid/5,yPos+170,40,40,Color.BLACK,Color.YELLOW,"-",Color.WHITE,new Font("Arial",Font.BOLD,20))); //pen size down
		buttons.add(new UIButton(game,xPos+wid/5,yPos+170,40,40,Color.BLACK,Color.YELLOW,"+",Color.WHITE,new Font("Arial",Font.BOLD,20))); //pen size up
		
		for(UIButton b : buttons)parts.add(b);
		
		update();
	}
	
	
	public void startImage(String prompt) {
		promptDisp.setText(prompt);
		submittedDrawing = null;
		drawing = new BufferedImage(wid,hei,BufferedImage.TYPE_INT_ARGB);
		disp.setImg(drawing);
	}
	
	public Image getSubmission() {return submittedDrawing;}
	
	@Override
	public void update() {
		promptDisp.center(xPos);
		Graphics g = drawing.getGraphics();
		if(lastx>=0 && lasty>=0) { //if pressed down
			Point b = MouseInfo.getPointerInfo().getLocation();
			SwingUtilities.convertPointFromScreen(b, game.draw);
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(Color.black); //set color
			int stroke = Integer.parseInt(penWid.getText());
			g2.setStroke(new BasicStroke(stroke,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));
			g2.drawLine(lastx-xPos+wid/2, lasty-yPos+hei/2, (int)(.5+b.getX())-xPos+wid/2, (int)(.5+b.getY())-yPos+hei/2); //draw from last position to current one
			disp.setImg(drawing); //display new image
			if(lastx>=0 && lasty>=0) {
				lastx = (int)(.5+b.getX()); //set new location
				lasty = (int)(.5+b.getY());
			}
			
		}
		for(int i=0; i<buttons.size(); i++) {
			buttons.get(i).update();
		}
		penWid.center(xPos);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		for(int i=0; i<buttons.size(); i++) {
			if(buttons.get(i).highlighted()){ //test all buttons to see if mouse is in area
				switch(buttons.get(i).text()) { //action based on button text
				case "Submit": //submit image
					submittedDrawing = drawing;
					break;
				case "-": //pen size down
					int stroke = Integer.parseInt(penWid.getText());
					if(stroke>1)
						penWid.setText((stroke-1)+"");
					break;
				case "+": //pen size up
					int stroke2 = Integer.parseInt(penWid.getText());
					if(stroke2<50)
						penWid.setText((stroke2+1)+"");
					break;
				}
			}
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
