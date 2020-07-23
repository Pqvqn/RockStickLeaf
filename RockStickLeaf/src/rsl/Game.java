package rsl;

import java.util.*;

import javax.swing.*;



public class Game extends JFrame{

	private static final long serialVersionUID = 1L;
	public final int Y_RESOL = 1020, X_RESOL = 1920; //game screen dimensions
	public Draw draw;
	public ArrayList<Controls> controls;
	public ArrayList<Player> players;
	public int playerCount;
	public MatchupLookup matchups;
	public Map<String,Unit> units;
	
	public Game(int playerNum) {
		super("Rock Stick Leaf");
		//engine classes
		playerCount = playerNum;
		players = new ArrayList<Player>();
		playerCount = 4;
		for(int i=0; i<playerCount; i++)
			players.add(new Player(this));
		draw = new Draw(this);
		controls = new ArrayList<Controls>();
		for(int i=0; i<players.size(); i++) {
			controls.add(new Controls(this,players.get(i),i));
		}
		
		//window settings
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(false);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
		setFocusable(true);
		for(int i=0; i<controls.size(); i++) {
			addKeyListener(controls.get(i));
		}
		requestFocus();
		//setBackground(Color.GRAY);
		//setForeground(Color.GRAY);
				
		add(draw);
		pack();
		
		//game data
		matchups = new MatchupLookup(this);
		createUnits();
	}

	private void createUnits() {
		units = new HashMap<String,Unit>();
	}
}
