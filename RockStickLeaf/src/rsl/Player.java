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
	
	public void craft(Unit u) {
		if(!canCraft(u))return;
		Iterator<Unit> uniter = u.recipe.materials.keySet().iterator();
		while(uniter.hasNext()) {
			Unit b = uniter.next();
			take(b,u.recipe.materials.get(b));
		}
		give(u);
	}
	
	public boolean has(Unit u) {
		if(u instanceof DefaultUnit)return true;
		return inventory.numberOf(u)>0;
	}
	public void give(Unit u) {
		give(u,1);
	}
	public void take(Unit u) {
		take(u,1);
	}
	public void give(Unit u,int quantity) {
		inventory.addUnits(u,quantity);
	}
	public void take(Unit u,int quantity) {
		give(u,-quantity);
	}
	public String toString() {
		return name+"\n"+inventory.toString();
	}
}
