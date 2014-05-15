package moara.normalization.functions;

import java.util.HashMap;

import moara.bio.entities.Organism;
import moara.normalization.NormalizationConstant;
import moara.normalization.NormalizationUtil;
import moara.normalization.entities.GenePrediction;
import moara.util.Constant;
import moara.wrapper.secondstring.SecondStringStringDistance;

/*
 * Description: Use SecondString to do gene normalization
 */

public class SoftMatchingNormalization extends GeneNormalization {

	public static SecondStringStringDistance sd = null;
	public int typeSimilarity;
	public NormalizationUtil util;
	public double pctSim;
	
	public SoftMatchingNormalization(Organism organism, double threshold) {
		super(organism);
		this.typeSimilarity = Constant.DISTANCE_SOFT_TFIDF;
		//this.pctSim = NormalizationConstant.DEFAULT_PCT_SIMILARITY;
		this.pctSim = threshold;
		this.util = new NormalizationUtil();
	}
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		sd = new SecondStringStringDistance(this.typeSimilarity);
		if (this.typeSimilarity==Constant.DISTANCE_SOFT_TFIDF)
			sd = this.util.trainSoftTFIDF(typeSimilarity,organism,null,sd);
		
		System.err.println("SoftTFIDF initilaized.");
	}

	@Override
	protected HashMap<String, GenePrediction> normalizeMention(String mention) {
		//HashMap<String,GenePrediction> predicted = new HashMap<String,GenePrediction>();
		// Soft matching
		MatchingSoft ms = new MatchingSoft(this.organism, pctSim, sd);
		super.gsa.soft = sd.soft;
		ms.match(mention,this.organism);
		//System.err.println(mention.Text() + ":" + me.Predicted().size());
		/*if (me.Predicted().size()!=0) {
			predicted = me.Predicted();
		}
		return predicted;*/
		
		return ms.Predicted();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
