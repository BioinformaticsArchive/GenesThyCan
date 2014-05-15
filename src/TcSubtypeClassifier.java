import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import martin.common.ArgParser;

import org.apache.commons.lang.StringUtils;

import uk.ac.man.Utils.SentenceSplitterWrapper;
import uk.ac.man.Utils.ShortFormsMiner;
import uk.ac.man.Utils.ShortFormsMiner.PairLSF;
import uk.ac.man.entitytagger.EntityTagger;
import uk.ac.man.entitytagger.Mention;
import uk.ac.man.textpipe.Annotator;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * Wrapper for the subtype classification tool for thyroid cancer literatue
 * @author Chengkun Wu
 */

public class TcSubtypeClassifier extends Annotator {
	public static ShortFormsMiner sfMiner = null;
	public static boolean EXPAND_ABBR = false;
	public static SentenceSplitterWrapper ssw = new SentenceSplitterWrapper();
	
	//SUBTYPE related
	public static int num_st = 8;
	public static String[] subtypeNames = {"TC","PTC","ATC","FTC","MTC","PDTC","HCTC","DTC"};

	//LINNAEUS related
	Logger l = Logger.getAnonymousLogger();
	uk.ac.man.entitytagger.matching.Matcher linnaeusMatcher = null;
	
	public class OneSentMatch{
		public int[] stCount = new int[num_st];
		public boolean anFlag = false;
		public boolean maFlag = false;
		
		
		//Get the internal score of a match result
		public double[] getInternalScores(){
			double[] scores = new double[num_st];
			
			/*
			 * Internal score scale (ISS) rules: 
			 * 1. If ST appears with AN & MA, then ISS = 1;
			 * 2. If ST appears with only AN or MA, then ISS = 0.5;
			 * 3. If ST appears alone without AN or MA, then ISS = 0.25;  
			 */
			double iss = 0.25;
			
			if(anFlag && maFlag){
				iss = 1;
			}else{
				if(anFlag || maFlag)
					iss = 0.5;
				else
					iss = 0.25;
			}
			
			for(int i = 0; i < num_st; i++){
				scores[i] = stCount[i] * iss;
			}
			
			return scores;
		}
		
		@Override
		public String toString(){

			if(anFlag && maFlag){
				String name = "";
				
				for(int i = 1; i < 8 ; i++){
					if(stCount[i] != 0){
						name += subtypeNames[i] + ",";
					}
				}
				
				if(name.equals(""))
					name = "TC";
				
				return name;
				
			}else{
				return "";
			}
		}
		
	}
	
	public class SubtypeInstance{
		public double[] subtypeScores = null;
		public String pmid;
		
		public SubtypeInstance(String id, double[] scores){
			pmid = id;
			
			//System.arraycopy(scores, 0, subtypeScores, 0, num_st);
			subtypeScores = scores.clone();
		}
		
		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			
			for(int i = 0; i < subtypeScores.length; i++){
				sb.append(subtypeScores[i]).append(",");
			}
			
