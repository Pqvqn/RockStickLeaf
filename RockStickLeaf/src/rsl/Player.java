package rsl;

import java.io.*;
import java.util.*;

public class Player {
	
	private Game game;
	private Inventory inventory;
	public String name;
	private Controls controls;
	private int actionsTaken, actionsCap; //limit on actions per turn
	public boolean isTurn; //whether it is this player's turn to act
	public ArrayList<Unit> targets; //units that this player is targeting to capture
	public Unit choice; //unit choice for this turn
	private String choosing; //presses for choice


	public Player(Game g,String playername,File datafile,int id) {
		game = g;
		inventory = new Inventory(game,datafile);
		name = playername;
		controls = new Controls(game,this,id);
	}
	
	public boolean canCraft(Recipe r) {
		Iterator<Unit> uniter = r.materialsIterator();
		while(uniter.hasNext()) {
			Unit b = uniter.next();
			if(inventory.numberOf(b)<r.amountNeeded(b))return false;
		}
		return true;
	}
	
	public void craft(Recipe r) {
		if(!canCraft(r))return;
		Iterator<Unit> uniter = r.materialsIterator();
		while(uniter.hasNext()) {
			Unit b = uniter.next();
			take(b,r.materials.get(b));
		}
		Iterator<Unit> uniter2 = r.productsIterator();
		while(uniter2.hasNext()) {
			Unit b = uniter2.next();
			give(b,r.products.get(b));
		}
		act();
		System.out.println("Successfully crafted");
	}
	
	public void capture(Player enemy, Unit target) {
		if(enemy.has(target)) {
			targets.add(target);
			enemy.take(target);
			act();
		}
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
	public void addDirKey(String dirkey) {
		choosing += dirkey;
	}
	public void completeDirKeys() {
		int num = game.decode(choosing);
		choice = (num<0 || num>=game.unitorder.size() || !has(game.unitorder.get(num)))?null:game.unitorder.get(num);
		choosing = "";
	}
	
	public boolean canAct() {
		return actionsTaken < actionsCap;
	}
	public void act() {
		actionsTaken++;
	}
	public void resetActions(boolean wonRound) {
		actionsTaken = 0;
		actionsCap = wonRound ? 6:3;
	}
	public int actionsCap() {
		return actionsCap;
	}
	public int actionsTaken() {
		return actionsTaken;
	}
	public Inventory getInventory() {return inventory;}
	public void writeFile() throws IOException {
		inventory.writeFile();
	}
	public String toString() {
		return name+"\n"+inventory.toString();
	}
}
