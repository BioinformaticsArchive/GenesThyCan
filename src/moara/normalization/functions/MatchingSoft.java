package moara.normalization.functions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moara.bio.entities.Organism;
import moara.gene.dbs.DBGeneSynonym;
import moara.gene.entities.GeneSynonym;
import moara.normalization.NormalizationConstant;
import moara.normalization.entities.GenePrediction;
import moara.util.text.StringUtil;
import moara.wrapper.secondstring.SecondStringStringDistance;

public class MatchingSoft extends MatchingProcedure {
	public SecondStringStringDistance sd;
	public double pctSim;
	public Organism organism;
	public static StringUtil su = new StringUtil();
	
	public MatchingSoft(Organism organism, double pctSim, SecondStringStringDistance sd){
		super();
		this.sd = sd;
		this.pctSim = pctSim;
	}
	
	public void match(String mention, Organism organism) {
		try {
			this.predicted = new HashMap<String, GenePrediction>();
			DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(organism);

			String cleanMention = su.handleSpecial(mention);
			//String cleanMention = mention;
			
			ArrayList<GeneSynonym> approximate = dbGeneSynonym
					.searchSimilarSynonymMention(cleanMention, pctSim, sd.soft);

			if (approximate.size() != 0) {
				// System.err.println("exact-match " + exact.size());
				for (int i = 0; i < approximate.size(); i++) {
					GeneSynonym gs = approximate.get(i);
					
		    		double simScore = gs.MaxScore();
		    		
		    		if (simScore>=pctSim){
						GenePrediction gp = new GenePrediction(gs.Id(),
								cleanMention, gs.Synonym(), gs.OriginalSynonym());
						gp.setTypeMatching(NormalizationConstant.MATCHING_APPROXIMATE);
						// if (word.length()>2 || (word.length()<=2 &&
						// ar.decideSimilarity(pmdi,gp)))
						gp.setScoreDisambig(simScore);
											
						if (this.predicted.containsKey(gs.Id())){
							GenePrediction preGp = this.predicted.get(gs.Id());
							
							if (preGp != null){
								if (simScore > preGp.ScoreDisambig()){
									this.predicted.put(gs.Id(), gp);
								}
							}
						}else{
							this.predicted.put(gs.Id(), gp);
						}
		    		}
				}
			}

		}
		catch(SQLException ex) { 
	     	System.err.println(ex);
	    }
	}
	
}
