package moara.normalization.functions;

import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import moara.bio.entities.Organism;
import moara.bio.entities.OrganismSynonym;
import moara.gene.dbs.DBGeneSynonym;
import moara.gene.entities.GeneSynonym;
import moara.bio.BioConstant;
import moara.util.Constant;
import moara.util.lexicon.BioWords;
import moara.util.lexicon.GreekLetter;
import moara.util.lexicon.Stopwords;
import moara.util.lexicon.AminoAcid;
import moara.util.text.StringUtil;
import moara.normalization.NormalizationConstant;

public class GeneSynonymSelection {

	private BioWords bio;
	private Stopwords stop;
	private OrganismSynonym org;
	
	public GeneSynonymSelection() {
		this.bio = new BioWords(NormalizationConstant.THRESHOLD_BIOWORDS,
			NormalizationConstant.THRESHOLD_BIOWORDS);
		this.stop = new Stopwords();
		this.org = new OrganismSynonym();
	}
	
	public GeneSynonymSelection(int threshold) {
		this.bio = new BioWords(NormalizationConstant.THRESHOLD_BIOWORDS,threshold);
		this.stop = new Stopwords();
		this.org = new OrganismSynonym();
	}
	
	public boolean checkGeneSynonym(String synonym, boolean isPart) {
		// check valid synonym
		if (!isValidSynonym(synonym,isPart))
			return false;
		// check length of synonym
		if (synonym.length()<0 || synonym.length()>100)
			return false;
		return true;
	}
	
	private boolean isValidSynonym(String synonym, boolean isPart) {
		StringUtil su = new StringUtil();
		GreekLetter greek = new GreekLetter();
		AminoAcid aa = new AminoAcid();
		String w = synonym.toLowerCase();
		// more than length 1
		if (w.length()<2)
			return false;
		if (isPart && su.getLengthWithoutSpace(w)<=3)
			return false;
		// have letters, not only numbers and symbols
		if (!su.hasLetter(w))
			return false;
		// only Roman numerals
		if (su.isRomanNumeral(w))
			return false;
		// format "-a"
		if (w.length()==2 && w.startsWith("-"))
			return false;
		// stopwords
		if (this.stop.isStopword(w))
			return false;
		// greek letters or "-"+greek letters
		if (greek.isGreekLetter(w) || 
			(greek.isGreekLetter(w.substring(1)) && w.substring(0,1).equals("-")))
				return false;
		// biowords
		if (this.bio.isBioWord(w))
			return false;
		if (w.endsWith("s") && this.bio.isBioWord(w.substring(0,w.length()-1)))
			return false;
		// amino acids
		if (aa.isAminoAcidName(w))
			return false;
		// organism
		if (this.org.isOrganism(w))
			return false;
		return true;
	}
	
	/*public ArrayList<PartSynonym> cleanAndSeparate(Organism organism, String synonym, 
			int start, int end) {
		ArrayList<PartSynonym> parts = new ArrayList<PartSynonym>();
		String complete = "";
		int startPart = start;
		//System.err.println("**"+synonym);
		//StringUtil su = new StringUtil();
		//String separated = su.orderAlphabetically(synonym,false,true,true,false);
		StringTokenizer tokens = new StringTokenizer(synonym," ,.';\\/()[]",true);
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			String delim = "";
			//System.err.println("1)"+token);
			if (tokens.hasMoreTokens())
				delim = tokens.nextToken();
			// check stopword
			if (this.stop.isStopword(token))
				continue;
			// check bio words
			if (this.bio.isBioWordClean(token))
				continue;
			// check same organism
			if (this.org.isOrganism(token) && this.org.isOrganismSynonym(organism,token))
				continue;
			// check other organism
			if (this.org.isOrganism(token) && !this.org.isOrganismSynonym(organism,token)) {
				parts = new ArrayList<PartSynonym>();
				return parts;
			}
			//System.err.println("2)"+token);
			parts.add(new PartSynonym(startPart,startPart+token.length()-1,token));
			complete += token + " " + delim;
			startPart += token.length();
			if (delim.trim().length()!=0)
				startPart += delim.trim().length();
		}
		complete = complete.trim();
		parts.add(0,new PartSynonym(start,end,complete));
		parts.trimToSize();
		//System.err.println(synonym + " -> " + complete + " : " + parts.size());
		return parts;
	}*/
	
