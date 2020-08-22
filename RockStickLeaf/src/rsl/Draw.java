package rsl;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import ui.*;

public class Draw extends JPanel{

	private static final long serialVersionUID = 1L;
	private Game game;
	private ArrayList<UIElement> ui;
	
	//various ui pieces
	public ArrayList<UIInventory> inventories;
	public UICatalogue catalogue;
	public UIMatch match;
	public UIInputType textinput;
	public UIInputDraw imageinput;
	
	public Draw(Game g) {
		super();
		game = g;
		ui = new ArrayList<UIElement>();
		inventories = new ArrayList<UIInventory>();
		setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		setBackground(Color.GRAY);
	}
	public void update(Graphics g) {
		paint(g);
	}
	public void addUI(UIElement thing) {
		ui.add(thing);
	}
	public void removeUI(UIElement thing) {
		ui.remove(thing);
	}
	public ArrayList<UIElement> getUIList() {return ui;}
	
	public void updateUIElement(UIElement uie) { //update said element
		uie.update();
	}
	public void updateUIElement(ArrayList<? extends UIElement> uis) { //update several elements in list
		for(int i=0; i<uis.size(); i++) {
			updateUIElement(uis.get(i));
		}
	}
	public void displayUIElement(UIElement uie, boolean disp) { //set displaying of element
		if(disp) {
			if(!ui.contains(uie))ui.add(uie);
		}else {
			if(ui.contains(uie))ui.remove(uie);
		}
	}
	public void displayUIElement(ArrayList<? extends UIElement> uis, boolean disp) { //set displaying of several elements in list
		for(int i=0; i<uis.size(); i++) {
			displayUIElement(uis.get(i),disp);
		}
	}
	
	//converts point based on scaling
	public Point convertPointFurther(Point b) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Point bb = new Point((int)(.5+b.x * (game.X_RESOL/screen.getWidth())),(int)(.5+b.y * (game.Y_RESOL/screen.getHeight())));
		return bb;
	}
	
	//draw all objects
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//scale screen
		Graphics2D g2 = (Graphics2D)g;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		g2.scale(screen.getWidth()/game.X_RESOL,screen.getHeight()/game.Y_RESOL);
		
		displayUIElement(inventories,true);
		displayUIElement(match,true);
		displayUIElement(catalogue,true);
		if(match!=null)updateUIElement(match);
		for(int i=0; i<ui.size(); i++) {
			if(ui.get(i)!=null)ui.get(i).paint(g);
		}
	}

	
}
