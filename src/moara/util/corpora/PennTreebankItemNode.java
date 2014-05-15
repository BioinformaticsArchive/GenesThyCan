package moara.util.corpora;

public class PennTreebankItemNode {

	private int sequential;
	private String tag;
	private String text;
	private int start;
	private int end;
	
	public PennTreebankItemNode(int sequential, String tag) {
		this.sequential = sequential;
		this.tag = tag;
		this.text = "";
		this.start = -1;
		this.end = -1;
	}
	
	public PennTreebankItemNode(PennTreebankItemNode node) {
		this.sequential = node.Sequential();
		this.tag = node.Tag();
		this.text = node.Text();
		this.start = node.Start();
		this.end = node.End();
	}
	
	public int Sequential() {
		return this.sequential;
	}
	
	public String Tag() {
		return this.tag;
	}
	
	public String Text() {
		return this.text.trim();
	}
	
	public int Start() {
		return this.start;
	}
	
	public int End() {
		return this.end;
	}
	
	public void addToText(String text) {
		this.text += " " + text;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public String toString() {
		return "[" + this.sequential + "," + this.tag + "]:(" + this.start + "," + this.end + ")" + this.text;
	}
	
	public void print() {
		System.out.println(this.toString());
	}
	
}
