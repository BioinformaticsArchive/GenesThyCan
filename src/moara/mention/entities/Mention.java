package moara.mention.entities;

import moara.normalization.entities.GenePrediction;

import java.util.ArrayList;

public class Mention {
	
	/**
	 * Encapsulates the attributes of a gene/protein mention extracted from a text.
	 * 
	 * @author			Mariana Neves
	 * @version			1.1.0 
	 * 
	 */
	
	// gene mention
	private String text;
	// type of the mention
	private String type;
	// start position of the gene mention in the text
	private int start;
	// end position of the gene mention in the text
	private int end;
	// category of the mention
	private String category;
	// document id of the mention
	private String documentId;
	// operation to be performed in the mention - insert or delete
	private int operation;
	// normalization predictions
	private ArrayList<GenePrediction> geneIds;
	// disambiguated prediction
	private GenePrediction geneId;
	
	public Mention (String text, String type, int start, int end, String category) {
		this.text = text;
		this.type = type;
		this.start = start;
		this.end = end;
		this.category = category;
		this.geneIds = new ArrayList<GenePrediction>(5);
	}
	
	/**
	 * Constructor for a gene/protein mention object.
	 *  
	 * @param	text		the text of the extracted mention
	 * @param	start		the position of the first character of the mention in the original text
	 * @param	end			the position of the last character of the mention in the original text
	 * 
	 */
	public Mention (String text, int start, int end) {
		this.text = text;
		this.start = start;
		this.end = end;
		this.geneIds = new ArrayList<GenePrediction>(5);
	}
	
	public Mention (String text, int start, int end, String documentId) {
		this.text = text;
		this.start = start;
		this.end = end;
		this.documentId = documentId;
		this.geneIds = new ArrayList<GenePrediction>(5);
	}
	
	public Mention (String text, int start, int end, int operation) {
		this.text = text;
		this.start = start;
		this.end = end;
		this.operation = operation;
		this.geneIds = new ArrayList<GenePrediction>(5);
	}
	
	/**
	 * Get the text of the mention.
	 *  
	 * @return	the text of the mention
	 * 
	 */
	public String Text() {
		return this.text;
	}
	
	public String Type() {
		return this.type;
	}
	
	/**
	 * Get the position of the first character of the mention.
	 *  
	 * @return	the position of the first character of the mention
	 * 
	 */
	public int Start() {
		return this.start;
	}
	
	/**
	 * Get the position of the last character of the mention.
	 *  
	 * @return	the position of the last character of the mention
	 * 
	 */
	public int End() {
		return this.end;
	}
	
	public String Category() {
		return this.category;
	}
	
	public String DocumentId() {
		return this.documentId;
	}
	
	public int Operation() {
		return this.operation;
	}
	
	public ArrayList<GenePrediction> GeneIds() {
		return this.geneIds;
	}
	
	public GenePrediction GeneId() {
		return this.geneId;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public void setGeneId(GenePrediction gp) {
		this.geneId = gp;
	}
	
	public void setDocumentId(String doc) {
		this.documentId = doc;
	}
	
	public void addGenePrediction(GenePrediction gp) {
		this.geneIds.add(gp);
	}
	
	public String toString() {
		return this.text + " " + this.start + "," + this.end;
	}
	
}

