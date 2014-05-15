package moara.bio.entities;

public class MeshTerm {

	// descriptor
	private String descriptor;
	// qualifier
	private String qualifier;
	
	public MeshTerm(String descriptor, String qualifier) {
		this.descriptor = descriptor;
		this.qualifier = qualifier;
	}
	
	public String Descriptor() {
		return this.descriptor;
	}
	
	public String Qualifier() {
		return this.qualifier;
	}
	
}
