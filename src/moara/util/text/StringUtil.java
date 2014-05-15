package moara.util.text;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moara.normalization.NormalizationConstant;
import moara.util.lexicon.GreekLetter;

public class StringUtil {

	public StringUtil() {
		
	}
	
	public boolean isEmptyToken(String s) {
		int length = s.trim().length();
		if (length==0)
			return true;
		else
			return false;
		/*for (int i=0; i<s.length(); i++) {
			if (s.charAt(i)!=' ')
				return false;
		}
		return true;*/
	}
	
	public boolean hasLetter(String s) {
		for (int i=0; i<s.length(); i++) {
			if (Character.isLetter(s.charAt(i)))
				return true;
		}
		return false;
	}
	
	public boolean isNumeral(String s) {
		for (int i=0; i<s.length(); i++) {
			if (!isNumeral(s.charAt(i)))
				return false;
		}
		return true;
	}
	
	public boolean isNumeral(char ch) {
		if (Character.isDigit(ch))
			return true;
		return false;
	}
	
	public boolean isNegativeNumeral(String s) {
		if (s.substring(0,1).equals("-") && isNumeral(s.substring(1)))
			return true;
		return false;
	}
	
	public boolean isFractionNumeral(String s) {
		if (s.indexOf("/")>=1) {
			int index = s.indexOf("/");
			if (isNumeral(s.substring(0,index)) && 
					isNumeral(s.substring(index+1,s.length())))
				return true;
		}
		return false;
	}
	
	public boolean isThousandNumeral(String s) {
		if (s.indexOf(",")>=1) {
			int index = s.indexOf(",");
			if (isNumeral(s.substring(0,index)) && 
					isNumeral(s.substring(index+1,s.length())))
				return true;
		}
		return false;
	}
	
	public boolean isOrdinalNumber(String s) {
		if (s.endsWith("st") || s.endsWith("nd") || s.endsWith("rd") || s.endsWith("th")) {
			if (isNumeral(s.substring(0,s.length()-2)))
				return true;
		}
		return false;
	}
	
	public boolean isRomanNumeral(String s) {
		// only small Roman numerals
		if (s.length()>4)
			return false;
		// check characters
		s = s.toLowerCase();
		char last = '.';
		for (int i=0; i<s.length(); i++) {
			Character c = new Character(s.charAt(i));
			// check if Roman numeral letter
			if (!isRomanCharacter(s.charAt(i)))
				return false;
			// check not allowed repetition
			if (last=='v' && c==last)
				return false;
			if (last=='l' && c==last)
				return false;
			if (last=='d' && c==last)
				return false;
			// not allowed sequences
			if (last=='i' && (c=='l' || c=='c' || c=='d' || c=='m'))
				return false;
			if (last=='v' && (c=='x' || c=='l' || c=='c' || c=='d' || c=='m'))
				return false;
			if (last=='x' && (c=='d' || c=='m'))
				return false;
			if (last=='l' && (c=='c' || c=='d' || c=='m'))
				return false;
			if (last=='d' && (c=='m'))
				return false;
			last = c;
		}
		return true;
	}
	
	public boolean isLetter(char ch) {
		if (Character.isLetter(ch))
			return true;
		return false;
	}
	
	private boolean isRomanCharacter(char c) {
		if (c=='i' || c=='v' || c=='x' || c=='l' || c=='c' || c=='d' || c=='m')
			return true;
		return false;
	}
	
	public boolean isNumberInterval(String s) {
		if (s.indexOf("-")>=1) {
			int index = s.indexOf("-");
			String left = s.substring(0,index);
			String right = s.substring(index+1,s.length());
			if (isNumeral(left) && isNumeral(right))
				return true;
		}
		return false;
	}
	
	public boolean isDecimal(String s) {
		if (s.indexOf(".")>=1) {
			int index = s.indexOf(".");
			String left = s.substring(0,index);
			String right = s.substring(index+1,s.length());
			if (isNumeral(left) && isNumeral(right))
				return true;
		}
		return false;
	}
	
