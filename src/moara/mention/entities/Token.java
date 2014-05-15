package moara.mention.entities;

public class Token {
	
	// token text
	private String token;
	// category of the token according to gene mention:
	// - true/false
	// - BIO
	// - BIEWO
	private String category;
	// tag of the word
	private String tag;
	private int start;
	private int end;
	private int sequential;
	
	public Token () {
		this.token = "";
		this.start = 0;
		this.end = 0;
	}
	
	public Token (String token) {
		this.token = token;
		this.start = 0;
		this.end = 0;
	}
	
	public Token (String token, String category, String tag) {
		this.token = token;
		this.category = category;
		this.tag = tag;
	}
	
	public Token (String token, int start, int end) {
		this.token = token;
		this.start = start;
		this.end = end;
	}
	
	public Token (String token, int start, int end, int sequential) {
		this.token = token;
		this.start = start;
		this.end = end;
		this.sequential = sequential;
	}
	
	public Token (String token, int sequential) {
		this.token = token;
		this.sequential = sequential;
		this.start = 0;
		this.end = 0;
	}
	
	public String TokenText() {
		return this.token;
	}
	
	public String Category() {
		return this.category;
	}
	
	public String Tag() {
		return this.tag;
	}
	
	public int Start() {
		return this.start;
	}
	
	public int End() {
		return this.end;
	}
	
	public int Sequential() {
		return this.sequential;
	}
	
	// set methods
	
	public void setTokenText(String text) {
		this.token = text;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public boolean equals(Token t) {
		if (this.start==t.Start() && this.end==t.End())
			return true;
		else
			return false;
	}
	
	public String toString() {
		return this.token + "," + this.start + "," + this.end;
	}
	
	public void print() {
		System.out.println("token=" + this.token + ", start=" + 
			this.start + ", end=" + this.end + ", seq=" + this.sequential);
	}
	
}