	/*public ArrayList<PartSynonym> separatePartsSynonym(String synonym, int start, int end) {
		ArrayList<PartSynonym> parts = new ArrayList<PartSynonym>();
		int startIndex = 0;
		StringTokenizer tokens = new StringTokenizer(synonym," -/");
		while (tokens.hasMoreTokens()) {
			String word = tokens.nextToken();
			int index = synonym.indexOf(word,startIndex);
			parts.add(new PartSynonym(start+index,start+index+word.length()-1,word));
			startIndex += word.length();
		}
		parts.trimToSize();
		return parts;
	}
	
	public ArrayList<PartSynonym> cleanMention(String synonym, int start, int end) {
		ArrayList<PartSynonym> parts = new ArrayList<PartSynonym>();
		parts.add(new PartSynonym(start,end,synonym));
		parts = getPartsSynonym(parts,0);
		return parts;
	}
	
	public String getCleanSynonym(ArrayList<PartSynonym> parts) {
		String cleaned = "";
		for (int i=0; i<parts.size(); i++) {
			cleaned += " " + parts.get(i).Part();
		}
		return cleaned.trim();
	}
	
	private ArrayList<PartSynonym> getPartsSynonym(ArrayList<PartSynonym> parts, int id) {
		id++;
		//Organism org = new Organism(this.dbMoara);
		BioWords bio = new BioWords();
		//ArrayList<String> lexicon = org.Synonyms();
		ArrayList<String> lexicon = new ArrayList<String>();
		lexicon.addAll(bio.getBioWords());
		lexicon.trimToSize();
		//System.err.println("parts=" + parts.size());
		boolean newLoop = false;
		for (int j=0; j<parts.size(); j++) {
			PartSynonym ps = parts.get(j);
			//System.err.println("**" + id + "**" + ps.Part() + "-" + ps.Start() + "-" + ps.End());
			for (int i=0; i<lexicon.size(); i++) {
				int index = ps.Part().indexOf(lexicon.get(i));
				if (index>=0) {
					//System.err.println("index=" + index + "-" + lexicon.get(i));
					// delete original part
					parts.remove(j);
					// save part 2, if any
					if ((ps.Start()+index)<ps.End()) {
						PartSynonym ps2 = new PartSynonym(ps.Start()+index+lexicon.get(i).length(),
							ps.End(),ps.Part().substring(index+lexicon.get(i).length(),
							ps.Part().length()));
						parts.add(j,ps2);
						//System.err.println("2)" + ps2.Part() + "-" + ps2.Start() + "-" + ps2.End());
					}
					// save part 1, if any
					if ((ps.Start()+index)>ps.Start()) {
						PartSynonym ps1 = new PartSynonym(ps.Start(),ps.Start()+index-1,
							ps.Part().substring(0,index));
						parts.add(j,ps1);
						//System.err.println("1)" + ps1.Part() + "-" + ps1.Start() + "-" + ps1.End());
					}
					newLoop = true;
					break;
				}
				if (newLoop)
					break;
			}
		}
		if (newLoop)
			parts = getPartsSynonym(parts,id);
		parts.trimToSize();
		return parts;
	}*/
	
