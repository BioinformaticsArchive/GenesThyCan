package moara.normalization.functions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import moara.bio.entities.Organism;
import moara.normalization.NormalizationConstant;
import moara.normalization.dbs.DBCaseFeature;
import moara.normalization.entities.FeatureSynonymComparison;
import moara.util.Constant;
import moara.util.machinelearning.Weka;

public class GeneSynonymWeka extends Weka {

	private String[] features;
	private Organism organism;
	
	public GeneSynonymWeka(String function, Organism organism, String[] features) {
		super(function);
		this.organism = organism;
		this.features = features;
	}
	
	protected void initAttributes() {
		try {
			this.attributes = new FastVector();
			for (int i=0; i<features.length; i++) {
				String feature = features[i];
				if (feature!=null) {
					if (feature.equals(NormalizationConstant.FEATURE_PREFIX3)) {
						Attribute prefix = new Attribute(NormalizationConstant.FEATURE_PREFIX3);
						this.attributes.addElement(prefix);
					}
					else if (feature.equals(NormalizationConstant.FEATURE_SUFFIX3)) {
						Attribute suffix = new Attribute(NormalizationConstant.FEATURE_SUFFIX3);
						this.attributes.addElement(suffix);
					}
					else if (feature.equals(NormalizationConstant.FEATURE_NUMBER)) {
						Attribute number = new Attribute(NormalizationConstant.FEATURE_NUMBER);
						this.attributes.addElement(number);
						Attribute totalNumber = new Attribute(NormalizationConstant.FEATURE_TOTAL_NUMBER);
						this.attributes.addElement(totalNumber);
					}
					else if (feature.equals(NormalizationConstant.FEATURE_GREEK_LETTER)) {
						Attribute greek = new Attribute(NormalizationConstant.FEATURE_GREEK_LETTER);
						this.attributes.addElement(greek);
					}
					else if (feature.equals(NormalizationConstant.FEATURE_BIGRAM)) {
						Attribute bigramSim = new Attribute(NormalizationConstant.FEATURE_BIGRAM);
						this.attributes.addElement(bigramSim);
					}
					else if (feature.equals(NormalizationConstant.FEATURE_TRIGRAM)) {
						Attribute trigramSim = new Attribute(NormalizationConstant.FEATURE_TRIGRAM);
						this.attributes.addElement(trigramSim);
					}
					else if (feature.equals(NormalizationConstant.FEATURE_SHAPE)) {
						Attribute shape = new Attribute(NormalizationConstant.FEATURE_SHAPE);
						this.attributes.addElement(shape);
					}
					else if (feature.equals(NormalizationConstant.FEATURE_TOKEN)) {
						FastVector values = null;
						Attribute common = new Attribute(NormalizationConstant.FEATURE_COMMON_TOKEN,values);
						this.attributes.addElement(common);
						Attribute different = new Attribute(NormalizationConstant.FEATURE_DIFFERENT_TOKEN,values);
						this.attributes.addElement(different);
					}
					else if (feature.equals(NormalizationConstant.FEATURE_STRING_SIMILARITY)) {
						Attribute stringsim = new Attribute(NormalizationConstant.FEATURE_STRING_SIMILARITY);
						this.attributes.addElement(stringsim);
					}
					else if (feature.equals(NormalizationConstant.FEATURE_DIFFERENT_TOKEN)) {
						DBCaseFeature dbCaseFeature = new DBCaseFeature();
						ArrayList<String> tokens = dbCaseFeature.getDistinctCaseFeatureTokens(this.organism,
							NormalizationConstant.TOKENS_DIFFERENT);
						for (int j=0; j<tokens.size(); j++) {
							Attribute token = new Attribute(NormalizationConstant.FEATURE_DIFFERENT_TOKEN + "_" + tokens.get(j));
							this.attributes.addElement(token);
						}
					}
				}
			}
			FastVector nominalvalues = new FastVector(2);
			nominalvalues.addElement(Constant.CLASS_POSITIVE);
			nominalvalues.addElement(Constant.CLASS_NEGATIVE);
			Attribute category = new Attribute("category",nominalvalues);
			this.attributes.addElement(category);
			this.attributes.trimToSize();
			//System.err.println("Total attributes: " + this.attributes.size());
		}
		catch(SQLException ex) { 
			System.err.println(ex);
		}
	}
	
