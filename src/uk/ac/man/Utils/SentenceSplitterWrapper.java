package uk.ac.man.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class SentenceSplitterWrapper {
	
	protected static SentenceDetectorME sd = null;
	protected static SentenceModel sentenceModel = null;
	
	public SentenceSplitterWrapper(){
		if (sentenceModel == null || sd == null) {
			InputStream modelIn = null;

			try {
				// Loading sentence detection model
				modelIn = new FileInputStream("models/opennlp/en-sent.bin");
				sentenceModel = new SentenceModel(modelIn);
				modelIn.close();
				sd = new SentenceDetectorME(sentenceModel);

			} catch (final IOException ioe) {
				ioe.printStackTrace();
			} finally {
				if (modelIn != null) {
					try {
						modelIn.close();
					} catch (final IOException e) {
					} // oh well!
				}
			}
		}
	}
	
	public String[] getSentences(String text){
		String[] sentences = sd.sentDetect(text);
		List<String> sentencesTuned = new ArrayList<String>();
		
		//Add some post processing to deal with the problem:
		// a sentence followed directly by the beginning of another sentence without any space. 
		for(int i = 0; i < sentences.length; i++){
			String pStr = "\\.([A-Z])";
			Pattern p = Pattern.compile(pStr);
			Matcher m = p.matcher(sentences[i]);
			
			boolean flag = false;
			int preStart = 0;
			
			while(m.find()){
				int periodIdx = m.start();
				
				String newSubSent = sentences[i].substring(preStart, periodIdx+1);
				preStart = periodIdx+1;
					
				sentencesTuned.add(newSubSent);
				
				flag = true;
			}
			
			if(flag && (preStart < sentences[i].length())){
				sentencesTuned.add(sentences[i].substring(preStart));
			}
			
			if(!flag)
				sentencesTuned.add(sentences[i]);
		}
		
		return sentencesTuned.toArray(new String[sentencesTuned.size()]);
	}
	
	
	public static void main(String[] args){
		SentenceSplitterWrapper ssw = new SentenceSplitterWrapper();
		
		String testStr = "To investigate and analyze the variation trends in the pathological composition of thyroid cancer patients treated in Tianjin Cancer Hospital from 1954 to 2009.To retrospectively analyze the incidence and clinical features of different pathological types of thyroid cancers in 4342 patients between different time periods from 1954 to 2009.In the four main pathological types of thyroid cancers, the component ratio of papillary thyroid cancer in every period was 68.1%, 78.3%, 81.3%, 82.1%, 85.8%, respectively, while the morbidity of patients with papillary thyroid carcinoma concurrent with Hashimoto's thyroiditis was increased, so was the proportion of tumors in diameter < or = 2 cm.";
		String[] subSents = ssw.getSentences(testStr);
		
		System.out.println( subSents.length + " sentences deteced. ");
		
		for(String subSent : subSents){
			System.out.println(subSent);
		}
	}
}
