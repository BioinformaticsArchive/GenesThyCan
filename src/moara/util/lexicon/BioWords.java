package moara.util.lexicon;

import java.sql.SQLException;
import java.util.ArrayList;
import moara.bio.dbs.DBBioThesaurus;

public class BioWords {

	private ArrayList<String> biowords;
	private ArrayList<String> biowordsClean;
	private DBBioThesaurus dbBioThesaurus;
	
	public BioWords(int threshold, int thresholdClean) {
		this.dbBioThesaurus = new DBBioThesaurus();
		loadBioWords(threshold,thresholdClean);
	}
	
	public void loadBioWords(int threshold, int thresholdClean) {
		try {
			this.biowords = this.dbBioThesaurus.getTermsFrequency(threshold);
			this.biowordsClean = this.dbBioThesaurus.getTermsFrequency(thresholdClean);
		}
		catch(SQLException e) { 
        	System.err.println(e);
        }
	}
	
	public boolean isBioWord(String s) {
		return this.biowords.contains(s);
	}
	
	public boolean isBioWordClean(String s) {
		return this.biowordsClean.contains(s);
	}
	
	 /*private String[] words = {
			 "dna",
			 "rna",
			 "kinase",
			 "mrna",
			 "kbp",
			 "trna",
			 "rrna",
			 "element",
			 "transcript",
			 "factor",
			 "cdna",
			 "domain",
			 "receptor",
			 "homolog",
			 "region",
			 "chromosome",
			 "product",
			 "type",
			 "growth",
			 "subunit",
			 "protein",
			 "proteins",
			 "molecule",
			 "molecules",
			 "peptide",
			 "antigen",
			 "mitochondrial",
			 // organisms
			 // yeast
			 "s. cerevisiae",
			 "saccharomyces cerevisiae",
			 "yeast",
			 // mouse
			 "mouse",
			 "mice",
			 // human
			 "human",
			 "h. sapiens",
			 // fly
			 "fly",
			 "drosophila",
			 "melanogaster",
			 "drosophila melanogaster",
			 // other
			 "kb",
			 "bp"
	 };
	 
	 private void loadBioWords(DBMySQL dbMoara, int threshold) {
		this.biowords = new ArrayList<String>();
		for (int i=0; i<this.words.length; i++) {
			this.biowords.add(this.words[i]);
		}
	}*/
	
}