	protected void createTrainingSet() {
		try {
			DBCaseFeature dbCaseFeature = new DBCaseFeature();
			Vector<Integer> pairs = dbCaseFeature.getAllCaseFeature(this.organism);
			this.dataset = new Instances("training",attributes,pairs.size());
			this.dataset.setClassIndex(this.dataset.numAttributes()-1);
			for (int i=0; i<pairs.size(); i++) {
	     		int sequential = pairs.elementAt(i).intValue();
	     		FeatureSynonymComparison fc = dbCaseFeature.getCaseFeature(sequential);
				Instance inst = getInstanceFeatureComparison(fc);
	     		inst.setDataset(this.dataset);
	     		this.dataset.add(inst);
	     		if (((i+1)%1000)==0)
					System.err.print((i+1) + "...");
	     		if (((i+1)%10000)==0)
					System.err.println();
	     	}
			System.err.println();
		}
		catch(SQLException ex) { 
			System.err.println(ex);
		}
	}
	
	private Instance getInstanceFeatureComparison(FeatureSynonymComparison fc) {
		Instance inst = new Instance(this.attributes.size());
		for (int j=0; j<this.attributes.size(); j++) {
			Attribute att = (Attribute)this.attributes.elementAt(j);
			if (att.name().equals(NormalizationConstant.FEATURE_PREFIX3))
 				inst.setValue(att,(fc.SamePrefix()==true?1:0));
 			else if (att.name().equals(NormalizationConstant.FEATURE_SUFFIX3))
 				inst.setValue(att,(fc.SameSuffix()==true?1:0));
 			else if (att.name().equals(NormalizationConstant.FEATURE_NUMBER))
 				inst.setValue(att,(fc.SameNumber()==true?1:0));
 			else if (att.name().equals(NormalizationConstant.FEATURE_GREEK_LETTER))
 				inst.setValue(att,(fc.SameGreekLetter()==true?1:0));
 			else if (att.name().equals(NormalizationConstant.FEATURE_TOTAL_NUMBER))
 				inst.setValue(att,fc.TotalNumber());
 			else if (att.name().equals(NormalizationConstant.FEATURE_BIGRAM))
 				inst.setValue(att,fc.BigramSimilarity());
 			else if (att.name().equals(NormalizationConstant.FEATURE_TRIGRAM))
 				inst.setValue(att,fc.TrigramSimilarity());
 			else if (att.name().equals(NormalizationConstant.FEATURE_SHAPE))
 				inst.setValue(att,fc.ShapeDifference());
 			else if (att.name().startsWith(NormalizationConstant.FEATURE_DIFFERENT_TOKEN)) {
 				String token = att.name().substring(NormalizationConstant.FEATURE_DIFFERENT_TOKEN.length()+1);
 				if (fc.DifferentTokens().contains(token))
 					inst.setValue(att,1);
 				else
 					inst.setValue(att,0);
 			}
 			else if (att.name().equals(NormalizationConstant.FEATURE_STRING_SIMILARITY))
 				inst.setValue(att,fc.StringSimilarity());
 			else if (att.name().equals("category") && fc.Category()!=null)
 				inst.setValue(att,fc.Category());
 		}
		return inst;
	}
	
	protected String classify(FeatureSynonymComparison fc) {
		Instances dataset = new Instances("test",this.attributes,1);
		dataset.setClassIndex(dataset.numAttributes()-1);
		Instance inst = getInstanceFeatureComparison(fc);
		inst.setDataset(this.dataset);
		return classify(inst);
	}
	
}
