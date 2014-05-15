package moara.mention;

import moara.mention.entities.CaseBIEWO;
import moara.mention.entities.CaseBIO;
import moara.mention.entities.CaseTrueFalse;
import moara.mention.entities.TypeCase;

public class MentionUtil {

	public MentionUtil() {
		
	}
	
	public TypeCase decideTypeCase(String typeCategory) {
		TypeCase tc;
		if (typeCategory.equals(MentionConstant.CATEGORY_TRUE_FALSE))
			tc = new CaseTrueFalse();
		else if (typeCategory.equals(MentionConstant.CATEGORY_BIO))
			tc = new CaseBIO();
		else if (typeCategory.equals(MentionConstant.CATEGORY_BIEWO))
			tc = new CaseBIEWO();
		else
			tc = null;
		return tc;
	}
	
}
