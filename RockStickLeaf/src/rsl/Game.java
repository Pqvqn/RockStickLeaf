package rsl;

import java.io.*;
import java.util.*;
import javax.swing.*;



public class Game extends JFrame{

	private static final long serialVersionUID = 1L;
	public final int Y_RESOL = 1020, X_RESOL = 1920; //game screen dimensions
	public String filepath; //where game data files are stored
	public String savename; //name of savefile
	public Draw draw;
	public ArrayList<Controls> controls;
	public ArrayList<Player> players;
	public int playerCount;
	public MatchupLookup matchups;
	public Map<String,Unit> units;
	private Scanner s;
	
	public Game(int playerNum) {
		super("Rock Stick Leaf");
		s = new Scanner(System.in);
		savename = "Yeehaw";
		filepath = System.getProperty("user.home")+"/Documents/RockStickLeaf/"+savename;
		//game data
		try {
			createUnits();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		matchups = new MatchupLookup(this,new File(filepath+"/matchups.txt"));
		
		//engine classes
		playerCount = playerNum;
		players = new ArrayList<Player>();
		for(int i=0; i<playerCount; i++) {
			System.out.println("Player "+i+": ");
			String pname = getConsoleInput();
			players.add(new Player(this,pname,new File(filepath+"/inventory_"+pname+".txt")));
		}
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
		
		//System.out.println(matchups.victor(units.get("Rock"),units.get("Fire")).name());
		
		//System.out.println(players.get(1).canCraft(units.get("Fire")));

		while(true) {
			System.out.print(players.get(0).name +": ");
			String p1c = getConsoleInput();
			while(p1c.substring(0,1).equals("#")) {
				Unit craft = units.get(p1c.substring(p1c.indexOf("#")+1));
				if(craft==null) {
					System.out.println("Recipe: ");
					craft = new Unit(this,p1c.substring(p1c.indexOf("#")+1)+":"+getConsoleInput());
					units.put(craft.name(),craft);
					craft.readRecipe();
				}
				players.get(0).craft(craft);
				System.out.print(players.get(0).name +": ");
				p1c = getConsoleInput();
			}
			System.out.print(players.get(1).name +": ");
			String p2c = getConsoleInput();
			while(p2c.substring(0,1).equals("#")) {
				Unit craft = units.get(p2c.substring(p2c.indexOf("#")+1));
				if(craft==null) {
					System.out.println("Recipe: ");
					craft = new Unit(this,getConsoleInput());
					units.put(craft.name(),craft);
					craft.readRecipe();
				}
				players.get(1).craft(craft);
				System.out.print(players.get(1).name +": ");
				p2c = getConsoleInput();
			}
			System.out.println("DID: "+doMatch(players.get(0),units.get(p1c),players.get(1),units.get(p2c)));
		}
	}

	private boolean doMatch(Player p1, Unit u1, Player p2, Unit u2) { //returns if match is successful; carries match out if can
		if(!(p1.has(u1) && p2.has(u2)))return false;
		if(u1.equals(u2))return false;
		Matchup m = new Matchup(new Unit[] {u1,u2});
		Unit losingUnit = (matchups.victor(m).equals(u1)) ? u2:u1;
		Player winner = losingUnit.equals(u1)? p2:p1;
		Player loser = losingUnit.equals(u1)? p1:p2;
		doTransfer(winner,loser,matchups.victor(m),losingUnit);
		return true;
	}
	
	private void doTransfer(Player winner, Player loser, Unit winning, Unit losing) {
		if(winning instanceof DefaultUnit) {
			winner.give(winning);
		}
		if(!(losing instanceof DefaultUnit)){
			loser.take(losing);
		}
		winner.give(losing);
		System.out.println(winner);
		System.out.println(loser);
		System.out.println(winner.name+" WIN");
		
	}
	
	private void createUnits() throws IOException { //builds all unit types from file
		units = new HashMap<String,Unit>();
		File b = new File(filepath+"/recipes.txt");
		b.getParentFile().mkdirs();
		b.createNewFile();
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
		//printRecipes();
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
	
	public String getConsoleInput() {
		String st = s.nextLine();
		return st;
	}
	
}
