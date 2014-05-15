package moara.gene.entities;

public class GeneProduct {
	
	// gene Id
	private String geneId;
	// product
	private String product;
	
	public GeneProduct(String geneId, String product) {
		this.geneId = geneId;
		this.product = product;
	}
	
	// get methods
	
	public String GeneId() {
		return this.geneId;	
	}
	
	public String Product() {
		return this.product;
	}
	
}