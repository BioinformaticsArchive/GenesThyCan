package moara.mention.entities;

import moara.util.Constant;

public class CaseTrueFalse extends TypeCase {

	public CaseTrueFalse() {
		super();
	}
	
	public String getCategoryNotGeneMention() {
		return Constant.CATEGORY_FALSE;
	}
	
	public String getCategoryGeneMention() {
		return Constant.CATEGORY_TRUE;
	}
	
	public String getCategoryMiddleGeneMention() {
		return Constant.CATEGORY_TRUE;
	}
	
	public boolean isGeneMentionCategory(String category) {
		if (category.equals(Constant.CATEGORY_TRUE))
			return true;
		return false;
	}
	
	public String getAlternateCategoryBefore(String categoryBefore) {
		if (categoryBefore.equals(Constant.CATEGORY_TRUE))
			return Constant.CATEGORY_FALSE;
		else
			return Constant.CATEGORY_TRUE;
	}
	
}
