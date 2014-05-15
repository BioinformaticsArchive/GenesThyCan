package moara.mention.entities;

import java.util.Vector;

import moara.util.Constant;

public class CaseBIEWO extends TypeCase {
	
	public CaseBIEWO() {
		super();
	}
	
	public String getCategoryNotGeneMention() {
		return Constant.TAG_BIEWO_O;
	}
	
	public String getCategoryGeneMention() {
		return Constant.TAG_BIEWO_B;
	}
	
	public String getCategoryMiddleGeneMention() {
		return Constant.TAG_BIEWO_I;
	}
	
	public boolean isGeneMentionCategory(String category) {
		if (category.equals(Constant.TAG_BIEWO_B) || 
			category.equals(Constant.TAG_BIEWO_I) ||
			category.equals(Constant.TAG_BIEWO_E) ||
			category.equals(Constant.TAG_BIEWO_W))
			return true;
		return false;
	}
	
	public void updateCategory(Vector<Token> words) {
    	boolean mentionFound = false;
		for (int i=0; i<=words.size(); i++) {
			String category;
			Token t = null;
			if (i<words.size()) {
				t = (Token)words.elementAt(i);
				category = t.Category();
			}
			// no more words...
			else
				category=Constant.CATEGORY_FALSE;
			// not gene mention
			if (category==Constant.CATEGORY_FALSE) {
				t.setCategory(Constant.TAG_BIEWO_O);
				if (mentionFound) {
					// get last token
					Token lastToken = (Token)words.elementAt(i-1);
					// last token is B, actual category W (single word mention)
					if (lastToken.Category().equals(Constant.TAG_BIEWO_B))
						lastToken.setCategory(Constant.TAG_BIEWO_W);
					// last token is I, actual category E (last word of mention)
					if (lastToken.Category().equals(Constant.TAG_BIEWO_I))
						lastToken.setCategory(Constant.TAG_BIEWO_E);
					mentionFound = false;
				}
			}
			// gene mention
			else {
				// first word of the mention
				if (!mentionFound) {
					mentionFound = true;
					category = Constant.TAG_BIO_B;
				}
				// other words of the mention
				else
					category = Constant.TAG_BIO_I;
			}
		}	
    }
	
	public String getAlternateCategoryBefore(String categoryBefore) {
		if (categoryBefore.equals(Constant.TAG_BIEWO_B))
			return Constant.TAG_BIEWO_O;
		else if (categoryBefore.equals(Constant.TAG_BIEWO_O))
			return Constant.TAG_BIEWO_B;
		else if (categoryBefore.equals(Constant.TAG_BIEWO_W))
			return Constant.TAG_BIEWO_O;
		else if (categoryBefore.equals(Constant.TAG_BIEWO_E))
			return Constant.TAG_BIEWO_O;
		else
			return Constant.TAG_BIO_O;
	}
	
}

