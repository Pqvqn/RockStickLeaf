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
	public Map<String,Unit> units; //map: get unit object from its name as a string
	public ArrayList<Unit> unitorder; //units stored in order
	public ArrayList<DefaultUnit> defaults;
	public Set<Recipe> recipes;
	private Scanner s;
	
	public Game(int playerNum) {
		super("Rock Stick Leaf");
		s = new Scanner(System.in);
		System.out.println("Save name: ");
		savename = getConsoleInput();
		filepath = System.getProperty("user.home")+"/Documents/RockStickLeaf/"+savename;
		//game data
		defaults = new ArrayList<DefaultUnit>();
		try {
			createUnits();
			createRecipes();
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

		while(defaults.isEmpty()) {
			System.out.println("List default units: ");
			String listu = getConsoleInput();
			String[] components = listu.split(",");
			for(int i=0; i<components.length; i++) {
				addNewUnit(new DefaultUnit(this,components[i]));
			}
		}
		String def = "";
		for(DefaultUnit du : defaults)def+=", "+du.name;
		System.out.println("Defaults are: "+def.substring(2));
		
		boolean doGame = true;
		while(doGame) {
			Player p1 = players.get(0);
			p1.targets = new ArrayList<Unit>();
			System.out.print(p1.name +" "+p1.actionsTaken()+"/"+p1.actionsCap()+": ");
			String p1c = getConsoleInput();
			if(p1c.equals("(QUIT)")) {
				doGame = false;
			}else {
				while(p1c.substring(0,1).equals("#") || p1c.substring(0,1).equals("@")) {
					if(p1.canAct()) {
						switch(p1c.substring(0,1)) {
						case "#": //crafting
							Recipe craftr = getRecipe(p1c);
							if(craftr!=null){
								p1.craft(craftr);
							}
							break;
						case "@": //capturing
							Unit hostage = units.get(p1c.substring(p1c.indexOf("@")+1));
							if(players.get(1).has(hostage)) {
								p1.targets.add(hostage);
								players.get(1).take(hostage);
								p1.act();
							}
							break;
						default:
							break;
						}
						
					}
					System.out.print(p1.name +" "+p1.actionsTaken()+"/"+p1.actionsCap()+": ");
					p1c = getConsoleInput();
				}
				Player p2 = players.get(1);
				p2.targets = new ArrayList<Unit>();
				System.out.print(p2.name +" "+p2.actionsTaken()+"/"+p2.actionsCap()+": ");
				String p2c = getConsoleInput();
				while(p2c.substring(0,1).equals("#") || p2c.substring(0,1).equals("@")) {
					if(p2.canAct()) {
						switch(p2c.substring(0,1)) {
						case "#": //crafting
							Recipe craftr = getRecipe(p2c);
							if(craftr!=null){
								p2.craft(craftr);
							}
							break;
						case "@": //capturing
							Unit hostage = units.get(p2c.substring(p2c.indexOf("@")+1));
							if(players.get(0).has(hostage)) {
								p2.targets.add(hostage);
								players.get(0).take(hostage);
								p2.act();
							}
							break;
						default:
							break;
						}
						
					}
					System.out.print(p2.name +" "+p2.actionsTaken()+"/"+p2.actionsCap()+": ");
					p2c = getConsoleInput();
				}
				System.out.println("DID: "+doMatch(players.get(0),units.get(p1c),players.get(1),units.get(p2c)));
			}
		}
		try {
			writeFiles();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Game has ended");
	}

	private boolean doMatch(Player p1, Unit u1, Player p2, Unit u2) { //returns if match is successful; carries match out if can
		if(!(p1.has(u1) && p2.has(u2)))return false;
		if(u1.equals(u2)) {
			doSwap(p1,p2,u1,u2);
			p1.resetActions(false);
			p2.resetActions(false);
			return true;
		}
		Matchup m = new Matchup(new Unit[] {u1,u2});
		Unit winningUnit = matchups.victor(m);
		if(winningUnit == null) {
			doSwap(p1,p2,u1,u2);
			p1.resetActions(false);
			p2.resetActions(false);
			return true;
		}
		Unit losingUnit = winningUnit.equals(u1) ? u2:u1;
		Player winner = losingUnit.equals(u1)? p2:p1;
		Player loser = losingUnit.equals(u1)? p1:p2;
		doTransfer(winner,loser,winningUnit,losingUnit);
		winner.resetActions(true);
		loser.resetActions(false);
		return true;
	}
	
	private void doTransfer(Player winner, Player loser, Unit winning, Unit losing) {
		if(winning instanceof DefaultUnit) {
			winner.give(winning);
		}
		if(!(losing instanceof DefaultUnit)){
			loser.take(losing);
		}
		for(Unit hostage : winner.targets) {
			if(!winning.equals(hostage)) {
				Matchup m = new Matchup(new Unit[] {winning,hostage});
				Unit winningUnit = matchups.victor(m);
				if(winningUnit.equals(winning)) {
					winner.give(hostage);
				}else{
					loser.give(hostage);
				}
			}else {
				loser.give(hostage);
			}
		}
		for(Unit hostage : loser.targets) {
			winner.give(hostage);
		}
		winner.give(losing);
		System.out.println(winner);
		System.out.println(loser);
		System.out.println(winner.name+" WIN");
		
	}
	
	private void doSwap(Player p1, Player p2, Unit p1u, Unit p2u) {
		if(!(p1u instanceof DefaultUnit))p1.take(p1u);
		if(!(p2u instanceof DefaultUnit))p2.take(p2u);
		p1.give(p2u);
		p2.give(p1u);
		System.out.println(p1);
		System.out.println(p2);
		System.out.println("TIE");
	}
	private void addNewUnit(Unit u) {
		if(u instanceof DefaultUnit)defaults.add((DefaultUnit)u);
		units.put(u.name,u);
		unitorder.add(u);
	}
	private void createUnits() throws IOException { //builds all unit types from file
		unitorder = new ArrayList<Unit>();
		units = new HashMap<String,Unit>();
		File b = new File(filepath+"/units.txt");
		b.getParentFile().mkdirs();
		b.createNewFile();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(b),"UTF-8"));
		String curr = reader.readLine();
		while(curr!=null) { //add all units to hashmap and arraylist
			Unit u;
			if(curr.substring(0,1).equals(":")) { //tests if this is a recipe or a list of defaults
				u = new DefaultUnit(this,curr.substring(1));
			}else {
				u = new Unit(this,curr);
			}
			addNewUnit(u);
			curr = reader.readLine();
		}
		//printRecipes();
		reader.close();
	}
	private void createRecipes() throws IOException { //builds all unit types from file
		recipes = new HashSet<Recipe>();
		File b = new File(filepath+"/recipes.txt");
		b.getParentFile().mkdirs();
		b.createNewFile();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(b),"UTF-8"));
		String curr = reader.readLine();
		while(curr!=null) { //create all recipes
			recipes.add(new Recipe(this,curr));
			curr = reader.readLine();
		}
		//printRecipes();
		reader.close();
	}
	
	/*private void printRecipes() { //prints out all recipes to console
		Iterator<Unit> uniter = units.values().iterator();
		while(uniter.hasNext()) {
			Unit u = uniter.next();
			Recipe recipe = u.recipe;
			System.out.print(u.name());
			Iterator<Unit> b = recipe.materialsIterator();
			while(b.hasNext()) {
				Unit c = b.next();
				System.out.print(" : "+c.name()+" x "+recipe.materials.get(c));
			}
			System.out.println();
		}
	}*/
	
	public String getConsoleInput() {
		String st = s.nextLine();
		return st;
	}
	
	public Recipe getRecipe(String u) {
		Unit craftu = units.get(u.substring(u.indexOf("#")+1));
		Recipe craftr = null;
		while(craftu==null) {
			System.out.println("Recipe: ");
			addNewUnit(new Unit(this,(u.substring(u.indexOf("#")+1))));
			craftr = new Recipe(this,getConsoleInput());
			craftu = units.get(u.substring(u.indexOf("#")+1));
			recipes.add(craftr);
		}
		while(craftr==null) {
			System.out.println("Choose Recipe:\n0: Cancel\n1: New");
			for(int i=0; i<craftu.recipes.size(); i++) {
				Recipe r = craftu.recipes.get(i);
				System.out.println((i+2)+": "+r);
			}
			try {
				int ans = Integer.parseInt(getConsoleInput());
				if(ans==0) {
					return null;
				}else if(ans==1) {
					System.out.println("Recipe: ");
					craftr = new Recipe(this,getConsoleInput());
					recipes.add(craftr);
					return craftr;
				}else {
					craftr = craftu.recipes.get(ans-2);
				}
			}catch (NumberFormatException e) {
				
			}
		}
		return craftr;
	}
	
	public void writeFiles() throws IOException {
		//units
		File unitFile = new File(filepath+"/units.txt");
		FileWriter unitWriter = new FileWriter(unitFile);
		for(Unit u : unitorder) { //list unit names in order, start with : if default
			unitWriter.write((u instanceof DefaultUnit ? ":" : "") + u.name+"\n");
		}
		unitWriter.close();
		
		//recipes
		File recipeFile = new File(filepath+"/recipes.txt");
		FileWriter recipeWriter = new FileWriter(recipeFile);
		Iterator<Recipe> recipiterator = recipes.iterator();
		while(recipiterator.hasNext()) { //take recipes from set and write them
			recipeWriter.write(recipiterator.next().toString()+"\n");
		}
		recipeWriter.close();
		
		//matchups
		matchups.writeFile();
		
		//players
		for(int i=0; i<players.size(); i++) {
			players.get(i).writeFile();
		}
		
		
	}
	
}
