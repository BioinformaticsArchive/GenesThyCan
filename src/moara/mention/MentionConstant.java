package moara.mention;

public class MentionConstant {

	// type of case
	public static String CASE_KNOWN = "K";
	public static String CASE_UNKNOWN = "U";
	public static String CASE_NO_CASE = "N";
	public static String CASE_POST = "P";
	public static String CASE_ABBREVIATION = "A";
	public static String CASE_POST_SAVE = "S";
	
	// type of known case
	public static String KNOWN_NORMAL = "N";
	public static String KNOWN_FAMILY = "F";
	
	// type of word in case
	public static String WORD_CASE_COMPLETE = "C";
	public static String WORD_CASE_PARTIAL = "P";
	
	// type of reading
	public static String READING_FORWARD = "F";
	public static String READING_BACKWARD = "B";
	
	// operation in gene mention
	public static int OPERATION_MENTION_INSERT = 1;
	public static int OPERATION_MENTION_DELETE = 2;
	
	// type of mention
	public static String MENTION_COMPLETE = "C";
	public static String MENTION_ALTERNATIVE = "A";
	
	// type of category
	public static String CATEGORY_TRUE_FALSE = "TF";
	public static String CATEGORY_BIO = "BIO";
	public static String CATEGORY_BIEWO = "BIEWO";
	
	// try number searching for cases
	public static String TRY_ELSE = "else";
	public static String TRY_EXACT = "exact";
	public static String TRY_SUFFIX = "suffix";
	public static String TRY_ALT_B1 = "alt b1";
	public static String TRY_ALT_B2 = "alt b2";
	public static String TRY_ALT_B1_B2 = "alt b1 b2";
	// levels of feature
	public static String TRY_W_CB1_WB1 = "w cb1 wb1";
	public static String TRY_W_CB1 = "w cb1";
	public static String TRY_W = "w";
	public static String TRY_ODD = "odd";
	
	// features
	public static String FEATURE_ORIGINAL_WORD = "originalWord";
	public static String FEATURE_WORD = "word";
	public static String FEATURE_WORD_BEFORE_1 = "wordBefore1";
	public static String FEATURE_WORD_BEFORE_2 = "wordBefore2";
	public static String FEATURE_WORD_AFTER_1 = "wordAfter1";
	public static String FEATURE_WORD_AFTER_2 = "wordAfter2";
	public static String FEATURE_CATEGORY_BEFORE_1 = "categoryBefore1";
	public static String FEATURE_CATEGORY_BEFORE_2 = "categoryBefore2";
	public static String FEATURE_POSTAG = "posTag";
	public static String FEATURE_POSTAG_BEFORE_1 = "posTagBefore1";
	public static String FEATURE_POSTAG_BEFORE_2 = "posTagBefore2";
	public static String FEATURE_POSTAG_AFTER_1 = "posTagAfter1";
	public static String FEATURE_POSTAG_AFTER_2 = "posTagAfter2";
	public static String FEATURE_FORMAT = "format";
	public static String FEATURE_FORMAT_BEFORE_1 = "formatBefore1";
	public static String FEATURE_FORMAT_BEFORE_2 = "formatBefore2";
	public static String FEATURE_FORMAT_AFTER_1 = "formatAfter1";
	public static String FEATURE_FORMAT_AFTER_2 = "formatAfter2";
	public static String FEATURE_SUFFIX3 = "suffix3";
	public static String FEATURE_SUFFIX4 = "suffix4";
	public static String FEATURE_PREFIX3 = "prefix3";
	public static String FEATURE_PREFIX4 = "prefix4";
	
	public static String[] KNOWN_FEATURES = {
			MentionConstant.FEATURE_ORIGINAL_WORD,
			MentionConstant.FEATURE_WORD,
			MentionConstant.FEATURE_CATEGORY_BEFORE_1};
	public static String[] UNKNOWN_FEATURES = {
    		MentionConstant.FEATURE_ORIGINAL_WORD,
    		MentionConstant.FEATURE_FORMAT,
    		MentionConstant.FEATURE_CATEGORY_BEFORE_1};
	
	public static String[] KNOWN_FEATURES_2 = {
		MentionConstant.FEATURE_ORIGINAL_WORD,
		MentionConstant.FEATURE_WORD,
		MentionConstant.FEATURE_CATEGORY_BEFORE_1,
		MentionConstant.FEATURE_WORD_BEFORE_1};
	public static String[] UNKNOWN_FEATURES_2 = {
		MentionConstant.FEATURE_ORIGINAL_WORD,
		MentionConstant.FEATURE_FORMAT,
		MentionConstant.FEATURE_CATEGORY_BEFORE_1,
		MentionConstant.FEATURE_FORMAT_BEFORE_1};
	
	// unknown case format symbols
	public static String SYMBOL_FORMAT_SUFFIX = "$";
	public static String SYMBOL_FORMAT_PREFIX = "$";
	public static String SYMBOL_FORMAT_STOPWORDS = "p";
	public static String SYMBOL_FORMAT_PUNCTUATION = ".";
	public static String SYMBOL_FORMAT_GREEK_LETTER = "g";
	public static String SYMBOL_FORMAT_CHEMICAL = "q";
	public static String SYMBOL_FORMAT_NUCLEIC_ACID = "d";
	public static String SYMBOL_FORMAT_AMINO_ACID = "m";
	public static String SYMBOL_FORMAT_UPPER_CASE = "A";
	public static String SYMBOL_FORMAT_LOWER_CASE = "a";
	public static String SYMBOL_FORMAT_NUMBER = "1";
	public static String SYMBOL_FORMAT_SEPARATION = "@";
	
	// suffix/prefix configuration
	public static int MAX_SUFFIX_LENGTH = 4;
	public static int MAX_PREFIX_LENGTH = 3;
	public static int MIN_TOKEN_LENGTH_SUFFIX = 4;
	public static int MIN_TOKEN_LENGTH_PREFIX = 4;
	public static int SUFFIX_LENGTH = 3;
	public static int[] array_suffix_length = {2,3,4};
	public static int[] array_prefix_length = {2,3};
	
	// cross validation
	public static int DATA_SET_YEAST = 11;
	public static int DATA_SET_MOUSE = 12;
	public static int DATA_SET_FLY = 13;
	public static int DATA_SET_HUMAN = 14;
	
	// Abner training data and tags
	public static int ABNER_BC1GM_DEFAULT = 1;
	public static int ABNER_BC2GM = 2;
	public static int ABNER_BC2GM_BC2GN_YEAST = 3;
	public static int ABNER_BC2GM_BC2GN_MOUSE = 4;
	public static int ABNER_BC2GM_BC2GN_FLY = 5;
	public static String ABNER_BIO_B = "B-PROTEIN";
	public static String ABNER_BIO_I = "I-PROTEIN";
	public static String ABNER_BIO_O = "O";
	
	// mention (tagger) system
	public static String TAGGER_BANNER = "banner";
	public static String TAGGER_ABNER = "abner";
	public static String TAGGER_CBR = "CBR";
	
	// training tagger
	public static String TABLE_USER = "user";
	public static String CORPUS_USER = "U";
	
	// models CBR-Tagger
	public static String MODEL_BC2 = "";
	public static String MODEL_BC2Y = "yeast";
	public static String MODEL_BC2M = "mouse";
	public static String MODEL_BC2F = "fly";
	public static String MODEL_BC2YMF = "all";
	public static String MODEL_USER = "user";
	
}
