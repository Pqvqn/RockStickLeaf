package rsl;

public class Matchup {

	private Unit[] units;
	
	public Matchup(Unit[] contenders) {
		units = contenders;
	}
	
	public Unit[] contenders() { //returns units involved in this matchup
		return units;
	}
	
	@Override
	public boolean equals(Object o) { //overrides equals; is this the same matchup
		if(!(o instanceof Matchup))
			return false;
		Matchup other = (Matchup)o;
		for(Unit u : units) {
			if(!other.involves(u))return false;
		}
		for(Unit u : other.contenders()) {
			if(!involves(u))return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {//overrides hashcode
		if(units[0].name().compareTo(units[1].name()) >= 0) {
			return(units[0].name()+"#"+units[1].name()).hashCode();
		}else {
			return(units[1].name()+"#"+units[0].name()).hashCode();
		}
	}
	
	public boolean involves(Unit u) { //if this matchup includes a certain unit
		for(Unit b : units) {
			if(b.equals(u))return true;
		}
		return false;
	}
	
	public String toString() {
		String match = "";
		for(Unit u : units) {
			match+=u.name()+" vs ";
		}
		match = match.substring(0,match.length()-4);
		return match;
	}
}
