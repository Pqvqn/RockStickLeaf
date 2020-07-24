package rsl;

import java.io.*;
import java.util.*;

public class Inventory {
	
	private Game game;
	private File data;
	private Map<Unit,Integer> inventory;
	
	public Inventory(Game g,File f) {
		game = g;
		data = f;
		try {
			buildTable();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Integer numberOf(Unit u) {
		if(!inventory.containsKey(u))return 0;
		return inventory.get(u);
	}
	
	private void buildTable() throws IOException {
		inventory = new HashMap<Unit,Integer>();
		data.getParentFile().mkdirs();
		data.createNewFile();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(data),"UTF-8"));
		String curr = reader.readLine();
		while(curr!=null) { //add all units' quantities to hashmap
			Unit material; //unit
			Integer quantity;//number required 
			if(curr.contains("*")) { //if quantity given, use it, otherwise use 1
				material = game.units.get(curr.split("\\*")[0]);
				quantity = Integer.parseInt(curr.split("\\*")[1]);
			}else {
				material = game.units.get(curr);
				quantity = 1;
			}
			if(inventory.containsKey(material))quantity+=inventory.get(material); //sum if material listed multiple times
			inventory.put(material,quantity);
			curr = reader.readLine();
		}
		reader.close();
	}
}
