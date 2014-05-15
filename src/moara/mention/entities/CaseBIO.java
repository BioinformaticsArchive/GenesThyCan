package moara.mention.entities;

import java.util.*;

import moara.util.Constant;

public class CaseBIO extends TypeCase {

	public CaseBIO() {
		super();
	}
	
	public String getCategoryNotGeneMention() {
		return Constant.TAG_BIO_O;
	}
	
	public String getCategoryGeneMention() {
		return Constant.TAG_BIO_B;
	}
	
	public String getCategoryMiddleGeneMention() {
		return Constant.TAG_BIO_I;
	}
	
	public boolean isGeneMentionCategory(String category) {
		if (category.equals(Constant.TAG_BIO_B) || 
			category.equals(Constant.TAG_BIO_I))
			return true;
		return false;
	}
	
	public void updateCategory(Vector<Token> words) {
    	boolean mentionFound = false;
		Enumeration<Token> enumer = words.elements();
		while (enumer.hasMoreElements()) {
			Token t = (Token)enumer.nextElement();
			String category = t.Category();
			// not gene mention
			if (category==Constant.CATEGORY_FALSE) {
				t.setCategory(Constant.TAG_BIO_O);
				if (mentionFound)
					mentionFound = false;
			}
			// gene mention
			else {
				// first word of the mention
				if (!mentionFound) {
					mentionFound = true;
					t.setCategory(Constant.TAG_BIO_B);
				}
				// other words of the mention
				else
					t.setCategory(Constant.TAG_BIO_I);
			}
			t.setCategory(category);
		}	
    }
	
	public String getAlternateCategoryBefore(String categoryBefore) {
		if (categoryBefore.equals(Constant.TAG_BIO_B))
			return Constant.TAG_BIO_O;
		else if (categoryBefore.equals(Constant.TAG_BIO_O))
			return Constant.TAG_BIO_B;
		else
			return Constant.TAG_BIO_O;
	}
	
}
