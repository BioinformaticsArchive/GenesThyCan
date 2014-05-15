package moara.bio.entities;

import java.sql.SQLException;
import moara.bio.dbs.DBOrganism;

public class Organism {

	private String code;
	private String name;
	private String shortName;
	
	public Organism(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public Organism(String code) {
		this.code = code;
		loadData();
	}
	
	public String Code() {
		return this.code;
	}
	
	public String Name() {
		return this.name;
	}
	
	public String ShortName() {
		return this.shortName;
	}
	
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	private void loadData() {
		try {
			DBOrganism dbOrganism = new DBOrganism();
			Organism org = dbOrganism.loadData(this.code);
			this.name = org.name;
			this.shortName = org.ShortName();
		}
		catch(SQLException ex) { 
		 	System.err.println(ex);
		}
	}
	
}
