package moara.normalization.functions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import moara.bio.entities.Organism;
import moara.bio.BioConstant;
import moara.gene.dbs.DBGeneSynonym;
//import moara.gene.entities.GeneSynonym;
import moara.normalization.NormalizationConstant;
import moara.normalization.dbs.DBGeneSynonymFeature;
//import moara.normalization.dbs.DBGeneSynonymCluster;
import moara.normalization.entities.FeatureSynonym;
import moara.normalization.entities.FeatureSynonymComparison;
import moara.normalization.entities.GenePrediction;
import moara.normalization.functions.GeneSynonymWeka;
import moara.util.Constant;
import moara.util.text.StringDistance;

public class MatchingMachineLearning extends MatchingProcedure {

	private Organism organism;
	private String[] features;
	private int typeSimilarity;
	private StringDistance sd;
	private double pctSim;
	private String selection;
	private GeneSynonymWeka weka;
	private String ml;
		
	public MatchingMachineLearning(Organism organism, String[] features, 
			int typeSimilarity, String ml, double pctSim, String selection, StringDistance sd) {
		super();
		this.organism = organism;
		this.features = features;
		this.ml = ml;
		this.typeSimilarity = typeSimilarity;
		this.pctSim = pctSim;
		this.selection = selection;
		this.sd = sd;
	}
	
	public void setWeka(GeneSynonymWeka weka) {
		this.weka = weka;
	}
	
	public GeneSynonymWeka Weka() {
		return this.weka;
	}
	
	public void trainClassifiers() {
		this.weka = new GeneSynonymWeka(this.ml,this.organism,this.features);
		this.weka.train();
	}
	
	public void match(String mention) {
		try {
			DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(this.organism);
			this.predicted = new HashMap<String,GenePrediction>();
			FeatureSynonym fsMention = new FeatureSynonym(null,mention,this.features);
			if (this.print)
				System.err.println(fsMention.Bigram().size() + " " + fsMention.Trigram().size());
			DBGeneSynonymFeature dBGeneSynonymFeature = new DBGeneSynonymFeature(this.organism);
			ArrayList<Integer> candidates = new ArrayList<Integer>();
			if (this.selection.equals(NormalizationConstant.FEATURE_SELECTION_BIGRAM) || 
					this.selection.equals(NormalizationConstant.FEATURE_SELECTION_BOTH)) {
				candidates = dBGeneSynonymFeature.getFeaturesByNGrams(fsMention.Bigram(),2,this.pctSim);
			}
			if (this.selection.equals(NormalizationConstant.FEATURE_SELECTION_TRIGRAM) || 
					this.selection.equals(NormalizationConstant.FEATURE_SELECTION_BOTH)) {
				if (candidates.size()==0)
					candidates = dBGeneSynonymFeature.getFeaturesByNGrams(fsMention.Trigram(),3,this.pctSim);
				else
					candidates.addAll(dBGeneSynonymFeature.getFeaturesByNGrams(fsMention.Trigram(),3,this.pctSim));
			}
			/*DBGeneSynonym dBGeneSynonym = new DBGeneSynonym(this.dbMoara);
			ArrayList<GeneSynonym> candidates = dBGeneSynonym.searchSimilarSynonymMention(organism,
				mention,this.pctSim,this.soft);
			DBGeneSynonymCluster dbGeneSynonymCluster = new DBGeneSynonymCluster(this.dbNorm);
			ArrayList<GeneSynonym> candidates = dbGeneSynonymCluster.getCandidatesMatching(
				this.organism,mention,this.pctSim,this.soft);*/
			if (this.print)
				System.err.println("candidates = " + candidates.size());
			for (int j=0; j<candidates.size(); j++) {
				Integer seq = candidates.get(j);
				FeatureSynonym fsSynonym = dBGeneSynonymFeature.getFeaturesBySequential(seq);
				//GeneSynonym gs = candidates.get(j);
				//FeatureSynonym fsSynonym = new FeatureSynonym(gs.Id(),gs.Synonym(),this.features);
				FeatureSynonymComparison fc = new FeatureSynonymComparison(this.organism,
					fsMention,fsSynonym,null,this.typeSimilarity,this.sd);
				String category = classify(fc);
				//System.err.println(category);
				fc.setCategory(category);
				if (print)
					System.err.println(fsMention.Synonym() + " " + fsSynonym.GeneId() + 
						"/" + fsSynonym.Synonym() + " " +  fc.StringSimilarity() + " " + 
						fc.Category());
				//System.err.println(fc.StringSimilarity() + " " + fc.Category());
				if (fc.Category().equals(Constant.CLASS_POSITIVE)) {
					String original = dbGeneSynonym.getOriginalGeneSynonym(fsSynonym.GeneId(),
						fsSynonym.Synonym(),BioConstant.SYNONYM_ORDERED);
					GenePrediction gp = new GenePrediction(fsSynonym.GeneId(),
						fsMention.Synonym(),fsSynonym.Synonym(),original);
					gp.setTypeMatching(NormalizationConstant.MATCHING_MACHINE_LEARNING);
					//gp.setNumberCase(fc.NumberCase());
					this.predicted.put(fsSynonym.GeneId(),gp);
				}
			}
		}
		catch(SQLException ex) { 
			System.err.println(ex);
		}
	}
	
	/*public void match(String mention) {
		try {
			FeatureSynonym fsMention = new FeatureSynonym(null,mention,this.features);
			if (print)
				System.err.println(fsMention.Bigram().size() + " " + fsMention.Trigram().size());
			if (fsMention.Bigram().size()>0 || fsMention.Trigram().size()>0) {
				DBGeneSynonymFeature dBGeneSynonymFeature = new DBGeneSynonymFeature(db);
				//Vector<Integer> candidates = dBGeneSynonymFeature.getFeaturesByNGrams(organism,fsMention.Bigram(),2,pctSim);
				Vector<Integer> candidates = dBGeneSynonymFeature.getFeaturesByNGrams(this.organism,
					fsMention.Trigram(),3,this.pctSim);
				for (int j=0; j<candidates.size(); j++) {
					int sequential =(candidates.elementAt(j)).intValue();
					FeatureSynonym fsSynonym = dBGeneSynonymFeature.getFeaturesBySequential(organism,sequential);
					FeatureSynonymComparison fc = new FeatureSynonymComparison(organism,fsMention,fsSynonym,null,
						this.typeSimilarity,this.soft);
					fc.setCategory(classify(fc));
					if (print)
						System.err.println(fsMention.Synonym() + " " + fsSynonym.GeneId() + 
							"/" + fsSynonym.Synonym() + " " +  fc.StringSimilarity() + " " + 
							fc.Category());
					//System.err.println(fc.StringSimilarity() + " " + fc.Category());
					if (fc.Category().equals(Constant.CLASS_POSITIVE)) {
						GenePrediction gp = new GenePrediction(fsSynonym.GeneId(),
								fsSynonym.Synonym(),fsMention.Synonym());
						gp.setTypeMatching(NormalizationConstant.MATCHING_MACHINE_LEARNING);
						//gp.setNumberCase(fc.NumberCase());
						predicted.put(fsSynonym.GeneId(),gp);
					}
				}
			}
		}
		catch(SQLException ex) { 
			System.err.println(ex);
		}
	}*/
	
	private String classify(FeatureSynonymComparison fc) {
		String category = Constant.CLASS_NEGATIVE;
		category = this.weka.classify(fc);
		return category;
	}
	
}