			return sb.toString();
		}
	}

	
	@Override
	public void init(Map<String, String> data) {
		if (sfMiner != null)
			return;
		
		//Init Abbreviation Miner
		try {
			sfMiner = new ShortFormsMiner();
			sfMiner.RETAIN_SHORT = false; //Only keep the long form instead of the short form;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Init LINAEUS
		String[] args_fake = {"--variantMatcher", "tc_parts_dict_linnaeus.list", "--ignoreCase", "true"};
		ArgParser ap = new ArgParser(args_fake);
		linnaeusMatcher = EntityTagger.getMatcher(ap, l);
		
	}

	@Override
	public String[] getOutputFields() {
		//return new String[]{"TC","PTC","ATC","FTC","MTC","PDTC","HCTC", "rb_label"};
		return new String[]{"TC","PTC","ATC","FTC","MTC","PDTC","HCTC", "DTC","rb_label"};
	}
	
	public double[] subtypeScores(Map<String, String> data){
		String text = data.get("doc_text");
		String title = data.get("doc_title");
		String abs = data.get("doc_abs");
		String doc_id = data.get("doc_id");
		//System.out.println(text);
		
		int count = 0;

		List<PairLSF> psfList = null;

		if (this.EXPAND_ABBR) {
			psfList = sfMiner.processText(title + "\n" + abs);
			title = abbrvSub(title, psfList);
		}

		int tcCount = 0;
			
		OneSentMatch titleMatch = linnaeusMatch(linnaeusMatcher, title);
		if (titleMatch.anFlag && titleMatch.maFlag)
			tcCount = 1;
			
		/*
			 * Score calculation using scale-factor for different sections of the abstract
			 * Rules:
			 * 1. Title * 4
			 * 2. 1st * 2
			 * 3. 2nd * 1
			 * 4. -1st * 2 (last one)
			 * 5. -2nd * 1 (last but one)
			 * 6. Others * 0.5
			 * 7. If no TC is mentioned, then 
		*/
			
		double[] stScores = titleMatch.getInternalScores();
			
		for (int i = 0; i < num_st; i++)
			stScores[i] = stScores[i] * 4; // Title

		String[] abstract_sentences = ssw.getSentences(abs);
		int len = abstract_sentences.length;

		for (int i = 0; i < len; i++) {
			double scaleFactor = 0.5;

			if ((i == 0) || (i == (len - 1))) {
				scaleFactor = 2;
			}

			if ((i == 1) || (i == (len - 2))) {
				scaleFactor = 1;
			}

			String sent_str = abstract_sentences[i];

			if (this.EXPAND_ABBR) {
				if (psfList != null)
					abbrvSub(abstract_sentences[i], psfList);
			}

			OneSentMatch sentMatchResult = linnaeusMatch(linnaeusMatcher,
					sent_str);

			if (sentMatchResult.anFlag && sentMatchResult.maFlag) {
				tcCount++;
			}

			double[] sentStScores = sentMatchResult.getInternalScores();

			for (int j = 0; j < num_st; j++) {
				stScores[j] += sentStScores[j] * scaleFactor;
			}
		}
			
		if (tcCount == 0) {
			for (int i = 0; i < num_st; i++)
				stScores[i] = 0; // Title
		} else
			stScores[0] = tcCount; // The score for TC (without subtype) is
									// actually the count for TC co-occurrences.

		// for PTC and FTC, DTC scores are added to them in the end.
	//	stScores[1] += stScores[7];
	//	stScores[3] += stScores[7];
			
		/*
		 * If you want to use view the tagging results, then use the following
		 * code
		 * 
		 * String title_result = linnaeusMatch(linnaeusMatcher,
		 * text_title).toString();
		 * 
		 * System.out.println("[TITLE]: " + title_result + ": " + text_title);
		 * 
		 * String[] abstract_sentences = ssw.getSentences(text_abstract);
		 * 
		 * System.out.println("[ABSTRACT]: ");
		 * 
		 * for(String sent : abstract_sentences){ String abstract_sent_result =
		 * linnaeusMatch(linnaeusMatcher, sent).toString() + ":";
		 * System.out.println(abstract_sent_result + sent); }
		 */

		/*
		 * [DEBUG] for(int i = 0; i < num_st; i++){
		 * System.out.println(subtypeNames[i] + " : " + stScores[i]); }
		 */
			
		return stScores;
	}
	
	@Override
	public List<Map<String, String>> process(Map<String, String> data) {
		//Classify the document and generate the label
		double[] stScores = subtypeScores(data);
		List<Map<String,String>> res = new ArrayList<Map<String,String>>();
		Map<String,String> map = new HashMap<String,String>();
		
		//{"doc_id","tc","ptc", "atc", "ftc", "mtc", "pdtc", "hctc"};
		for (int i = 0; i < 7; i++){
			map.put(subtypeNames[i], "" + stScores[i]);
		}
		
		//Debug 
		map.put("DTC", "" + stScores[7]);		

		map.put("rb_label", classifyRB(stScores));
		
		res.add(map);
		
		return res;		
	}

	@Override
	public void destroy() {
	}
	
	
	// Using the global SHORT/LONG form pairs
	public String abbrvSub(String text, List<PairLSF> psfList) {
		// Long form retained, as indicated by the flag "sfMiner.RETAIN_SHORT"

		String newText = sfMiner.subAbbrv(text, psfList);

		return newText;
	}
	
	public OneSentMatch linnaeusMatch(uk.ac.man.entitytagger.matching.Matcher linnaeusMatcher, String text){
		OneSentMatch osm = new OneSentMatch();
		
		List<Mention> exactMatches = linnaeusMatcher.match(text);
		
		/*
		if(text.contains("thyroid")){
			osm.anFlag = true;
		}
		*/
		
		//LINNAEUS does not deal with Unicode string properly
		if (text.contains("Hürthle")){
			osm.stCount[6]++; 
		}
		
		for(Mention m : exactMatches) {
			String mention = m.getText();
			String id = m.getMostProbableID();

			String[] idTypeNo = id.split(":");
			
			if(idTypeNo.length != 2){
				System.err.println("Something wrong in the dictionary: " + id + "\t" + mention);
				return null;
			}
			
			String idType = idTypeNo[0];
			int idNo = Integer.parseInt(idTypeNo[1]);
			
			if(idType.equals("an")){
				osm.anFlag = true;
			} else{
				if(idType.equals("ma")){
					osm.maFlag = true;
				}else{
					if(idType.equals("st")){
						osm.stCount[idNo]++; 
					}else{
						if(idType.equals("ab")){
							osm.stCount[idNo]++;
							osm.anFlag = true;
							osm.maFlag = true;
						}
					}
				}
			}
		}
		
		return osm;
	}
	
	public double stSum(double[] stScores){
		double sum = 0.0;
		
		for (int i = 1; i <= 7; i++){
			sum += stScores[i];
		}
		
		return sum;
	}
	

	public String classifyRB(double[] stScores){
		//Need to create the instance first
		double dSt_sum = stSum(stScores);
		double dTcAndSt = dSt_sum + stScores[0];
		double[] stScoresPer = stScores.clone();
		double maxStScore = -1;
		double maxStIndex = -1;

		for (int i = 1; i <=7; i++) {
			if (dSt_sum > 0) {
				stScoresPer[i] = stScores[i] / dSt_sum;
				
				if (maxStScore < stScores[i]){
					maxStScore = stScores[i];
					maxStIndex = i;
				}
			} else
				stScores[i] = 0;
		}
		
		ArrayList<String> labels = new ArrayList<String>();
		String pred_label = "";
		
		// No very informative
		if (dTcAndSt < 4.5){
			if (stScores[0] < 4.5)
				pred_label = "NON";
			else
				pred_label = "TC";
			
			return pred_label;
		}else{
			
			if (dTcAndSt > 2*dSt_sum)
				return "TC";
			
			if (stScores[1] > 3 && stScoresPer[1] > 0.15){
				labels.add("PTC");
			}
			
			if (stScores[2] > 2.5 && stScoresPer[2] > 0.15){
				labels.add("ATC");
			} 
			
			if (stScores[3] > 3 && stScoresPer[3] > 0.15){
				labels.add("FTC");
			}
			
			if (stScores[4] > 2.5 && stScoresPer[4] > 0.15){
				labels.add("MTC");
			}
			
			if (stScores[5] > 3 && stScoresPer[5] > 0.15){
				labels.add("PDTC");
			}
			
			if (stScores[6] > 3 && stScoresPer[6] > 0.15){
				labels.add("HCTC");
			}
			
			//DTC
			if (stScores[7] > 3 ){
				
				boolean addPTC = true;
				boolean addFTC = true;
				
				if (stScores[3] - stScores[1] > 2)
					addPTC = false;
				
				if (stScores[1] - stScores[3] > 2)
					addFTC = false;
				
				if(addPTC && !labels.contains("PTC"))
					labels.add("PTC");
				
				if(addFTC && !labels.contains("FTC"))
					labels.add("FTC");
			}
			
			if (labels.size() ==0)
				labels.add("NON");
		}
		
		pred_label = StringUtils.join(labels, "|");
		
		return pred_label;
	}
}