	/*ORIGINAL!!!!! public void orderGeneSynonyms(String organism) {
		try {
			DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(this.dbMoara,organism);
			dbGeneSynonym.deleteGeneSynonymType(BioConstant.SYNONYM_ORDERED);
			ArrayList<GeneSynonym> synonyms = dbGeneSynonym.getSynonymsOrganismType(
					BioConstant.SYNONYM_LIST);
			StringUtil su = new StringUtil();
			for (int i=0; i<synonyms.size(); i++) {
				GeneSynonym gs = synonyms.get(i);
				String original = gs.Synonym().trim();
				String synonym = gs.Synonym().trim().toLowerCase();
				// separate parts
				ArrayList<String> parentesis = separateParenthesis(synonym);
				synonym = "";
				for (int j=0; j<parentesis.size(); j++)
					synonym += parentesis.get(j) + " ";
				synonym = synonym.trim();
				String ordered = su.orderAlphabetically(synonym,true,true,true,true);
				if (this.checkGeneSynonym(ordered,organism,false))
					dbGeneSynonym.insertGeneSynonymType(gs.Id(),BioConstant.SYNONYM_ORDERED,
						ordered,original);
				for (int j=0; j<parentesis.size(); j++) {
					synonym = parentesis.get(j);
					ordered = su.orderAlphabetically(synonym,true,true,true,true);
					if (this.checkGeneSynonym(ordered,organism,true))
						dbGeneSynonym.insertGeneSynonymType(gs.Id(),
							BioConstant.SYNONYM_ORDERED,ordered,original);
					// clean synonym
					String separated = su.orderAlphabetically(synonym,true,true,true,false);
					ArrayList<PartSynonym> parts = this.cleanAndSeparate(organism,separated,
						0,ordered.length()-1);
					// save original cleaned mention
					if (parts.size()>0) {
						String cleaned = su.orderAlphabetically(parts.get(0).Part(),true,true,true,true);
						if (cleaned.length()>0 && !cleaned.equals(ordered) &&
								this.checkGeneSynonym(cleaned,organism,true))
							dbGeneSynonym.insertGeneSynonymType(gs.Id(),
								BioConstant.SYNONYM_ORDERED,cleaned,original);
					}
				}
				if (((i+1)%500)==0) {
					System.err.print("synm-order..." + organism + "..." + (i+1) + "...");
					this.dbMoara.executeCommit();
				}
			}
			this.dbMoara.executeCommit();
		}
		catch(SQLException ex) { 
			System.err.println(ex);
			this.dbMoara.executeRollback();
		}
	}*/
	
	public void orderGeneSynonyms(Organism organism) {
		DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(organism);
		try {
			dbGeneSynonym.deleteGeneSynonymType(BioConstant.SYNONYM_ORDERED);
			ArrayList<GeneSynonym> synonyms = dbGeneSynonym.getSynonymsOrganismType(
				BioConstant.SYNONYM_LIST);
			for (int i=0; i<synonyms.size(); i++) {
				GeneSynonym gs = synonyms.get(i);
				String original = gs.Synonym().trim();
				ArrayList<String> list = generateSynonyms(organism,original);
				for (int j=0; j<list.size(); j++) {
					dbGeneSynonym.insertGeneSynonymType(gs.Id(),BioConstant.SYNONYM_ORDERED,
						list.get(j),original);
				}
				//if (((i+1)%500)==0) {
					System.err.println("synm-order..." + organism.Code() + "..." + (i+1) + "...");
					dbGeneSynonym.executeCommit();
				//}
			}
			dbGeneSynonym.executeCommit();
		}
		catch(SQLException ex) { 
			System.err.println(ex);
			dbGeneSynonym.executeRollback();
		}
	}
	
