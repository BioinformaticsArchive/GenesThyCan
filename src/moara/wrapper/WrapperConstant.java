package moara.wrapper;

import moara.util.EnvironmentVariable;

public class WrapperConstant {

	// Abner constants
	public static int ABNER_BIOCREATIVE = 1;
	public static int ABNER_NLPBA = 2;
	public static int ABNER_BC2 = 3;
	public static int ABNER_BC2_YEAST = 4;
	public static int ABNER_BC2_MOUSE = 5;
	public static int ABNER_BC2_FLY = 6;
	public static int ABNER_BC2_ALL = 7;
	
	// Abner model
	public static String MODEL_ABNER_BC2 = EnvironmentVariable.getMoaraHome()+"wrappers/abner/abner_bc2.model";
	public static String MODEL_ABNER_BC2_YEAST = EnvironmentVariable.getMoaraHome()+"wrappers/abner/abner_bc2_bc1yeast.model";
	public static String MODEL_ABNER_BC2_MOUSE = EnvironmentVariable.getMoaraHome()+"wrappers/abner/abner_bc2_bc1mouse.model";
	public static String MODEL_ABNER_BC2_FLY = EnvironmentVariable.getMoaraHome()+"wrappers/abner/abner_bc2_bc1fly.model";
	public static String MODEL_ABNER_BC2_ALL = EnvironmentVariable.getMoaraHome()+"wrappers/abner/abner_bc2_bc1yeastmousefly.model";
	
	// BANNER model
	public static String MODEL_BANNER = EnvironmentVariable.getMoaraHome()+"wrappers/banner/gene_model_v02.bin";
	public static String PROPERTIES_BANNER = EnvironmentVariable.getMoaraHome()+"wrappers/banner/banner.properties";
	
	// LingPipe POS model
	public static String MODEL_LINGPIPE_POS_MEDPOST = EnvironmentVariable.getMoaraHome()+"wrappers/lingpipe/pos-en-bio-medpost.HiddenMarkovModel";
	public static String MODEL_LINGPIPE_POS_GENIA = EnvironmentVariable.getMoaraHome()+"wrappers/lingpipe/pos-en-bio-genia.HiddenMarkovModel";
	
	// Stanford model
	public static String MODEL_STANFORD_PARSER = EnvironmentVariable.getMoaraHome()+"wrappers/stanford/englishPCFG.ser.gz";
	
}
