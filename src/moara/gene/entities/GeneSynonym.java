package moara.gene.entities;

public class GeneSynonym {
	
	// Gene ID
	private String id;
	// Gene synonym
	private String synonym;
	// Original gene synonym
	private String originalSynonym;
	// Maximum score
	private double maxScore;
	// number of articles in training set that contains the synonym
	private int numTrainingArticlesSynonym;
	// number of articles in training set that contains the synonym and the gene
	private int numTrainingArticlesSynonymGene;
	
	public GeneSynonym(String id, String synonym) {
		this.id = id;
		this.synonym = synonym;
		this.numTrainingArticlesSynonym = 0;
		this.numTrainingArticlesSynonymGene = 0;
	}
	
	public String Id() {
		return this.id;	
	}
	
	public String Synonym() {
		return this.synonym;
	}
	
	public String OriginalSynonym() {
		return this.originalSynonym;
	}
	
	public double MaxScore() {
		return this.maxScore;
	}
	
	public int NumTrainingArticlesSynonym() {
		return this.numTrainingArticlesSynonym;
	}
	
	public int NumTrainingArticlesSynonymGene() {
		return this.numTrainingArticlesSynonymGene;
	}
	
	public void setOriginalSynonym(String original) {
		this.originalSynonym = original;
	}
	
	public void setMaxScore(double score) {
		this.maxScore = score;
	}
	
	public void addTrainingArticleSynonym() {
		this.numTrainingArticlesSynonym++;
	}
	
	public void addTrainingArticleSynonymGene() {
		this.numTrainingArticlesSynonymGene++;
	}
	
}
