package moara.normalization;

import moara.util.Constant;

public class NormalizationConstant {

	// gene synonym features
	public static String FEATURE_SUFFIX3 = "suffix3";
	public static String FEATURE_PREFIX3 = "prefix3";
	public static String FEATURE_PREFIX4 = "prefix4";
	public static String FEATURE_BIGRAM = "bigram";
	public static String FEATURE_TRIGRAM = "trigram";
	public static String FEATURE_NUMBER = "number";
	public static String FEATURE_GREEK_LETTER = "greek";
	public static String FEATURE_SHAPE = "shape";
	public static String FEATURE_TOTAL_NUMBER = "totalNumber";
	public static String FEATURE_GRAM_SIMILARITY = "gramSim";
	public static String FEATURE_TOKEN = "token";
	public static String FEATURE_COMMON_TOKEN = "common";
	public static String FEATURE_DIFFERENT_TOKEN = "different";
	public static String FEATURE_STRING_SIMILARITY = "stringsim";
	public static String FEATURE_PCT_SIMILARITY = "pctsim";
	public static String FEATURE_TYPE_SIMILARITY = "typesim";
	public static String FEATURE_COSINE_SIMILARITY = "cosine";
	public static String FEATURE_NUM_COMMON_WORDS = "commonwords";
	// features f1 (all features)
	public static String[] arrayFeaturesGroup1 = {FEATURE_TRIGRAM,FEATURE_BIGRAM,
		FEATURE_PREFIX3,FEATURE_SUFFIX3,FEATURE_NUMBER,FEATURE_GREEK_LETTER,
		FEATURE_SHAPE,FEATURE_STRING_SIMILARITY};
	public static String NAME_FEATURES_F1 = "f1";
	// features f2
	public static String[] arrayFeaturesGroup2 = {FEATURE_TRIGRAM,FEATURE_BIGRAM,
		FEATURE_NUMBER,FEATURE_STRING_SIMILARITY};
	public static String NAME_FEATURES_F2 = "f2";
	// features f3
	public static String[] arrayFeaturesGroup3 = {FEATURE_TRIGRAM,FEATURE_BIGRAM,
		FEATURE_PREFIX3,FEATURE_STRING_SIMILARITY,FEATURE_DIFFERENT_TOKEN};
	public static String NAME_FEATURES_F3 = "f3";
	public static String[] arrayFeatures = {NAME_FEATURES_F1,NAME_FEATURES_F2};
	
	// type of tokens of the comparison
	public static String TOKENS_DIFFERENT = "D";
	public static String TOKENS_COMMON = "C";
	
	// symbols for the tokens (categories)
	public static String TOKENS_NUMBER = "1";
	public static String TOKENS_GREEK_LETTER = "alpha";
	public static String TOKENS_ROMAN_NUMBER = "ivxlcdm";
	
	// shape of gene synonyms
	public static String SYMBOL_FORMAT_LOWER_CASE = "a";
	public static String SYMBOL_FORMAT_UPPER_CASE = "A";
	public static String SYMBOL_FORMAT_NUMBER = "1";
	
	// category of configuration
	public static String CATEGORY_TRUE = "T";
	public static String CATEGORY_FALSE = "F";
	
	// parameters of synonym case feature
	public static int TOTAL_CASES = 15000;
	public static double PCT_GRAM_SIMILARITY = 0.8;
	public static double PCT_SOFTFIDF_SIMILARITY = 0.9;
	public static double PCT_SOFTFIDF_CLUSTER = 0.3;
	public static double[] arrayPctTfIdfSimilarity = {0.5,0.6,0.7,0.8,0.9};
	//public static double[] arrayPctTfIdfSimilarity = {0.9,0.8,0.7,0.6,0.5};
	
	// types of selecting feature pairs
	public static String FEATURE_SELECTION_BIGRAM = "bi";
	public static String FEATURE_SELECTION_TRIGRAM = "tri";
	public static String FEATURE_SELECTION_BOTH = "both";
	public static String[] arrayFeatureSelection = {
		FEATURE_SELECTION_BIGRAM,
		FEATURE_SELECTION_TRIGRAM,
		FEATURE_SELECTION_BOTH
	};
	
	// type of string matching
	public static String MATCHING_EXACT = "E";
	public static String MATCHING_APPROXIMATE = "A";
	public static String MATCHING_MACHINE_LEARNING = "M";
	public static int DISTANCE_PERSONALIZED = 5;
	
