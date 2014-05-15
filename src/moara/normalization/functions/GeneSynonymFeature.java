package moara.normalization.functions;

import java.sql.SQLException;
import java.util.ArrayList;
import moara.bio.BioConstant;
import moara.bio.entities.Organism;
import moara.gene.dbs.DBGeneSynonym;
import moara.gene.entities.GeneSynonym;
import moara.normalization.NormalizationUtil;
import moara.normalization.NormalizationConstant;
import moara.normalization.dbs.DBCaseFeature;
//import moara.normalization.dbs.DBFeatureConfiguration;
import moara.normalization.dbs.DBGeneSynonymFeature;
import moara.normalization.dbs.DBFeaturePair;
import moara.normalization.entities.FeatureSynonym;
import moara.normalization.entities.FeatureSynonymComparison;
import moara.util.Constant;
import moara.wrapper.secondstring.SecondStringStringDistance;
//import moara.util.Time;
//import moara.normalization.dbs.DBGeneSynonymCluster;

public class GeneSynonymFeature {
	
	public GeneSynonymFeature() {
		
	}
	
	public void extractFeaturesSynonyms(Organism organism) {
		DBGeneSynonymFeature dBGeneSynonymFeature = new DBGeneSynonymFeature(organism);
		try {
			NormalizationUtil nu = new NormalizationUtil();
			String[] arrayFeatures = nu.getGroupFeatures(NormalizationConstant.NAME_FEATURES_F1);
			// clear features
			dBGeneSynonymFeature.trucateSynonymFeature();
			DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(organism);
			ArrayList<GeneSynonym> synonyms = dbGeneSynonym.getSynonymsOrganismType(BioConstant.SYNONYM_ORDERED);
			for (int i=0; i<synonyms.size(); i++) {
				GeneSynonym gs = synonyms.get(i);
				String synonym = gs.Synonym().trim();
				FeatureSynonym fs = new FeatureSynonym(gs.Id(),synonym.toLowerCase(),
					arrayFeatures);
				dBGeneSynonymFeature.insertFeatureSynonym(fs);
				if (((i+1)%1000)==0) {
					System.err.print("feat-synonym..." + organism + "..." + (i+1) + "...");
					dBGeneSynonymFeature.executeCommit();
				}
			}
			dBGeneSynonymFeature.executeCommit();
		}
		catch(SQLException ex) { 
			System.err.println(ex);
			dBGeneSynonymFeature.executeRollback();
		}
	}
	
	public void selectFeaturePairs(Organism organism, double pct) {
		DBFeaturePair dbFeaturePair = new DBFeaturePair(organism);
		try {
			// clear old data
			dbFeaturePair.deleteFeaturePair(pct);
			// get list of genes
			DBGeneSynonymFeature dBGeneSynonymFeature = new DBGeneSynonymFeature(organism);
			ArrayList<Integer> synonyms = dBGeneSynonymFeature.getAllSequentialOrganism();
			// cases
			int totalBiPos = 0;
			int totalTriPos = 0;
			synonymLoop:
			for (int i=0; i<synonyms.size(); i++) {
				int seq1 = (synonyms.get(i)).intValue();
				FeatureSynonym fs1 = dBGeneSynonymFeature.getFeaturesBySequential(seq1);
				// bigram
				if (fs1.Bigram().size()>0) {
					ArrayList<int[]> bigrams = dBGeneSynonymFeature.getFeaturesByNGramsGene(
						fs1.Bigram(),fs1.GeneId(),fs1.Synonym(),2,pct);
					for (int j=0; j<bigrams.size(); j++) {
						int seq2 = (bigrams.get(j))[0];
						int category = (bigrams.get(j))[1];
						dbFeaturePair.insertFeaturePairBigram(seq1,seq2,pct,category);
						if (category==1)
							totalBiPos++;
						if (totalBiPos>=NormalizationConstant.TOTAL_CASES)
							break synonymLoop;
					}
				}
				// trigram
				if (fs1.Trigram().size()>0) {
					ArrayList<int[]> trigrams = dBGeneSynonymFeature.getFeaturesByNGramsGene(
						fs1.Trigram(),fs1.GeneId(),fs1.Synonym(),3,pct);
					for (int j=0; j<trigrams.size(); j++) {
						int seq2 = (trigrams.get(j))[0];
						int category = (trigrams.get(j))[1];
						dbFeaturePair.insertFeaturePairTrigram(seq1,seq2,pct,category);
						if (category==1)
							totalTriPos++;
						if (totalTriPos>=NormalizationConstant.TOTAL_CASES)
							break synonymLoop;
					}
				}
				if (((i+1)%10)==0) {
					System.err.println("feat-pairs..." + organism.Name() + "..." + (i+1) + "...");
					dbFeaturePair.executeCommit();
				}
			}
			dbFeaturePair.executeCommit();
		}
		catch(SQLException ex) { 
			System.err.println(ex);
			dbFeaturePair.executeRollback();
		}
	}
	
