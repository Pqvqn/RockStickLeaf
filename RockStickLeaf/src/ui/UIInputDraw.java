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
	private UIRectangle penCol; //color of pen
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
		parts.add(penCol = new UIRectangle(game,xPos-50,yPos+245,100,30,Color.BLACK,true));
		
		//create all buttons
		buttons = new ArrayList<UIButton>();
		buttons.add(new UIButton(game,xPos,yPos-230,100,60,Color.BLACK,Color.YELLOW,false,"Submit",Color.WHITE,new Font("Arial",Font.BOLD,20))); //submit
		buttons.add(new UIButton(game,xPos-wid/5,yPos+170,40,40,Color.BLACK,Color.YELLOW,false,"-",Color.WHITE,new Font("Arial",Font.BOLD,20))); //pen size down
		buttons.add(new UIButton(game,xPos+wid/5,yPos+170,40,40,Color.BLACK,Color.YELLOW,false,"+",Color.WHITE,new Font("Arial",Font.BOLD,20))); //pen size up
		buttons.add(new UIButton(game,xPos-wid/5,yPos+220,40,40,Color.BLACK,Color.YELLOW,false,"R+",Color.WHITE,new Font("Arial",Font.BOLD,20))); //pen color red up
		buttons.add(new UIButton(game,xPos,yPos+220,40,40,Color.BLACK,Color.YELLOW,false,"G+",Color.WHITE,new Font("Arial",Font.BOLD,20))); //pen color green up
		buttons.add(new UIButton(game,xPos+wid/5,yPos+220,40,40,Color.BLACK,Color.YELLOW,false,"B+",Color.WHITE,new Font("Arial",Font.BOLD,20))); //pen color blue up
		buttons.add(new UIButton(game,xPos-wid/5,yPos+300,40,40,Color.BLACK,Color.YELLOW,false,"R-",Color.WHITE,new Font("Arial",Font.BOLD,20))); //pen color red down
		buttons.add(new UIButton(game,xPos,yPos+300,40,40,Color.BLACK,Color.YELLOW,false,"G-",Color.WHITE,new Font("Arial",Font.BOLD,20))); //pen color green down
		buttons.add(new UIButton(game,xPos+wid/5,yPos+300,40,40,Color.BLACK,Color.YELLOW,false,"B-",Color.WHITE,new Font("Arial",Font.BOLD,20))); //pen color blue down
		for(int i=0; i<10; i++) {
			buttons.add(new UIButton(game,xPos-70+(i%5*35),yPos+360+(i/5*35),30,30,Color.BLACK,Color.YELLOW,false,"#",Color.WHITE,new Font("Arial",Font.BOLD,20))); //pen color blue down
		}
		
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
			//get correct mouse location on draw
			Point b = MouseInfo.getPointerInfo().getLocation();
			SwingUtilities.convertPointFromScreen(b, game.draw);
			b = game.draw.convertPointFurther(b);
			
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(penCol.getColor()); //set color
			int stroke = Integer.parseInt(penWid.getText());
			g2.setStroke(new BasicStroke(stroke,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));
			g2.drawLine(lastx-xPos+wid/2, lasty-yPos+hei/2, (int)(.5+b.getX())-xPos+wid/2, (int)(.5+b.getY())-yPos+hei/2); //draw from last position to current one
			g2.setStroke(new BasicStroke());
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
				Color color = penCol.getColor();
				int stroke = Integer.parseInt(penWid.getText());
				switch(buttons.get(i).text()) { //action based on button text
				case "Submit": //submit image
					submittedDrawing = drawing;
					break;
				case "-": //pen size down
					if(stroke>1)
						penWid.setText((stroke-1)+"");
					break;
				case "+": //pen size up
					if(stroke<50)
						penWid.setText((stroke+1)+"");
					break;
				case "R+": //pen color red up
					penCol.setColor(new Color(color.getRed()+15>255?color.getRed()-255:color.getRed()+15,color.getGreen(),color.getBlue()));
					break;
				case "R-": //pen color red down
					penCol.setColor(new Color(color.getRed()-15<0?color.getRed()+255:color.getRed()-15,color.getGreen(),color.getBlue()));
					break;
				case "G+": //pen color green up
					penCol.setColor(new Color(color.getRed(),color.getGreen()+15>255?color.getGreen()-255:color.getGreen()+15,color.getBlue()));
					break;
				case "G-": //pen color green down
					penCol.setColor(new Color(color.getRed(),color.getGreen()-15<0?color.getGreen()+255:color.getGreen()-15,color.getBlue()));
					break;
				case "B+": //pen color blue up
					penCol.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue()+15>255?color.getBlue()-255:color.getBlue()+15));
					break;
				case "B-": //pen color blue down
					penCol.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue()-15<0?color.getBlue()+255:color.getBlue()-15));
					break;
				case "#": //pen color preset
					if(arg0.getButton() == MouseEvent.BUTTON3) { //set color storage to current pen color with right click
						buttons.get(i).setColor(color);
					}else { //set pen color to color storage otherwise
						penCol.setColor(buttons.get(i).buttonColor());
					}
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
		Point b = new Point(arg0.getX(),arg0.getY());
		b = game.draw.convertPointFurther(b);
		lastx = b.x;
		lasty = b.y;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		lastx = -1;
		lasty = -1;
	}

	
}
