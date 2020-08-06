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
		
		
		//engine classes
		playerCount = playerNum;
		players = new ArrayList<Player>();
		for(int i=0; i<playerCount; i++) { //create each player
			System.out.println("Player "+i+": ");
			String pname = getConsoleInput(); //ask for player name
			players.add(new Player(this,pname,new File(filepath+"/inventory_"+pname+".txt"),i));
			draw.inventories.add(new UIInventory(this,70+i*300,100,20,players.get(i)));
		}	
		//create ui
		draw.match = new UIMatch(this,X_RESOL/2,Y_RESOL-100,50,players);
		draw.catalogue = new UICatalogue(this,50,Y_RESOL/2+100,10,25);
		
		//TODO: fix this garbage (two controls instances?? one in player class, one here)
		controls = new ArrayList<Controls>();
		for(int i=0; i<players.size(); i++) {
			controls.add(new Controls(players.get(i),i));
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

		while(defaults.isEmpty()) { //ask players for beginning units
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
			
			for(int i=doneMove.size()-1; i>=0; i--) { //let each player take their turn
				Player p1 = doneMove.get(i); //in order of who did turn last
				p1.targets = new ArrayList<Unit>(); //reset targets
				System.out.println(p1.name +" "+p1.actionsTaken()+"/"+p1.actionsCap()+": ");
				
				p1.isTurn = true;
				String p1c = retrieveSequence(p1); //get choice of action
				
				if(p1c.equals("<")) { //< = quit and save
					doGame = false;
				}else {
					while(p1.isTurn && p1c.length()>0) { //continue asking until turn ended (presses up without choice before it)
						if(p1.canAct()) {
							switch(p1c) {
							case "v": //v = crafting
								System.out.println("Product: ");
								Recipe craftr = getRecipe(retrieveSequence(p1),p1);
								if(craftr!=null){
									p1.craft(craftr);
								}
								break;
							case ">": //> = capturing
								System.out.println("Target: ");
								p1.capture(otherPlayer(p1),unitorder.get(decode(retrieveSequence(p1))));
								break;
							case "": //^ only = end turn
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
				//countdown for throw
				System.out.println("3");
				freeze(1000);
				System.out.println("2");
				freeze(1000);
				System.out.println("1");
				freeze(1000);
				System.out.println("GO");
				doneMove = new ArrayList<Player>();
				for(int i=0; i<players.size(); i++) { //prepare players
					players.get(i).choice = null;
					players.get(i).startSequence();
				}
				while(doneMove.size()<players.size()) { //test if players have chosen yet until all players have chosen
					for(int i=0; i<players.size(); i++) {
						if(!doneMove.contains(players.get(i)) && players.get(i).choice!=null) { //if player has just thrown unit
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
		//post-game code
		System.out.println("Game has ended");
		System.exit(0);
	}

	private boolean doMatch(Player p1, Unit u1, Player p2, Unit u2) { //returns if match is successful; carries match out if can
		if(!(p1.has(u1) && p2.has(u2)))return false; //match fails if players don't own thrown units
		if(u1.equals(u2)) { //tie if both units are the same
			doSwap(p1,p2,u1,u2); //swap units
			p1.resetActions(false);
			p2.resetActions(false);
			return true;
		}
		Matchup m = new Matchup(new Unit[] {u1,u2});
		Unit winningUnit = matchups.victor(m);
		if(winningUnit == null) { //if tie
			doSwap(p1,p2,u1,u2); //swap units
			p1.resetActions(false);
			p2.resetActions(false);
			return true;
		}
		Unit losingUnit = winningUnit.equals(u1) ? u2:u1;
		Player winner = losingUnit.equals(u1)? p2:p1;
		Player loser = losingUnit.equals(u1)? p1:p2;
		doTransfer(winner,loser,winningUnit,losingUnit); //transfer loser to winner
		winner.resetActions(true);
		loser.resetActions(false);
		return true;
	}
	
	private void doTransfer(Player winner, Player loser, Unit winning, Unit losing) { //gives winner loser's unit
		if(winning instanceof DefaultUnit) { //winner gets copy of their unit if it was a default
			winner.give(winning);
		}
		if(!(losing instanceof DefaultUnit)){ //loser loses their unit if it wasn't a default
			loser.take(losing);
		}
		for(Unit hostage : winner.targets) { //take care of winner's target captures
			if(!winning.equals(hostage)) {
				Matchup m = new Matchup(new Unit[] {winning,hostage});
				Unit winningUnit = matchups.victor(m);
				if(winningUnit.equals(winning)) {
					winner.give(hostage); //winner takes target if their thrown unit beats the hostage
				}else{
					loser.give(hostage); //if winning unit loses to hostage, the hostage is returned
				}
			}else {
				loser.give(hostage); //return hostage if tied from being the same unit
			}
		}
		for(Unit hostage : loser.targets) { //take care of loser's target captures
			winner.give(hostage); //givem hostages back
		}
		winner.give(losing); //winner gets the losing unit
		System.out.println(winner);
		System.out.println(loser);
		System.out.println(winner.name+" WIN");
		
	}
	
	private void doSwap(Player p1, Player p2, Unit p1u, Unit p2u) { //swaps thrown units
		if(!(p1u instanceof DefaultUnit))p1.take(p1u); //take units if not default
		if(!(p2u instanceof DefaultUnit))p2.take(p2u);
		p1.give(p2u); //give each player the other player's unit
		p2.give(p1u);
		System.out.println(p1);
		System.out.println(p2);
		System.out.println("TIE");
	}
	private void addNewUnit(Unit u) {
		if(u instanceof DefaultUnit)defaults.add((DefaultUnit)u); //add to default list if needed
		units.put(u.name,u); //put in unit lists
		unitorder.add(u);
		if(draw.catalogue!=null) { //update catalogue ui
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
			if(curr.substring(0,1).equals(":")) { //tests if this unit is a default (has : as prefix)
				u = new DefaultUnit(this,curr.substring(1));
			}else {
				u = new Unit(this,curr);
			}
			addNewUnit(u);
			curr = reader.readLine();
		}
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
		reader.close();
	}
	public Player otherPlayer(Player compare) { //returns player that is not the given player
		for(Player p : players) {
			if(!p.equals(compare))return p;
		}
		return null;
	}
	
	public String getConsoleInput() { //asks for inputted string in console
		String st = s.nextLine();
		return st;
	}
	
	public Recipe getRecipe(String useq, Player p) { //given sequence for unit, have player choose/make a recipe
		int n = decode(useq); //index of chosen unit
		Unit craftu = (n>=0 && n<unitorder.size())?unitorder.get(n):null; //unit to be crafted, if it exists yet
		Recipe craftr = null;
		while(craftu==null) { //if new unit must be made (sequence doesn't correspond to a unit)
			System.out.println("New Unit: ");
			addNewUnit(craftu = new Unit(this,getConsoleInput()));
			System.out.println("Recipe: ");
			craftr = new Recipe(this,getConsoleInput());
			recipes.add(craftr);
		}
		while(craftr==null) { //until recipe chosen, ask
			System.out.println("Choose Recipe:\n"+encode(0)+": Cancel\n"+encode(1)+": New"); //ask player which recipe to do
			for(int i=0; i<craftu.recipes.size(); i++) {
				Recipe r = craftu.recipes.get(i);
				System.out.println(encode(i+2)+": "+r);
			}
			try {
				int ans = decode(retrieveSequence(p));
				if(ans==0) { //cancel crafting
					return null;
				}else if(ans==1) { //create new recipe
					System.out.println("Recipe: ");
					craftr = new Recipe(this,getConsoleInput());
					recipes.add(craftr);
					return craftr;
				}else { //pick from chosen recipe
					if(craftu.recipes.size()>ans-2) //choose recipe as long as it exists
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
		ArrayList<Unit> reorder = new ArrayList<Unit>();
		for(Unit u : unitorder) { //reorder units based on complexity
			if(reorder.isEmpty()) { //if first, just add
				reorder.add(u);
				u.complexity(); //make sure complexity is calculated
			}else {
				int left = 0; //set left and right bounds
				int right = reorder.size();
				int comp = u.complexity();
				while(right-left>1) { //while not zeroed in on correct placement
					int divider = (right-left)/2+left; //dividing index
					if(reorder.get(divider).lastComplexity<comp) {//test if complexity is lower or higher than divider, adjust range
						left = divider;
					}else if (reorder.get(divider).lastComplexity>comp){
						right = divider;
					}else {
						if(unitorder.indexOf(u)<unitorder.indexOf(reorder.get(divider))) { //if same complexity, use original ordering
							right = divider;
						}else {
							left = divider;
						}
					}
				}
				if(reorder.get(0).lastComplexity>comp) { //make sure first index can be replaced
					reorder.add(0,u);
				}else if(right<reorder.size() && reorder.get(right).lastComplexity<comp) { //place relative to right side
					reorder.add(right+1,u);
				}else {
					reorder.add(right,u);
				}

			}
		}
		for(Unit u : reorder) { //list unit names in order, start with : if default
			unitWriter.write((u instanceof DefaultUnit ? ":" : "")+u.name+"\n");
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
	
	public String retrieveSequence(Player p) { //returns a string sequence of dir keys pressed by player
		p.startSequence();
		while(p.awaiting && p.sequence()==null) {
			freeze(1); //chill
		}
		String pc = p.sequence();
		p.endSequence();
		return pc;
	}
	
}