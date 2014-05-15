package moara.bio.functions;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.sql.SQLException;
import moara.bio.BioConstant;
import moara.bio.entities.Organism;
import moara.bio.dbs.DBOrganism;
import moara.bio.dbs.DBOrganismSynonym;
import moara.util.text.StringUtil;
import moara.util.lexicon.GreekLetter;

public class OrganismSynonymExtraction {

	public OrganismSynonymExtraction() {
	
	}
	
	public void extractDerivedSynonym() {
		DBOrganism dbOrganism = new DBOrganism();
		DBOrganismSynonym dbOrganismSynonym = new DBOrganismSynonym();
		try {
			dbOrganismSynonym.deleteSynonymCategory(BioConstant.ORGANISM_DERIVED);
			ArrayList<Organism> organisms = dbOrganism.getScientificOrganisms();
			System.err.println("organisms=" + organisms.size());
			for (int i=0; i<organisms.size(); i++) {
				Organism org = organisms.get(i);
				StringTokenizer tokens = new StringTokenizer(org.Name());
				String first = tokens.nextToken();
				String second = tokens.nextToken();
				boolean firstOk = isValidFirst(first);
				boolean secondOk = isValidSecond(second);
				/*if (org.Code().equals("404237") || org.Code().equals("212729")) {
					System.err.println(first + " " + firstOk);
					System.err.println(second + " " + secondOk);
				}*/
				// Mus musculus
				if (firstOk && secondOk)
					dbOrganismSynonym.insertOrganismSynonym(org,first+" "+second,
						BioConstant.ORGANISM_SCIENTIFIC);
				// Mus
				if (firstOk)
					dbOrganismSynonym.insertOrganismSynonym(org,first,
						BioConstant.ORGANISM_DERIVED);
				// musculus
				if (secondOk)
					dbOrganismSynonym.insertOrganismSynonym(org,second,
						BioConstant.ORGANISM_DERIVED);
				// M. musculus
				if (firstOk && secondOk)
					dbOrganismSynonym.insertOrganismSynonym(org,
						first.charAt(0)+". "+second,BioConstant.ORGANISM_DERIVED);
				if ((i%1000)==0) {
					System.err.println(i + "...");
					dbOrganismSynonym.executeCommit();
				}
			}
			dbOrganismSynonym.executeCommit();
		}
		catch(SQLException e) { 
        	System.err.println(e);
        	dbOrganismSynonym.executeRollback();
        }
	}
	
	private boolean isValidFirst(String first) {
		StringUtil su = new StringUtil();
		return su.hasFirstUpperCaseLetter(first);
	}
	
	private boolean isValidSecond(String second) {
		StringUtil su = new StringUtil();
		GreekLetter greek = new GreekLetter();
		return (su.hasOnlyLowerCaseLetters(second) && !greek.isGreekLetter(second));
	}
	
	public static void main(String[] args) {
		OrganismSynonymExtraction app = new OrganismSynonymExtraction();
		app.extractDerivedSynonym();
	}
	
}
