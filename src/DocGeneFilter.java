
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import banner.BannerProperties;
import banner.Sentence;
import banner.processing.PostProcessor;
import banner.tagging.CRFTagger;
import banner.tagging.Mention;
import banner.tokenization.Tokenizer;

public class DocGeneFilter {
	
	Tokenizer tokenizer = null;
    CRFTagger tagger = null;
    PostProcessor postProcessor = null;
    
    public static GreekConverter gc = new GreekConverter();
    public static Logger logger = Logger.getLogger(DocGeneFilter.class.getName());
    
	List<Mention> geneMentions;
	
	public DocGeneFilter(){
		logger.info("Initialising BANNER Gene tagger...");
		if (loadGeneTagger()){
			logger.info("BANNER tagger successfully loaded...");
		}
	}
	
	public DocGeneFilter(Tokenizer paraTokenizer, 
			CRFTagger paraTagger, PostProcessor paraPostProcessor){
		tokenizer = paraTokenizer;
		tagger = paraTagger;
		postProcessor = paraPostProcessor;
	}
	
	public boolean loadGeneTagger(){
		String propertiesFilename = "data/banner.properties"; // banner.properties
        String modelFilename = "train/gene_model_v02.bin"; // model.bin

        // Get the properties and create the tagger
        try {
        	BannerProperties properties = BannerProperties.load(propertiesFilename);
	        tokenizer = properties.getTokenizer();
			tagger = CRFTagger.load(new File(modelFilename), properties.getLemmatiser(), properties.getPosTagger());
			postProcessor = properties.getPostProcessor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error in loading Gene Tagger");
			e.printStackTrace();
		}
        
		if (tokenizer == null || tagger == null || postProcessor == null)
			return false;
		
		return true;
	}
	
	public List<Mention> getGeneMentions(String docText) {
		geneMentions = null;

		if (tokenizer == null || tagger == null || postProcessor == null
				|| docText == null || docText.length() ==0) {
			System.out.println("BANNER components not set properly");
			return null;
		}

		String sentenceText = docText;
		if (sentenceText.length() > 0) {
			try{
				Sentence sentence = new Sentence(null, sentenceText);
				tokenizer.tokenize(sentence);
				tagger.tag(sentence);
				if (postProcessor != null)
					postProcessor.postProcess(sentence);

				geneMentions = sentence.getMentions();
			} catch (Exception e){
				return null;
			}
		}
		
//		System.out.println(sentenceText);
//		System.out.println(docText);

		return geneMentions;
	}
	
	public static void main(String[] args) throws IOException{
		
       DocGeneFilter doc = new DocGeneFilter();
       BufferedReader br = null;
       
       //1718282.txt
       br = new BufferedReader(new FileReader("1718282.txt"));
       String line = null;
       
       while ((line = br.readLine()) != null) {
			//System.out.println(line);
    	   for(Mention m : doc.getGeneMentions(line)){
    		   String term = m.getText();
    		   System.out.println(term);
    	   }
		}

       
      // System.out.println(doc.getGeneMentions("In addition, the recent findings of H-RAS mutations in 56% of RET-negative sporadic MTC and the activation of the mammalian target of rapamycin (mTOR) intracellular signaling pathway in hereditary MTC suggests that additional or alternative genetic events are important for MTC pathogenesis.Recently, vandetanib (ZD6474), an inhibitor of vascular endothelial growth factor receptor (VEGFR) 2 and VEGFR 3, RET, and epidermal growth factor receptor (EGFR), was approved for the treatment of adults with symptomatic or progressive MTC."));
	}
}
