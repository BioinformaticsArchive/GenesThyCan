

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.LanguageAnalyser;
import gate.Resource;
import gate.creole.ExecutionException;
import gate.creole.POSTagger;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.creole.splitter.SentenceSplitter;
import gate.creole.tokeniser.DefaultTokeniser;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.philgooch.BiomedicalAbbreviationExpander;

import dragon.onlinedb.Article;

public class ShortFormsMiner {
	//Data
	public static BiomedicalAbbreviationExpander badrex = null;
	public static SerialAnalyserController serialController = null;
	
	private static DefaultTokeniser tokeniser = null;
	private static SentenceSplitter splitter = null;
	private static POSTagger tagger = null;
	private static Logger logger = null;
	public static boolean RETAIN_SHORT = true;
	
	private File aPluginDir;
	private File bPluginDir;
	
	public static class PairLSF{
		public String longForm;
		public String shortForm;
		public String orgStr;
		public long sfStart;
		public long sfEnd;
		public long lfStart;
		public long lfEnd;
		
		public PairLSF(String lf, String sf, long lfStart, long lfEnd, long sfStart, long sfEnd){
			longForm = lf;
			shortForm = sf;
			this.sfStart = sfStart;
			this.sfEnd = sfEnd;
			this.lfStart = lfStart;
			this.lfEnd = lfEnd;
		}
	}
	
	public ShortFormsMiner() throws Exception{
		System.out.println("Initializing ShortFormsMiner...");
        
		initGate();
        
        initBADREX();
        
        System.out.println("Abbreviation miner ready. ");
	}

	public void initGate() throws Exception{
		Gate.runInSandbox(true);
		//Gate.setGateHome(new File("/Applications/GATE_Developer_7.0"));
        
        File pluginsDir = new File("GATE_PLUGINS");
        Gate.setPluginsHome(pluginsDir);
        
        Gate.init();
        
        //  load the Tools plugin
        
        aPluginDir = new File(pluginsDir, "ANNIE");
        bPluginDir = new File(pluginsDir, "BADREX");
        
        // load the plugin.
        System.out.println(aPluginDir.toURI().toURL());
        System.out.println(bPluginDir.toURI().toURL());
        Gate.getCreoleRegister().registerDirectories(aPluginDir.toURI().toURL());
        Gate.getCreoleRegister().registerDirectories(bPluginDir.toURI().toURL());
      
        LanguageAnalyser sentenceSplitter = (LanguageAnalyser)Factory.createResource("gate.creole.splitter.RegexSentenceSplitter");
        if (serialController == null)
        		serialController = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController");
        
        //badrex = new BiomedicalAbbreviationExpander();
        badrex = (BiomedicalAbbreviationExpander) Factory.createResource("org.philgooch.BiomedicalAbbreviationExpander");
        
        serialController.add(sentenceSplitter);
	}
	
