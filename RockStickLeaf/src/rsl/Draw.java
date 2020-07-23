package rsl;

import java.awt.*;

import javax.swing.*;

public class Draw extends JPanel{

	private static final long serialVersionUID = 1L;
	private Game game;
	
	public Draw(Game g) {
		super();
		game = g;
		setPreferredSize(new Dimension(game.X_RESOL, game.Y_RESOL));
		setBackground(Color.GRAY);
	}
	
}
