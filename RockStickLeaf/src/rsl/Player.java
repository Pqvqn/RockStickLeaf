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
	private String sequence;

	public Player(Game g,String playername,File datafile,int id) {
		game = g;
		inventory = new Inventory(game,datafile);
		name = playername;
		controls = new Controls(this,id);
		game.controls.add(controls);
		actionsCap = 3;
		sequence = null;
	}
	
	public boolean canCraft(Recipe r) { //if player has sufficient materials to craft a recipe
		Iterator<Unit> uniter = r.materialsIterator();
		while(uniter.hasNext()) {
			Unit b = uniter.next();
			if(inventory.numberOf(b)<r.amountNeeded(b))return false;
		}
		return true;
	}
	
	public void craft(Recipe r) { //crafts recipe
		if(!canCraft(r))return;
		Iterator<Unit> uniter = r.materialsIterator();
		while(uniter.hasNext()) { //take materials
			Unit b = uniter.next();
			take(b,r.materials.get(b));
		}
		Iterator<Unit> uniter2 = r.productsIterator();
		while(uniter2.hasNext()) { //add products
			Unit b = uniter2.next();
			give(b,r.products.get(b));
		}
		act();
		System.out.println("Successfully crafted");
	}
	
	public void capture(Player enemy, Unit target) { //takes targeted opponent unit temporarily; adds to target list
		if(enemy.has(target)) {
			targets.add(target);
			enemy.take(target);
			System.out.println(target.name+" taken hostage");
			act();
		}
	}
	
	public boolean has(Unit u) { //if player owns at least one of unit
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
	
	public void makeChoice(String presses) { //turns presses into unit choice when throwing units
		int num = game.decode(presses);
		choice = (num<0 || num>=game.unitorder.size() || !has(game.unitorder.get(num)))?null:game.unitorder.get(num);
	}
	
	public String sequence() { //returns current sent sequence
		return sequence;
	}
	public void startSequence() { //begin collecting sequence
		sequence = null;
		controls.startSequence();
	}
	public void endSequence() { //stop collecting sequence
		sequence = null;
		controls.endSequence();
	}
	public void sendSequence(String seq) { //submit sequence after it is typed
		sequence = seq;
	}
	
	public boolean canAct() { //if player can continue to craft, capture, etc. this turn
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
		return name;
		//return name+"\n"+inventory.toString();
	}
}