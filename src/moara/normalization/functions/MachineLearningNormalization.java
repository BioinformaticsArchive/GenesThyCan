package moara.normalization.functions;

import java.util.ArrayList;
import java.util.HashMap;

import moara.bio.entities.Organism;
import moara.mention.entities.GeneMention;
import moara.normalization.NormalizationConstant;
import moara.normalization.NormalizationUtil;
import moara.normalization.entities.GenePrediction;
import moara.util.Constant;
import moara.wrapper.secondstring.SecondStringStringDistance;

public class MachineLearningNormalization extends GeneNormalization {

	private MatchingMachineLearning matchML;
	private int typeSimilarity;
	private String[] features;
	private String ml;
	private double pctSym;
	private String selection;
	private String featureName;
	private NormalizationUtil util;
	
	public MachineLearningNormalization(Organism organism) {
		super(organism);
		this.typeSimilarity = NormalizationConstant.DEFAULT_STRING_SIMILARITY;
		this.features = NormalizationConstant.DEFAULT_FEATURES;
		this.featureName = NormalizationConstant.DEFAULT_FEATURE_NAME;
		this.ml = NormalizationConstant.DEFAULT_MACHINE_LEARNING;
		this.selection = NormalizationConstant.DEFAULT_PAIR_SELECTION;
		this.pctSym = NormalizationConstant.DEFAULT_PCT_SIMILARITY;
		this.util = new NormalizationUtil();
	}
	
	public void setPctSymilarity(double pct) {
		this.pctSym = pct;
	}
	
	public void setFeatures(String setFeatures) {
		this.features = this.util.getGroupFeatures(setFeatures);
		this.featureName = setFeatures;
	}
	
	public void setStringSimilarity(int similarity) {
		this.typeSimilarity = similarity;
	}
	
	public void setMachineLearningAlgorithm(String algorithm) {
		this.ml = algorithm;
	}
	
	public void setGramSelection(String gram) {
		this.selection = gram;
	}
	
	protected void init() {
		SecondStringStringDistance sd = new SecondStringStringDistance(this.typeSimilarity);
		if (this.typeSimilarity==Constant.DISTANCE_SOFT_TFIDF)
			this.util.trainSoftTFIDF(typeSimilarity,organism,null,sd);
		this.matchML = new MatchingMachineLearning(this.organism,this.features,
			this.typeSimilarity,this.ml,this.pctSym,this.selection,sd);
		GeneSynonymWeka gsw = new GeneSynonymWeka(this.ml,this.organism,this.features);
		gsw.initAttributes();
		NormalizationUtil nu = new NormalizationUtil();
		gsw.readModel(nu.getModelName(this.organism,this.ml,this.typeSimilarity,
			this.featureName,this.pctSym,this.selection));
		this.matchML.setWeka(gsw);
		this.matchML.setPrint(false);
	}
	
	protected HashMap<String,GenePrediction> normalizeMention(String mention) {
		//HashMap<String,GenePrediction> predicted = new HashMap<String,GenePrediction>();
		// exact matching
		MatchingExact me = new MatchingExact();
		me.match(mention,this.organism);
		//System.err.println(mention.Text() + ":" + me.Predicted().size());
		if (me.Predicted().size()==0) {
			this.matchML.match(mention);
			//System.err.println("ml");
			return this.matchML.Predicted();
		}
		else {
			//System.err.println("exact");
			return me.Predicted();
		}
		//return predicted;
	}
	
	public static void main(String[] args) {
		String text = "The regulation of the Saccharomyces cerevisiae DPP1-encoded " +
			"diacylglycerol pyrophosphate (DGPP) phosphatase by inositol supplementation " +
			"and growth phase was examined. Addition of inositol to the growth medium " +
			"resulted in a dose-dependent increase in the level of DGPP phosphatase " +
			"activity in both exponential and stationary phase cells. Activity was greater " +
			"in stationary phase cells when compared with exponential phase cells, and the " +
			"inositol- and growth phase-dependent regulations of DGPP phosphatase were " +
			"additive. Analyses of DGPP phosphatase mRNA and protein levels, and expression " +
			"of beta-galactosidase activity driven by a P(DPP1)-lacZ reporter gene, indicated " +
			"that a transcriptional mechanism was responsible for this regulation. Regulation " +
			"of DGPP phosphatase by inositol and growth phase occurred in a manner that was " +
			"opposite that of many phospholipid biosynthetic enzymes. Regulation of DGPP " +
			"phosphatase expression by inositol supplementation, but not growth phase, was " +
			"altered in opi1Delta, ino2Delta, and ino4Delta phospholipid synthesis regulatory " +
			"mutants. CDP-diacylglycerol, a phospholipid pathway intermediate used for the " +
			"synthesis of phosphatidylserine and phosphatidylinositol, inhibited DGPP phosphatase " +
			"activity by a mixed mechanism that caused an increase in K(m) and a decrease in " +
			"V(max). DGPP stimulated the activity of pure phosphatidylserine synthase by a " +
			"mechanism that increased the affinity of the enzyme for its substrate " +
			"CDP-diacylglycerol. Phospholipid composition analysis of a dpp1Delta mutant showed " +
			"that DGPP phosphatase played a role in the regulation of phospholipid metabolism by " +
			"inositol, as well as regulating the cellular levels of phosphatidylinositol.";
		// mentions
		/*GeneRecognition gr = new GeneRecognition();
		ArrayList<GeneMention> gms = gr.extractForYeast(text);
		for (int i=0; i<gms.size(); i++) {
			GeneMention gm = gms.get(i);
			System.err.println(gm.Start() + "\t" + gm.End() + "\t" + gm.Text());
		}*/
		ArrayList<GeneMention> gms = new ArrayList<GeneMention>();
		gms.add(new GeneMention("1 dpp p",0,0));
		gms.add(new GeneMention("dgpp phosphatase",0,0));
		gms.add(new GeneMention("1 delta dpp",0,0));
		gms.add(new GeneMention("1 cerevisiae dgpp diacylglycerol dpp encoded phosphatase pyrophosphate saccharomyces",0,0));
		System.err.println(gms.size());
		// normalization
		MachineLearningNormalization app = new MachineLearningNormalization(new Organism(Constant.ORGANISM_YEAST));
		gms = app.normalize(text,gms);
		System.err.println(gms.size());
		for (int i=0; i<gms.size(); i++) {
			GeneMention gm = gms.get(i);
			System.err.println(gm.Start() + "\t" + gm.End() + "\t" + gm.Text() + "\t" + gm.GeneIds().size());
			if (gm.GeneIds().size()>0) {
				for (int j=0; j<gm.GeneIds().size(); j++)
					System.err.println(gm.GeneIds().get(j).GeneId() + " " + 
						gm.GeneIds().get(j).Synonym() + " " + gm.GeneIds().get(j).ScoreDisambig());
				System.err.println("Best gene: " + gm.GeneId().GeneId());
			}
		}
	}
	
}
