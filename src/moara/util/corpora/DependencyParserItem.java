package moara.util.corpora;

public class DependencyParserItem {

	private Token token1;
	private Token token2;
	private String tag;
	
	public DependencyParserItem(Token token1, Token token2, String tag) {
		this.token1 = token1;
		this.token2 = token2;
		this.tag = tag;
	}
	
	public Token Token1() {
		return this.token1;
	}
	
	public Token Token2() {
		return this.token2;
	}
	
	public String Tag() {
		return this.tag;
	}
	
	public void print() {
		System.out.println(this.tag + ":" + this.token1.TokenText()+"-"+this.token1.Sequential() + "," + 
			this.token2.TokenText() + "-" + this.token2.Sequential());
	}
	
}
