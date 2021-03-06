package rsl;

import java.io.*;
import java.util.*;

public class MatchupLookup {

	private Game game;
	private Map<Matchup,Unit> table;
	private File data;
	
	public MatchupLookup(Game g,File f) {
		game = g;
		data = f;
		try {
			buildTable();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Unit victor(Matchup m) { //winner of matchup
		if(!table.containsKey(m)) {
			createMatch(m);
		}
		return table.get(m);
	}
	public Unit victor(Unit a, Unit b) {
		return victor(new Matchup(new Unit[] {a,b}));
	}
	public void createMatch(Matchup m) { //gets player input to determine winner
		String seq = null;
		while(seq == null) { //while no agreement between players on winner
			for(int i=0; i<game.players.size(); i++) { //for each player, ask winner, proceed when there is a consensus
				Player p = game.players.get(i);
				int[] nums = new int[m.contenders().length+1]; //get all unit names/numbers for menu
				String[] strs = new String[m.contenders().length+1];
				nums[0] = -2;
				strs[0] = "TIE";
				for(int j=0; j<nums.length-1; j++) {
					nums[j+1] = game.unitorder.indexOf(m.contenders()[j]);
					strs[j+1] = m.contenders()[j].name;
				}
				game.draw.match.setMenu(p,"Winner of "+m+" is:",nums,strs,true);
				p.isTurn = true;
				String resp = game.retrieveSequence(p);
				if(resp=="")resp = "tie";
				p.isTurn = false;
				if(seq==null) { //first ask
					seq = resp;
				}else {
					if(!seq.equals(resp)) { //keep asking if no consensus
						seq = null;
					}
				}
				game.draw.match.setMenu(p,"",null,null,false);
			}
			if(seq!=null) { //test if chosen sequence corresponds to a contender
				boolean ok = false;
				if(seq.equals("tie")) {
					ok = true;
				}else {
					for(Unit u:m.contenders()) {
						if(game.unitorder.get(game.decode(seq)).equals(u)) {
							ok = true;
						}
					}	
				}
				if(!ok)seq = null;
			}
		}
		if(seq.equals("tie")) {
			game.draw.match.dispNotif(m+" is a tie");
			addResult(m,null);
		}else {
			Unit u = game.unitorder.get(game.decode(seq));
			game.draw.match.dispNotif(u.name +" is victor of "+m);
			addResult(m,u);
		}

	}
	public void addResult(Matchup m, Unit victor) {
		table.put(m,victor);
	}
	
	public void buildTable() throws IOException {
		table = new HashMap<Matchup,Unit>();
		data.getParentFile().mkdirs();
		data.createNewFile();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(data),"UTF-8"));
		String curr = reader.readLine();
		while(curr!=null) { //add all units to hashmap
			String[] contenders = curr.split(">|<|="); //split contenders by comparison symbol
			Unit[] units = new Unit[contenders.length];
			for(int i=0; i<contenders.length; i++) {
				units[i] = game.units.get(contenders[i]);
			}
			Matchup m = new Matchup(units);
			if(curr.contains(">")) { //get winner based on symbol used
				addResult(m,units[0]);
			}else if(curr.contains("<")) {
				addResult(m,units[1]);
			}else if(curr.contains("=")) {
				addResult(m,null);
			}
			
			curr = reader.readLine();
		}
		reader.close();
	}
	
	public void writeFile() throws IOException {
		FileWriter matchupWriter = new FileWriter(data);
		Iterator<Matchup> matchuperator = table.keySet().iterator();
		while(matchuperator.hasNext()) { //take recipes from set and write them
			Matchup m = matchuperator.next();
			Unit[] contenders = m.contenders();
			String operator = (victor(m)==null)?"=":(victor(m).equals(contenders[0]))?">":"<";
			
			matchupWriter.write(contenders[0].name+operator+contenders[1].name+"\n");
		}
		matchupWriter.close();
	}
}
