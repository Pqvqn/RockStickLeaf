package ui;

import java.awt.*;
import java.util.*;

import rsl.*;

public class UIMenu extends UIElement{

	private UIRectangle backing;
	private UIText header;
	private ArrayList<UIMenuOption> options;
	
	public UIMenu(Game frame, int x, int y) {
		super(frame,x,y);
		parts.add(backing = new UIRectangle(game,xPos-150,yPos-20,300,0,new Color(0,0,0,100),true));
		parts.add(header = new UIText(game,xPos-150,yPos-25,"",Color.WHITE,new Font("Arial",Font.ITALIC,20)));
		options = new ArrayList<UIMenuOption>();
		update();
	}
	
	public void setMenu(String prompt, int[] nums, String[] strings, boolean display) { //add all menu options
		for(UIMenuOption mu : options) {
			parts.remove(mu);
		}
		options = new ArrayList<UIMenuOption>();
		if(display) {
			header.setText(prompt);
			backing.sethLen(nums.length*50);
			for(int i=0; i<nums.length; i++) {
				UIMenuOption mu = new UIMenuOption(game,xPos-90,yPos+50*i,nums[i],strings[i]);
				options.add(mu);
				parts.add(mu);
			}
		}else {
			header.setText("");
			backing.sethLen(0);
		}
	}
	
	@Override
	public void update() {
	}
}