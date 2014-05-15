package moara.wrapper.porter;

public class PorterStemmer {

	private Stemmer stemmer;
	
	public PorterStemmer() {
		this.stemmer = new Stemmer();
	}
	
	public String getStem(String token) {
		this.stemmer = new Stemmer();
		for (int i=0; i<token.length(); i++)
			this.stemmer.add(token.charAt(i));
		this.stemmer.stem();
		return this.stemmer.toString().toLowerCase();
	}
	
	public static void main(String[] args) {
		PorterStemmer app = new PorterStemmer();
		System.out.println(app.getStem("test"));
	}
	
}
