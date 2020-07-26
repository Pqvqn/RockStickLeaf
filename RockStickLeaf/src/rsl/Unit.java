package rsl;

import java.util.*;

public class Unit {

	protected Game game;
	public ArrayList<Recipe> recipes;
	public String name;
	
	public Unit(Game g, String unitname) {
		game = g;
		name = unitname;
		recipes = new ArrayList<Recipe>();
	}
	
	
}
