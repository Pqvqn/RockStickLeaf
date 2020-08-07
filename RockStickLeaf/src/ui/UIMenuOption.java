package ui;

import java.awt.*;

import rsl.*;

public class UIMenuOption extends UIElement{

	private int number;
	private String string;
	
	public UIMenuOption(Game frame, int x, int y, int num, String str) {
		super(frame,x,y);
		number = num;
		string = str;
		String dirkeys;
		if(number<0) {
			dirkeys = "_";
		}else {
			dirkeys = game.encode(number);
		}
		parts.add(new UIText(game, xPos+40, yPos+8, string, new Color(255,255,255,200), new Font("Arial",Font.BOLD,20))); //text
		for(int i=0; i<dirkeys.length(); i++) { //symbols for dirkeys
			parts.add(new UISymbol(game,xPos+40*i-20,yPos,20,dirkeys.substring(i,i+1),Color.WHITE,Color.BLACK));
		}
		update();
	}
	
	@Override
	public void update() {
	}
	
}