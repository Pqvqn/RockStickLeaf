package rsl;

import java.io.*;
import java.util.*;
import javax.swing.*;



public class Game extends JFrame{

	private static final long serialVersionUID = 1L;
	public final int Y_RESOL = 1020, X_RESOL = 1920; //game screen dimensions
	public String filepath; //where game data files are stored
	public Draw draw;
	public ArrayList<Controls> controls;
	public ArrayList<Player> players;
	public int playerCount;
	public MatchupLookup matchups;
	public Map<String,Unit> units;

	
	public Game(int playerNum) {
		super("Rock Stick Leaf");
		filepath = System.getProperty("user.home")+"\\Documents\\RockStickLeaf";
		System.out.println(filepath);
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
		try {
			createUnits();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createUnits() throws IOException { //builds all unit types from file
		units = new HashMap<String,Unit>();
		File b = new File(filepath+"\\yeehaw.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(b),"UTF-8"));
		String curr = reader.readLine();
		while(curr!=null) { //add all units to hashmap
			Unit u = curr.contains("#")?new DefaultUnit(this,curr):new Unit(this,curr);
			units.put(u.name(),u);
			curr = reader.readLine();
		}
		Iterator<Unit> uniter = units.values().iterator();
		while(uniter.hasNext()) { //create recipes for each unit
			uniter.next().readRecipe();
		}
		printRecipes();
		reader.close();
	}
	
	private void printRecipes() { //prints out all recipes to console
		Iterator<Unit> uniter = units.values().iterator();
		while(uniter.hasNext()) {
			Unit u = uniter.next();
			Recipe recipe = u.recipe;
			System.out.print(u.name());
			Iterator<Unit> b = recipe.materials.keySet().iterator();
			while(b.hasNext()) {
				Unit c = b.next();
				System.out.print(" : "+c.name()+" x "+recipe.materials.get(c));
			}
			System.out.println();
		}
	}
}