	public boolean isDecimalInterval(String s) {
		if (s.indexOf("-")>=3) {
			int index = s.indexOf("-");
			String left = s.substring(0,index);
			String right = s.substring(index+1,s.length());
			if (isDecimal(left) && isDecimal(right))
				return true;
			if (isDecimal(left) && isNumeral(right))
				return true;
			if (isNumeral(left) && isNumeral(right))
				return true;
		}
		return false;
	}
	
	public boolean hasOnlyLetters(String s) {
		for (int i=0; i<s.length(); i++) {
			if (!Character.isLetter(s.charAt(i)))
				return false;
		}
		return true;
	}
	
	public boolean hasNoLetters(String s) {
		for (int i=0; i<s.length(); i++) {
			if (Character.isLetter(s.charAt(i)))
				return false;
		}
		return true;
	}
	
	public boolean hasLowerCaseLetter(String s) {
		for (int i=0; i<s.length(); i++) {
			if (Character.isLetter(s.charAt(i)) && Character.isLowerCase(s.charAt(i)))
				return true;
		}
		return false;
	}
	
	public boolean hasOnlyLowerCaseLetters(String s) {
		for (int i=0; i<s.length(); i++) {
			Character c = new Character(s.charAt(i));
			if (isNumeral(c.toString()))
				return false;
			if (Character.isLetter(s.charAt(i)) && Character.isUpperCase(s.charAt(i)))
				return false;
		}
		return true;
	}
	
	public boolean hasUpperCaseLetter(String s) {
		for (int i=0; i<s.length(); i++) {
			if (Character.isLetter(s.charAt(i)) && Character.isUpperCase(s.charAt(i)))
				return true;
		}
		return false;
	}
	
	public boolean hasFirstUpperCaseLetter(String s) {
		for (int i=0; i<s.length(); i++) {
			Character c = new Character(s.charAt(i));
			if (!Character.isLetter(c))
				return false;
			if (i==0 && Character.isLowerCase(c))
				return false;
			if (i!=0 && Character.isUpperCase(c))
				return false;
		}
		return true;
	}
	
	public boolean hasOnlyUpperCaseLetters(String s) {
		for (int i=0; i<s.length(); i++) {
			Character c = new Character(s.charAt(i));
			if (isNumeral(c.toString()))
				return false;
			if (Character.isLetter(s.charAt(i)) && Character.isLowerCase(s.charAt(i)))
				return false;
		}
		return true;
	}
	
	public boolean hasPredominantlyUpperCaseLetters(String s) {
		int upper = 0;
		int lower = 0;
		for (int i=0; i<s.length(); i++) {
			Character c = new Character(s.charAt(i));
			if (isNumeral(c.toString()))
				upper++;
			else if (Character.isLetter(s.charAt(i)) && Character.isUpperCase(s.charAt(i)))
				upper++;
			else
				lower++;
		}
		if (upper>=lower)
			return true;
		else
			return false;
	}
	
	public boolean hasSuffix(String word, int length) {
		String suffix = word.substring(word.length()-length,word.length());
		if (hasOnlyLowerCaseLetters(suffix))
			return true;
		return false;
	}
	
	public boolean hasPrefix(String word, int length) {
		String prefix = word.substring(0,length);
		if (hasOnlyLowerCaseLetters(prefix))
			return true;
		return false;
	}
	
	// start and end referred from the text without space
	public int getIndexWithSpace(String textWithSpace, int index) {
		int indexWithSpace = 0;
		int indexWithoutSpace = 0;
		StringTokenizer st = new StringTokenizer(textWithSpace," \n",true);
		while (st.hasMoreTokens()) {
			String w = st.nextToken();
			indexWithSpace += w.length();
			if (!this.isEmptyToken(w)) {
				indexWithoutSpace += w.length();
				//System.out.println(indexWithSpace + " " + indexWithoutSpace + " " + w);
				if (index>=(indexWithoutSpace-w.length()) && index<indexWithoutSpace) {
					indexWithSpace -= indexWithoutSpace-index;
					return indexWithSpace;
				}
			}
		}
		return -1;
	}
	
