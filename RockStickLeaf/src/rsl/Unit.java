package rsl;

import java.util.*;

public class Unit {

	protected Game game;
	public ArrayList<Recipe> recipes;
	public String name;
	public int lastComplexity;
	
	public Unit(Game g, String unitname) {
		game = g;
		name = unitname;
		recipes = new ArrayList<Recipe>();
	}
	
	public int complexity() {//least amount of separate items required to craft (defaults + all mid-level items)
		lastComplexity = complexity(new ArrayList<Unit>());
		return lastComplexity;
	}
	
	public int complexity(ArrayList<Unit> branch) { //complexity with list of units to avoid recipes with (to prevent loops). returns -1 if looped
		if(branch.contains(this))return -1; //abort if recipe already looked into
		if(recipes.isEmpty())return -1; //abort if doesn;t have any recipes
		int[] opt = complexityAll(branch);
		int least = opt[0];
		for(int i : opt) { //find smallest complexity of recipe options
			if((i<least || least<0) && i>=0)least = i;
		}
		return least;
	}
	
	public int[] complexityAll(ArrayList<Unit> branch) { //list of smallest complexities per recipe
		int[] ret = new int[recipes.size()];
		for(int i=0; i<ret.length; i++) { //spot in list for each recipe
			Iterator<Unit> required = recipes.get(i).materialsIterator(); //loop through all of recipe's materials
			boolean valid = true;
			while(required.hasNext()) {
				Unit u = required.next();
				if(recipes.get(i).materials.get(u) > 0) {
					ArrayList<Unit> nbranch = new ArrayList<Unit>(); //preserve branch
					for(Unit a : branch)nbranch.add(a);
					nbranch.add(this);
					int com = u.complexity(nbranch);
					if(com < 0)valid = false; //if canceled because of loop, cancel this value
					ret[i] += (com+1)*recipes.get(i).materials.get(u); //recursively add each material's complexity, +1 for the material itself
				}
				
			}
			if(!valid)ret[i] = -1;
		}
		return ret;
	}
	
	
}
