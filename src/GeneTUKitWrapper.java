import genetukit.api.GNProcessor;
import genetukit.api.GNResultItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moara.bio.entities.Organism;
import moara.mention.entities.GeneMention;
import moara.normalization.entities.GenePrediction;
import moara.normalization.functions.ExactMatchingNormalization;
import moara.util.Constant;
import moara.util.EnvironmentVariable;
import moara.util.text.StringUtil;
import uk.ac.man.biocontext.util.Misc;
import uk.ac.man.entitytagger.Mention;
import uk.ac.man.entitytagger.generate.GenerateMatchers;
import uk.ac.man.textpipe.Annotator;
/**
 * Wrapper for the GeneTUKit gene NER tool.
 * @author Martin
 */
public class GeneTUKitWrapper extends Annotator {
	private GNProcessor processor;
	private File tmpDir;
	private ExactMatchingNormalization gn;
	private boolean verbose = true;

	@Override
	public void init(Map<String, String> data) {
		this.tmpDir = new File("./tmp/");

		//Load GeneTUKit
		this.processor = new GNProcessor();
		processor.open(true);
		
		EnvironmentVariable.setMoaraHome("./");
		Organism human = new Organism(Constant.ORGANISM_HUMAN);	
		gn = new ExactMatchingNormalization(human);
	}

	@Override
	public String[] getOutputFields() {
		return new String[]{"id","entity_id","entity_species", "entity_start","entity_end","entity_term","org_entity_term", "entity_group", "confidence"};
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

	public void debug_output(GNResultItem[] items){
		
		//Output GN results:
		for(int j=0; j<items.length; j++)
		{
		    StringBuffer sb = new StringBuffer();
		    sb.append(items[j].getID());
		    sb.append("\t");
		    for(int k=0; k<items[j].getGeneMentionList().size(); k++)
		    {
		        if(k!=0) sb.append("|");
		        sb.append(items[j].getGeneMentionList().get(k));
		    }
		    sb.append("\t");
		    sb.append(items[j].getSpeciesID());
		    sb.append("\t");
		    sb.append(items[j].getScore());
		    System.out.println(sb.toString());
		}
	}
	
	@Override
	public List<Map<String, String>> process(Map<String, String> data) {
		String text = data.get("doc_text");
		//System.out.println(text);

		GNResultItem[] items;
		File f=null;
		try {
			if (data.get("doc_id").startsWith("PMC") && data.containsKey("doc_xml")){
				f = Misc.writeTempFile(tmpDir, data.get("doc_xml")); //add extra newline and a hyphen, since GeneTUKit assumes at least two lines
				items = processor.process(f.getAbsolutePath(), GNProcessor.FileType.NXML);
				f.delete();
			} else {
				if (verbose)
					System.out.println(data.get("doc_id"));
				f = Misc.writeTempFile(tmpDir, text + "\n-"); //add extra newline and a hyphen, since GeneTUKit assumes at least two lines
				items = processor.process(f.getAbsolutePath(), GNProcessor.FileType.PLAIN);
				f.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("error\tgenetukit crash\t" + data.get("doc_id") + "\t" + e.toString());
			if (f != null)
				f.delete();
			return new ArrayList<Map<String,String>>();
		}

		List<Mention> mentions = new ArrayList<Mention>();

		Map<String,String> geneIdToSpecies = new HashMap<String,String>();
		if (verbose)
			debug_output(items);

		for (GNResultItem item : items){
			geneIdToSpecies.put(item.getID(), item.getSpeciesID());

			for (String term : item.getGeneMentionList()){
				term = term.replace("\\/", "/");
				
				Pattern p = Pattern.compile("\\b" + GenerateMatchers.escapeRegexp(term.toLowerCase()) + "\\b");
				Matcher m = p.matcher(text.toLowerCase());
				while (m.find()){
					Mention mention = new Mention(item.getID(), m.start(), m.end(), text.substring(m.start(), m.end()));
					mention.setProbabilities(new Double[]{item.getScore()});
					Mention norMention = normalizeWithMoara(text, mention);
					if (verbose){
						System.out.println(mention.toString());
					}
					mentions.add(mention);
				}
			}
		}

		mentions = disambiguate(mentions);
		uk.ac.man.entitytagger.matching.Matcher.detectEnumerations(mentions, text);
		Collections.sort(mentions);
		
		return toMaps(mentions, geneIdToSpecies);		
	}
	
	public Mention normalizeWithMoara(String text, Mention m){
		ArrayList<GeneMention> gms = new ArrayList<GeneMention>();
		Mention norMention = null;
		
		GeneMention gm = new GeneMention(m.getText(), m.getStart(), m.getEnd());
		gms.add(gm);
		
		if (gms == null || gms.size() == 0)
			return m;
		
		gms = gn.normalize(text,gms); //The text here is the whole document text, not the mention text
		// Listing normalized identifiers...
		StringUtil su = new StringUtil();
		if (verbose)
			System.out.println("Normalized by Moara\nStart\tEnd\t#Pred\tMention");
		
		for (int i=0; i<gms.size(); i++) {
			gm = gms.get(i);
			if (gm.GeneIds().size()>0) {
				if (verbose) {
					System.out.println(gm.Start() + "\t" + gm.End() + "\t"
							+ gm.GeneIds().size() + "\t" + gm.Text());
					for (int j = 0; j < gm.GeneIds().size(); j++) {
						GenePrediction gp = gm.GeneIds().get(j);
						System.out.print("\t" + gp.GeneId() + " "
								+ gp.OriginalSynonym() + " "
								+ gp.ScoreDisambig());
						System.out.println((gm.GeneId().GeneId()
								.equals(gp.GeneId()) ? " (*)" : ""));
					}
				}
				
				Mention mention = new Mention(gm.GeneId().GeneId(), gm.Start(), gm.End(), gm.Text());
				mention.setProbabilities(new Double[]{gm.GeneId().ScoreDisambig()});
				mention.setComment(gm.GeneId().OriginalSynonym());
				norMention = mention;
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
			map.put("org_entity_term", m.getComment());

			String group = null;
			if (m.getComment().indexOf("group: ") != -1){
				int s = m.getComment().indexOf("group: ") + "group: ".length();
				group = m.getComment().substring(s, m.getComment().length());
			}
			
			map.put("entity_group", group);

			map.put("confidence", ""+m.getProbabilities()[0]);

			map.put("entity_species", geneIdToSpecies.get(m.getIds()[0]));

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
		processor.close();
	}
	
	/**
	 * test main method
	 * @param args
	 */
	public static void main(String[] args){
		
		boolean useBanner = true;

		GNProcessor processor = new GNProcessor();
		processor.open(useBanner);

		GNProcessor.FileType fileType = GNProcessor.FileType.PLAIN;
		GNResultItem[] items=null;
		long t = System.currentTimeMillis();

		/*
		for (int i = 0; i < 1; i++)
			items = processor.process("1934391.nxml", fileType);
		*/
		
		System.out.println((System.currentTimeMillis() - t));

		items = processor.process("1718282.txt", GNProcessor.FileType.PLAIN);

		processor.close();

		for (GNResultItem item : items){
			for (String term : item.getGeneMentionList()){
				//term = term.replace("\\/", "/");
				System.out.println(term);
				//item.getGeneMentionList();
				
				ArrayList<Mention> gms = null;
			}
		}
		
		
	//	GeneTUKitWrapper.CKTest();
	}
}
