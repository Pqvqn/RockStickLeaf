package rsl;

import java.io.*;
import java.util.*;
import javax.swing.*;

import ui.*;

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
		draw = new Draw(this);
		defaults = new ArrayList<DefaultUnit>();
		try {
			createUnits();
			createRecipes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		matchups = new MatchupLookup(this,new File(filepath+"/matchups.txt"));
		
		Unit uuu = unitorder.get((int)(Math.random()*unitorder.size()));
		System.out.println(uuu.name+" "+uuu.complexity());
		
		//engine classes
		playerCount = playerNum;
		players = new ArrayList<Player>();
		for(int i=0; i<playerCount; i++) {
			System.out.println("Player "+i+": ");
			String pname = getConsoleInput();
			players.add(new Player(this,pname,new File(filepath+"/inventory_"+pname+".txt"),i));
			draw.inventories.add(new UIInventory(this,70+i*300,100,20,players.get(i)));
		}	
		draw.match = new UIMatch(this,X_RESOL/2,Y_RESOL-100,50,players);
		draw.catalogue = new UICatalogue(this,50,Y_RESOL/2+100,10,25);
		
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
		ArrayList<Player> doneMove = players; //order in which players did move
		while(doGame) {
			
			for(int i=doneMove.size()-1; i>=0; i--) {
				Player p1 = doneMove.get(i);
				p1.targets = new ArrayList<Unit>();
				System.out.println(p1.name +" "+p1.actionsTaken()+"/"+p1.actionsCap()+": ");
				
				p1.isTurn = true;
				String p1c = retrieveSequence(p1);
				
				if(p1c.equals("<")) {
					doGame = false;
				}else {
					while(p1.isTurn && p1c.length()>0) {
						if(p1.canAct()) {
							switch(p1c) {
							case "v": //crafting
								System.out.println("Product: ");
								Recipe craftr = getRecipe(retrieveSequence(p1),p1);
								if(craftr!=null){
									p1.craft(craftr);
								}
								break;
							case ">": //capturing
								System.out.println("Target: ");
								p1.capture(otherPlayer(p1),unitorder.get(decode(retrieveSequence(p1))));
								break;
							case "":
								p1.isTurn = false;
								break;
							default:
								break;
							}
							
						}
						System.out.println(p1.name +" "+p1.actionsTaken()+"/"+p1.actionsCap()+": ");
						p1c = retrieveSequence(p1);
					}
					p1.isTurn = false;
				}
			}
			if(doGame) {
			
				System.out.println("3");
				freeze(1000);
				System.out.println("2");
				freeze(1000);
				System.out.println("1");
				freeze(1000);
				System.out.println("GO");
				doneMove = new ArrayList<Player>();
				for(int i=0; i<players.size(); i++) {
					players.get(i).choice = null;
					players.get(i).startSequence();
				}
				while(doneMove.size()<players.size()) {
					for(int i=0; i<players.size(); i++) {
						if(!doneMove.contains(players.get(i)) && players.get(i).choice!=null) {
							doneMove.add(players.get(i));
							System.out.println(players.get(i).name+" LOCKED");
							players.get(i).endSequence();
						}
					}
				}
				System.out.println(players.get(0).choice.name +" v "+ players.get(1).choice.name);
				System.out.println("DID: "+doMatch(players.get(0),players.get(0).choice,players.get(1),players.get(1).choice)+"\n");
				players.get(0).choice = null;
				players.get(1).choice = null;
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
		if(draw.catalogue!=null) {
			draw.updateUIElement(draw.catalogue);
			draw.repaint();
		}
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
	public Player otherPlayer(Player compare) {
		for(Player p : players) {
			if(!p.equals(compare))return p;
		}
		return null;
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
	
	public Recipe getRecipe(String useq, Player p) { //given sequence for unit, have player choose/make a recipe
		int n = decode(useq);
		Unit craftu = (n>=0 && n<unitorder.size())?unitorder.get(n):null;
		Recipe craftr = null;
		while(craftu==null) {
			System.out.println("New Unit: ");
			addNewUnit(craftu = new Unit(this,getConsoleInput()));
			System.out.println("Recipe: ");
			craftr = new Recipe(this,getConsoleInput());
			recipes.add(craftr);
		}
		while(craftr==null) {
			System.out.println("Choose Recipe:\n"+encode(0)+": Cancel\n"+encode(1)+": New");
			for(int i=0; i<craftu.recipes.size(); i++) {
				Recipe r = craftu.recipes.get(i);
				System.out.println(encode(i+2)+": "+r);
			}
			try {
				int ans = decode(retrieveSequence(p));
				if(ans==0) {
					return null;
				}else if(ans==1) {
					System.out.println("Recipe: ");
					craftr = new Recipe(this,getConsoleInput());
					recipes.add(craftr);
					return craftr;
				}else {
					if(craftu.recipes.size()>ans-2)
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
	
	//given string of button presses (down, left, right), return int number
	public int decode(String presses) {
		int num = 0;
		for(int i=1; i<=presses.length(); i++) {
			String character = presses.substring(presses.length()-i,presses.length()-i+1);
			int value = 0;
			switch(character) {
			case "v":
				value = 1;
				break;
			case "<":
				value = 2;
				break;
			case ">":
				value = 3;
				break;
			}
			num += value*Math.pow(3,i-1);
		}
		return num-1;
	}
	//given int number, return string of button presses (down, left, right)
	public String encode(int number) {

		String ret = "";
		number++;
		for(int i=0; number>0; i++) {
			int larger = (int)(Math.pow(3,i+1));
			int left = number%larger;
			int num = left==0 ? 3 : (int)(left/Math.pow(3,i));
			switch(num) {
			case 1:
				ret="v"+ret;
				break;
			case 2:
				ret="<"+ret;
				break;
			case 3:
				ret=">"+ret;
				break;
				
			}
			number -= num*Math.pow(3,i);
		}
		return ret;
	}
	
	//sleep
	public void freeze(int millis) {
		try {
			Thread.sleep(millis);
		}catch(InterruptedException e){};
	}
	
	public String retrieveSequence(Player p) {
		p.startSequence();
		while(p.awaiting && p.sequence()==null) {
			freeze(1); //chill
		}
		String pc = p.sequence();
		p.endSequence();
		return pc;
	}
	
}