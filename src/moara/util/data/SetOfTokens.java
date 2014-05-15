package moara.util.data;

import java.util.ArrayList;

import moara.util.corpora.Token;
import moara.util.data.ItemSet;

public class SetOfTokens implements Cloneable, ItemSet {

	private ArrayList<Token> tokens;
	
	public SetOfTokens(Token t) {
		this.tokens = new ArrayList<Token>(1);
		this.tokens.add(t.copy());
	}
	
	public ArrayList<Token> Tokens() {
		return this.tokens;
	}
	
	public int Start() {
		return this.tokens.get(0).Start();
	}
	
	public int End() {
		return this.tokens.get(getLastIndex()).End();
	}
	
	public Token getStartToken() {
		return this.tokens.get(0);
	}
	
	public Token getEndToken() {
		return this.tokens.get(getLastIndex());
	}
	
	public void addStartToken(Token t) {
		this.tokens.add(0,t.copy());
	}
	
	public void addEndToken(Token t) {
		this.tokens.add(t.copy());
	}
	
	private int getLastIndex() {
		return this.tokens.size()-1;
	}
	
	public String toString() {
		String text = "";
		for (int i=0; i<this.tokens.size(); i++)
			text += this.tokens.get(i).toString();
		return text;
	}
	
	public void print() {
		System.out.println(this.toString());
	}
	
	public SetOfTokens copy() {
		try {
			return (SetOfTokens)this.clone();
		}
		catch(CloneNotSupportedException ex) { 
	     	System.err.println(ex);
	    }
		return null;
	}
	
	// ItemSet
	
	public String getName() {
		return null;
	}
	
	public String Role() {
		return this.tokens.get(0).Role();
	}
	
	public int Sequential() {
		return 0;
	}
	
	public void addToName(String name) {
		
	}
	
	public void setRole(String role) {
		for (int i=0; i<this.Tokens().size(); i++) {
			
		}
	}
	
}
