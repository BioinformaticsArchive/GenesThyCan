package moara.bio.entities;

import java.util.ArrayList;

public class GeneOntology {
	
	// id
	private String id;
	// name
	private String goName;
	// namespace
	private String namespace;
	// definition
	private String definition;
	// synonym
	private String synonym;
	// obsolete
	private boolean isObsolete;
	// alternative ids
	private ArrayList<String> alternativeIds;
	
	public GeneOntology() {
		this.definition = "";
		this.synonym = "";
		this.isObsolete = false;
		this.alternativeIds = new ArrayList<String>();
	}
	
	public GeneOntology(String id, String goName, String namespace) {
		this.id = id;
		this.goName = goName;
		this.namespace = namespace;
		this.definition = "";
		this.synonym = "";
		this.isObsolete = false;
		this.alternativeIds = new ArrayList<String>();
	}
	
	// get methods
	
	public String Id() {
		return this.id;	
	}
	
	public String GoName() {
		return this.goName;
	}
	
	public String Namespace() {
		return this.namespace;
	}
	
	public String Definition() {
		return this.definition;
	}
	
	public String Synonym() {
		return this.synonym;
	}
	
	public boolean IsObsolete() {
		return this.isObsolete;
	}
	
	public ArrayList<String> AlternativeIds() {
		return this.alternativeIds;
	}
	
	// set methods
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setGoName(String goName) {
		this.goName = goName;
	}
	
	public void setNamespace(String namespace) {
		if (namespace.equals("biological_process"))
			this.namespace = "P";
		else if (namespace.equals("molecular_function"))
			this.namespace = "F";
		else if (namespace.equals("cellular_component"))
			this.namespace = "C";
	}
	
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}
	
	public void setIsObsolete(boolean isObsolete) {
		this.isObsolete = isObsolete;
	}
	
	// other methods
	
	public void addSynonym(String s) {
		if (this.synonym==null)
			this.synonym = s;
		else
			this.synonym = this.synonym + " " + s;
	}
	
	public void addAlternativeId(String id) {
		this.alternativeIds.add(id);
	}
	
}
