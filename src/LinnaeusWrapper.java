import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import martin.common.ArgParser;

import org.apache.commons.lang.StringUtils;

import uk.ac.man.entitytagger.EntityTagger;
import uk.ac.man.entitytagger.Mention;
import uk.ac.man.textpipe.Annotator;



public class LinnaeusWrapper extends Annotator{
	protected uk.ac.man.entitytagger.matching.Matcher linnaeusMatcher = null;
	protected Logger l = null;
	
	@Override
	public void init(Map<String, String> data) {
		if (data.size() > 0){
			String dictFile = data.get("dict");
			
			if (dictFile != null){
				l = Logger.getAnonymousLogger();
				String[] args_fake= {"--variantMatcher", dictFile, "--ignoreCase", "true"};
				ArgParser ap = new ArgParser(args_fake);
				linnaeusMatcher = EntityTagger.getMatcher(ap, l);
			}
		}

		if (linnaeusMatcher == null){
			System.err.println("Dictionary file not specified. LINNEAUS not initialized.");
			System.exit(-1);
		}
	}

	@Override
	public String[] getOutputFields() {
		return new String[]{"id","entity_id", "entity_start","entity_end","entity_term"};
	}
	
	@Override
	public List<Map<String, String>> process(Map<String, String> data) {
		String text = data.get("doc_text");
		String doc_id = data.get("doc_id");
		HashMap<String, Mention> entities = new HashMap<String, Mention>();
		
		System.out.println("Processing doc#" + doc_id);

		List<Mention> matchResult = linnaeusMatcher.match(text);

		return toMaps(matchResult);		
	}
	
	/**
	 * Converts a list of mentions to textpipe-style output maps.
	 * @param mentions
	 * @param geneIdToSpecies a map of gene -> species links
	 * @return a list of maps containing key/value pairs describing the mentions
	 */
	private List<Map<String, String>> toMaps(List<Mention> mentions) {
		List<Map<String,String>> res = new ArrayList<Map<String,String>>();
		int i = 0;
		for (Mention m : mentions){
			Map<String,String> map = new HashMap<String,String>();

			map.put("id", ""+i++);
			map.put("entity_id", StringUtils.join(m.getIds(), '|'));
			map.put("entity_start", ""+ m.getStart());
			map.put("entity_end", ""+ m.getEnd());
			map.put("entity_term", ""+ m.getText());

			res.add(map);
			
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
