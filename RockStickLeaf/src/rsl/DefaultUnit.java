package rsl;

import java.util.ArrayList;

public class DefaultUnit extends Unit{

	public DefaultUnit(Game g, String unitdata) {
		super(g,unitdata);
	}
	
	
	public int complexity(ArrayList<Unit> branch) { //default units are infinite, so complexity of 0
		return 0;
	}
}
