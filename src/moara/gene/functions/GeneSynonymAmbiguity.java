package moara.gene.functions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import moara.bio.entities.Organism;
import moara.gene.dbs.DBGeneTextTerm;
import moara.normalization.NormalizationConstant;
import moara.normalization.entities.GenePrediction;
import moara.util.Constant;
import moara.util.text.CosineSimilarity;
import moara.util.text.StringUtil;
import moara.util.text.TermSpaceModel;
import moara.util.text.Tokenizer;
import moara.wrapper.secondstring.SecondStringStringDistance;

import com.wcohen.ss.SoftTFIDF;

public class GeneSynonymAmbiguity {
	
	// type of disambiguation
	private int type;
	// selection of disambiguation
	private int selection;
	// organism
	private Organism organism;
	// genes similarity
	private HashMap<String,GenePrediction> similarities;
	// tokenizer
	private Tokenizer t;
	private GeneVectorModel gvm;
	private DBGeneTextTerm dbTermGene;
	public SoftTFIDF soft;
	public static StringUtil su = new StringUtil();
	
	public GeneSynonymAmbiguity(int type, int selection, Organism organism) {
		this.type = type;
		this.selection = selection;
		this.organism = organism;
		this.similarities = new HashMap<String,GenePrediction>();
		this.t = new Tokenizer();
		this.gvm = new GeneVectorModel(this.organism);
		this.dbTermGene = new DBGeneTextTerm(this.organism);
	}
	
