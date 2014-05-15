package moara.normalization.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import moara.bio.entities.Organism;
import moara.gene.functions.GeneSynonymAmbiguity;
import moara.mention.entities.GeneMention;
import moara.mention.entities.Mention;
import moara.normalization.NormalizationConstant;
import moara.normalization.entities.GenePrediction;

public abstract class GeneNormalization {

	private int disambiguationMeasure;
	private int disambiguationSelection;
	protected Organism organism;
	public GeneSynonymAmbiguity gsa;
	private boolean variablesStarted;
	public GeneSynonymSelection gss;
		
	public GeneNormalization(Organism organism) {
		this.disambiguationMeasure = NormalizationConstant.DEFAULT_DISAMBIGUATION_TYPE;
		this.disambiguationSelection = NormalizationConstant.DEFAULT_DISAMBIGUATION_SELECTION;
		this.variablesStarted = false;
		this.gss = new GeneSynonymSelection();
		this.organism = organism;
	}
	
	protected abstract void init();
	
	protected abstract HashMap<String,GenePrediction> normalizeMention(String mention);
	
	public void useDisambiguationMeasure(int measure) {
		this.disambiguationMeasure = measure;
	}
	
	public void useDisambiguationSelection(int selection) {
		this.disambiguationSelection = selection;
	}
	
	private void startVariables() {
		if (!this.variablesStarted) {
			init();
			this.variablesStarted = true;
		}
	}
	
	public ArrayList<GeneMention> normalize(String text, ArrayList<GeneMention> mentions) {
		startVariables();
		this.gsa = new GeneSynonymAmbiguity(this.disambiguationMeasure,
			this.disambiguationSelection,this.organism);
		// edit mentions
		//editMentions(mentions,this.organism);
		// search genes
		//Iterator<String> iter1 = this.flexibleMentions.keySet().iterator();
		//while (iter1.hasNext()) {
			//String key = iter1.next();
			//GeneMention mention = this.flexibleMentions.get(key);
		for (int i=0; i<mentions.size(); i++) {
			GeneMention mention = mentions.get(i);
			HashMap<String,GenePrediction> predicted = new HashMap<String,GenePrediction>();
			
			if (this.disambiguationMeasure == NormalizationConstant.DISAMBIGUATION_SOFT_TFIDF){
				//predicted.putAll(normalizeMention(mention.Text()));
				predicted = normalizeMention(mention.Text());
			}else{
				ArrayList<String> variations = getFlexibleMentions(mention.Text());
				//System.out.println("----> " + mention.Text());
				for (int j=0; j<variations.size(); j++) {
					String variation = variations.get(j);
					//System.out.println(variation);
					predicted.putAll(normalizeMention(variation));
					//System.err.println(mention.Text() + " / " + predicted.size());
				}
			}
			
			// set type prediction
			String type;
			if (predicted.size()==1)
				type = NormalizationConstant.PREDICTION_UNIQUE;
			else
				type = NormalizationConstant.PREDICTION_AMBIGUOUS;
			Iterator<GenePrediction> iter = predicted.values().iterator();
			while (iter.hasNext()) {
				GenePrediction gp = iter.next();
				gp.setType(type);
				
				if (type.equals(NormalizationConstant.PREDICTION_UNIQUE))
					mention.setGeneId(gp);
				mention.addGenePrediction(gp);
			}
			// ambiguity
			if (this.disambiguationMeasure!=NormalizationConstant.DISAMBIGUATION_NONE )//&& 
					//mention.GeneIds().size()>1) //Chengkun
				resolveAmbiguity(text,mention);
		}
		//closeDatabase();
		/*mentions.clear();
		Iterator<String> iter3 = this.flexibleMentions.keySet().iterator();
		while (iter3.hasNext()) {
			String key = iter3.next();
			GeneMention mention = this.flexibleMentions.get(key);
			mentions.add(mention);
		}
		mentions.trimToSize();*/
		return mentions;
	}
	
	public void resolveAmbiguity(String text, GeneMention mention) {
		this.gsa.resolveAmbiguity(text,mention.GeneIds());

		// get chosen
		for (int i=0; i<mention.GeneIds().size(); i++) {
			if (mention.GeneIds().get(i).Chosen())
				mention.setGeneId(mention.GeneIds().get(i));
		}
	}
	
