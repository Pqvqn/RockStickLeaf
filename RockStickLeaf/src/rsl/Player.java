package rsl;

public class Player {
	
	private Game game;
	private Inventory inventory;

	public Player(Game g) {
		game = g;
		inventory = new Inventory(game);
	}
	
}
