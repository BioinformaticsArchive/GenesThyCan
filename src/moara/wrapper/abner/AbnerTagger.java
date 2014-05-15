package moara.wrapper.abner;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.File;
import moara.mention.entities.Mention;
import moara.mention.MentionConstant;
import moara.wrapper.WrapperConstant;
import moara.util.Constant;
import abner.Tagger;

// Abner version 1.5, March 2005

public class AbnerTagger {

	private Tagger abner;
	
	public AbnerTagger(int corpus) {
		this.abner = decideTagger(corpus);
	}
	
	public ArrayList<Mention> extract(String text) {
		String result = this.abner.tagABNER(text);
		//System.out.println(result);
		String mention = "";
		String tagMention = "";
		int start = 0;
		int end = 0;
		int startMention = 0;
		StringTokenizer tokens = new StringTokenizer(result);
		ArrayList<Mention> mentions = new ArrayList<Mention>(tokens.countTokens());
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			StringTokenizer pair = new StringTokenizer(token,"|");
			String word = pair.nextToken();
			String tag = pair.nextToken();
			if (tag.startsWith("O")) {
				if (mention.length()>0) {
					Mention gm = new Mention(mention.trim(),
						MentionConstant.MENTION_COMPLETE,startMention,end,
						Constant.CATEGORY_TRUE);
					mentions.add(gm);
				}
				mention = "";
				tagMention = "";
				startMention = 0;
			}
			else if (tag.startsWith("B") || tag.startsWith("I")) {
				if (mention.length()==0)
					startMention = start;
				mention += " " + word;
				tagMention = tag;
			}
			end = start + word.length() - 1;
			start = end + 1;
		}
		if (mention.length()>0) {
			Mention gm = new Mention(mention,MentionConstant.MENTION_COMPLETE,
				start,end,tagMention);
			mentions.add(gm);
		}
		mentions.trimToSize();
		return mentions;
	}
	
	private Tagger decideTagger(int corpus) {
		Tagger abner = null;
		if (corpus==WrapperConstant.ABNER_BIOCREATIVE)
			abner = new Tagger(Tagger.BIOCREATIVE);
		else if (corpus==WrapperConstant.ABNER_NLPBA)
			abner = new Tagger(Tagger.NLPBA);
		else if (corpus==WrapperConstant.ABNER_BC2)
			abner = new Tagger(new File(WrapperConstant.MODEL_ABNER_BC2));
		else if (corpus==WrapperConstant.ABNER_BC2_YEAST)
			abner = new Tagger(new File(WrapperConstant.MODEL_ABNER_BC2_YEAST));
		else if (corpus==WrapperConstant.ABNER_BC2_MOUSE)
			abner = new Tagger(new File(WrapperConstant.MODEL_ABNER_BC2_MOUSE));
		else if (corpus==WrapperConstant.ABNER_BC2_FLY)
			abner = new Tagger(new File(WrapperConstant.MODEL_ABNER_BC2_FLY));
		else if (corpus==WrapperConstant.ABNER_BC2_ALL)
			abner = new Tagger(new File(WrapperConstant.MODEL_ABNER_BC2_ALL));
		return abner;
	}
	
	public static void main(String[] args) {
		String text = "A gene (pkt1) was isolated from the filamentous fungus " +
			"Trichoderma reesei, which exhibits high homology with the yeast YPK1 " +
			"and YKR2 (YPK2) genes. It contains a 2123-bp ORF that is interrupted by " +
			"two introns, and it encodes a 662-amino-acid protein with a calculated " +
			"M(r) of 72,820. During active growth,pkt1 is expressed as two mRNAs of " +
			"3.1 and 2.8 kb which differ in the 3' untranslated region due to the use " +
			"of two different polyadenylation sites.";
		AbnerTagger abner = new AbnerTagger(WrapperConstant.ABNER_BC2);
		ArrayList<Mention> gms = abner.extract(text);
		for (int i=0; i<gms.size(); i++) {
			Mention gm = gms.get(i);
			System.out.println(gm.Start() + "\t" + gm.End() + "\t" + gm.Text() + "\t");
		}
			
	}
	
}
