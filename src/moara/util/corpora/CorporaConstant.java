package moara.util.corpora;

public class CorporaConstant {

	// chunks tags
	public static String CHUNK_NOUN_PHRASE = "NP";
	public static String CHUNK_VERB_PHRASE = "VP";
	public static String CHUNK_SBAR_PHRASE = "SBAR";
	public static String CHUNK_PP_PHRASE = "PP";
	public static String CHUNK_NONE = "O";
	
	// Penn Treebank part-of-speech tags
	// http://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html
	public static String POS_TAG_CC = "CC";  	// Coordinating conjunction
	public static String POS_TAG_CD = "CD";		// Cardinal number
	public static String POS_TAG_DT = "DT";		// Determiner
	public static String POS_TAG_EX = "EX";		// Existential there
	public static String POS_TAG_FW = "FW";		// Foreign word
	public static String POS_TAG_IN = "IN";		// Preposition or subordinating conjunction
	public static String POS_TAG_JJ = "JJ";		// Adjective
	public static String POS_TAG_JJR = "JJR";	// Adjective, comparative
	public static String POS_TAG_JJS = "JJS";	// Adjective, superlative
	public static String POS_TAG_LS = "LS";		// List item marker
	public static String POS_TAG_MD = "MD";		// Modal
	public static String POS_TAG_NN = "NN";		// Noun, singular or mass
	public static String POS_TAG_NNS = "NNS";	// Noun, plural
	public static String POS_TAG_NNP = "NNP";	// Proper noun, singular
	public static String POS_TAG_NNPS = "NNPS";	// Proper noun, plural
	public static String POS_TAG_PDT = "PDT";	// Predeterminer
	public static String POS_TAG_POS = "POS";	// Possessive ending
	public static String POS_TAG_PRP = "PRP";	// Personal pronoun
	public static String POS_TAG_PRP$ = "PRP$";	// Possessive pronoun
	public static String POS_TAG_RB = "RB";		// Adverb
	public static String POS_TAG_RBR = "RBR";	// Adverb, comparative
	public static String POS_TAG_RBS = "RBS";	// Adverb, superlative
	public static String POS_TAG_RP = "RP";		// Particle
	public static String POS_TAG_SYM = "SYM";	// Symbol
	public static String POS_TAG_TO = "TO";		// to
	public static String POS_TAG_UH = "UH";		// Interjection (no group)
	public static String POS_TAG_VB = "VB";		// Verb, base form
	public static String POS_TAG_VBD = "VBD";	// Verb, past tense
	public static String POS_TAG_VBG = "VBG";	// Verb, gerund or present participle
	public static String POS_TAG_VBN = "VBN";	// Verb, past participle
	public static String POS_TAG_VBP = "VBP";	// Verb, non-3rd person singular present
	public static String POS_TAG_VBZ = "VBZ";	// Verb, 3rd person singular present
	public static String POS_TAG_WDT = "WDT";	// Wh-determiner
	public static String POS_TAG_WP = "WP";		// Wh-pronoun
	public static String POS_TAG_WP$ = "WP$";	// Possessive wh-pronoun
	public static String POS_TAG_WRB = "WRB";	// Wh-adverb 
	
	public static String POS_TAG_$ = "$";				// $
	public static String POS_TAG_OPEN_PAR = "(";		// (
	public static String POS_TAG_CLOSE_PAR = ")";		// )
	public static String POS_TAG_COMMA = ",";			// ,
	public static String POS_TAG_COLON = ":";			// :
	public static String POS_TAG_PERIOD = ".";			// .
	public static String POS_TAG_HYPHEN = "HYPH";		// -
	
	public static String GROUP_ADJECTIVES = "adjectives";			// adjectives 
	public static String GROUP_ADVERBS = "adverbs";					// adverbs
	public static String GROUP_CONJUNCTIONS = "conjunctions";		// conjunctions
	public static String GROUP_DETERMINERS = "determiners";			// determiners
	public static String GROUP_MODALS = "modals";					// modals
	public static String GROUP_NOUNS = "nouns";						// nouns
	public static String GROUP_PREPOSITIONS = "prepositions";		// prepositions
	public static String GROUP_PRONOUNS = "pronouns";				// pronouns
	public static String GROUP_PUNCTUATIONS = "punctuations";		// punctuations
	public static String GROUP_VERBS = "verbs";						// verbs
	
	public static String[] posTagGroups = {
		GROUP_ADJECTIVES,
		GROUP_ADVERBS,
		GROUP_CONJUNCTIONS,
		GROUP_DETERMINERS,
		GROUP_MODALS,
		GROUP_NOUNS,
		GROUP_PREPOSITIONS,
		GROUP_PRONOUNS,
		GROUP_PUNCTUATIONS,
		GROUP_VERBS,
	};
	
