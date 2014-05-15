package moara.normalization;

import java.sql.SQLException;
import java.util.ArrayList;
import moara.bio.entities.Organism;
import moara.bio.BioConstant;
import moara.gene.dbs.DBGeneSynonym;
import moara.gene.entities.GeneSynonym;
import moara.normalization.functions.MachineLearningModel;
import moara.util.Constant;
import moara.wrapper.secondstring.SecondStringStringDistance;

public class NormalizationUtil {

	public NormalizationUtil() {
		
	}
	
	public MachineLearningModel getModel(Organism organism, double pct, 
			String selection, String feature, int ss, String ml) {
		MachineLearningModel mlm = new MachineLearningModel(organism);
		mlm.setPctSymilarity(pct);
		mlm.setFeatures(feature);
		mlm.setStringSimilarity(ss);
		mlm.setMachineLearningAlgorithm(ml);
		mlm.setGramSelection(selection);
		return mlm;
	}
	
	public String[] getGroupFeatures(String features) {
		if (features.equals(NormalizationConstant.NAME_FEATURES_F1))
			return NormalizationConstant.arrayFeaturesGroup1;
		if (features.equals(NormalizationConstant.NAME_FEATURES_F2))
			return NormalizationConstant.arrayFeaturesGroup2;
		if (features.equals(NormalizationConstant.NAME_FEATURES_F3))
			return NormalizationConstant.arrayFeaturesGroup3;
		return null;
	}
	
	public String getModelName(Organism organism, String ml, int ss, String features, 
			double pctSym, String selection) {
		NormalizationUtil nu = new NormalizationUtil();
		String pct = (new Double(pctSym)).toString().replace(".","");
		return "model_" + organism.ShortName() + "_" + ml + "_" + 
			nu.getNameStringSimilarity(ss) + "_" + features + "_" + 
			pct + "_" + selection;
	}
	
	public String getNameStringSimilarity(int number) {
		if (number==Constant.DISTANCE_JARO_WINKLER)
			return Constant.NAME_DISTANCE_JARO_WINKLER;
		else if (number==Constant.DISTANCE_LEVENSTEIN)
			return Constant.NAME_DISTANCE_LEVENSTEIN;
		else if (number==Constant.DISTANCE_MONGE_ELKAN)
			return Constant.NAME_DISTANCE_MONGE_ELKAN;
		else if (number==Constant.DISTANCE_SMITH_WATERMAN)
			return Constant.NAME_DISTANCE_SMITH_WATERMAN;
		else if (number==Constant.DISTANCE_SOFT_TFIDF)
			return Constant.NAME_DISTANCE_SOFT_TFIDF;
		return null;
	}
	
	public int getNumberStringSimilarity(String name) {
		if (name.equals(Constant.NAME_DISTANCE_JARO_WINKLER))
			return Constant.DISTANCE_JARO_WINKLER;
		else if (name.equals(Constant.NAME_DISTANCE_LEVENSTEIN))
			return Constant.DISTANCE_LEVENSTEIN;
		else if (name.equals(Constant.NAME_DISTANCE_MONGE_ELKAN))
			return Constant.DISTANCE_MONGE_ELKAN;
		else if (name.equals(Constant.NAME_DISTANCE_SMITH_WATERMAN))
			return Constant.DISTANCE_SMITH_WATERMAN;
		else if (name.equals(Constant.NAME_DISTANCE_SOFT_TFIDF))
			return Constant.DISTANCE_SOFT_TFIDF;
		return 0;
	}
	
	public String getNameDisambiguationSelection(int number) {
		if (number==NormalizationConstant.DISAMBIGUATION_SINGLE)
			return "single";
		else if (number==NormalizationConstant.DISAMBIGUATION_MULTIPLE)
			return "multiple";
		return null;
	}
	
	public String getNameDisambiguationType(int number) {
		if (number==NormalizationConstant.DISAMBIGUATION_NONE)
			return "none";
		else if (number==NormalizationConstant.DISAMBIGUATION_COSINE)
			return "cosine";
		else if (number==NormalizationConstant.DISAMBIGUATION_NUMWORDS)
			return "numword";
		else if (number==NormalizationConstant.DISAMBIGUATION_COSINE_NUMWORDS)
			return "cosnumword";
		else if (number==NormalizationConstant.DISAMBIGUATION_JACCARD)
			return "jaccard";
		else if (number==NormalizationConstant.DISAMBIGUATION_COSINE_JACCARD)
			return "cosjaccard";
		else if (number==NormalizationConstant.DISAMBIGUATION_SOFT_TFIDF)
			return "softtfidf";
		return null;
	}
	
	public String getNamePctSimilarity(double pct) {
		return (new Double(pct)).toString().replace(".","");
	}
	
	public SecondStringStringDistance trainSoftTFIDF(int type, Organism organism, 
			ArrayList<GeneSynonym> synonyms, SecondStringStringDistance sd) {
		try {
			if (synonyms==null) {
				DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(organism);
				synonyms = dbGeneSynonym.getSynonymsOrganismType(BioConstant.SYNONYM_LIST);
			}
			ArrayList<String> tokens = new ArrayList<String>(synonyms.size());
			for (int i=0; i<synonyms.size(); i++) {
				tokens.add(synonyms.get(i).Synonym());
			}
			tokens.trimToSize();
			sd.train(tokens);
			return sd;
		}
		catch(SQLException ex) { 
			System.err.println(ex);
		}
		return null;
	}
	
}
