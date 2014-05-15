package moara.gene.functions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import moara.bio.entities.Organism;
import moara.gene.dbs.DBGene;
import moara.gene.dbs.DBGeneTerm;
import moara.gene.dbs.DBGeneTextTerm;
import moara.util.text.TermSpaceModel;
import moara.util.text.VectorSpaceModel;

public class GeneVectorModel extends VectorSpaceModel {

	private Organism organism;
	
	public GeneVectorModel(Organism organism) {
		super();
		this.organism = organism;
	}
	
	protected int getNumDoc() {
		// number of genes (with terms) for the organism
		try {
			DBGene dbGene = new DBGene(this.organism);
			return dbGene.getTotalNumGene();
		}
		catch(SQLException ex) { 
			System.err.println(ex);
		}
		return 0;
	}
	
	protected int getNumDocByTerm(String term) {
		// get number of genes with the specified term, for the organism
		try {
			DBGeneTerm dbGeneTerm = new DBGeneTerm(this.organism);
			return dbGeneTerm.getNumGenesTerm(term);
		}
		catch(SQLException ex) { 
			System.err.println(ex);
		}
		return 0;
	}
	
	protected ArrayList<String> getTrainingTerms() {
		// total of terms for the organism
		try {
			DBGeneTerm dbGeneTerm = new DBGeneTerm(this.organism);
			return dbGeneTerm.getAllTerms();
		}
		catch(SQLException ex) { 
			System.err.println(ex);
		}
		return null;
	}
	
	protected HashMap<String,TermSpaceModel> getTermsDoc(int id) {
		// get terms of a specified gene
		try {
			DBGeneTextTerm dbGeneTextTerm = new DBGeneTextTerm(this.organism);
			return dbGeneTextTerm.getTermsGene((new Integer(id)).toString());
		}
		catch(SQLException ex) { 
			System.err.println(ex);
		}
		return null;
	}
	
	protected void removeLowFrequencyTerms(int frequency) {
		try {
			DBGeneTerm dbGeneTerm = new DBGeneTerm(this.organism);
			dbGeneTerm.removeUnfrequentTerms(frequency);
		}
		catch(SQLException ex) { 
			System.err.println(ex);
		}
	}
	
	protected void removeFewDocsTerms(int numDoc) {
		
	}
	
}