	public ArrayList<GenePrediction> resolveAmbiguity(String text, ArrayList<GenePrediction> genes) {
		// get terms of text
		HashMap<String,TermSpaceModel> termsText = getTermsText(text.toLowerCase());
		int similar = -1;
		double maxScore = 0;
		double score = 0;
		HashMap<String, Double> gpDisambiguateScores = new HashMap<String, Double>();
		
		this.similarities.clear();
		
		for (int i=0; i<genes.size(); i++) {
			GenePrediction gp = genes.get(i);
			
			if (this.similarities.containsKey(gp.GeneId())) {
				GenePrediction backup = this.similarities.get(gp.GeneId());
				gp.setWordsDisambiguation(backup.WordsDisambiguation());
				gp.setScoreDisambig(backup.ScoreDisambig());
				score = backup.ScoreDisambig();
			}
			else {
				HashMap<String,TermSpaceModel> termsGene = getTermsGene(gp.GeneId());
				CosineSimilarity cs = new CosineSimilarity();
				cs.calculateDistance(termsText,termsGene);
				
				gpDisambiguateScores.put(gp.GeneId(), cs.Similarity()*jaccardSimilarity(cs.NumCommonTerms(),termsGene.size(),termsText.size()));
				
				if (this.type==NormalizationConstant.DISAMBIGUATION_COSINE)
					score = cs.Similarity();
				else if (this.type==NormalizationConstant.DISAMBIGUATION_NUMWORDS)
					score = cs.NumCommonTerms();
				else if (this.type==NormalizationConstant.DISAMBIGUATION_COSINE_NUMWORDS)
					score = cs.Similarity()*cs.NumCommonTerms();
				else if (this.type==NormalizationConstant.DISAMBIGUATION_JACCARD)
					score = jaccardSimilarity(cs.NumCommonTerms(),termsGene.size(),termsText.size());
				else if (this.type==NormalizationConstant.DISAMBIGUATION_COSINE_JACCARD)
					score = cs.Similarity()*jaccardSimilarity(cs.NumCommonTerms(),termsGene.size(),termsText.size());
				else if (this.type==NormalizationConstant.DISAMBIGUATION_SOFT_TFIDF){
					String mention = su.handleSpecial(gp.Mention()).toLowerCase(), candSyn = gp.Synonym().toLowerCase();
					
					double gpScore = gp.ScoreDisambig(); //Which was stored while searching for similar synonyms
					
					SecondStringStringDistance sd = new SecondStringStringDistance(Constant.DISTANCE_JARO_WINKLER);
					double jwScore = sd.score(mention, candSyn);
					
					HashMap<String,TermSpaceModel> termsMention = getTermsText(mention);
					HashMap<String,TermSpaceModel> termsCand = getTermsText(gp.Synonym());
					
					if (termsMention.size() < 3){
						//double candScore = sd.score(mention, candSyn);
						//gpScore = candScore;
					}
					
					//double jaccardScore = jaccardSimilarity(cs.NumCommonTerms(),termsGene.size(),termsText.size());
					
					//score = Math.tan(softScore*(Math.PI/2-0.1));
					score = gpScore;
					
				}
				gp.setWordsDisambiguation(cs.CommonTerms());
				gp.setScoreDisambig(score);
				this.similarities.put(gp.GeneId(),gp);
			}
			gp.setType(NormalizationConstant.PREDICTION_AMBIGUOUS);
			//System.err.println(gp.GeneId() + " " + gp.ScoreDisambig() + " " + gp.WordsDisambiguation());
			if (score>maxScore) {
				maxScore = score;
				similar = i;
			}
		}
		// set chosen genes
		if (this.selection==NormalizationConstant.DISAMBIGUATION_SINGLE) {
			if (similar!=-1){
				genes.get(similar).setChosen(true);
			}
		}
		else {
			double halfMax = maxScore/(double)2;
			//System.err.println("halfMax=" + halfMax);
			genes = selectBestGenes(genes,halfMax);
		}
		
		// Chengkun
		// Let's select genes by ourselves if SOFT-TFIDF
		if (this.type == NormalizationConstant.DISAMBIGUATION_SOFT_TFIDF) {

			ArrayList<Integer> bestGenesIdx = new ArrayList<Integer>();

			for (int i = 0; i < genes.size(); i++) {

				GenePrediction gp = genes.get(i);
				gp.setChosen(false);
				double gpScore = gp.ScoreDisambig();
				// HashMap<String,TermSpaceModel> termsMention =
				// getTermsText(gp.Mention());
				int termSize = gp.Mention().split(" ").length;
				
				if (Math.abs(maxScore - 1) < 0.01){
					if (Math.abs(gpScore - maxScore) < 0.01){
						bestGenesIdx.add(i);
					}
				}else{
					if (Math.abs(gpScore - maxScore) < 0.05
							|| (termSize <= 3 && Math.abs(gpScore - maxScore) < 0.02)) {
						bestGenesIdx.add(i);
					}
				}
			}

			if (bestGenesIdx.size() > 0) {

				double localMaxScore = 0.0;
				int bestIdx = bestGenesIdx.get(0);

				for (int j = 0; j < bestGenesIdx.size(); j++) {

					GenePrediction gp = genes.get(bestGenesIdx.get(j));
					String mention = su.handleSpecial(gp.Mention()), candSyn = gp
							.Synonym();

					double gpScore = gp.ScoreDisambig();

					// if (Math.abs(gpScore - 1) < 0.001 || termsMention.size()
					// <=2){
					double cosineScore0 = gpDisambiguateScores.get(genes.get(
							bestIdx).GeneId());
					double cosineScore1 = gpDisambiguateScores.get(gp.GeneId());
					//gp.setScoreDisambig(cosineScore1);
					//gp.setScoreDisambig(Math.tan(gpScore*(Math.PI/2-0.05))*Math.cbrt(cosineScore1));

					if (cosineScore1 > localMaxScore) {
						localMaxScore = cosineScore1;
					}

					if (cosineScore1 >= cosineScore0) {
						bestIdx = bestGenesIdx.get(j);
					}
					// }
				}

				if (bestIdx != -1) {
					// genes.get(bestIdx).setScoreDisambig(1.0);
					genes.get(bestIdx).setChosen(true);
				}
			}
		}
		
		return genes;
	}
	
	private double jaccardSimilarity(int commom, int numTerms1, int numTerms2) {
		return (double)commom/(double)(numTerms1+numTerms2-commom);
	}
	
	/*private ArrayList<GenePrediction> selectBestGenes(ArrayList<GenePrediction> genes, 
			double average, double maxScore) {
		/*GenePrediction[] ordered = new GenePrediction[genes.size()]; 
		for (int i=0; i<genes.size(); i++) {
			GenePrediction gp = genes.get(i);
			ordered[i] = gp;
		}
		Arrays.sort(ordered);
		genes.clear();*/
		/*double halfMax = maxScore;
		for (int i=0; i<genes.size(); i++) {
			GenePrediction gp = genes.get(i);
			if (gp.ScoreDisambig()>halfMax)
				gp.setChosen(true);
		}
		return genes;
	}*/
	
