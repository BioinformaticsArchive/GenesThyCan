package moara.normalization.entities;

import java.lang.Comparable;

public class GenePrediction implements Comparable<GenePrediction> {
	
	// PMDI id
	private int pmdi;
	// Gene ID
	private String geneId;
	// Gene synonym words
	private String synonym;
	private String originalSynonym;
	// Gene mention words
	private String mention;
	// Maximum score
	private double maxScore;
	// type of prediction
	private String type;
	// words that coincides in the disambiguation
	private String wordsDisambiguation;
	// type of matching
	private String typeMatching;
	// number of the case
	private int numberCase;
	// score disambiguation
	private double scoreDisambig;
	// chosen prediction
	private boolean chosen;
	// status
	private String status;
	
	public GenePrediction(String geneId, String mention, String synonym, String originalSynonym) {
		this.geneId = geneId;
		this.synonym = synonym;
		this.mention = mention;
		this.chosen = false;
		this.originalSynonym = originalSynonym;
	}
	
	public GenePrediction(int pmdi, String geneId, String mention, String synonym) {
		this.pmdi = pmdi;
		this.geneId = geneId;
		this.synonym = synonym;
		this.mention = mention;
	}
	
	public GenePrediction(int pmdi, String geneId) {
		this.pmdi = pmdi;
		this.geneId = geneId;
	}
	
	public int Pmdi() {
		return this.pmdi;
	}
	
	public String GeneId() {
		return this.geneId;	
	}
	
	public String Synonym() {
		return this.synonym;
	}
	
	public String OriginalSynonym() {
		return this.originalSynonym;
	}
	
	public String Mention() {
		return this.mention;
	}
	
	public double MaxScore() {
		return this.maxScore;
	}
	
	public String Type() {
		return this.type;
	}
	
	public String WordsDisambiguation() {
		return this.wordsDisambiguation;
	}
	
	public String TypeMatching() {
		return this.typeMatching;
	}
	
	public int NumberCase() {
		return this.numberCase;
	}
	
	public double ScoreDisambig() {
		return this.scoreDisambig;
	}
	
	public boolean Chosen() {
		return this.chosen;
	}
	
	public String Status() {
		return this.status;
	}
	
	public void setMaxScore(double maxScore) {
		this.maxScore = maxScore;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setWordsDisambiguation(String words) {
		this.wordsDisambiguation = words;
	}
	
	public void setTypeMatching(String typeMatching) {
		this.typeMatching = typeMatching;
	}
	
	public void setNumberCase(int c) {
		this.numberCase = c;
	}
	
	public void setScoreDisambig(double s) {
		this.scoreDisambig = s;
	}
	
	public void setChosen(boolean chosen) {
		this.chosen = chosen;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public int compareTo(GenePrediction gp) {
		if (this.scoreDisambig==gp.ScoreDisambig())
			return 0;
		else if (this.scoreDisambig>gp.ScoreDisambig())
			return 1;
		else
			return -1;
	}
	
}
