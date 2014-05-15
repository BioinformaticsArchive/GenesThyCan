package moara.gene.functions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import moara.bio.entities.GeneOntology;
import moara.bio.entities.Organism;
import moara.gene.dbs.DBGene;
import moara.gene.dbs.DBGeneGO;
import moara.gene.dbs.DBGeneSynonym;
import moara.gene.dbs.DBGeneProduct;
import moara.gene.dbs.DBGeneTerm;
import moara.gene.dbs.DBGeneTextTerm;
import moara.gene.entities.Gene;
import moara.util.EnvironmentVariable;
import moara.util.text.TermSpaceModel;
import moara.util.text.Tokenizer;

public class GeneTextIndexing {

	private Organism organism;
	private DBGene dbGene;
	private DBGeneTerm dbGeneTerm;
	private DBGeneTextTerm dbGeneTextTerm;
	private DBGeneProduct dbGeneProduct;
	private DBGeneSynonym dbGeneSynonym;
	private DBGeneGO dbGeneGO;
	private Tokenizer t;
	
	public GeneTextIndexing(Organism organism) {
		this.organism = organism;
		this.dbGene = new DBGene(this.organism);
		this.dbGeneTerm = new DBGeneTerm(this.organism);
		this.dbGeneTextTerm = new DBGeneTextTerm(this.organism);
		this.dbGeneProduct = new DBGeneProduct();
		this.dbGeneSynonym = new DBGeneSynonym(this.organism);
		this.dbGeneGO = new DBGeneGO(this.organism);
		initTokenizer();
	}
	
	private void initTokenizer() {
		this.t = new Tokenizer();
		this.t.setDoStemming(true);
		this.t.setIgnoreStopwords(true);
		this.t.setIgnoreSymbols(true);
		this.t.setIgnoreNumerals(true);
		this.t.setIgnoreUnits(true);
		this.t.setMinNumLetter(2);
		this.t.setMaxNumLetter(100);
	}
	
	public void processGeneText() {
		try {
			// delete old data
			this.dbGeneTerm.truncateTerm();
			this.dbGeneTextTerm.truncateTermGene();
			// process gene text
			this.processAllGeneText();
			this.calculateMeasureValue();
			this.dbGeneTerm.executeCommit();
			this.dbGeneTextTerm.executeCommit();
		}
		catch(SQLException ex) { 
		 	System.err.println(ex);
		 	this.dbGeneTerm.executeRollback();
		 	this.dbGeneTextTerm.executeRollback();
		}
	}
	
	private void processAllGeneText() {
		//System.err.println("Processing gene text...");
		try {
			int count = 0;
			GeneVectorModel gvm = new GeneVectorModel(this.organism);
			Vector<String> genes = this.dbGene.getAllIdGene();
			for (int i=0; i<genes.size(); i++) {
				String id = genes.elementAt(i);

				// get gene
				Gene g = this.dbGene.getGeneObject(id);
				// set synonyms
				g.setSynonyms(this.dbGeneSynonym.getSynonymsGene(id));
				// set products
				g.setProducts(this.dbGeneProduct.getProductsGene(this.organism,id));
				// set GOs
				g.setGoTerms(this.dbGeneGO.getGOsGene(id));
				// process gene text
				String geneText = processGeneText(g);
				String tokenizedText = this.t.tokenize(geneText);
				this.dbGene.updateTokenizedTextGene(id,tokenizedText);
				// get terms and insert them
				gvm.buildVectorModel(this.t.getTokens());
				HashMap<String,TermSpaceModel> terms = gvm.Terms();
				this.dbGeneTerm.insertTerms(terms);
				this.dbGeneTextTerm.insertTermGene(id,terms);
				count++;
				//if ((count%100)==0) {
					System.err.println("term..." + organism.Code() + "..." + count + "...");
					this.dbGene.executeCommit();
					this.dbGeneTerm.executeCommit();
					this.dbGeneTextTerm.executeCommit();
				//}
			}
			// remove terms that do not reach minimum frequency and gene number
			/*System.err.println("Removing unfrequent words...");
			dbGeneTerm.removeUnfrequentTerms(organism,textIndexing.MinFrequencyTerm());
			System.err.println("Removing terms with few genes...");
			dbGeneTerm.removeTermsFewGenes(organism,textIndexing.MinNumGeneTerm());*/
			this.dbGene.executeCommit();
			this.dbGeneTerm.executeCommit();
			this.dbGeneTextTerm.executeCommit();
		}
		catch(SQLException ex) { 
		 	System.err.println(ex);
		 	this.dbGene.executeRollback();
		 	this.dbGeneTerm.executeRollback();
		 	this.dbGeneTextTerm.executeRollback();
		}
	}
	
	private String processGeneText(Gene g) {
		String completeText = "";
		// symbol
		if (g.Symbol()!=null)
			completeText += " " + g.Symbol();
		// description
		completeText += " " + g.Description();
		// phenotype
		if (g.Phenotype()!=null)
			completeText += " " + g.Phenotype();
		// name
		if (g.Name()!=null)
			completeText += " " + g.Name();
		// synonyms
		ArrayList<String> synonyms = g.Synonyms();
		for (int i=0; i<synonyms.size(); i++) {
			String synonym = synonyms.get(i);
			completeText += " " + synonym;
		}
		// products
		ArrayList<String> products = g.Products();
		for (int i=0; i<products.size(); i++) {
			String product = products.get(i);
			completeText += " " + product;
		}
		// gene ontology
		ArrayList<GeneOntology> goTerms = g.GoTerms();
		for (int i=0; i<goTerms.size(); i++) {
			GeneOntology goTerm = goTerms.get(i);
			completeText += " " + goTerm.GoName();
			completeText += " " + goTerm.Namespace();
			completeText += " " + goTerm.Definition();
			completeText += " " + goTerm.Synonym();
		}
		// process complete text
		return completeText.toLowerCase();
	}
	
	private void calculateMeasureValue() {
		try {
			int count = 0;
			GeneVectorModel gvm = new GeneVectorModel(this.organism);
			Vector<String> genes = this.dbGene.getAllIdGene();
			for (int i=0; i<genes.size(); i++) {
				String id = genes.elementAt(i);
				HashMap<String,TermSpaceModel> terms = this.dbGeneTextTerm.getTermsGene(id);
				gvm.calculateMeasureValue(terms);
				Iterator<TermSpaceModel> iter = gvm.Terms().values().iterator();
				while (iter.hasNext()) {
					TermSpaceModel tvm = iter.next();
					this.dbGeneTextTerm.updateWeightTerm(id,tvm);
				}
				count++;
				if ((count%100)==0) {
					System.err.println("tfidf..." + this.organism.Code() + "..." + count + "...");
					this.dbGeneTextTerm.executeCommit();
				}
			}
		}
		catch(SQLException ex) { 
		 	System.err.println(ex);
		 	this.dbGeneTextTerm.executeRollback();
		}
	}
	
	public static void main(String[] args) {
		EnvironmentVariable.setMoaraHome("./");
		GeneTextIndexing app = new GeneTextIndexing(new Organism("9606"));
		app.processGeneText();
		//app.calculateMeasureValue("4932");
	}
	
}

