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
	
	public static boolean validRecipe(Game g, String r) { //if recipe string is able to create a valid recipe
		String[] components = r.split(":");
		if(components.length!=2)return false; //no colon or too many colons (must be 1 to separate products/materials)
		for(int i=0; i<components.length; i++) {
			String[] components2 = components[i].split(",");
			for(int j=0; j<components2.length; j++) {
				String[] components3 = components2[j].split("\\*");
				if(components3.length==1) {
					if(!g.units.keySet().contains(components3[0]))return false; //unit name is invalid
				}else if(components3.length==2) {
					if(!g.units.keySet().contains(components3[0]))return false; //unit name is invalid
					try {
						Integer.parseInt(components3[1]);
					}catch(NumberFormatException e) {
						return false; //unit quantity is invalid
					}
				}else {
					return false; //unit is not formatted in Name*Quantity
				}
			}
		}
		return true;
	}
	
	private void create(String r) {
		materials = new HashMap<Unit,Integer>();
		products = new HashMap<Unit,Integer>();
		if(r==null)return;
		String[] components = r.split(":"); //split materials from products and make maps of them
		stringToMap(components[0],products);
		stringToMap(components[1],materials);
		Iterator<Unit> uniter = productsIterator();
		while(uniter.hasNext()) { //add this recipe to all products' list of recipes
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
				unit = game.units.get(components2[0]);
				quantity = Integer.parseInt(components2[1]);
			}else {
				unit = game.units.get(component);
				quantity = 1;
			}
			if(m.containsKey(unit))quantity+=m.get(unit); //sum if material listed multiple times
			m.put(unit,quantity);
		}
	}
	
	public String toString() { //returns recipe as string, formatted as Product1,Product2*Quantity,Product3...:Material1,Material2*Quantity,Material3...
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
