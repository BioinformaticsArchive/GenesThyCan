package moara.util.corpora;

import java.util.ArrayList;

public class Document {

	protected String id;
	protected String title;
	protected String summary;
	protected ArrayList<Sentence> titleSentences;
	protected ArrayList<Sentence> sentences;
	protected int lengthTextNoSpaces;
	
	public Document(String id) {
		this.id = id;
		this.titleSentences = new ArrayList<Sentence>();
		this.sentences = new ArrayList<Sentence>();
	}
	
	public String Id() {
		return this.id;
	}
	
	public String Title() {
		return this.title;
	}
	
	public String Summary() {
		return this.summary;
	}
	
	public String Text() {
		String text = this.title;
		if (this.summary!=null)
			text += " " + this.summary;
		return text;
	}
	
	public ArrayList<Sentence> TitleSentences() {
		return this.titleSentences;
	}
	
	public ArrayList<Sentence> Sentences() {
		return this.sentences;
	}
	
	public int LengthTextNoSpaces() {
		return this.lengthTextNoSpaces;
	}
	
	public Sentence getSentenceBySeq(int seq) {
		for (int i=0; i<this.sentences.size(); i++) {
			Sentence s = this.sentences.get(i);
			if (s.Sequential()==seq)
				return s;
		}
		return null;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public void addTitleSentence(Sentence s) {
		this.titleSentences.add(s);
	}
	
	public void addSentence(Sentence s) {
		this.sentences.add(s);
	}
	
	public void clean() {
		this.titleSentences = new ArrayList<Sentence>();
		this.sentences = new ArrayList<Sentence>();
	}
	
	public void print() {
		System.out.println("document=" + this.id + "," + this.title);
		for (int i=0; i<this.sentences.size(); i++)
			this.sentences.get(i).print();
	}
	
}
