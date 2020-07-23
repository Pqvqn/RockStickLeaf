package rsl;

public class Unit {

	protected Game game;
	protected Recipe recipe;
	protected String name;
	protected String data;
	
	public Unit(Game g, String unitdata) {
		game = g;
		data = unitdata;
		name = data.substring(0,data.indexOf(":"));
	}
	
	public String name() {return name;}
	public void readRecipe() {
		recipe = new Recipe(game,this,data.substring(data.indexOf(":")+1));
	}
	
}