	public String getStringWithSpace(String textWithSpace, int start, int end) {
		String text = "";
		int index = 0;
		boolean open = false;
		StringTokenizer st = new StringTokenizer(textWithSpace);
		while (st.hasMoreTokens()) {
			String w = st.nextToken();
			if (!open && index==start) {
				open = true;
				text += " " + w;
			}
			else if (!open && (start>index && start<(index+w.length())) ) {
				open = true;
				text += " " + w.substring(start-index,w.length());
			}
			else if (open)
				text += " " + w;
			index += w.length();
			if (index==end)
				break;
		}
		return text.trim();
	}
	
	/*public String getStringWithSpace(String textWithSpace, int start, int end) {
		//System.err.println(start + " " + end);
		//System.err.println(textWithSpace.length() + " " + textWithSpace);
		String text = textWithSpace.replace(" ","");
		//System.err.println(text.length() + " " + text);
		return text.substring(start,end);
	}*/
	
	/*public int getLengthWithoutSpace(String word) {
		int length = 0;
		for (int i=0; i<word.length(); i++) {
			if (word.charAt(i)!=' ')
				length++;
		}
		return length;
	}*/
	
	public String getStringWithoutSpace(String original) {
		String noSpaces = original.replace(" ","");
		return noSpaces;
	}
	
	public int getLengthWithoutSpace(String original) {
		String noSpaces = original.replace(" ","");
		return noSpaces.length();
	}
	
	public boolean isAbbreviation(String s) {
		if (s.length()>8 || s.length()<2)
			return false;
		for (int i=0; i<s.length(); i++) {
			if (Character.isLetter(s.charAt(i)) && Character.isLowerCase(s.charAt(i)))
				return false;
		}
		return true;
	}
	
	public String getCommonPrefix(String token1, String token2) {
		String prefix = "";
		boolean keepLoop = true;
		int index = 0;
		while (keepLoop && index<token1.length() && index<token2.length()) {
			if (token1.charAt(index)==token2.charAt(index))
				prefix += token1.charAt(index);
			else
				keepLoop = false;
			index++;
		}
		return prefix.trim();
	}
	
	public String getCommonSuffix(String token1, String token2) {
		String suffix = "";
		boolean keepLoop = true;
		int index = 1;
		while (keepLoop && index<=token1.length() && index<=token2.length()) {
			//System.err.println(token1 + " " + token2 + " " + index);
			if (token1.charAt(token1.length()-index)==token2.charAt(token2.length()-index))
				suffix += token1.charAt(token1.length()-index);
			else
				keepLoop = false;
			index++;
		}
		return suffix.trim();
	}
	
	public String getShape(String s) {
		StringUtil su = new StringUtil();
		String s1 = "";
		String lastCode = "";
		for (int i=0; i<s.length(); i++) {
			char c = s.charAt(i);
			if (su.isLetter(c) && Character.isLowerCase(c) && 
					!lastCode.equals(NormalizationConstant.SYMBOL_FORMAT_LOWER_CASE)) {
				s1 += NormalizationConstant.SYMBOL_FORMAT_LOWER_CASE;
				lastCode = NormalizationConstant.SYMBOL_FORMAT_LOWER_CASE;
			}
			else if (su.isLetter(c) && Character.isUpperCase(c) &&
					!lastCode.equals(NormalizationConstant.SYMBOL_FORMAT_UPPER_CASE)) {
				s1 += NormalizationConstant.SYMBOL_FORMAT_UPPER_CASE;
				lastCode = NormalizationConstant.SYMBOL_FORMAT_UPPER_CASE;
			}
			else if (su.isNumeral(c) && 
					!lastCode.equals(NormalizationConstant.SYMBOL_FORMAT_NUMBER)) {
				s1 += NormalizationConstant.SYMBOL_FORMAT_NUMBER;
				lastCode = NormalizationConstant.SYMBOL_FORMAT_NUMBER;
			}
			else if (!su.isLetter(c) && !su.isNumeral(c)) {
				s1 += c;
				lastCode = (new Character(c)).toString();
			}
				
		}
		return s1;
	}
	
