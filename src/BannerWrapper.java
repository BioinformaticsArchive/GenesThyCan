import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moara.bio.entities.Organism;
import moara.mention.entities.GeneMention;
import moara.normalization.NormalizationConstant;
import moara.normalization.entities.GenePrediction;
import moara.normalization.functions.GeneNormalization;
import moara.normalization.functions.MachineLearningNormalization;
import moara.normalization.functions.SoftMatchingNormalization;
import moara.util.Constant;
import moara.util.EnvironmentVariable;
import moara.util.text.StringUtil;
import uk.ac.man.textpipe.Annotator;
import banner.tagging.Mention;
/**
 * Wrapper for the GeneTUKit gene NER tool.
 * @author Martin
 */
public class BannerWrapper extends Annotator {
	private boolean verbose = true;
	private static DocGeneFilter banner = null;
	public static GeneNormalization gn = null;
	public static ShortFormsMiner sfMiner = null;
	public Map<String, String> lf2sf = null;
	
	@Override
	public void init(Map<String, String> data) {
		EnvironmentVariable.setMoaraHome("./");
		Organism human = new Organism(Constant.ORGANISM_HUMAN);	
		//gn = new ExactMatchingNormalization(human);
		MachineLearningNormalization mln = new MachineLearningNormalization(human);
		double threshold = 0.93;
		
		if (data.size() > 0){
			String paraSim = data.get("sim");
			
			if (paraSim != null){
				threshold = Double.parseDouble(paraSim);
				System.out.println("Soft matching threshold set to be " + paraSim);
			}
		}
		
		SoftMatchingNormalization smn = new SoftMatchingNormalization(human, threshold);
		//mln.setStringSimilarity(Constant.DISTANCE_SOFT_TFIDF);
		//gn = mln;
		gn = smn;
		gn.useDisambiguationMeasure(NormalizationConstant.DISAMBIGUATION_SOFT_TFIDF);
		
		banner = new DocGeneFilter();
		
		if (sfMiner == null){
			try {
				sfMiner = new ShortFormsMiner();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		lf2sf = new HashMap<String, String>();
	}

	@Override
	public String[] getOutputFields() {
		return new String[]{"id","entity_id", "entity_start","entity_end","entity_term","org_entity_term", "confidence"};
	}
	
	public List<GeneMention> correctOffsets(List<Mention> mentions, String docText){
		List<GeneMention> geneMentions = new ArrayList<GeneMention>();
		List<Mention> sortedMentions = new ArrayList<Mention>();
		sortedMentions.addAll(mentions);
		
		//Sort the mentions by offsets
		Collections.sort(sortedMentions, new Comparator<Mention>() {
	        public int compare(Mention m1, Mention m2) {
	            return m1.getStart() - m2.getEnd();
	        }
	    });
		
		//System.err.println("Size before offset correction : " + sortedMentions.size());
		int preStart = 0;
		
		for (Mention m : sortedMentions){
			int found = -1;

			if ((found = docText.indexOf(m.getText(), preStart)) >= 0) {
				GeneMention geneMention = new GeneMention(m.getText(), found,
						found + m.getText().length());
				preStart = found + m.getText().length();
				geneMentions.add(geneMention);
				
				if (!m.getText().equals(docText.substring(geneMention.Start(), geneMention.End()))){
					System.err.println("Offsets not matched!");
				}
			}
		}
		
		//System.err.println("Size after offset correction : " + geneMentions.size());
		
		return geneMentions;
	}
	
	@Override
	public List<Map<String, String>> process(Map<String, String> data) {
		String text = data.get("doc_text");
		String doc_id = data.get("doc_id");
		HashMap<String, GeneMention> foundGenes = new HashMap<String, GeneMention>();
		
		System.out.println("Processing doc#" + doc_id);

		List<GeneMention> norMentions = new ArrayList<GeneMention>();
		List<GeneMention> remainMentions = new ArrayList<GeneMention>();
		List<GeneMention> bannerMentions = correctOffsets(banner.getGeneMentions(text), text);
		List<ShortFormsMiner.PairLSF> res = sfMiner.processText(text);
		lf2sf = new HashMap<String, String>();
		
		for (ShortFormsMiner.PairLSF lsf : res){
			lf2sf.put(lsf.longForm, lsf.shortForm);
		}
		
		for (GeneMention m : bannerMentions){
			GeneMention norMention = null;
			
			if (foundGenes.containsKey(m.Text())){
				GeneMention preMention = foundGenes.get(m.Text());
				
				if (preMention != null){
					norMention = new GeneMention(m.Text(), m.Start(), m.End());
					
					norMention.addGenePrediction(preMention.GeneId());
					norMention.setGeneId(preMention.GeneId());
				}
				
			}else{
				norMention = normalizeWithMoara(text, m);
				foundGenes.put(m.Text(), norMention);
			}
			
			if (norMention != null){
				norMentions.add(norMention);
				if (verbose){
					System.out.println(norMention.Start() + "\t" + norMention.End() + "\t"
							+ norMention.GeneId().GeneId() + "\t" + norMention.Text());
				}
			}
			else{
				remainMentions.add(m);
			}
		}
		
		for (GeneMention m : remainMentions){
			GeneMention norMention = null;
			for(String mStr : foundGenes.keySet()){
				if (m.Text().toLowerCase().contains(mStr.toLowerCase()) && (mStr.length() * 2 > m.Text().length())){
					GeneMention preMention = foundGenes.get(mStr);
					
					if (preMention == null){
						continue;
					}
					
					norMention = new GeneMention(m.Text(), m.Start(), m.End());
					
					norMention.addGenePrediction(preMention.GeneId());
					norMention.setGeneId(preMention.GeneId());
					break;
				}
			}
			
			if (norMention != null){
				norMentions.add(norMention);
				
				if (verbose){
					System.out.println(norMention.Start() + "\t" + norMention.End() + "\t"
							+ norMention.GeneId().GeneId() + "\t" + norMention.Text());
				}
			}
		}
		
		/*
		for(banner.tagging.Mention m : banner.getGeneMentions(text)){
 		   String term = m.getText();
 		   GeneMention gm = new GeneMention(m.getText(), m.getStart(), m.getEnd());
 		   
			GeneMention norMention = normalizeWithMoara(text, gm);
			if (verbose){
				System.out.println(gm.toString());
			}
			if (norMention != null)
				norMentions.add(norMention);
 	   }
 	   */
		
		return toMaps(norMentions);		
	}
	
	public GeneMention normalizeWithMoara(String text, GeneMention m){
		ArrayList<GeneMention> gms = new ArrayList<GeneMention>();
		GeneMention norMention = null;
		
		String[] parts = m.Text().split("\\.\\s");
		
		for (String part : parts) {
			//candidateParts.add(part);
			if (lf2sf.containsKey(part)){
				part = lf2sf.get(part);
			}
			GeneMention candM = new GeneMention(part, m.Start(), m.End());
			gms.add(candM);
		}
				
		if (gms == null || gms.size() == 0)
			return m;
		
		//Debug
		if (!m.Text().equalsIgnoreCase("integrins")){
			//return null;
		}
		
		if (verbose)
			System.out.println("\nBeing normalized by Moara\nStart\tEnd\t#Pred\tMention");
		
		gms = gn.normalize(text,gms); //The text here is the whole document text, not the mention text
		// Listing normalized identifiers...
		StringUtil su = new StringUtil();
		
		
		for (int i=0; i<gms.size(); i++) {
			m = gms.get(i);
			if (m.GeneIds() != null && m.GeneIds().size()>0) {
				if (verbose) {
					System.out.println(m.Start() + "\t" + m.End() + "\t"
							+ m.GeneIds().size() + "\t" + text.substring(m.Start(), m.End()));
					for (int j = 0; j < m.GeneIds().size(); j++) {
						GenePrediction gp = m.GeneIds().get(j);
						
						if (m.GeneId() != null) {
							System.out.print("\t" + gp.GeneId() + " "
									+ gp.Synonym() + " "
									+ gp.ScoreDisambig());
							
							if(gp.Chosen()){

								System.out.println((m.GeneId().GeneId()
										.equals(gp.GeneId()) ? " (*)" : ""));

								norMention = m;
							}else
								System.out.println();
						}
					}
				}
				
			}
			
		}
		
		return norMention;
	}
	
	/**
	 * Converts a list of mentions to textpipe-style output maps.
	 * @param mentions
	 * @param geneIdToSpecies a map of gene -> species links
	 * @return a list of maps containing key/value pairs describing the mentions
	 */
	private List<Map<String, String>> toMaps(List<GeneMention> mentions) {
		List<Map<String,String>> res = new ArrayList<Map<String,String>>();
		int i = 0;
		for (GeneMention m : mentions){
			Map<String,String> map = new HashMap<String,String>();

			map.put("id", ""+i++);
			map.put("entity_id", m.GeneId().GeneId());
			map.put("entity_start", ""+ m.Start());
			map.put("entity_end", ""+m.End());
			map.put("entity_term", ""+m.Text());
			map.put("org_entity_term", m.GeneId().OriginalSynonym());

			map.put("confidence", ""+m.GeneId().ScoreDisambig());

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
	
	/**
	 * test main method
	 * @param args
	 */
	public static void main(String[] args){
		
	
	}
}
