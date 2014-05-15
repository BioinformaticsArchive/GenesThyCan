package moara.mention.entities;

import java.util.Vector;

public class TypeCase {
	
	public TypeCase() {
		
	}
	
	public String getCategoryNotGeneMention() {
		return "";
	}
	
	public String getCategoryGeneMention() {
		return "";
	}
	
	public String getCategoryMiddleGeneMention() {
		return "";
	}
	
	public void updateCategory(Vector<Token> words) {
		return;
	}
	
	public boolean isGeneMentionCategory(String category) {
		return false;
	}
	
	public String getAlternateCategoryBefore(String category) {
		return "";
	}
	
}