	public static String[] adjectives = {
		POS_TAG_JJ,
		POS_TAG_JJR,
		POS_TAG_JJS
	};
	public static String[] adverbs = {
		POS_TAG_RB,
		POS_TAG_RBR,
		POS_TAG_RBS,
		POS_TAG_RP
	};
	public static String[] conjunctions = {
		POS_TAG_CC,
		POS_TAG_WRB
	};
	public static String[] determiners = {
		POS_TAG_DT,
		POS_TAG_PDT
	};
	public static String[] modals = {
		POS_TAG_MD
	};
	public static String[] nouns = {
		POS_TAG_NN,
		POS_TAG_NNS,
		POS_TAG_NNP,
		POS_TAG_NNPS,
		POS_TAG_CD,
		POS_TAG_FW
	};
	public static String[] prepositions = {
		POS_TAG_IN,
		POS_TAG_TO
	};
	public static String[] pronouns = {
		POS_TAG_PRP,
		POS_TAG_PRP$,
		POS_TAG_EX,
		POS_TAG_POS,
		POS_TAG_WDT,
		POS_TAG_WP,
		POS_TAG_WP$
	};
	public static String[] punctuations = {
		POS_TAG_$,
		POS_TAG_OPEN_PAR,
		POS_TAG_CLOSE_PAR,
		POS_TAG_COMMA,
		POS_TAG_COLON,
		POS_TAG_PERIOD,
		POS_TAG_LS,
		POS_TAG_SYM,
		POS_TAG_UH,
		POS_TAG_HYPHEN
	};
	public static String[] verbs = {
		POS_TAG_VB,
		POS_TAG_VBD,
		POS_TAG_VBG,
		POS_TAG_VBN,
		POS_TAG_VBP,
		POS_TAG_VBZ
	};
	
	/*public enum POSTags {
		POS_TAG_CC,POS_TAG_CD,POS_TAG_DT,POS_TAG_EX,
		POS_TAG_FW,POS_TAG_IN,POS_TAG_JJ,POS_TAG_JJR,
		POS_TAG_JJS,POS_TAG_LS,POS_TAG_MD,POS_TAG_NN,
		POS_TAG_NNS,POS_TAG_NNP,POS_TAG_NNPS,POS_TAG_PDT,
		POS_TAG_POS,POS_TAG_PRP,POS_TAG_PRP$,POS_TAG_RB,
		POS_TAG_RBR,POS_TAG_RBS,POS_TAG_RP,POS_TAG_SYM,
		POS_TAG_TO,POS_TAG_UH,POS_TAG_VB,POS_TAG_VBD,
		POS_TAG_VBG,POS_TAG_VBN,POS_TAG_VBP,POS_TAG_VBZ,
		POS_TAG_WDT,POS_TAG_WP,POS_TAG_WP$,POS_TAG_WRB 
	}*/
	
	// Tags description:
	// http://bulba.sdsu.edu/jeanette/thesis/PennTags.html
	
	// Penn Treebank clause level
	public static String PARSER_TAG_S = "S";			
	public static String PARSER_TAG_SBAR = "SBAR";		
	public static String PARSER_TAG_SBARQ = "SBARQ";	 
	public static String PARSER_TAG_SINV = "SINV";		 
	public static String PARSER_TAG_SQ = "SQ";			 
	
	public static String[] ClauseTags = {
		PARSER_TAG_S, PARSER_TAG_SBAR, PARSER_TAG_SBARQ, PARSER_TAG_SINV, PARSER_TAG_SQ
	};
	
	// Penn Treebank phrase level
	public static String PARSER_TAG_ADJP = "ADJP";	
	public static String PARSER_TAG_ADVP = "ADVP";			 
	public static String PARSER_TAG_CONJP = "CONJP";		 
	public static String PARSER_TAG_FRAG = "FRAG";
	public static String PARSER_TAG_INTJ = "INTJ";
	public static String PARSER_TAG_LST = "LST";			 
	public static String PARSER_TAG_NAC = "NAC";
	public static String PARSER_TAG_NP = "NP";	
	public static String PARSER_TAG_NX = "NX";				
	public static String PARSER_TAG_PP = "PP";		
	public static String PARSER_TAG_PRN = "PRN";	
	public static String PARSER_TAG_PRT = "PRT";			
	public static String PARSER_TAG_QP = "QP";				
	public static String PARSER_TAG_RRC = "RRC";			
	public static String PARSER_TAG_UCP = "UCP";			
	public static String PARSER_TAG_VP = "VP";				
	public static String PARSER_TAG_WHADJP = "WHADJP";		
	public static String PARSER_TAG_WHAVP = "WHAVP";		
	public static String PARSER_TAG_WHANP = "WHANP";		
	public static String PARSER_TAG_X = "X";				
	
	public static String[] PhraseTags  = {
		PARSER_TAG_ADJP, PARSER_TAG_ADVP, PARSER_TAG_CONJP, PARSER_TAG_FRAG, 
		PARSER_TAG_INTJ, PARSER_TAG_LST, PARSER_TAG_NAC, PARSER_TAG_NP, 
		PARSER_TAG_NX, PARSER_TAG_PP, PARSER_TAG_PRN, PARSER_TAG_PRT, 
		PARSER_TAG_QP, PARSER_TAG_RRC, PARSER_TAG_UCP, PARSER_TAG_VP, 
		PARSER_TAG_WHADJP, PARSER_TAG_WHAVP, PARSER_TAG_WHANP, PARSER_TAG_X
	};
	
}