	public ArrayList<String> generateSynonyms(Organism organism, String s) {
		ArrayList<String> variations = new ArrayList<String>();
		ArrayList<String> originals = new ArrayList<String>();
		StringUtil su = new StringUtil();
		// complete
		String synonym = s.trim().toLowerCase();
		originals.add(synonym);
		// separate by parenthesis
		ArrayList<String> parentesis = separateParenthesis(synonym);
		for (int i=0; i<parentesis.size(); i++) {
			String part1 = parentesis.get(i);
			if (!originals.contains(part1))
				originals.add(part1);
			// separate by parts
			ArrayList<PartSynonym> parts = 
				this.separateParts(organism,part1,0,part1.length()-1);
			for (int j=0; j<parts.size(); j++) {
				String part2 = parts.get(j).Part();
				if (!originals.contains(part2))
					originals.add(part2);
			}
		}
		originals.trimToSize();
		// filtering
		for (int k=0; k<NormalizationConstant.thresholds.length; k++) {
			int threshold = NormalizationConstant.thresholds[k];
			this.bio.loadBioWords(NormalizationConstant.THRESHOLD_BIOWORDS,threshold);
			for (int i=0; i<originals.size(); i++) {
				String original = originals.get(i);
				String ordered = su.orderAlphabetically(original,true,true,true,true);
				if (this.checkGeneSynonym(ordered,false) && !variations.contains(ordered))
					variations.add(ordered);
				// filter stopwords
				String stopword = filterStopwords(ordered);
				if (this.checkGeneSynonym(stopword,false) && !variations.contains(stopword))
					variations.add(stopword);
				// filter biowords
				String bioword = filterBiowords(stopword);
				if (this.checkGeneSynonym(bioword,false) && !variations.contains(bioword))
					variations.add(bioword);
			}
			
		}
		this.bio.loadBioWords(NormalizationConstant.THRESHOLD_BIOWORDS,
				NormalizationConstant.THRESHOLD_BIOWORDS);
		variations.trimToSize();
		return variations;
	}
	