	public String[] getTokens(String text) {
		String[] terms;
		if (text !=null && text.length()>0) {
			StringTokenizer tokens = new StringTokenizer(text,"|");
			terms = new String[tokens.countTokens()];
			int index = 0;
			while (tokens.hasMoreTokens()) {
				terms[index] = tokens.nextToken();
				index++;
			}
		}
		else {
			terms = new String[0];
		}
		return terms;
	}
	
	public String unifyTokensUnderscore(String terms) {
		String unified = "";
		StringTokenizer tokens = new StringTokenizer(terms);
		while (tokens.hasMoreTokens()) {
			unified += tokens.nextToken() + "_";
		}
		if (unified.endsWith("_"))
			unified = unified.substring(0,unified.length()-1);
		return unified;
	}
	
	public String separateTokensUnderscore(String unified) {
		String separated = "";
		StringTokenizer tokens = new StringTokenizer(unified,"_");
		while (tokens.hasMoreTokens()) {
			separated += tokens.nextToken() + " ";
		}
		return separated.trim();
	}
	
	public String getTokenByPosition(String text, int start, int end) {
		String token = "";
		int position = 0;
		for (int i=0; i<text.length(); i++) {
			char c = text.charAt(i);
			if (position>=start && position<=end)
				token += c;
			if (c!=' ')
				position++;
		}
		return token;
	}
	
	public String orderAlphabetically(String original, boolean byHyphenUnderscore,
			boolean byCommaPeriod, boolean byParenthesisBrackets, boolean byNumber) {
		String symbols = " ";
		GreekLetter greek = new GreekLetter();
		if (greek.hasGreekLetter(original))
			original = separateGreekLetters(original,greek.getGreekLetter(original));
		if (byNumber)
			original = separateNumbers(original);
		if (byHyphenUnderscore)
			symbols += "+-_";
		if (byCommaPeriod)
			symbols += ",.';\\/";
		if (byParenthesisBrackets)
			symbols += "()[]";
		StringTokenizer tokenizer = new StringTokenizer(original,symbols,false);
		String[] tokens = new String[tokenizer.countTokens()];
		int index = 0;
		while (tokenizer.hasMoreTokens()) {
			tokens[index] = tokenizer.nextToken();
			index++;
		}
		Arrays.sort(tokens);
		String ordered = "";
		for (int i=0; i<tokens.length; i++)
			ordered += tokens[i] + " ";
		ordered = ordered.trim();
		//System.err.println(original + " -> " + ordered);
		return ordered;
	}
	
	private String separateGreekLetters(String original, String greek) {
		int index = original.toLowerCase().indexOf(greek);
		String before = original.substring(0,index).trim();
		String after = original.substring(index+greek.length(),original.length()).trim();
		String separated = "";
		if (before.length()>0) {
			StringTokenizer tokens1 = new StringTokenizer(before);
			while (tokens1.hasMoreTokens())
				separated += tokens1.nextToken() + " ";
		}
		separated += original.substring(index,index+greek.length()) + " ";
		if (after.length()>0) {
			StringTokenizer tokens2 = new StringTokenizer(after);
			while (tokens2.hasMoreTokens())
				separated += tokens2.nextToken() + " ";
		}
		return separated.trim();
	}
	
	private String separateNumbers(String original) {
		String separated = "";
		boolean numberFound = false;
		String number = "";
		for (int i=0; i<original.length(); i++) {
			char c = original.charAt(i);
			if (this.isNumeral(c)) {
				number += c;
				numberFound = true;
				separated += " ";
			}
			else {
				if (numberFound) {
					separated += number + " ";
					number = "";
					numberFound = false;
				}
				separated += c;
			}
		}
		if (numberFound)
			separated += number + " ";
		return separated.trim();
	}
	
