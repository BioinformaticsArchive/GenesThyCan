package moara.util.text;

public class Document {

	private String id;
	private String text;
	
	public Document(String id, String text) {
		this.id = id;
		this.text = text;
	}
	
	public String Id() {
		return this.id;
	}
	
	public String Text() {
		return this.text;
	}
	
}
