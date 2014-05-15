package moara.util.corpora;

import java.util.ArrayList;

public abstract class Corpus {

	protected ArrayList<Document> documents;
	protected String type;
	protected String directory;
	
	public Corpus() {
		super();
		this.documents = new ArrayList<Document>();
	}
	
	public Corpus(String type) {
		this.type = type;
		this.documents = new ArrayList<Document>();
		setDirectory();
	}
	
	public ArrayList<Document> Documents() {
		this.documents.trimToSize();
		return this.documents;
	}
	
	public String Type() {
		return this.type;
	}
	
	public String Directory() {
		return this.directory;
	}
	
	public void addDocument(Document doc) {
		this.documents.add(doc);
	}
	
	public void clean() {
		this.documents.clear();
	}
	
	protected abstract void setDirectory();
	
}