	public void initBADREX() throws Exception {
        
		String pluginHomePath = Gate.getPluginsHome().getPath();
		pluginHomePath = URLDecoder.decode(pluginHomePath, "utf-8");
				
        badrex.setConfigFileURL(new File(bPluginDir, "resources/config.txt").toURI().toURL());
        badrex.setGazetteerListsURL(new	File(bPluginDir, "resources/lookup/abbrevs.def").toURI().toURL());
        badrex.setExpandAllShortFormInstances(Boolean.FALSE);
        badrex.setLongType("Long");
        badrex.setLongTypeFeature("longForm");
        badrex.setMaxInner(5);
        badrex.setMaxOuter(5);
        badrex.setSentenceType("Sentence");
        badrex.setShortType("Short");
        badrex.setShortTypeFeature("shortForm");
        badrex.setSwapShortest(Boolean.TRUE);
        badrex.setThreshold(0.9f);
        badrex.setUseBidirectionMatch(Boolean.FALSE);
        badrex.setUseLookups(Boolean.FALSE);
        
        Resource result = badrex.init();
	}
	
	
	public List<PairLSF> processText(String text){
		Corpus corpus = null;
		
		try {
			corpus = Factory.newCorpus("Corpus" + text.hashCode());
		} catch (ResourceInstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			Document d = null;
			try {
				d = Factory.newDocument(text);
			} catch (ResourceInstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(d == null){
				System.out.println("Error when trying to create document from text:" + text);
				return null;
			}
			
			corpus.add(d);
			
			// Create a stub Gate app and document, sentence splitter, and BADREX
	        try {
	        		serialController.setCorpus(corpus);
				serialController.execute();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			badrex.setDocument(d);
			try {
				badrex.execute();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			AnnotationSet defaultAS = d.getAnnotations();
			for(String type: defaultAS.getAllTypes()){
			//	System.out.println(type);
			}
			
		//	System.out.println("List of results:");
			AnnotationSet abbrevAS = d.getAnnotations().get("Short");
			AnnotationSet termAS = d.getAnnotations().get("Long");
			
			List<Annotation> abSorted = new ArrayList<Annotation>(abbrevAS);
			List<Annotation> termSorted = new ArrayList<Annotation>(termAS);
			Collections.sort(abSorted, gate.Utils.OFFSET_COMPARATOR);
			Collections.sort(termSorted, gate.Utils.OFFSET_COMPARATOR);
			
			List<PairLSF> listPairLSF = new ArrayList<PairLSF>();
			
			Iterator<Annotation> termASIter = termSorted.iterator();
			Iterator<Annotation> abbrevASIter = abSorted.iterator();
			
			while (termASIter.hasNext() && abbrevASIter.hasNext()){
				Annotation term = termASIter.next();
				Annotation abbrev = abbrevASIter.next();
				
				FeatureMap termFeats = term.getFeatures();
				FeatureMap abbrevFeats = abbrev.getFeatures();
				String shortForm = (String) termFeats.get("shortForm");
				String longForm = (String) abbrevFeats.get("longForm");
				long sfStart = abbrev.getStartNode().getOffset();
				long sfEnd = abbrev.getEndNode().getOffset();
				long lfStart = term.getStartNode().getOffset();
				long lfEnd = term.getEndNode().getOffset();
				
			//	System.out.println(shortForm);
		//		System.out.println(longForm);
				PairLSF plsf = new PairLSF(longForm, shortForm, lfStart, lfEnd, sfStart, sfEnd);
				plsf.orgStr = text;
				
				listPairLSF.add(plsf);
			}
			

			Factory.deleteResource(d);

			corpus.clear();
		
		
		return listPairLSF;
	}
	
	
	public boolean keepShortForm(PairLSF plsf){
		if(plsf == null)
			return false;
		
		String[] lfParts = plsf.longForm.split(" ");
		
		for(String str : lfParts){
			if(str.equalsIgnoreCase("of")){
				return true;
			}
		}
		
		return false;
	}
	
	public String subAbbrv(String str, List<PairLSF> psfList){
		String result = str;
		
		if(psfList == null)
			return result;
		
		for(PairLSF plsf: psfList ){
			String sfPattern = "[^a-zA-Z]+" + plsf.shortForm + "[^a-zA-Z]+";
			
			result = result.replaceAll(sfPattern, " [ " + plsf.longForm + " ]");
		}
		
		/*
		if(!result.equals(str))
			System.out.println("Abbrv sub took place");
			*/
		
		return result;
	}
	
	public String retainOneForm(String str){
		String result = str;
		
		List<PairLSF> psfList = this.processText(str);
		/*for (PairLSF plsf: psfList){
			System.out.println(plsf.longForm);
			System.out.println(plsf.shortForm);
		}*/
		
		if(psfList == null)
			return result;
		
		for(PairLSF plsf: psfList ){
			boolean sfMatch = false;
			boolean lfMatch = false;
			boolean special = false;

			String sfPattern = "\\s*[\\(\\[]\\s*" + plsf.shortForm + "\\s*[\\)\\]]";
			

			Matcher matcher = Pattern.compile(sfPattern).matcher(str);

			while (matcher.find()) {
				sfMatch = true;
			}

			String lfPattern = "\\s*[\\[\\(]\\s*" + plsf.longForm + "\\s*[\\)\\]]";

			matcher = Pattern.compile(lfPattern).matcher(str);

			while (matcher.find()) {
				lfMatch = true;
			}

			if (sfMatch) {
				if(!special && (keepShortForm(plsf) || this.RETAIN_SHORT)){
					result = result.replaceAll(plsf.longForm + "\\s*", "");
					result = result.replaceAll("[\\[\\(]\\s*" + plsf.shortForm + "\\s*[\\)\\]]", plsf.shortForm);
				//	System.err.println("Keep short form: " + String.format(" [%s] instead of [%s]", plsf.shortForm, plsf.longForm));
				}else{
					result = result.replaceAll(sfPattern, "");
				//	System.err.println("Normal acronym detected!!");
				}
			}

			//The long form in the bracket
			if (lfMatch) {
				result = result.replaceAll(lfPattern, "");
			//	System.err.println("Keep short form: " + String.format(" [%s] instead of [%s]", plsf.shortForm, plsf.longForm));
			//	System.err.println("Reverse acronym detected!!");
			}

			if (!lfMatch && !sfMatch) {
			//	System.err.println("SF & LF not detected!");
			//	System.out.println(result);
			} else {
				result = result.replaceAll("\\s+", " ");
			//	System.out.println(result);
			}
			
		}
		
		return result;
	}

	public void test2() throws Exception{
		String text = "Biological significance of aminopeptidase N/CD13 in thyroid carcinomas. Aminopeptidase N (APN)/CD13 is a transmembrane ectopeptidase expressed on a wide variety of cells. However, the precise function of APN/CD13 in tumor cells and the relationship of APN/CD13 to thyroid cancer remain unclear. In our study, we quantified the expression of APN/CD13 and additionally dipeptidyl peptidase IV (DPIV)/CD26 in thyroid carcinoma cell lines and in tissues of patients with thyroid carcinomas. Undifferentiated anaplastic thyroid carcinomas expressed more APN/CD13 than differentiated thyroid carcinomas. DPIV/CD26 showed an opposite expression pattern. We detected higher levels of DPIV/CD26 in follicular thyroid carcinomas (FTCs) and papillary thyroid carcinomas than in undifferentiated anaplastic thyroid carcinomas. In the undifferentiated thyroid carcinoma cell line 1736, APN/CD13 mRNA expression could be increased by epidermal growth factor, basic fibroblast growth factor, interleukin-6, and tumor necrosis factor alpha. FTC-133 cells stably transfected with an expression vector for APN-enhanced green fluorescent protein showed a higher migration rate than FTC-133 cells transfected with the enhanced green fluorescent protein-control plasmid. Overexpression of APN/CD13 in stably transfected cells is associated with down-regulation of N-myc down-regulated gene (NDRG)-1, melanoma-associated antigen ME491/CD63, and DPIV/CD26 gene expression. Inhibition of APN/CD13 mRNA expression by small interfering RNA induced NDRG-1, ME491/CD63, and DPIV/CD26 mRNA expression in cells of the undifferentiated thyroid carcinoma cell line C643. We conclude that APN/CD13-associated down-regulation of NDRG-1, ME491/CD63, and DPIV/CD26 in thyroid carcinoma cells is an important step of tumor progression to more malignant phenotypes, and we underline the important role of APN/CD13 as mediator in a multimolecular process regulating cell migration.";
		System.out.println(this.retainOneForm(text));
		
		System.out.println("String processed.");
		
		
		List<PairLSF> res = this.processText(text);
		for (PairLSF plsf: res){
			System.out.println(plsf.longForm + String.format(" [%d, %d]", plsf.lfStart, plsf.lfEnd));
			
			System.out.println(plsf.shortForm + String.format(" [%d, %d]", plsf.sfStart, plsf.sfEnd));
		}
	}
	
	public static void main(String[] args){
		ShortFormsMiner sfMiner;
		
		
		try {
			sfMiner = new ShortFormsMiner();
			sfMiner.RETAIN_SHORT = false;
			sfMiner.test2();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