	// type of disambiguation
	public static int DISAMBIGUATION_NONE = 0;
	public static int DISAMBIGUATION_COSINE = 1;
	public static int DISAMBIGUATION_COSINE_NUMWORDS = 2;
	public static int DISAMBIGUATION_NUMWORDS = 3;
	public static int DISAMBIGUATION_JACCARD = 4;
	public static int DISAMBIGUATION_COSINE_JACCARD = 5;
	public static int DISAMBIGUATION_SOFT_TFIDF = 6;
	public static int[] arrayTypeDisambiguation = {
		DISAMBIGUATION_NONE,
		DISAMBIGUATION_COSINE,
		DISAMBIGUATION_COSINE_NUMWORDS,
		DISAMBIGUATION_NUMWORDS,
		DISAMBIGUATION_SOFT_TFIDF
		//DISAMBIGUATION_JACCARD,
		//DISAMBIGUATION_COSINE_JACCARD
	};
	// disambiguation selection
	public static int DISAMBIGUATION_SINGLE = 4;
	public static int DISAMBIGUATION_MULTIPLE = 5;
	public static int[] arrayDisambiguationSelection = {
		DISAMBIGUATION_SINGLE,
		DISAMBIGUATION_MULTIPLE
	};
	
	// type of data of predictions
	public static String DATA_PREDICTION = "P";
	public static String DATA_GOLD_STANDARD = "G";
	
	// type of prediction
	public static String PREDICTION_UNIQUE = "U";
	public static String PREDICTION_AMBIGUOUS = "A";
	
	// synonym differences (position)
	public static String SYNONYM_DIFFERENCE_START = "start";
	public static String SYNONYM_DIFFERENCE_END = "end";
	public static String SYNONYM_DIFFERENCE_CENTER = "center";
	public static String SYNONYM_DIFFERENCE_ALL = "all";
	
	// gene synonyms variations
	// operations
	public static String VAR_OPERATION_ADD = "A";
	public static String VAR_OPERATION_REMOVE = "R";
	public static String VAR_OPERATION_SUBSTITUTE = "S";
	// positions
	public static String VAR_POSITION_START = "S";
	public static String VAR_POSITION_MIDDLE = "M";
	public static String VAR_POSITION_END = "E";
	
	// threshold biowords
	public static int THRESHOLD_BIOWORDS_ZERO = 0;
	public static int THRESHOLD_BIOWORDS_10 = 10;
	public static int THRESHOLD_BIOWORDS_50 = 50;
	public static int THRESHOLD_BIOWORDS_100 = 100;
	public static int THRESHOLD_BIOWORDS_1000 = 1000;
	public static int THRESHOLD_BIOWORDS_10000 = 10000;
	public static int THRESHOLD_BIOWORDS_ALL = 100000;
	public static int THRESHOLD_BIOWORDS = THRESHOLD_BIOWORDS_10;
	public static int[] thresholds = {
		THRESHOLD_BIOWORDS_ZERO, THRESHOLD_BIOWORDS_10, THRESHOLD_BIOWORDS_50,
		THRESHOLD_BIOWORDS_100, THRESHOLD_BIOWORDS_1000, THRESHOLD_BIOWORDS_10000, 
		THRESHOLD_BIOWORDS_ALL
	};
	
	// default configuration
	public static int DEFAULT_DISAMBIGUATION_TYPE = DISAMBIGUATION_COSINE_NUMWORDS;
	public static int DEFAULT_DISAMBIGUATION_SELECTION = DISAMBIGUATION_SINGLE;
	public static int DEFAULT_STRING_SIMILARITY = Constant.DISTANCE_SMITH_WATERMAN;
	public static String[] DEFAULT_FEATURES = arrayFeaturesGroup2;
	public static String DEFAULT_FEATURE_NAME = NAME_FEATURES_F2;
	public static String DEFAULT_MACHINE_LEARNING = Constant.ML_SVM;
	public static double DEFAULT_PCT_SIMILARITY = 0.9;
	public static String DEFAULT_PAIR_SELECTION = FEATURE_SELECTION_BOTH;
	
	// editing operations
	public static String EDITING_NONE = "none";
	public static String EDITING_LOWERCASE = "lowercase";
	public static String EDITING_LOWERCASE_PARTS = "parts";
	public static String EDITING_LOWERCASE_PARTS_ORDERING = "ordering";
	public static String EDITING_LOWERCASE_PARTS_ORDERING_STOPWORDS = "stopwords";
	public static String EDITING_LOWERCASE_PARTS_ORDERING_STOPWORDS_BIOWORDS = "biowords";
	public static String EDITING_LOWERCASE_PARTS_ORDERING_STOPWORDS_OTHER = "other";
	
	//public static double THRESHOLD_PERSONALIZED = 3.0;
	public static int[] arrayStringSimilarity = {Constant.DISTANCE_LEVENSTEIN,
		Constant.DISTANCE_SMITH_WATERMAN,Constant.DISTANCE_MONGE_ELKAN,
		Constant.DISTANCE_JARO_WINKLER,Constant.DISTANCE_SOFT_TFIDF};
	
}