	private ArrayList<GenePrediction> selectBestGenes(ArrayList<GenePrediction> genes, 
			double threshold) {
		for (int i=0; i<genes.size(); i++) {
			GenePrediction gp = genes.get(i);
			if (gp.ScoreDisambig()>threshold) {
				gp.setChosen(true);
				System.err.println(gp.GeneId() + " " + gp.ScoreDisambig() + " " + gp.Mention() + " " + gp.OriginalSynonym());
			}
		}
		return genes;
	}
	
	private HashMap<String,TermSpaceModel> getTermsText(String text) {
		this.t.setDoStemming(false);
		this.t.setIgnoreStopwords(true);
		this.t.setIgnoreSymbols(true);
		this.t.setIgnoreNumerals(true);
		this.t.setIgnoreUnits(true);
		this.t.setMinNumLetter(2);
		this.t.setMaxNumLetter(100);
		this.t.tokenize(text);
		// get terms and insert them
		this.gvm.builtVectorModelText(this.t.getTokens());
		return this.gvm.Terms();
	}
	
	private HashMap<String,TermSpaceModel> getTermsGene(String id) {
		try {
			return this.dbTermGene.getTermsGene(id);
		}
		catch(SQLException ex) { 
	     	System.err.println(ex);
	    }
		return null;
	}
	
	/*public boolean decideSimilarity(int pmdi, GenePrediction gp) {
		// terms of article
		CollectionTerms termsArticle = getTermsArticle(pmdi);
		// terms of gene
		HashMap<String,TermVectorModel> termsGene = getTermsGene(gp.GeneId());
		//System.err.println("candidate-exact " + gp.GeneId() + " gene=" + termsGene.Base().size() + 
		//	" art=" + termsArticle.Base().size());
		CosineSimilarity cs = new CosineSimilarity(termsGene,termsArticle);
		cs.calculateDistance();
		double score = cs.Similarity();
		gp.setWordsDisambiguation(cs.Words());
		double maxScore = 0.0;
		if (score>maxScore) {
			return true;
		}
		return false;
	}
	
	public CosineSimilarity calculateSimilarity(int pmdi, GenePrediction gp,
			CollectionTerms termsArticle) {
		// terms of gene
		CollectionTerms termsGene = getTermsGene(gp.GeneId());
		//System.err.println("candidate-exact " + gp.GeneId() + " gene=" + termsGene.Base().size() + 
		//	" art=" + termsArticle.Base().size());
		CosineSimilarity cs = new CosineSimilarity(termsGene,termsArticle);
		cs.calculateDistance();
		return cs;
	}*/
	
	public static void main(String[] args) {
		GeneSynonymAmbiguity gsa = new GeneSynonymAmbiguity(0,0,new Organism("7227"));
		String text = "We have cloned vermilion (v), one of the genes required for " +
			"brown eye pigment synthesis in Drosophila, using a mutant (vH2a) 'tagged' " +
			"with the transposon P factor. Mutations that disrupt v gene expression are " +
			"clustered within approximately 2 kilobases of DNA. A 1.4-kilobase transcript, " +
			"homologous to this same region, is present in v+ RNA but absent in RNA from " +
			"several v mutants. The spontaneous v alleles that are suppressed by the " +
			"suppressor of sable [su(s)] are apparently identical insertions of 412, a " +
			"copia-like transposable element. Preliminary evidence suggests that " +
			"su(s)-suppressible alleles at other loci may also be 412 insertions.";
		HashMap<String,TermSpaceModel> terms = gsa.getTermsText(text.toLowerCase());
		System.out.println(terms.size());
		double length = 0;
		Iterator<String> iter = terms.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			System.out.println(key + " " + terms.get(key).Frequency() + 
				" " + terms.get(key).MeasureValue());
			length += terms.get(key).MeasureValue()*terms.get(key).MeasureValue();
		}
		length = Math.sqrt(length);
		System.out.println(length);
	}
	
}
