package moara.util;

public class Constant {

	// MySQL databases
	public static String MYSQL_MOARA = "moara";
	public static String MYSQL_TEXT_MINING = "text_mining";
	
	// corpora
	public static String CORPUS_TRAINING = "T";
	public static String CORPUS_DEVELOPMENT = "D";
	public static String CORPUS_TEST = "S";
	
	// type of data
	public static String DATA_PREDICTION = "P";
	public static String DATA_GOLD_STANDARD = "G";
	
	// organisms
	public static String ORGANISM_YEAST = "4932";
	public static String ORGANISM_FLY = "7227";
	public static String ORGANISM_MOUSE = "10090";
	public static String ORGANISM_HUMAN = "9606";
	public static String ORGANISM_ALL = "all";
	
	// curator of PubMed
	public static String CURATOR_MGI = "MGI";
	public static String CURATOR_GXD = "GXD";
	public static String CURATOR_BIOCREATIVE = "BC";
	
	// status of result
	public static String STATUS_FALSE_NEGATIVE = "FN";
	public static String STATUS_FALSE_POSITIVE = "FP";
	public static String STATUS_TRUE_POSITIVE = "TP";
	
	// classification
	public static String CLASS_POSITIVE = "P";
	public static String CLASS_NEGATIVE = "N";
	
	// machine learning algorithms
	public static String ML_CBR = "cbr";
	public static String ML_SVM = "svm";
	public static String ML_RANDOM_FOREST = "rf";
	public static String ML_LOGISTIC = "log";
	public static String ML_CATEGORY = "category";
	
	// types of approximated matching (predefined costs)
	public static int MATCHING_GENE_NAME  = 1;
	public static double THRESHOLD_GENE_NAME = 3.0;
	
	// terms (vector space model)
	public static int MIN_FREQUENCY_TERMS  = 2;
	public static int MIN_NUMDOC_TERMS  = 2;
	
	// cross validation datasets
	public static int ALL_SET = 0;
	public static int DATA_SET_1 = 1;
	public static int DATA_SET_2 = 2;
	public static int DATA_SET_3 = 3;
	public static int DATA_SET_4 = 4;
	public static int DATA_SET_5 = 5;
	public static int DATA_SET_6 = 6;
	public static int DATA_SET_7 = 7;
	public static int DATA_SET_8 = 8;
	public static int DATA_SET_9 = 9;
	public static int DATA_SET_10 = 10;
	
	// window of text
	// window
	public static String MINUS_FEATURE = "Minus";
	public static String PLUS_FEATURE = "Plus";
	
	// binary category
	public static String CATEGORY_TRUE = "T";
	public static String CATEGORY_FALSE = "F";
	
	// tags (B/I/O)
	public static String TAG_BIO = "BIO";
	public static String TAG_BIO_B = "B";
	public static String TAG_BIO_I = "I";
	public static String TAG_BIO_O = "O";
	
	// tags (B/I/E/W/O)
	public static String TAG_BIEWO = "BIEWO";
	public static String TAG_BIEWO_B = "B";
	public static String TAG_BIEWO_I = "I";
	public static String TAG_BIEWO_E = "E";
	public static String TAG_BIEWO_W = "W";
	public static String TAG_BIEWO_O = "O";
	
	// method of similarity
	public static int DISTANCE_LEVENSTEIN = 0;
	public static int DISTANCE_SMITH_WATERMAN = 1;
	public static int DISTANCE_MONGE_ELKAN = 2;
	public static int DISTANCE_JARO_WINKLER = 3;
	public static int DISTANCE_SOFT_TFIDF = 4;
	public static String NAME_DISTANCE_LEVENSTEIN = "lev";
	public static String NAME_DISTANCE_SMITH_WATERMAN = "sw";
	public static String NAME_DISTANCE_MONGE_ELKAN = "me";
	public static String NAME_DISTANCE_JARO_WINKLER = "jw";
	public static String NAME_DISTANCE_SOFT_TFIDF = "soft";
	
}
