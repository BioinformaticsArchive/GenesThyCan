package moara.normalization.functions;

import moara.bio.entities.Organism;
import moara.normalization.NormalizationConstant;
import moara.normalization.NormalizationUtil;
import moara.wrapper.secondstring.SecondStringStringDistance;

public class MachineLearningModel {

	private int typeSimilarity;
	private String[] features;
	private String featureName;
	private String ml;
	private double pctSym;
	private String selection;
	private Organism organism;
	private NormalizationUtil util;
	
	public MachineLearningModel(Organism organism) {
		this.organism = organism;
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
	
	public void train() {
		// features
		prepareModel();
		// training model
		NormalizationUtil nu = new NormalizationUtil();
		System.err.println("train..." + this.organism.Name() + "..." + this.ml + "..." + 
			nu.getNameStringSimilarity(this.typeSimilarity) + "..." + this.featureName + 
			"..." + this.pctSym + "..." + this.selection);
		// train model
		String file = nu.getModelName(this.organism,this.ml,this.typeSimilarity,
			this.featureName,this.pctSym,this.selection);
		SecondStringStringDistance sd = new SecondStringStringDistance(this.typeSimilarity);
		MatchingMachineLearning matchML = new MatchingMachineLearning(this.organism,
			this.features,this.typeSimilarity,this.ml,this.pctSym,this.selection,sd);
		matchML.trainClassifiers();
		matchML.Weka().saveModel(file);
		matchML.Weka().writeArffFile(file+".arff");
	}
	
	private void prepareModel() {
		GeneSynonymFeature gsf = new GeneSynonymFeature();
		gsf.selectFeaturePairs(this.organism,NormalizationConstant.DEFAULT_PCT_SIMILARITY);
		gsf.selectRandomPairs(this.organism,NormalizationConstant.DEFAULT_PCT_SIMILARITY,
			NormalizationConstant.DEFAULT_PAIR_SELECTION);
		gsf.extractFeaturesPairs(this.organism,this.pctSym,this.selection,this.typeSimilarity);
	}
	
}
