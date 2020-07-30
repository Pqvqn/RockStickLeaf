package rsl;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import ui.*;

public class Draw extends JPanel{

	private static final long serialVersionUID = 1L;
	private Game game;
	private ArrayList<UIElement> ui;
	
	public Draw(Game g) {
		super();
		game = g;
		ui = new ArrayList<UIElement>();
		setPreferredSize(new Dimension(game.X_RESOL, game.Y_RESOL));
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
	
	//draw all objects
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(int i=0; i<ui.size(); i++) {
			if(ui.get(i)!=null)ui.get(i).paint(g);
		}
		
	}

	
}
