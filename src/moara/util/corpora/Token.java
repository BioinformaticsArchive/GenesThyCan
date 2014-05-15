package moara.util.corpora;

import java.lang.CloneNotSupportedException;
import java.lang.Cloneable;
import java.lang.Comparable;

import moara.util.corpora.Sentence;
import moara.util.data.WindowTextItem;
import moara.util.data.ItemSet;
import moara.tagger.basic.entities.FeatureArgument;

public class Token implements WindowTextItem, Cloneable, Comparable<Token>, ItemSet, FeatureArgument {
	
	private int sequential;
	private String token;
	private int start;
	private int end;
	private String posTag; // hyphen-separated
	private String entityTag;
	private ChunkTag chunkTag;
	private PennTreebankItem ptbParserItem;
	private String parserTag;
	private Sentence sentence;
	private String name;
	private String[] bigrams;
	private String[] trigrams;
	private String role;
	private String id;
	private String equivalentId;
	// delete it!!
	private String tag;
	
	public Token () {
		init();
	}
	
	public Token (String token) {
		this.token = token;
		this.start = 0;
		this.end = 0;
		this.id = "";
	}
	
	public Token (String token, int start, int end) {
		this.token = token;
		this.start = start;
		this.end = end;
		this.id = "";
	}
	
	public Token (String token, int start, int end, int sequential, Sentence s) {
		this.token = token;
		this.start = start;
		this.end = end;
		this.sequential = sequential;
		this.sentence = s;
		this.id = "";
	}
	
	public Token (String token, int sequential) {
		this.token = token;
		this.sequential = sequential;
		this.start = 0;
		this.end = 0;
		this.id = "";
	}
	
	public int Sequential() {
		return this.sequential;
	}
	
	public String TokenText() {
		return this.token;
	}
	
	public int Start() {
		return this.start;
	}
	
	public int End() {
		return this.end;
	}
	
	public String PosTag() {
		return this.posTag;
	}
	
	public String EntityTag() {
		return this.entityTag;
	}
	
	public ChunkTag ChunkTag() {
		return this.chunkTag;
	}
	
	public PennTreebankItem PtbParserItems() {
		return this.ptbParserItem;
	}
	
	public String ParserTag() {
		return this.parserTag;
	}
	
	public Sentence Sentence() {
		return this.sentence;
	}
	
	public String Tag() {
		return this.tag;
	}
	
	public String[] Bigrams() {
		if (this.bigrams==null)
			generateBigrams();
		return this.bigrams;
	}
	
	public String[] Trigrams() {
		if (this.trigrams==null)
			generateTrigrams();
		return this.trigrams;
	}
	
	public String Role() {
		return this.role;
	}
	
	public String Id() {
		return this.id;
	}
	
	public String EquivalentId() {
		return this.equivalentId;
	}
	
	public void setSequential(int sequential) {
		this.sequential = sequential;
	}
	
	public void setTokenText(String text) {
		this.token = text;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public void setPosTag(String tag) {
		this.posTag = tag;
	}
	
	public void addToPosTag(String tag) {
		this.posTag += "-" + tag;
	}
	
	public void setEntityTag(String tag) {
		this.entityTag = tag;
	}
	
	public void setChunkTag(ChunkTag tag) {
		this.chunkTag = tag;
	}
	
	public void setPtbParserItem(PennTreebankItem item) {
		this.ptbParserItem = item;
	}
	
	public void setParserTag(String tag) {
		this.parserTag = tag;
	}
	
	public void setSentence(Sentence s) {
		this.sentence = s;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setEquivalentId(String id) {
		this.equivalentId = id;
	}
	
	private void generateBigrams() {
		int total;
		int start = 0;
		int end;
		if (this.token.length()>2) {
			total = this.token.length()-1;
			end = 2;
		}
		else {
			total = 1;
			end = this.token.length();
		}
		this.bigrams = new String[total];
		for (int i=0; i<total; i++) {
			System.out.println(this.token.length() + " " + start + " " + end);
			this.bigrams[i] = this.token.substring(start,end).toLowerCase();
			start++;
			end++;
		}
	}
	
	private void generateTrigrams() {
		int total;
		int start = 0;
		int end;
		if (this.token.length()>3) {
			total = this.token.length()-2;
			end = 3;
		}
		else {
			total = 1;
			end = this.token.length();
		}
		this.trigrams = new String[total];
		for (int i=0; i<total; i++) {
			this.trigrams[i] = this.token.substring(start,end).toLowerCase();
			start++;
			end++;
		}
	}
	
	public boolean equals(Token t) {
		if (this.start==t.Start() && this.end==t.End())
			return true;
		else
			return false;
	}
	
	public String toString() {
		String text = "";
		text += "token=" + this.token + ", (" + this.start + "," + this.end + 
			") seq=" + this.sequential + ", pos=" + this.posTag + 
			", entity=" + this.entityTag + 
			", chunk=" + (this.chunkTag==null?"null":this.chunkTag.Tag()) + 
			", chunkPos=" + (this.chunkTag==null?"null":this.chunkTag.PositionTag()) + 
			", chunkSeqPhrase=" + (this.chunkTag==null?"null":this.chunkTag.SeqPhrase()) + 
			//", parser=" + this.ptbParserItem + 
			", role=" + this.role;
		return text;
	}
	
	public void print() {
		System.err.println(toString());
	}
	
	public String getName() {
		return this.name;
	}
	
	public void addToName(String name) {
		this.name = name;
	}
	
	public String getComplement() {
		return null;
	}
	
	// WindowTextItem
	
	public void init() {
		this.token = null;
		this.start = -1;
		this.end = -1;
		this.id = "";
	}
	
	public Token newObject() {
		Token t = new Token();
		t.init();
		return t;
	}
	
	public Token copy() {
		try {
			return (Token)this.clone();
		}
		catch(CloneNotSupportedException ex) { 
	     	System.err.println(ex);
	    }
		return null;
	}
	
	public boolean check(String value) {
		if (this.token!=null)
			return this.token.equals(value);
		else
			return false;
	}
	
	public int compareTo(Token t) {
		if (this.start>t.Start())
			return 1;
		else if (this.start<t.Start())
			return -1;
		else
			return 0;
	}
	
}