	public String handleSpecial(String mention){
		ArrayList<String> mentionParts = new ArrayList<String>();
		ArrayList<String> candidateParts = new ArrayList<String>();
		String noPt = mention.trim(); // No parenthesis
		Pattern p = Pattern.compile("(\\(.+?\\)|\\[.+?\\])");
		Matcher m = p.matcher(noPt);
		
		while (m.find()){
			String temp = m.group();
			System.out.println(temp);
			do{
				String newNoPt = noPt;
				newNoPt = noPt.replace(temp, "");
				if (newNoPt == noPt)
					break;
				noPt = newNoPt;
			} while (true);
		}
		
		if (noPt.trim().length() > 0){
			mention = noPt;
		}

		if (mention.indexOf(" ") < 0) {
			mention = mention.replace("-", "");
		}
		
		return mention;
	}

	
	public static void main(String[] args) {
		StringUtil app = new StringUtil();
		//System.err.println(app.orderAlphabetically("basic leucine zipper protein p21snft",true,true,true,true));
		//System.err.println(app.hasFirstUpperCaseLetter("abc"));
		//System.err.println(app.isRomanNumeral("ivi"));
		//System.err.println(app.getStringWithSpace("Recent evidence",0,10));
		
		String text = "NF-kappa B controls expression of inhibitor I kappa B alpha: evidence for an inducible autoregulatory pathway.  The eukaryotic transcription factor nuclear factor-kappa B (NF-kappa B) participates in many parts of the genetic program mediating T lymphocyte activation and growth. Nuclear expression of NF-kappa B occurs after its induced dissociation from its cytoplasmic inhibitor I kappa B alpha. Phorbol ester and tumor necrosis factor-alpha induction of nuclear NF-kappa B is associated with both the degradation of performed I kappa B alpha and the activation of I kappa B alpha gene expression. Transfection studies indicate that the I kappa B alpha gene is specifically induced by the 65-kilodalton transactivating subunit of NF-kappa B. Association of the newly synthesized I kappa B alpha with p65 restores intracellular inhibition of NF-kappa B DNA binding activity and prolongs the survival of this labile inhibitor. Together, these results show that NF-kappa B controls the expression of I kappa B alpha by means of an inducible autoregulatory pathway.";
		//String text = "Reactive oxygen intermediate-dependent NF-kappaB activation by interleukin-1beta requires 5-lipoxygenase or NADPH oxidase activity.";
		//System.err.println(app.getLengthWithoutSpace(text));
		System.err.println(app.getIndexWithSpace(text,29));
		
		/*String title = "CD2 signalling induces phosphorylation of CREB in primary lymphocytes.";
		String text = "Promoter sequences responsive to cyclic AMP (cAMP) are found in a number of cellular genes, and bind transcription factors of the cAMP response element binding protein (CREB)/activating transcription factor-1 (ATF-1) family. We have used a human T-lymphotropic virus type 1 (HTLV-1) model of cAMP response element (CRE) transcription to investigate the influence of lymphocyte activation on transcription from homologous regions in the viral promoter. We previously demonstrated increased HTLV-1 transcription following CD2 but not CD3 receptor cross-linking. We hypothesized that this increased viral transcription was mediated, in part, through the phosphorylation of CREB. Therefore, we investigated CD2 and CD3 receptor-mediated signalling in primary human peripheral blood mononuclear cells (PBMC). CD2, but not CD3, cross-linking increased cAMP detected by competitive enzyme-linked immunosorbent assay (ELISA) approximately fourfold. CD2 cross-linking concurrently increased phosphorylation of CREB detected by immunoblot assay eightfold. Consistent with post-translational regulation, no change in total level of CREB protein was observed. Phosphorylation of CREB occurred through a herbimycin A and Rp-cAMP- sensitive pathway, suggesting phosphorylation required antecedent activation of both protein tyrosine kinases (PTK) and protein kinase A (PKA). Both CD2 and CD3 cross-linking increased binding of nuclear proteins to a radiolabelled CRE oligonucleotide probe in electrophoretic mobility shift assays suggesting that lymphocyte activation enhances binding independently of phosphorylation of CREB at serine 133. These data indicate specific modulation of the CREB/ATF-1 family of transcription factors by the CD2 signalling pathway and suggest CD2 receptor modulation of CRE-mediated transcription following ligand engagement (e.g. cell-to-cell contact).";
		System.out.println(text.substring(42,46));*/
	}
	
}
