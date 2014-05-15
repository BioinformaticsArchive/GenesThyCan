import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moara.mention.entities.Mention;
import moara.normalization.entities.GenePrediction;
import moara.util.text.StringUtil;
import uk.ac.man.PathNER.PathNER;
import uk.ac.man.PathNER.PathNER.MergedMatch;
import uk.ac.man.textpipe.Annotator;

/**
 * Wrapper for the PathNER : a biological pathway NER tool.
 * @author Martin
 */
public class PathNERWrapper extends Annotator {
	private boolean verbose = true;
	private PathNER pathNER = null;
	
	@Override
	public void init(Map<String, String> data) {
		if (pathNER == null){
			pathNER = new PathNER();
			
			try {
				pathNER.initGate();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("PathNER annotator successfully initialized!");
		}
	}

	@Override
	public String[] getOutputFields() {
		return new String[]{"entity_id", "entity_start","entity_end","entity_term","org_text", "entity_source", "confidence"};
	}
	
	@Override
	public List<Map<String, String>> process(Map<String, String> data) {
		String text = data.get("doc_text");
		String doc_id = data.get("doc_id");
	
		System.out.println("Processing doc#" + doc_id);
		
		List<MergedMatch> pwMentions = new ArrayList<MergedMatch>();
		pwMentions = pathNER.testOnTextStr(text);
		
		return toMaps(pwMentions);		
	}
	
	/**
	 * Converts a list of mentions to textpipe-style output maps.
	 * @param mentions
	 * @param document id
	 * @return a list of maps containing key/value pairs describing the mentions
	 */
	private List<Map<String, String>> toMaps(List<MergedMatch> mentions) {
		List<Map<String,String>> res = new ArrayList<Map<String,String>>();
		int i = 0;
		for (MergedMatch m : mentions){
			Map<String,String> map = new HashMap<String,String>();

			
			String srcDB = null;
			String dbEntryId = null;
			String orgTerm = null;
			
			if (m.entry != null){
				m.entry = m.entry.substring(1, m.entry.length() -1 );
				String parts[] = m.entry.split(":");
				
				srcDB = parts[0];
				
				
				dbEntryId = parts[1];
				
				if (parts.length > 3){
					dbEntryId += ":" + parts[2];
					orgTerm = parts[3];
				}else
					orgTerm = parts[2];
			}
			
			map.put("entity_id", dbEntryId);
			map.put("entity_start", "" + m.startOffset);
			map.put("entity_end", "" + m.endOffset);
			map.put("entity_term", orgTerm);
			map.put("org_text", m.matchedStr);
			map.put("entity_source", srcDB);
			map.put("confidence", ""+m.score);
			
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
