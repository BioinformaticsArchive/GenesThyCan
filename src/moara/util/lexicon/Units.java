package moara.util.lexicon;

import moara.util.text.StringUtil;

public class Units {

	// units
	private String units[] = {
		"amino-acid","amino","&apos",
		"basepair","base-pair","bp",
		"cm",
		"days-old","day-old","d-old","day","days",
		"fold",
		"hour","hz","hr","hrs",
		"kilobase-long","kilobase-pair","kilobase","kb-long","kda","kbp","kb",
		"kd","k",
		"months-old","month-old","mo-old","month","minute","min","mhz","mmhg","mg","mb",
		"nucleotides","nucleotide","nm-diamet","nm",
		"pound","pounds",
		"qter","&quot",
		"residues","residue",
		"subunit","side",
		"weeks-old","week-old","wk-old","week",
		"year-old","years-old","year","yr","yrs",
		// part of units
		"am",
		"base",
		"d",
		"g","&gt",
		"h",
		"kg",
		"l","lb","lbs",
		"mm","ml",
		"pm",
		"s","sec",
		"y"
	};
	// number separators
	private String separators[] = {"x","-","--"};
	
	public Units() {
		
	}
	
	public boolean hasUnit(String word) {
		for (int i=0; i<units.length; i++) {
			String u = units[i];
			if (word.endsWith("-"+u) && 
				(isNumber(word.substring(0,word.length()-u.length()-1)) ||
				isNumberSeparator(word.substring(0,word.length()-u.length()-1)) ) )
				return true;
			if (word.endsWith(u) && 
				(isNumber(word.substring(0,word.length()-u.length())) ||
				isNumberSeparator(word.substring(0,word.length()-u.length()))	) )
				return true;
		}
		return false;
	}
	
	private boolean isNumber(String token) {
		StringUtil su = new StringUtil();
		if (su.isNumeral(token) || su.isDecimal(token) || 
				su.isThousandNumeral(token))
			return true;
		return false;
	}
	
	private boolean isNumberSeparator(String word) {
		for (int i=0; i<separators.length; i++) {
			String s = separators[i];
			if (word.indexOf(s)>0) {
				int position = word.indexOf(s);
				if (isNumber(word.substring(0,position)) && isNumber(word.substring(position+s.length(),word.length())) )
					return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		Units app = new Units();
		System.err.println(app.hasUnit("10am"));
	}
	
}
