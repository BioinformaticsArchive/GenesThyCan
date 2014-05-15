package moara.bio.entities;

import java.sql.SQLException;
import moara.bio.dbs.DBOrganismSynonym;

public class OrganismSynonym {

	private DBOrganismSynonym dbOrganismSynonym;
	
	public OrganismSynonym() {
		this.dbOrganismSynonym = new DBOrganismSynonym();
	}
	
	public boolean isOrganism(String synonym) {
		boolean test = false;
		try {
			test = this.dbOrganismSynonym.isOrganism(synonym);
		}
		catch(SQLException e) { 
        	System.err.println(e);
        }
		return test;
	}
	
	public boolean isOrganismSynonym(Organism organism, String synonym) {
		boolean test = false;
		try {
			test = this.dbOrganismSynonym.isSynonymsOrganism(organism,synonym);
		}
		catch(SQLException e) { 
        	System.err.println(e);
        }
		return test;
	}
	
}
