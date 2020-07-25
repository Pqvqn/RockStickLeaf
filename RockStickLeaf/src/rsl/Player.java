package rsl;

import java.io.*;
import java.util.*;

public class Player {
	
	private Game game;
	private Inventory inventory;
	public String name;

	public Player(Game g,String playername,File datafile) {
		game = g;
		inventory = new Inventory(game,datafile);
		name = playername;
	}
	
	public boolean canCraft(Unit u) {
		Iterator<Unit> uniter = u.recipe.materials.keySet().iterator();
		while(uniter.hasNext()) {
			Unit b = uniter.next();
			if(inventory.numberOf(b)<u.recipe.numberOf(b))return false;
		}
		return true;
	}
	public boolean has(Unit u) {
		return inventory.numberOf(u)>0;
	}
	public void give(Unit u) {
		inventory.addUnits(u,1);
	}
	public void take(Unit u) {
		inventory.addUnits(u,-1);
	}
}
