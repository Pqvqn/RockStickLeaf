package rsl;

import java.util.*;

public class Recipe {

	private Game game;
	private Unit unit;
	public Map<Unit,Integer> materials; //key is unit, value is quantity needed
	
	public Recipe(Game g, Unit u, String recipedata) {
		game = g;
		unit = u;
		create(recipedata);
	}
	
	private void create(String r) {
		materials = new HashMap<Unit,Integer>();
		if(r==null)return;
		String[] components = r.split(","); //commas separate material units
		for(int i=0; i<components.length; i++) {
			String component = components[i];
			Unit material; //material
			Integer quantity;//number required 
			if(component.contains("*")) { //if quantity given, use it, otherwise use 1
				material = game.units.get(component.split("\\*")[0]);
				quantity = Integer.parseInt(component.split("\\*")[1]);
			}else {
				material = game.units.get(component);
				quantity = 1;
			}
			if(materials.containsKey(material))quantity+=materials.get(material); //sum if material listed multiple times
			materials.put(material,quantity);
		}
	}
	
}
