package moara.gene.entities;

import java.util.ArrayList;

import moara.bio.entities.GeneOntology;
import moara.util.text.TermSpaceModel;

public class Gene {
	
	// gene Id
	private String geneId;
	// locus name
	private String symbol;
	// ORF name
	private String name;
	// description
	private String description;
	// phenotype
	private String phenotype;
	// other names
	private ArrayList<String> synonyms;
	// products
	private ArrayList<String> products;
	// gene ontology
	private ArrayList<GeneOntology> goTerms;
	// other gene ids
	private ArrayList<String> ids;
	// gene text
	private String geneText;
	// vector space model length
	private double vectorLength;
	// terms of the gene
	ArrayList<TermSpaceModel> terms;
	
	public Gene(String geneId) {
		this.geneId = geneId;
		this.ids = new ArrayList<String>(1);
	}
	
	// get methods
	
	public String GeneId() {
		return this.geneId;	
	}
	
	public String Symbol() {
		return this.symbol;
	}
	
	public String Name() {
		return this.name;
	}
	
	public String Description() {
		return this.description;
	}
	
	public String Phenotype() {
		return this.phenotype;
	}
	
	public ArrayList<String> Synonyms() {
		return this.synonyms;
	}
	
	public ArrayList<String> Products() {
		return this.products;
	}
	
	public ArrayList<GeneOntology> GoTerms() {
		return this.goTerms;
	}
	
	public ArrayList<String> Ids() {
		return this.ids;
	}
	
	public String GeneText() {
		return this.geneText;
	}
	
	public double VectorLength() {
		return this.vectorLength;
	}
	
	public ArrayList<TermSpaceModel> Terms() {
		return this.terms;
	}
	
	// set methods
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setPhenotype(String phenotype) {
		this.phenotype = phenotype;
	}
	
	public void setSynonyms(ArrayList<String> v) {
		this.synonyms = v;
	}
	
	public void setProducts(ArrayList<String> v) {
		this.products = v;
	}
	
	public void setGoTerms(ArrayList<GeneOntology> v) {
		this.goTerms = v;
	}
	
	public void addId(String id) {
		this.ids.add(id);
	}
	
	public void setGeneText(String geneText) {
		this.geneText = geneText;
	}
	
	public void setVectorLength(double vectorLength) {
		this.vectorLength = vectorLength;
	}
	
	public void setTerms(ArrayList<TermSpaceModel> terms) {
		this.terms = terms;
	}
	
}
