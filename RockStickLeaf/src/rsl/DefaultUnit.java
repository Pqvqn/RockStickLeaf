package rsl;

public class DefaultUnit extends Unit{

	public DefaultUnit(Game g, String unitdata) {
		super(g,unitdata);
	}
	
	public void readRecipe() {
		recipe = new Recipe(game,this,null);
	}
}
