package rsl;

import java.io.*;
import java.util.*;

public class Player {
	
	private Game game;
	private Inventory inventory;

	public Player(Game g,String name,File datafile) {
		game = g;
		inventory = new Inventory(game,datafile);
	}
	
	public boolean canCraft(Unit u) {
		Iterator<Unit> uniter = u.recipe.materials.keySet().iterator();
		while(uniter.hasNext()) {
			Unit b = uniter.next();
			if(inventory.numberOf(b)<u.recipe.numberOf(b))return false;
		}
		return true;
	}
	
}
