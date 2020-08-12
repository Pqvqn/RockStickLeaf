package rsl;

import java.io.*;
import java.nio.file.*;
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
	public ArrayList<Player> turnOrder; //order in which players take their turn (decided by inverse of throw order)
	public int turn;
	public int playerCount;
	public MatchupLookup matchups;
	public Map<String,Unit> units; //map: get unit object from its name as a string
	public ArrayList<Unit> unitorder; //units stored in order
	public ArrayList<DefaultUnit> defaults;
	public Set<Recipe> recipes;
	
	public Game(int playerNum) {
		super("Rock Stick Leaf");
		
		draw = new Draw(this);
		draw.textinput = new UITextInput(this,X_RESOL/2,Y_RESOL/2);
		
		//window settings
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(false);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
		setFocusable(true);
		requestFocus();
		//setBackground(Color.GRAY);
		//setForeground(Color.GRAY);
				
		add(draw);
		pack();
		
		savename = getScreenInput("Save name:");

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
		controls = new ArrayList<Controls>();
		playerCount = playerNum;
		players = new ArrayList<Player>();
		for(int i=0; i<playerCount; i++) { //create each player
			String pname = getScreenInput("Player "+i+": "); //ask for player name
			players.add(new Player(this,pname,new File(filepath+"/inventory_"+pname+".txt"),i));
			draw.inventories.add(new UIInventory(this,70+i*330,100,20,players.get(i)));
		}
		for(int i=0; i<controls.size(); i++) {
			addKeyListener(controls.get(i));
		}
		
		try {
			loadMatch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//create ui
		draw.match = new UIMatch(this,X_RESOL/2,Y_RESOL-100,50,players);
		draw.catalogue = new UICatalogue(this,50,Y_RESOL/2+100,10,25);

		while(defaults.isEmpty()) { //ask players for beginning units
			String listu = getScreenInput("Comma-Separated Default Unit List:");
			String[] components = listu.split(",");
			for(int i=0; i<components.length; i++) {
				addNewUnit(new DefaultUnit(this,components[i]));
			}
		}	
		String def = "";
		for(DefaultUnit du : defaults)def+=", "+du.name;
		draw.match.dispNotif("Defaults are: "+def.substring(2));
		
		boolean doGame = true;
		while(doGame) {
			for(int i=turn; i<turnOrder.size() && doGame; i++) { //let each player take their turn
				Player p1 = turnOrder.get(i); //in order of who did turn last
				p1.isTurn = true;
				draw.match.setMenu(p1, "Choose Action:",new int[] {0,1,2}, new String[] {"Craft","Save","Target"}, true);
				String p1c = retrieveSequence(p1); //get choice of action
				while(p1.isTurn && p1c.length()>0) { //continue asking until turn ended (presses up without choice before it)
					if(p1.canAct()) {
						switch(p1c) {
						case "v": //v = crafting
							draw.match.setMenu(p1, "Product:",new int[] {-1}, new String[] {"Input Unit Code"}, true);
							Recipe craftr = getRecipe(retrieveSequence(p1),p1);
							if(craftr!=null){
								p1.craft(craftr);
							}
							break;
						case ">": //> = capturing
							Unit u = null;
							while(u == null){ //ask for target until valid one given
								draw.match.setMenu(p1, "Target:",new int[] {-1}, new String[] {"Input Unit Code"}, true);
								int t = decode(retrieveSequence(p1));
								u = (t<unitorder.size() && t>=0)?unitorder.get(t):null;
							}
							p1.target(otherPlayer(p1),u); //add targets to list
							break;
						case "<": //< = save
							draw.match.setMenu(p1, "Save:",new int[] {0,1,2}, new String[] {"Cancel","Save & Continue","Save & Quit"}, true);
							int t = decode(retrieveSequence(p1));
							if(t==1 || t==2) {
								try {
									writeFiles();
									draw.match.dispNotif("Saved");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							if(t==2) {
								//p1.isTurn = false;
								p1c = "";
								doGame = false;
							}
							break;
						case "": //^ only = end turn
							p1.isTurn = false;
							break;
						default:
							break;
						}
						
					}
					if(doGame) {
						draw.match.setMenu(p1, "Choose Action:",new int[] {0,1,2}, new String[] {"Craft","Save","Target"}, true);
						p1c = retrieveSequence(p1);
					}
				}
				if(doGame) {
					draw.match.setMenu(p1, "", null, null, false);
					p1.isTurn = false;
				}
			}
			if(doGame) {
				
				for(int i=0; i<players.size(); i++) { 
					players.get(i).capture(otherPlayer(players.get(i))); //take all targets hostage
				}
				
				//countdown for throw
				draw.match.count(3,true);
				freeze(1000);
				draw.match.count(2,true);
				freeze(1000);
				draw.match.count(1,true);
				freeze(1000);
				draw.match.count(0,true);
				turnOrder = new ArrayList<Player>();
				for(int i=0; i<players.size(); i++) { //prepare players
					players.get(i).choice = null;
					players.get(i).startSequence();
				}
				while(turnOrder.size()<players.size()) { //test if players have chosen yet until all players have chosen
					for(int i=0; i<players.size(); i++) {
						if(!turnOrder.contains(players.get(i)) && players.get(i).choice!=null) { //if player has just thrown unit
							turnOrder.add(turnOrder.size(),players.get(i)); //add player to end of turn order list
							players.get(i).endSequence();
						}
					}
				}
				draw.match.count(0,false);
				if(doMatch(players.get(0),players.get(0).choice,players.get(1),players.get(1).choice)) {

				}else {
					draw.match.dispNotif(players.get(0).choice.name +" v "+ players.get(1).choice.name+"; Match Failed");
				}
				players.get(0).choice = null;
				players.get(1).choice = null;
				turn = 0;
			}
		}
		//post-game code
		System.exit(0);
	}

	private boolean doMatch(Player p1, Unit u1, Player p2, Unit u2) { //returns if match is successful; carries match out if can
		if(!(p1.has(u1) && p2.has(u2)))return false; //match fails if players don't own thrown units
		if(u1.equals(u2)) { //tie if both units are the same
			doSwap(p1,p2,u1,u2); //swap units
			p1.resetActions(false);
			p2.resetActions(false);
			draw.match.dispNotif(players.get(0).choice.name +" v "+ players.get(1).choice.name+"; TIE");
			return true;
		}
		Matchup m = new Matchup(new Unit[] {u1,u2});
		Unit winningUnit = matchups.victor(m);
		if(winningUnit == null) { //if tie
			doSwap(p1,p2,u1,u2); //swap units
			p1.resetActions(false);
			p2.resetActions(false);
			draw.match.dispNotif(players.get(0).choice.name +" v "+ players.get(1).choice.name+"; TIE");
			return true;
		}
		Unit losingUnit = winningUnit.equals(u1) ? u2:u1;
		Player winner = losingUnit.equals(u1)? p2:p1;
		Player loser = losingUnit.equals(u1)? p1:p2;
		doTransfer(winner,loser,winningUnit,losingUnit); //transfer loser to winner
		winner.resetActions(true);
		loser.resetActions(false);
		draw.match.dispNotif(players.get(0).choice.name +" v "+ players.get(1).choice.name+"; "+winner.name+" WINS");
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
		loser.targets = new ArrayList<Unit>(); //reset target lists
		winner.targets = new ArrayList<Unit>();
		winner.give(losing); //winner gets the losing unit	
	}
	
	private void doSwap(Player p1, Player p2, Unit p1u, Unit p2u) { //swaps thrown units
		if(!(p1u instanceof DefaultUnit))p1.take(p1u); //take units if not default
		if(!(p2u instanceof DefaultUnit))p2.take(p2u);
		p1.give(p2u); //give each player the other player's unit
		p2.give(p1u);
		for(Unit hostage : p1.targets) { //give hostages back to owner
			p2.give(hostage);
		}
		for(Unit hostage : p2.targets) { //give hostages back to owner
			p1.give(hostage);
		}
		p1.targets = new ArrayList<Unit>(); //reset target lists
		p2.targets = new ArrayList<Unit>();
	}
	private void addNewUnit(Unit u) {
		if(u instanceof DefaultUnit)defaults.add((DefaultUnit)u); //add to default list if needed
		units.put(u.name,u); //put in unit lists
		unitorder.add(u);
		try {
			u.imgFile = unitImage(u);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	private void createRecipes() throws IOException { //builds all recipes from file
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
	private void loadMatch() throws IOException { //loads extra data for pvp match
		turnOrder = new ArrayList<Player>();
		turn = 0;
		for(Player p : players) {
			turnOrder.add(p);
		}
		File b = new File(filepath+"/match_data.txt");
		b.getParentFile().mkdirs();
		b.createNewFile();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(b),"UTF-8"));
		String curr = reader.readLine();
		while(curr!=null) {
			if(curr.length()>0 && curr.substring(0,1).equals("#")) { //if on a match header
				String[] names = curr.substring(1).split("#");
				boolean correct = names.length == players.size();
				for(Player p : players) { //test if header contains all players with no extra
					boolean has = false;
					for(String n : names) {if(n.equals(p.name))has = true;}
					if(!has)correct=false;
				}
				if(correct) { //if this is the correct header
					turnOrder = new ArrayList<Player>();
					curr = reader.readLine();
					while(curr!=null && curr.length()>0 && curr.substring(0,1).equals(":")) { //read all players
						String[] data = curr.split("\\|");
						Player p = player(data[0].substring(1));
						turnOrder.add(p); //add players to turnOrder in order of how they are listed
						for(int i=1; i<data.length; i++) {
							String[] components = data[i].split(":");
							switch(components[0]) {
							case "TURN": //set this player to the current turn
								turn = turnOrder.size()-1;
								break;
							case "ACT": //set player's actions taken/cap
								String[] components2 = components[1].split("/");
								p.setActions(Integer.parseInt(components2[0]),Integer.parseInt(components2[1]));
								break;
							case "TARGET": //set player's target list
								String[] components3 = components[1].split(",");
								for(int j=0; j<components3.length; j++) {
									p.targets.add(units.get(components3[j]));
								}
								break;
							}
						}
						curr = reader.readLine();
					}
					curr = null;
				}else {
					curr = reader.readLine();
				}
			}else {
				curr = reader.readLine();
			}
		}
		reader.close();
		
	}
	
	
	public Player otherPlayer(Player compare) { //returns player that is not the given player
		for(Player p : players) {
			if(!p.equals(compare))return p;
		}
		return null;
	}
	public Player player(String name) { //returns player with given name
		for(Player p : players) {
			if(name.equals(p.name))return p;
		}
		return null;
	}
	
	public String getScreenInput(String prompt) { //asks for inputted string on screen, prompt displayed above field
		draw.textinput.startText(prompt);
		draw.displayUIElement(draw.textinput,true);
		addKeyListener(draw.textinput);
		while(draw.textinput.getSubmission()==null) {
			freeze(1);
			draw.updateUIElement(draw.textinput);
			draw.repaint();
		}
		removeKeyListener(draw.textinput);
		draw.displayUIElement(draw.textinput,false);
		return draw.textinput.getSubmission();
	}
	
	public Recipe getRecipe(String useq, Player p) { //given sequence for unit, have player choose/make a recipe
		int n = decode(useq); //index of chosen unit
		Unit craftu = (n>=0 && n<unitorder.size())?unitorder.get(n):null; //unit to be crafted, if it exists yet
		Recipe craftr = null;
		while(craftu==null) { //if new unit must be made (sequence doesn't correspond to a unit)
			addNewUnit(craftu = new Unit(this,getScreenInput("New Unit Name:")));
			draw.match.dispNotif(craftu.name+" created");
			String r = "no";
			while(!Recipe.validRecipe(this,r) && !r.isEmpty()) {
				r = getScreenInput("New Recipe:");
			}
			if(!r.isEmpty()) {
				craftr = new Recipe(this,r);
				draw.match.dispNotif(craftr.toString()+" added");
				recipes.add(craftr);
			}
		}
		while(craftr==null) { //until recipe chosen, ask
			int[] nums = new int[2+craftu.recipes.size()];
			String[] strs = new String[2+craftu.recipes.size()];
			nums[0]=0;strs[0]="Cancel";
			nums[1]=1;strs[1]="New Recipe";
			for(int i=0; i<craftu.recipes.size(); i++) {
				Recipe r = craftu.recipes.get(i);
				nums[i+2] = i+2;
				strs[i+2] = r+"";
			}
			draw.match.setMenu(p, "Choose Recipe:",nums,strs, true);
			try {
				int ans = decode(retrieveSequence(p));
				if(ans<=1) { //cancel crafting
					return null;
				}else if(ans==1) { //create new recipe
					String r = "no";
					while(!Recipe.validRecipe(this,r) && !r.isEmpty()) {
						r = getScreenInput("New Recipe:");
					}
					if(!r.isEmpty()) {
						craftr = new Recipe(this,r);
						draw.match.dispNotif(craftr.toString()+" added");
						recipes.add(craftr);
					}
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
		
		//match data
		File dataFile = new File(filepath+"/match_data.txt");
		List<String> textlines = Files.readAllLines(dataFile.toPath());
		FileWriter dataWriter = new FileWriter(dataFile);
		boolean remove = false;
		for(int i=0; i<textlines.size(); i++) {
			String curr = textlines.get(i);
			if(curr.length()>0 && curr.substring(0,1).equals("#")) { //if on a match header
				String[] names = curr.substring(1).split("#");
				boolean correct = names.length == players.size();
				for(Player p : players) { //test if header contains all players with no extra
					boolean has = false;
					for(String n : names) {if(n.equals(p.name))has = true;}
					if(!has)correct=false;
				}
				if(correct) { //if found header
					remove = true;
				}else {
					remove = false;
				}
			}
			if(!remove) {
				dataWriter.write(curr+"\n");
			}
		}

		String w = "";
		for(Player p : players)w+="#"+p.name;
		dataWriter.write(w+"\n");
		for(int i=0; i<players.size(); i++) {
			Player p = players.get(i);
			w = ":"+p.name;
			if(p.isTurn) {
				w += "|TURN";
			}
			w += "|ACT:"+p.actionsTaken()+"/"+p.actionsCap();
			if(!p.targets.isEmpty()) {
				w += "|TARGET:";
				for(Unit t : p.targets) {
					w+=t.name+",";
				}
			}
			dataWriter.write(w+"\n");
		}
		dataWriter.close();
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
		while(p.sequence()==null) {
			freeze(1); //chill
		}
		String pc = p.sequence();
		p.endSequence();
		return pc;
	}
	
	//returns unit's image file
	public File unitImage(Unit u) throws IOException {
		File ret = new File(filepath+"/unim/"+u.name+".png");
		ret.getParentFile().mkdirs();
		ret.createNewFile();
		return ret;
	}
	
}