	public void selectRandomPairs(Organism organism, double pct, String selection) {
		DBFeaturePair dbFeaturePair = new DBFeaturePair(organism);
		try {
			dbFeaturePair.cleanSelectedPairs(pct,selection);
			int maxPairs = dbFeaturePair.getMaxPairs(pct,selection);
			if (maxPairs>15000)
				maxPairs = 15000;
			// set random pairs (positive)
			dbFeaturePair.setRandomPairs(pct,selection,1,maxPairs);
			// set random pairs (negative)
			dbFeaturePair.setRandomPairs(pct,selection,0,maxPairs);
			dbFeaturePair.executeCommit();
		}
		catch(SQLException ex) { 
			System.err.println(ex);
			dbFeaturePair.executeRollback();
		}
	}
	
	public void extractFeaturesPairs(Organism organism, double pct, String selection, int typeSimilarity) {
		DBCaseFeature dbCaseFeature = new DBCaseFeature();
		try {
			// clear old data
			dbCaseFeature.deleteCaseFeature(organism);
			// type similarity
			//DBFeatureConfiguration dBFeatureConfiguration = new DBFeatureConfiguration(this.dbNorm);
			//dBFeatureConfiguration.updateTypeSimilarity(organism,typeSimilarity);
			// get list of bigram+trigram feature pairs
			DBFeaturePair dbFeaturePair = new DBFeaturePair(organism);
			ArrayList<int[]> pairs = dbFeaturePair.getSelectedPairs(pct,selection);
			// if SoftTFIDF, train classifier
			SecondStringStringDistance sd = new SecondStringStringDistance(typeSimilarity);
			if (typeSimilarity==Constant.DISTANCE_SOFT_TFIDF) {
				DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(organism);
				ArrayList<GeneSynonym> synonyms = dbGeneSynonym.getSynonymsOrganismType(BioConstant.SYNONYM_ORDERED);
				NormalizationUtil nu = new NormalizationUtil();
				nu.trainSoftTFIDF(typeSimilarity,organism,synonyms,sd);
			}
			DBGeneSynonymFeature dBGeneSynonymFeature = new DBGeneSynonymFeature(organism);
			for (int i=0; i<pairs.size(); i++) {
				int[] seqs = pairs.get(i);
				int seq1 = seqs[0];
				int seq2 = seqs[1];
				int category = seqs[2];
				FeatureSynonym fs1 = dBGeneSynonymFeature.getFeaturesBySequential(seq1);
				FeatureSynonym fs2 = dBGeneSynonymFeature.getFeaturesBySequential(seq2);
				FeatureSynonymComparison fc;
				if (category==1) {
					fc = new FeatureSynonymComparison(organism,fs1,fs2,
						Constant.CLASS_POSITIVE,typeSimilarity,sd);
				}
				else {
					fc = new FeatureSynonymComparison(organism,fs1,fs2,
						Constant.CLASS_NEGATIVE,typeSimilarity,sd);
				}
				dbCaseFeature.insertCaseFeature(organism,fc);
				if (((i+1)%100)==0) {
					System.err.println("feat-extr..." + organism.Name() + "..." + (i+1) + "...");
					dbCaseFeature.executeCommit();
				}
			}
			dbCaseFeature.executeCommit();
		}
		catch(SQLException ex) { 
			System.err.println(ex);
			dbCaseFeature.executeRollback();
		}
	}
	
