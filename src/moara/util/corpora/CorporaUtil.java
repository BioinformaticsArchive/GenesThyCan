package moara.util.corpora;

public class CorporaUtil {

	public CorporaUtil() {
		
	}
	
	public String getGroup(String tag) {
		for (int i=0; i<CorporaConstant.posTagGroups.length; i++) {
			String name = CorporaConstant.posTagGroups[i];
			String[] group = mapNameToGroup(name);
			if (isSyntacticGroup(group,tag))
				return name;
		}
		return null;
	}
	
	private String[] mapNameToGroup(String name) {
		if (name.equals(CorporaConstant.GROUP_ADJECTIVES))
			return CorporaConstant.adjectives;
		else if (name.equals(CorporaConstant.GROUP_ADVERBS))
			return CorporaConstant.adverbs;
		else if (name.equals(CorporaConstant.GROUP_CONJUNCTIONS))
			return CorporaConstant.conjunctions;
		else if (name.equals(CorporaConstant.GROUP_DETERMINERS))
			return CorporaConstant.determiners;
		else if (name.equals(CorporaConstant.GROUP_MODALS))
			return CorporaConstant.modals;
		else if (name.equals(CorporaConstant.GROUP_NOUNS))
			return CorporaConstant.nouns;
		else if (name.equals(CorporaConstant.GROUP_PREPOSITIONS))
			return CorporaConstant.prepositions;
		else if (name.equals(CorporaConstant.GROUP_PRONOUNS))
			return CorporaConstant.pronouns;
		else if (name.equals(CorporaConstant.GROUP_PUNCTUATIONS))
			return CorporaConstant.punctuations;
		else if (name.equals(CorporaConstant.GROUP_VERBS))
			return CorporaConstant.verbs;
		return null;
	}
	
	private boolean isSyntacticGroup(String[] group, String tag) {
		for (int i=0; i<group.length; i++) {
			if (group[i].equals(tag))
				return true;
		}
		return false;
	}
	
}
