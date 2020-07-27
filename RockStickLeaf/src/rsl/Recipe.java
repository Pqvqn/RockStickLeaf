package rsl;

import java.util.*;

public class Recipe {

	private Game game;
	public Map<Unit,Integer> materials; //key is unit, value is quantity needed
	public Map<Unit,Integer> products; //key is unit, value is quantity produced
	
	public Recipe(Game g, String recipedata) {
		game = g;
		create(recipedata);
	}
	
	public Integer amountNeeded(Unit u) {
		if(!materials.containsKey(u))return 0;
		return materials.get(u);
	}
	public Integer amountProduced(Unit u) {
		if(!products.containsKey(u))return 0;
		return products.get(u);
	}
	public Iterator<Unit> materialsIterator(){
		return materials.keySet().iterator();
	}
	public Iterator<Unit> productsIterator(){
		return products.keySet().iterator();
	}
	
	
	private void create(String r) {
		materials = new HashMap<Unit,Integer>();
		products = new HashMap<Unit,Integer>();
		if(r==null)return;
		String[] components = r.split(":");
		stringToMap(components[0],products);
		stringToMap(components[1],materials);
		Iterator<Unit> uniter = productsIterator();
		while(uniter.hasNext()) {
			uniter.next().recipes.add(this);
		}
		
	}
	
	private void stringToMap(String r, Map<Unit,Integer> m) { //takes string of comma-separated names/quantities and puts them into a map
		String[] components = r.split(","); //commas separate material units
		for(int i=0; i<components.length; i++) {
			String component = components[i];
			Unit unit; //unit
			Integer quantity;//number required 
			if(component.contains("*")) { //if quantity given, use it, otherwise use 1
				String[] components2 = (component.split("\\*"));
				if(!game.units.containsKey(components2[0])) {
					game.units.put(components2[0],new Unit(game,components2[0]));
				}
				unit = game.units.get(components2[0]);
				quantity = Integer.parseInt(components2[1]);
			}else {
				if(!game.units.containsKey(component)) {
					game.units.put(component,new Unit(game,component));
				}
				unit = game.units.get(component);
				quantity = 1;
			}
			if(m.containsKey(unit))quantity+=m.get(unit); //sum if material listed multiple times
			m.put(unit,quantity);
		}
	}
	
	public String toString() {
		String ret = "";
		Iterator<Unit> p = productsIterator();
		while(p.hasNext()) {
			Unit pu = p.next();
			if(products.get(pu)<=0) {
			}else if(products.get(pu)==1) {
				ret+=pu.name+",";
			}else {
				ret+=pu.name+"*"+products.get(pu)+",";
			}
		}
		ret = ret.substring(0,ret.length()-1);
		ret+=":";
		Iterator<Unit> m = materialsIterator();
		while(m.hasNext()) {
			Unit mu = m.next();
			if(materials.get(mu)<=0) {
			}else if(materials.get(mu)==1) {
				ret+=mu.name+",";
			}else {
				ret+=mu.name+"*"+materials.get(mu)+",";
			}

		}
		ret = ret.substring(0,ret.length()-1);
		return ret;
	}
	
}
