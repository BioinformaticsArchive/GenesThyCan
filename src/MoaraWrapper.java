import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moara.bio.entities.Organism;
import moara.mention.MentionConstant;
import moara.mention.entities.GeneMention;
import moara.mention.functions.GeneRecognition;
import moara.normalization.entities.GenePrediction;
import moara.normalization.functions.ExactMatchingNormalization;
import moara.normalization.functions.GeneNormalization;
import moara.normalization.functions.MachineLearningNormalization;
import moara.util.Constant;
import moara.util.EnvironmentVariable;
import moara.wrapper.WrapperConstant;
import moara.wrapper.abner.AbnerTagger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import uk.ac.man.entitytagger.Mention;
import uk.ac.man.textpipe.Annotator;

/**
 * Wrapper for the Moara gene NER & Normalisation tool.
 * @author Chengkun Wu
 */
public class MoaraWrapper extends Annotator{
	
	public static GeneRecognition gr = null;
	public static GeneNormalization gn = null;
	public static AbnerTagger abner = null;
	public static boolean useML = false; //Use Machine Learning or Exact Matching for normalisation?
	public static boolean verbose = true;
	public static ShortFormsMiner sfMiner = null;
	
	@Override
	public void init(Map<String, String> data) {
		EnvironmentVariable.setMoaraHome("./");
		Organism human = new Organism(Constant.ORGANISM_HUMAN);
		
		if (gn == null){
			if (useML)
				gn = new MachineLearningNormalization(human);
			else
				gn = new ExactMatchingNormalization(human);
		}
		if (gr == null)
			gr = new GeneRecognition();
		
		if (abner == null){
			abner = new AbnerTagger(WrapperConstant.ABNER_BC2);
		}
		
		if (sfMiner == null){
			try {
				sfMiner = new ShortFormsMiner();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (data != null) {
			for (String key : data.keySet()) {
				if (key.equals("method")) {
					String method = data.get(key);
					if (method.equals("ML")) {
						this.useML = true;
						System.out
								.println("Using Machine learning method for normalization!");
					}
				}
			}
		}
		
		System.out.println("Moara initialized.");
	}

	@Override
	public String[] getOutputFields() {
		return new String[]{"id","entity_id",
				"entity_species", "entity_start","entity_end",
				"entity_term","nor_entity_term", "confidence"};
	}
	
	/**
	 * Disambiguate a list of possibly overlapping mentions, by picking the highest-confidence mentions.
	 * @param mentions a list of possibly overlapping mentions
	 * @return a list of mentions that do not overlap
	 */
	public List<Mention> disambiguate(List<Mention> mentions){
		List<Mention> disambiguatedMentions = new ArrayList<Mention>();

		while (mentions.size() > 0){
			boolean ambig = false;
			for (int j = 1; j < mentions.size(); j++){
				if (mentions.get(0).overlaps(mentions.get(j))){
					//					uncomment to disambiguate by longest-length instead
					//					if (mentions.get(0).getText().length() > mentions.get(j).getText().length()){
					//						mentions.remove(j--);
					//					} else if (mentions.get(0).getText().length() < mentions.get(j).getText().length()) {
					//						mentions.remove(0);
					//						ambig=true;
					//						break;						
					//					} else {
					if (mentions.get(0).getProbabilities()[0] > mentions.get(j).getProbabilities()[0]){
						mentions.remove(j--);
					} else {
						mentions.remove(0);
						ambig=true;
						break;						
					}
					//					}
				}
			}

			if (!ambig)
				disambiguatedMentions.add(mentions.remove(0));
		}

		return disambiguatedMentions;
	}

	public Mention conver2Mention(GeneMention gm){
		Mention m = new Mention(gm.GeneId().GeneId(), gm.Start(), gm.End(), gm.Text());
		double score = 0.0;
		
		if(gm.GeneId() == null)
			return null;
		
		for (int j = 0; j < gm.GeneIds().size(); j++) {
			GenePrediction gp = gm.GeneIds().get(j);
			
			if (gm.GeneId().GeneId().equals(gp.GeneId())){
				score = gp.ScoreDisambig();
				m.setComment(gp.OriginalSynonym());
			}
		}
		
		m.setProbabilities(new Double[]{score});
		
		return m;
	}
	
	@Override
	public List<Map<String, String>> process(Map<String, String> data) {
		String text = data.get("doc_text");
		String doc_id = data.get("doc_id");
		String temp = text;

		List<Mention> mentions = new ArrayList<Mention>();
		Map<String,String> geneIdToSpecies = new HashMap<String,String>();

		gr.setDocId(doc_id);
		ArrayList<GeneMention> gms = null;
		
		try{
			gms = processByMoara(text);
		} catch (Exception e){
			e.printStackTrace();
			System.err.println("[ERROR]: errors when trying to process PMID" + doc_id);
		}
		
		if (!temp.equals(text) && verbose)
			System.out.println("[ALERT]: Text changed by Moara!");
		
		if (gms == null){
			return new ArrayList<Map<String, String>>();
		}

		// Listing normalized identifiers...
		System.out.println("[PMID]:" + doc_id);
		System.out.println("\nStart\tEnd\t#Pred\tMention\tNor_Mention");
		for (int i = 0; i < gms.size(); i++) {
			GeneMention gm = gms.get(i);
			if (gm.GeneIds().size() > 0) {
				
				//Convert GeneMention object to Mention
				Mention m = conver2Mention(gm);
				
				if (m == null)
					continue;
				
				mentions.add(m);
				
				System.out.println(m.getStart()
						+ "\t"
						+ m.getEnd()
						+ "\t"
						+ m.getMostProbableID() //m.getIds()[0]
						+ "\t" + text.substring(m.getStart(), m.getEnd()));
						//+ m.getText().trim() + "\t" + gm.GeneId().OriginalSynonym());
			}
		}

		mentions = disambiguate(mentions);
		uk.ac.man.entitytagger.matching.Matcher.detectEnumerations(mentions, text);
		Collections.sort(mentions);
		
		return toMaps(mentions, geneIdToSpecies);		
	}
	
	public ArrayList<GeneMention> processByMoara(String text){
		String rpText = text;

		Map<ImmutablePair, ImmutablePair> rpSpanMap = new HashMap<ImmutablePair, ImmutablePair>();
		Map<String, String> lf2sf = new HashMap<String, String>();
		
		try {
			List<ShortFormsMiner.PairLSF> res = sfMiner.processText(text);
			
			/*
			 * Replace the long form into short form:
			 * 1. Long form only: replace it with Sf and pad with space
			 * 2. LF and SF together : keep the SF only, get rid of the brackets and pad with space. 
			 */
			
			if (res.size() > 0 && verbose){
				System.out.println("Long/short forms found!");
			}
			
			for(ShortFormsMiner.PairLSF plsf : res){
				String lf = plsf.longForm, 
					   sf = plsf.shortForm;
				lf2sf.put(lf, sf);
				
				//Deal with the situation where LF and SF together
				long lfStart = plsf.lfStart,
					 lfEnd   = plsf.lfEnd, 
					 sfStart = plsf.sfStart,
					 sfEnd   = plsf.sfEnd;
				//Define the start and end for the replacement span
				int rpStart = (int) Math.min(lfStart, sfStart),
					 rpEnd   = (int) Math.max(lfEnd, sfEnd);
				
				if (rpEnd < rpText.length()-2){
					if (rpText.substring(rpEnd, rpEnd + 1).matches("[\\]\\)\\}]")){
						rpEnd++;
					}
				}
				
				String paddedReplace = StringUtils.leftPad(plsf.shortForm, rpEnd - rpStart);
				rpText = rpText.substring(0, rpStart) + paddedReplace + rpText.substring(rpEnd);
				
				if (text.length() != rpText.length() && verbose)
					System.err.println("Length not equal after replacement!");

				if (verbose){
					System.out.println("Original: " + text.substring(rpStart, rpEnd));
					System.out.println("Replaced: " + paddedReplace);
				}
				
				ImmutablePair<Integer, Integer> rpSpan = new ImmutablePair<Integer, Integer>(rpStart, rpEnd);
				ImmutablePair<String, String> rpStr = new ImmutablePair<String, String>(text.substring(rpStart, rpEnd), paddedReplace);
				rpSpanMap.put(rpSpan, rpStr);
			}

			//System.out.println(rpText);
			//Now to deal with the long form only case:
			//Need to be very careful here, only a long form preceding by a space character should be replaced. 
			for(String lf : lf2sf.keySet()){
				Pattern p = Pattern.compile(lf,Pattern.CASE_INSENSITIVE+Pattern.LITERAL);
				Matcher m = p.matcher(rpText);
				while(m.find()){
					int mStart = m.start(),
						mEnd = m.end();
					String lfStr = m.group();
					//System.out.println(lfStr);
					
					if ((mStart > 0 && Character.isWhitespace(rpText.charAt(mStart-1))) || mStart == 0){
						String paddedReplace = StringUtils.leftPad(lf2sf.get(lf), lfStr.length());
						rpText = rpText.substring(0, mStart) + paddedReplace + rpText.substring(mEnd);
						//System.out.println(rpText);
						
						ImmutablePair<Integer, Integer> rpSpan = new ImmutablePair<Integer, Integer>(mStart, mEnd);
						ImmutablePair<String, String> rpStr = new ImmutablePair<String, String>(text.substring(mStart, mEnd), paddedReplace);
						rpSpanMap.put(rpSpan, rpStr);
						
						if(verbose){
							System.out.println("Original: " + text.substring(mStart, mEnd));
							System.out.println("Replaced: " + paddedReplace);
						}
					}

				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (text.length() != rpText.length())
			System.err.println("Length not equal after replacement!");
		
		if (text.equals(rpText) && verbose)
			System.out.println("Nothing replaced!");
		
		//This is necessary as the Moara will get the offset wrong if there is any double quote in the text;
		rpText = rpText.replace('\"', ' ');

		
		ArrayList<GeneMention> gms = gr.extract(MentionConstant.MODEL_BC2,rpText);
		//ArrayList<GeneMention> gms = abner.extract(rpText);
		ArrayList<GeneMention> gmFinal = new ArrayList<GeneMention>();

		// Listing normalized identifiers...
		StringUtil su = new StringUtil();
		// Listing mentions...
		
		int preEnd = 0;
		
		if (verbose) {
			System.out.println("Start\tEnd\tMention");
			for (int i = 0; i < gms.size(); i++) {
				GeneMention gm = gms.get(i);
				System.out.println(gm.Start() + "\t" + gm.End() + "\t"
						+ gm.Text());
			}
		}
		
		//Adjust offsets and things before doing normalization
		ArrayList<GeneMention> gmsTemp = new ArrayList<GeneMention>();
		for (GeneMention gm : gms){
			String gmText = gm.Text();
			gm = getOriginalMention(gm, rpSpanMap, rpText, preEnd, text);
			
			preEnd = gm.End();
			
			gmsTemp.add(gm);
		}
		
		// Normalizing mentions...
		Organism human = new Organism(Constant.ORGANISM_HUMAN);
		//ExactMatchingNormalization gn = new ExactMatchingNormalization(human);
		//MachineLearningNormalization gn = new MachineLearningNormalization(human);
		gms = gn.normalize(rpText, gmsTemp);
		
		if (verbose){
			System.out.println("\nStart\tEnd\t#Pred\tMention");
		}
		
		for (int i = 0; i < gms.size(); i++) {
			GeneMention gm = gms.get(i);
			//gm = getOriginalMention(gm, rpSpanMap, rpText, preEnd, text);
			
			if (verbose) {
				System.out.println(gm.Start() + "\t" + gm.End() + "\t" + gm.Text());
			}
			
			if (gm.GeneIds() != null && gm.GeneId() != null)
				gmFinal.add(gm);
			else
				continue;
			
			if (verbose) {
				/*
				if ( gm.GeneIds().size() > 0) {
					System.out.println(gm.Start() + "\t" + gm.End() + "\t"
							+ gm.GeneIds().size() + "\t" // + gm.Text()
							// + getTokenByPosition(text, gm.Start(),
							// gm.End()).trim());
							+ text.substring(gm.Start(), gm.End()));
					for (int j = 0; j < gm.GeneIds().size(); j++) {
						GenePrediction gp = gm.GeneIds().get(j);
						System.out.print("\t" + gp.GeneId() + " "
								+ gp.OriginalSynonym() + " "
								+ gp.ScoreDisambig());
						System.out.println((gm.GeneId().GeneId()
								.equals(gp.GeneId()) ? " (*)" : ""));
					}
				}*/
			}
		}
		
		System.out.println("Processed by Moara.");
		
		return gmFinal;
	}
	
	/**
	 * Converts a list of mentions to textpipe-style output maps.
	 * @param mentions
	 * @param geneIdToSpecies a map of gene -> species links
	 * @return a list of maps containing key/value pairs describing the mentions
	 */
	private List<Map<String, String>> toMaps(List<Mention> mentions, Map<String, String> geneIdToSpecies) {
		List<Map<String,String>> res = new ArrayList<Map<String,String>>();
		int i = 0;
		for (Mention m : mentions){
			Map<String,String> map = new HashMap<String,String>();

			map.put("id", ""+i++);
			map.put("entity_id", m.getIds()[0]);
			map.put("entity_start", ""+m.getStart());
			map.put("entity_end", ""+m.getEnd());
			map.put("entity_term", ""+m.getText());
			map.put("confidence", ""+m.getProbabilities()[0]);

			map.put("entity_species", geneIdToSpecies.get(m.getIds()[0]));
			map.put("nor_entity_term", m.getComment());

			res.add(map);
			
			/*
			if (m.getProbabilities()[0] > 0.001){
				res.add(map);
			}*/
		}

		return res;
	}

	@Override
	public void destroy() {
	
	}
	
	static public  String getTokenByPosition(String text, int start, int end) {
		String token = "";
		int position = 0;
		for (int i=0; i<text.length(); i++) {
			char c = text.charAt(i);
			if (position>=start && position<=end)
				token += c;
			if (!Character.isWhitespace(c))
				position++;
		}
		return token;
	}
	
	static public void adjustOffsets(GeneMention gm, int preEnd, String text){
		String mText = getTokenByPosition(text, gm.Start(), gm.End()).trim();
		//String mText = gm.Text();
		
		int mStart = text.indexOf(mText, preEnd);
		int mEnd = mStart + mText.length();
		
		gm.setStart(mStart);
		gm.setEnd(mEnd);
	}
	
	static public GeneMention getOriginalMention(GeneMention gm, Map<ImmutablePair, ImmutablePair> rpSpanMap, String rpText, int preEnd, String orgText){
		adjustOffsets(gm, preEnd, rpText);
		
		int start = gm.Start(),
			end   = gm.End();
		
		for(ImmutablePair ip : rpSpanMap.keySet()){
			int rpStart = (Integer)ip.getLeft();
			int rpEnd   = (Integer)ip.getRight();
			
			if ((rpStart >= start && rpStart < end) || (rpEnd >= start && rpEnd <= end)){
				if (rpStart < start)
					start = rpStart;
				if (rpEnd > end)
					end = rpEnd;
				
				break;
			}
		}
		
		do{
			char c = orgText.charAt(start);
			if (Character.isWhitespace(c) || c == '.' || c == '?' || c == '!')
				start++;
			else
				break;
		}while(start < end);
		
		gm.setStart(start);
		gm.setEnd(end);
		
		if (start < end)
			gm.setText(orgText.substring(start, end));
		
		return gm;
	}
	
	public static void main(String[] args) {

		// Abstract Pubmed Id 8076837
		/*String text = "A gene (pkt1) was isolated from the filamentous fungus Trichoderma " +
				"reesei, which exhibits high homology with the yeast YPK1 and YKR2 (YPK2) " +
				"genes. It contains a 2123-bp ORF that is interrupted by two introns, and it " +
				"encodes a 662-amino-acid protein with a calculated M(r) of 72,820. During " +
				"active growth, pkt1 is expressed as two mRNAs of 3.1 and 2.8 kb which differ " +
				"in the 3' untranslated region due to the use of two different polyadenylation " +
				"sites. ";*/
		
		
		

		// Abstract PubMed ID
		String text = "Biological significance of aminopeptidase N/CD13 in thyroid carcinomas.\nAminopeptidase N (APN)/CD13 is a transmembrane ectopeptidase expressed on a wide variety of cells. However, the precise function of APN/CD13 in tumor cells and the relationship of APN/CD13 to thyroid cancer remain unclear. In our study, we quantified the expression of APN/CD13 and additionally dipeptidyl peptidase IV (DPIV)/CD26 in thyroid carcinoma cell lines and in tissues of patients with thyroid carcinomas. Undifferentiated anaplastic thyroid carcinomas expressed more APN/CD13 than differentiated thyroid carcinomas. DPIV/CD26 showed an opposite expression pattern. We detected higher levels of DPIV/CD26 in follicular thyroid carcinomas (FTCs) and papillary thyroid carcinomas than in undifferentiated anaplastic thyroid carcinomas. In the undifferentiated thyroid carcinoma cell line 1736, APN/CD13 mRNA expression could be increased by epidermal growth factor, basic fibroblast growth factor, interleukin-6, and tumor necrosis factor alpha. FTC-133 cells stably transfected with an expression vector for APN-enhanced green fluorescent protein showed a higher migration rate than FTC-133 cells transfected with the enhanced green fluorescent protein-control plasmid. Overexpression of APN/CD13 in stably transfected cells is associated with down-regulation of N-myc down-regulated gene (NDRG)-1, melanoma-associated antigen ME491/CD63, and DPIV/CD26 gene expression. Inhibition of APN/CD13 mRNA expression by small interfering RNA induced NDRG-1, ME491/CD63, and DPIV/CD26 mRNA expression in cells of the undifferentiated thyroid carcinoma cell line C643. We conclude that APN/CD13-associated down-regulation of NDRG-1, ME491/CD63, and DPIV/CD26 in thyroid carcinoma cells is an important step of tumor progression to more malignant phenotypes, and we underline the important role of APN/CD13 as mediator in a multimolecular process regulating cell migration.";
		MoaraWrapper mw = new MoaraWrapper();
		mw.init(null);
		
		mw.processByMoara(text);
		/*
		String rpText = text;
		
		ShortFormsMiner sfMiner;
		Map<ImmutablePair, ImmutablePair> rpSpanMap = new HashMap<ImmutablePair, ImmutablePair>();
		Map<String, String> lf2sf = new HashMap<String, String>();
		
		try {
			sfMiner = new ShortFormsMiner();
			List<ShortFormsMiner.PairLSF> res = sfMiner.processText(text);
			
			
			 //Replace the long form into short form:
			 //1. Long form only: replace it with Sf and pad with space
			 //2. LF and SF together : keep the SF only, get rid of the brackets and pad with space. 
			 
			
			if (res.size() > 0){
				System.out.println("Long/short forms found!");
			}
			
			
			for(ShortFormsMiner.PairLSF plsf : res){
				String lf = plsf.longForm, 
					   sf = plsf.shortForm;
				lf2sf.put(lf, sf);
				
				//Deal with the situation where LF and SF together
				long lfStart = plsf.lfStart,
					 lfEnd   = plsf.lfEnd, 
					 sfStart = plsf.sfStart,
					 sfEnd   = plsf.sfEnd;
				//Define the start and end for the replacement span
				int rpStart = (int) Math.min(lfStart, sfStart),
					 rpEnd   = (int) Math.max(lfEnd, sfEnd);
				
				if (rpEnd < rpText.length()-2){
					if (rpText.substring(rpEnd, rpEnd + 1).matches("[\\]\\)\\}]")){
						rpEnd++;
					}
				}
				
				String paddedReplace = StringUtils.leftPad(plsf.shortForm, rpEnd - rpStart);
				rpText = rpText.substring(0, rpStart) + paddedReplace + rpText.substring(rpEnd);
				
				if (text.length() != rpText.length())
					System.err.println("Length not equal after replacement!");

				System.out.println("Original: " + text.substring(rpStart, rpEnd));
				System.out.println("Replaced: " + paddedReplace);
				
				ImmutablePair<Integer, Integer> rpSpan = new ImmutablePair<Integer, Integer>(rpStart, rpEnd);
				ImmutablePair<String, String> rpStr = new ImmutablePair<String, String>(text.substring(rpStart, rpEnd), paddedReplace);
				rpSpanMap.put(rpSpan, rpStr);
			}

			//System.out.println(rpText);
			//Now to deal with the long form only case:
			//Need to be very careful here, only a long form preceding by a space character should be replaced. 
			for(String lf : lf2sf.keySet()){
				Pattern p = Pattern.compile(lf,Pattern.CASE_INSENSITIVE+Pattern.LITERAL);
				Matcher m = p.matcher(rpText);
				while(m.find()){
					int mStart = m.start(),
						mEnd = m.end();
					String lfStr = m.group();
					//System.out.println(lfStr);
					
					if ((mStart > 0 && Character.isWhitespace(rpText.charAt(mStart-1))) || mStart == 0){
						String paddedReplace = StringUtils.leftPad(lf2sf.get(lf), lfStr.length());
						rpText = rpText.substring(0, mStart) + paddedReplace + rpText.substring(mEnd);
						//System.out.println(rpText);
						
						ImmutablePair<Integer, Integer> rpSpan = new ImmutablePair<Integer, Integer>(mStart, mEnd);
						ImmutablePair<String, String> rpStr = new ImmutablePair<String, String>(text.substring(mStart, mEnd), paddedReplace);
						rpSpanMap.put(rpSpan, rpStr);
						
						System.out.println("Original: " + text.substring(mStart, mEnd));
						System.out.println("Replaced: " + paddedReplace);
					}

				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (text.length() != rpText.length())
			System.err.println("Length not equal after replacement!");
		
		if (text.equals(rpText))
			System.out.println("Nothing replaced!");
		
		// Extracting...
		EnvironmentVariable.setMoaraHome("./");
		//GeneRecognition gr = new GeneRecognition();
		AbnerTagger abner = new AbnerTagger(WrapperConstant.ABNER_BC2);
		ArrayList<GeneMention> gms = abner
				.extract(rpText);

		// Listing normalized identifiers...
		// Listing mentions...
		System.out.println("Start\tEnd\tMention");
		int preEnd = 0;
		for (int i = 0; i < gms.size(); i++) {
			GeneMention gm = gms.get(i);
			System.out.println(gm.Start() + "\t" + gm.End() + "\t" + gm.Text());
			System.out.println(getTokenByPosition(rpText, gm.Start(), gm.End()));
		}
		
		// Normalizing mentions...
		Organism human = new Organism(Constant.ORGANISM_HUMAN);
		ExactMatchingNormalization gn = new ExactMatchingNormalization(human);
		//MachineLearningNormalization gn = new MachineLearningNormalization(human);
		gms = gn.normalize(rpText, gms);
		
		System.out.println(rpText);
		
		System.out.println("\nStart\tEnd\t#Pred\tMention");
		for (int i = 0; i < gms.size(); i++) {
			GeneMention gm = gms.get(i);
			//adjustOffsets(gm, preEnd, rpText);
			gm = getOriginalMention(gm, rpSpanMap, rpText, preEnd);
			preEnd = gm.End();
			if (gm.GeneIds().size() > 0) {
				System.out.println(gm.Start()
						+ "\t"
						+ gm.End()
						+ "\t"
						+ gm.GeneIds().size()
						+ "\t" //+ gm.Text()
						//+ getTokenByPosition(text, gm.Start(), gm.End()).trim());
						+ text.substring(gm.Start(), gm.End()));
				for (int j = 0; j < gm.GeneIds().size(); j++) {
					GenePrediction gp = gm.GeneIds().get(j);
					System.out.print("\t" + gp.GeneId() + " "
							+ gp.OriginalSynonym() + " " + gp.ScoreDisambig());
					System.out.println((gm.GeneId().GeneId()
							.equals(gp.GeneId()) ? " (*)" : ""));
				}
			}
		}
		*/
	}

}