	private String filterStopwords(String synonym) {
		String filtered = "";
		StringTokenizer tokens = new StringTokenizer(synonym);
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			if (this.stop.isStopword(token))
				continue;
			filtered += token + " ";
		}
		return filtered.trim();
	}
	
	private String filterBiowords(String synonym) {
		String filtered = "";
		StringTokenizer tokens = new StringTokenizer(synonym);
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			if (this.bio.isBioWordClean(token))
				continue;
			filtered += token + " ";
		}
		return filtered.trim();
	}
	
	private ArrayList<PartSynonym> separateParts(Organism organism, String synonym, 
			int start, int end) {
		ArrayList<PartSynonym> parts = new ArrayList<PartSynonym>();
		int startPart = start;
		//System.err.println("**"+synonym);
		//StringUtil su = new StringUtil();
		//String separated = su.orderAlphabetically(synonym,false,true,true,false);
		StringTokenizer tokens = new StringTokenizer(synonym," ,.';\\/()[]",true);
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			String delim = "";
			//System.err.println("1)"+token);
			if (tokens.hasMoreTokens())
				delim = tokens.nextToken();
			// check stopword
			if (this.stop.isStopword(token))
				continue;
			// check bio words
			if (this.bio.isBioWordClean(token))
				continue;
			// check same organism
			if (this.org.isOrganism(token) && this.org.isOrganismSynonym(organism,token))
				continue;
			// check other organism
			if (this.org.isOrganism(token) && !this.org.isOrganismSynonym(organism,token)) {
				parts = new ArrayList<PartSynonym>();
				return parts;
			}
			//System.err.println("2)"+token);
			parts.add(new PartSynonym(startPart,startPart+token.length()-1,token));
			startPart += token.length();
			if (delim.trim().length()!=0)
				startPart += delim.trim().length();
		}
		parts.trimToSize();
		//System.err.println(synonym + " -> " + complete + " : " + parts.size());
		return parts;
	}
	
	private ArrayList<String> separateParenthesis(String original) {
		StringUtil su = new StringUtil();
		ArrayList<String> parts = new ArrayList<String>();
		String temp = original;
		int open = original.indexOf("(");
		int close = original.indexOf(")",open);
		while (open>=0 && close>=0 && close>open) {
			String before = temp.substring(0,open).trim();
			String middle = temp.substring(open+1,close).trim();
			String after = temp.substring(close+1,temp.length()).trim();
			String out = before + " " + after;
			if (su.hasLetter(middle))
				parts.add(middle);
			open = out.indexOf("(");
			close = out.indexOf(")",open);
			temp = out;
			if (open==-1 || close==-1) {
				parts.add(out);
				break;
			}
		}
		if (parts.size()==0)
			parts.add(original);
		parts.trimToSize();
		return parts;
	}
	
	/*public ArrayList<String> generateSynonyms(Organism organism, String s) {
		ArrayList<String> list = new ArrayList<String>();
		StringUtil su = new StringUtil();
		String synonym = s.trim().toLowerCase();
		// separate parts
		ArrayList<String> parentesis = separateParenthesis(synonym);
		synonym = "";
		for (int j=0; j<parentesis.size(); j++)
			synonym += parentesis.get(j) + " ";
		synonym = synonym.trim();
		String ordered = su.orderAlphabetically(synonym,true,true,true,true);
		if (this.checkGeneSynonym(ordered,false) && !list.contains(ordered))
			list.add(ordered);
		for (int j=0; j<parentesis.size(); j++) {
			synonym = parentesis.get(j);
			ordered = su.orderAlphabetically(synonym,true,true,true,true);
			if (this.checkGeneSynonym(ordered,true) && !list.contains(ordered))
				list.add(ordered);
			// clean synonym
			String separated = su.orderAlphabetically(synonym,true,true,true,false);
			for (int k=0; k<NormalizationConstant.thresholds.length; k++) {
				int threshold = NormalizationConstant.thresholds[k];
				this.bio.loadBioWords(NormalizationConstant.THRESHOLD_BIOWORDS,threshold);
				//this.bio = new BioWords(NormalizationConstant.THRESHOLD_BIOWORDS,threshold);
				ArrayList<PartSynonym> parts = this.cleanAndSeparate(organism,separated,0,ordered.length()-1);
				// save original cleaned mention
				if (parts.size()>0) {
					String cleaned = su.orderAlphabetically(parts.get(0).Part(),true,true,true,true);
					if (cleaned.length()>0 && !cleaned.equals(ordered) &&
							this.checkGeneSynonym(cleaned,true) &&
							!list.contains(ordered))
						list.add(cleaned);
				}
			}
		}
		list.trimToSize();
		return list;
	}*/
	
	public class PartSynonym {
		
		private int start;
		private int end;
		private String part;
		
		public PartSynonym(int start, int end, String part) {
			this.start = start;
			this.end = end;
			this.part = part;
		}
		
		public int Start() {
			return this.start;
		}
		
		public int End() {
			return this.end;
		}
		
		public String Part() {
			return this.part;
		}
		
	}
	
	public static void main(String[] args) {
		GeneSynonymSelection app = new GeneSynonymSelection();
		//System.err.println(app.checkGeneSynonym("dna","4932",false));
		
		//app.orderGeneSynonyms(Constant.ORGANISM_YEAST);
		
		/*ArrayList<String> par = app.separateParenthesis("abc (def) ghi (jkl) mno");
		for (int i=0; i<par.size(); i++)
			System.out.println(par.get(i));*/
		
		ArrayList<String> list = app.generateSynonyms(new Organism(Constant.ORGANISM_YEAST),
			"Hac1 p");
		for (int i=0; i<list.size(); i++) {
			System.err.println(list.get(i));
		}
		
		/*ArrayList<PartSynonym> parts = app.cleanAndSeparate(Constant.ORGANISM_YEAST,
			"delta yku70",0,15);
		for (int i=0; i<parts.size(); i++) {
			System.err.println(parts.get(i).Part() + " " + parts.get(i).Start() + 
				" " + parts.get(i).End());
		}*/
	}
	
}




