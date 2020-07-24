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
	
	public Unit victor(Matchup m) {
		return table.get(m);
	}
	public Unit victor(Unit a, Unit b) {
		return victor(new Matchup(new Unit[] {a,b}));
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
			String[] contenders = curr.split(">|<");
			Unit[] units = new Unit[contenders.length];
			for(int i=0; i<contenders.length; i++) {
				units[i] = game.units.get(contenders[i]);
			}
			Matchup m = new Matchup(units);
			if(curr.contains(">")) {
				addResult(m,units[0]);
			}else if(curr.contains("<")) {
				addResult(m,units[1]);
			}
			
			curr = reader.readLine();
		}
		reader.close();
	}
}