	public ArrayList<String> getFlexibleMentions(String mention) {
		return this.gss.generateSynonyms(this.organism,mention);
		
	}
	
	/*public ArrayList<GeneMention> getFlexibleMentions(GeneMention gm, String organism) {
		ArrayList<GeneMention> newMentions = new ArrayList<GeneMention>();
		StringUtil su = new StringUtil();
		for (int k=0; k<NormalizationConstant.thresholds.length; k++) {
			int threshold = NormalizationConstant.thresholds[k];
			GeneSynonymSelection gss = new GeneSynonymSelection(threshold);
			String original = gm.Text().toLowerCase().trim();
			String ordered = su.orderAlphabetically(original,true,true,true,true);
			// save original mention
			if (gss.checkGeneSynonym(ordered,false)) {
				if (!this.flexibleMentions.containsKey(ordered)) {
					GeneMention mention = new GeneMention(ordered,gm.Start(),gm.End());
					this.flexibleMentions.put(ordered,mention);
					newMentions.add(mention);
				}
			}
			// clean mention
			ArrayList<PartSynonym> parts = gss.cleanAndSeparate(organism,original,
				gm.Start(),gm.End());
			//System.err.println(parts.size());
			if (parts.size()>0) {
				boolean isPart = false;
				if (!parts.get(0).Part().equals(original))
					isPart = true;
				// save original cleaned mention
				String cleaned = su.orderAlphabetically(parts.get(0).Part(),true,true,true,true);
				if (gss.checkGeneSynonym(cleaned,isPart)) {
					if (!this.flexibleMentions.containsKey(cleaned)) {
						GeneMention mention = new GeneMention(cleaned,gm.Start(),gm.End());
						this.flexibleMentions.put(cleaned,mention);
						newMentions.add(mention);
					}
				}
				for (int j=1; j<parts.size(); j++) {
					PartSynonym part = parts.get(j);
					String partString = su.orderAlphabetically(part.Part(),true,true,true,true);
					if (!this.flexibleMentions.containsKey(partString) && 
							gss.checkGeneSynonym(partString,true)) {
						//System.err.println("**part..." + part.Part());
						GeneMention mention = new GeneMention(partString,part.Start(),part.End());
						this.flexibleMentions.put(partString,mention);
						newMentions.add(mention);
					}
				}
			}
		}
		if (this.orderMentions)
			newMentions = orderMentions(newMentions);
		return newMentions;
	}
	
	private void editMentions(ArrayList<GeneMention> mentions,
			String organism) {
		this.orderMentions = false;
		for (int i=0; i<mentions.size(); i++) {
			GeneMention gm = mentions.get(i);
			getFlexibleMentions(gm,organism);
		}
		this.orderMentions = true;
	}
	
	private ArrayList<GeneMention> orderMentions(ArrayList<GeneMention> mentions) {
		ArrayList<GeneMention> ordered = new ArrayList<GeneMention>();
		for (int i=0; i<mentions.size(); i++) {
			GeneMention gm1 = mentions.get(i);
			if (ordered.size()==0)
				ordered.add(gm1);
			else {
				boolean found = false;
				for (int j=0; j<ordered.size(); j++) {
					GeneMention gm2 = mentions.get(i);
					if (gm1.Start()<gm2.Start()) {
						ordered.add(j,gm2);
						found = true;
					}
					else if (gm1.Start()==gm2.Start()) {
						if (gm1.End()<gm2.End()) {
							ordered.add(j,gm2);
							found = true;
						}
					}
					if (found)
						break;
				}
				if (!found)
					ordered.add(gm1);
			}
		}
		return ordered;
	}*/
	
