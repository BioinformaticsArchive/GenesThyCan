package moara.normalization.functions;

import java.util.ArrayList;
import java.util.HashMap;
import moara.util.Constant;
import moara.bio.entities.Organism;
import moara.normalization.entities.GenePrediction;

public class ExactMatchingNormalization extends GeneNormalization {

	public ExactMatchingNormalization(Organism organism) {
		super(organism);
	}
	
	protected void init() {
		
	}
	
	protected HashMap<String,GenePrediction> normalizeMention(String mention) {
		//HashMap<String,GenePrediction> predicted = new HashMap<String,GenePrediction>();
		// exact matching
		MatchingExact me = new MatchingExact();
		me.match(mention,this.organism);
		//System.err.println(mention.Text() + ":" + me.Predicted().size());
		/*if (me.Predicted().size()!=0) {
			predicted = me.Predicted();
		}
		return predicted;*/
		return me.Predicted();
	}
	
	public static void main(String[] args) {
		Organism yeast = new Organism(Constant.ORGANISM_YEAST);
		ExactMatchingNormalization app = new ExactMatchingNormalization(yeast);
		//String text = "YPK1 and YKR2(YPK2) genes ";
		String text = "alpha subunit of the rod cGMP-gated channel";
		ArrayList<String> variations = app.getFlexibleMentions(text);
		for (int i=0; i<variations.size(); i++)
			System.out.println(variations.get(i));
	}
	
}
