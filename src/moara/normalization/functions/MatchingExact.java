package moara.normalization.functions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import moara.bio.entities.Organism;
import moara.gene.dbs.DBGeneSynonym;
import moara.gene.entities.GeneSynonym;
import moara.normalization.NormalizationConstant;
import moara.normalization.entities.GenePrediction;

public class MatchingExact extends MatchingProcedure {
	
	public MatchingExact() {
		super();
	}
	
	public void match(String mention, Organism organism) {
		try {
			this.predicted = new HashMap<String,GenePrediction>();
			DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(organism);
			ArrayList<GeneSynonym> exact = dbGeneSynonym.getGenesSynonym(mention,
				false,false,true);
			if (exact.size()!=0) {
				//System.err.println("exact-match " + exact.size());
				for (int i=0; i<exact.size(); i++) {
					GeneSynonym gs = exact.get(i);
					GenePrediction gp = new GenePrediction(gs.Id(),mention,
						gs.Synonym(),gs.OriginalSynonym());
					gp.setTypeMatching(NormalizationConstant.MATCHING_EXACT);
					//if (word.length()>2 || (word.length()<=2 && ar.decideSimilarity(pmdi,gp)))
					this.predicted.put(gs.Id(),gp);
				}
			}
		}
		catch(SQLException ex) { 
	     	System.err.println(ex);
	    }
	}
	
}