	/* LAST VERSION
	private HashMap<String,GeneMention> editMentions(ArrayList<GeneMention> mentions,
			String organism) {
		HashMap<String,GeneMention> newMentions = new HashMap<String,GeneMention>();
		StringUtil su = new StringUtil();
		for (int i=0; i<mentions.size(); i++) {
			GeneMention gm = mentions.get(i);
			for (int k=0; k<NormalizationConstant.thresholds.length; k++) {
				int threshold = NormalizationConstant.thresholds[k];
				GeneSynonymSelection gss = new GeneSynonymSelection(this.dbMoaraGene,this.dbMoara,threshold);
				String original = gm.Text().toLowerCase().trim();
				String ordered = su.orderAlphabetically(original,true,true,true,true);
				// save original mention
				if (gss.checkGeneSynonym(ordered,false))
					if (!newMentions.containsKey(ordered))
						newMentions.put(ordered,new GeneMention(ordered,gm.Start(),gm.End()));
				// clean mention
				//System.err.println(original);
				//String separated = su.orderAlphabetically(original,true,true,true,true);
				//System.err.println("* " + separated);
				ArrayList<PartSynonym> parts = gss.cleanAndSeparate(organism,ordered,
					gm.Start(),gm.End());
				//System.err.println(parts.size());
				if (parts.size()>0) {
					boolean isPart = false;
					if (!parts.get(0).Part().equals(ordered))
						isPart = true;
					// save original cleaned mention
					String cleaned = su.orderAlphabetically(parts.get(0).Part(),true,true,true,true);
					if (gss.checkGeneSynonym(cleaned,isPart))
						if (!newMentions.containsKey(cleaned))
							newMentions.put(cleaned,new GeneMention(cleaned,gm.Start(),gm.End()));
					for (int j=1; j<parts.size(); j++) {
						PartSynonym part = parts.get(j);
						String partString = su.orderAlphabetically(part.Part(),true,true,true,true);
						if (!newMentions.containsKey(partString) && 
								gss.checkGeneSynonym(partString,true)) {
							//System.err.println("**part..." + part.Part());
							newMentions.put(partString,new GeneMention(partString,part.Start(),part.End()));
						}
					}
				}
			}
		}
		return newMentions;
	}*/
	
	/*private HashMap<String,GeneMention> editMentions(ArrayList<GeneMention> mentions,
			String organism) {
		GeneSynonymSelection gss = new GeneSynonymSelection(this.db,this.dbMoara);
		HashMap<String,GeneMention> newMentions = new HashMap<String,GeneMention>();
		//ArrayList<String> newStrings = new ArrayList<String>();
		StringUtil su = new StringUtil();
		for (int i=0; i<mentions.size(); i++) {
			GeneMention gm = mentions.get(i);
			String original = gm.Text().toLowerCase().trim();
			String ordered = su.orderAlphabetically(original,true,true,true,true);
			// save original mention
			if (gss.checkGeneSynonym(ordered,organism,false))
				if (!newMentions.containsKey(ordered))
					newMentions.put(ordered,new GeneMention(ordered,gm.Start(),gm.End()));
			// clean mention
			ArrayList<PartSynonym> parts = gss.cleanMention(ordered,gm.Start(),gm.End());
			// save original cleaned mention
			String cleaned = su.orderAlphabetically(gss.getCleanSynonym(parts),true,true,true,true);
			if (gss.checkGeneSynonym(cleaned,organism,false))
				if (!newMentions.containsKey(cleaned))
					newMentions.put(cleaned,new GeneMention(cleaned,gm.Start(),gm.End()));
			int index = 0;
			PartSynonym ps = parts.get(index);
			while (ps!=null) {
				parts.remove(index);
				ArrayList<PartSynonym> newParts = gss.separatePartsSynonym(ps.Part(),ps.Start(),ps.End());
				parts.addAll(index,newParts);
				index += newParts.size();
				if (index<parts.size())
					ps = parts.get(index);
				else
					ps = null;
			}
			parts.trimToSize();
			for (int j=0; j<parts.size(); j++) {
				PartSynonym part = parts.get(j);
				String partString = su.orderAlphabetically(part.Part(),true,true,true,true);
				//System.err.println("***part " + part.Part() + " ... " + gss.checkGeneSynonym(part.Part(),organism));
				if (gss.checkGeneSynonym(partString,organism,true) && !newMentions.containsKey(partString)) {
					newMentions.put(partString,new GeneMention(partString,part.Start(),part.End()));
					//newStrings.add(part.Part());
				}
			}
		}
		return newMentions;
	}*/
	
}