	/*public void createFeaturesPairs(String organism, String features, double pctSim, 
			int typeSimilarity) {
		try {
			NormalizationUtil nu = new NormalizationUtil();
			String[] arrayFeatures = nu.getGroupFeatures(features);
			// clear old data
			DBCaseFeature dbCaseFeature = new DBCaseFeature(this.dbNorm);
			dbCaseFeature.deleteCaseFeature(organism);
			// type similarity
			DBFeatureConfiguration dBFeatureConfiguration = new DBFeatureConfiguration(this.dbNorm);
			dBFeatureConfiguration.updateTypeSimilarity(organism,typeSimilarity);
			// get organism synonyms
			DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(this.dbMoara);
			ArrayList<GeneSynonym> synonyms = dbGeneSynonym.getSynonymsOrganismType(organism,
				BioConstant.SYNONYM_ORDERED,BioConstant.SYNONYM_RELIABLE);
			// if SoftTFIDF, train classifier
			SoftTFIDF soft = null;
			//if (typeSimilarity==NormalizationConstant.DISTANCE_SOFT_TFIDF)
			soft = nu.trainSoftTFIDF(this.dbMoara,organism,synonyms);
			int totalPos = 0;
			int totalNeg = 0;
			for (int i=0; i<synonyms.size(); i++) {
				GeneSynonym gs1 = synonyms.get(i);
				FeatureSynonym fs1 = new FeatureSynonym(gs1.Id(),gs1.Synonym().toLowerCase(),
					arrayFeatures);
				GeneSynonym gs2 = null;
				if (totalPos<(NormalizationConstant.TOTAL_CASES/2)) {
					// positive
					gs2 = dbGeneSynonym.searchSimilarSynonym(organism,gs1.Id(),
						gs1.Synonym().toLowerCase(),pctSim,true,soft);
					if (gs2!=null) {
						FeatureSynonym fs2 = new FeatureSynonym(gs2.Id(),gs2.Synonym().toLowerCase(),
							arrayFeatures);
						FeatureSynonymComparison fc = new FeatureSynonymComparison(organism,fs1,fs2,
							Constant.CLASS_POSITIVE,typeSimilarity,soft);
						dbCaseFeature.insertCaseFeature(organism,fc);
						totalPos++;
					}
				}
				if (gs2!=null || totalNeg<totalPos) {
					// negative
					GeneSynonym gs3 = dbGeneSynonym.searchSimilarSynonym(organism,gs1.Id(),
						gs1.Synonym().toLowerCase(),pctSim,false,soft);
					if (gs3!=null) {
						FeatureSynonym fs3 = new FeatureSynonym(gs3.Id(),gs3.Synonym().toLowerCase(),
							arrayFeatures);
						FeatureSynonymComparison fc = new FeatureSynonymComparison(organism,fs1,fs3,
							Constant.CLASS_NEGATIVE,typeSimilarity,soft);
						dbCaseFeature.insertCaseFeature(organism,fc);
						totalNeg++;
					}
				}
				if (((i+1)%10)==0) {
					System.err.print("pair-feat..." + organism + "..." + (i+1) + "...");
					System.err.print("total pos..." + totalPos + "...total neg..." + totalNeg + "...");
					this.dbNorm.executeCommit();
				}
			}
			System.err.print("total pos..." + totalPos + "...total neg..." + totalNeg + "...");
			this.dbNorm.executeCommit();
		}
		catch(SQLException ex) { 
			System.err.println(ex);
			this.dbNorm.executeRollback();
		}
	}
	
	public void createClusterSynonym(String organism, double pctSim) {
		try {
			// clean old data
			DBGeneSynonymCluster dbGeneSynonymCluster = new DBGeneSynonymCluster(this.dbNorm);
			dbGeneSynonymCluster.deleteGeneSynonymCluster(organism);
			// get all synonyms
			DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(this.dbMoara);
			ArrayList<GeneSynonym> synonyms = dbGeneSynonym.getSynonymsOrganismType(organism,
				BioConstant.SYNONYM_ORDERED,BioConstant.SYNONYM_RELIABLE);
			NormalizationUtil nu = new NormalizationUtil();
			SoftTFIDF soft = nu.trainSoftTFIDF(this.dbMoara,organism,synonyms);
			int seqCluster = 1;
			for (int i=0; i<synonyms.size(); i++) {
				GeneSynonym gs1 = synonyms.get(i);
				// get list of reference synonyms
				System.err.print((i+1) + "..." + gs1.Id() + "..." + gs1.Synonym() + "...");
				ArrayList<Integer> list = dbGeneSynonymCluster.getSimilarRefSynonyms(organism,
					gs1.Synonym(),pctSim,soft);
				System.err.print(list.size() + "...");
				if (list.size()>0) {
					for (int j=0; j<list.size(); j++) {
						// add to cluster
						dbGeneSynonymCluster.insertGeneSynonymCluster(organism,gs1.Id(),
							gs1.Synonym(),list.get(j),false);
					}
				}
				else {
					// new cluster
					dbGeneSynonymCluster.insertGeneSynonymCluster(organism,gs1.Id(),
						gs1.Synonym(),seqCluster,true);
					seqCluster++;
				}
				System.err.println("ok");
				if (((i+1)%100)==0) {
					System.err.print("cluster..." + organism + "..." + (i+1) + "...");
					this.dbNorm.executeCommit();
				}
			}
			this.dbNorm.executeCommit();
		}
		catch(SQLException ex) { 
			System.err.println(ex);
			this.dbNorm.executeRollback();
		}
	}*/
	
	public static void main(String[] args) {
		GeneSynonymFeature gsf = new GeneSynonymFeature();
		// organism
		Organism organism = new Organism(Constant.ORGANISM_FLY);
		// features
		String features = NormalizationConstant.NAME_FEATURES_F3;
		// string similarity
		int typeSim = Constant.DISTANCE_JARO_WINKLER;
		// gram similarity
		double pct = 0.9;
		// synonyms
		//gsf.extractFeaturesSynonyms(organism);
	    // pair features
		/*for (int i=0; i<NormalizationConstant.arrayPctTfIdfSimilarity.length; i++) {
			double pct = NormalizationConstant.arrayPctTfIdfSimilarity[i];
			gsf.selectFeaturePairs(organism,pct);
		}*/
		//gsf.selectFeaturePairs(organism,pct);
		// random pairs
		gsf.selectRandomPairs(organism,pct,NormalizationConstant.FEATURE_SELECTION_BOTH);
		// extract features
	    gsf.extractFeaturesPairs(organism,pct,NormalizationConstant.FEATURE_SELECTION_BOTH,typeSim);
		//SoftTFIDF soft = gsf.trainSoftTFIDF(organism,null);
		//System.err.println(soft.score("AMC1","ABC1"));
		//gsf.createFeaturesPairs(organism,features,pct,typeSim);
		//gsf.createClusterSynonym(organism,NormalizationConstant.PCT_SOFTFIDF_CLUSTER);
	}
	
}
