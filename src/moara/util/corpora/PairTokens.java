package moara.util.corpora;

import java.util.HashMap;

import moara.util.data.PairItems;
import moara.tagger.basic.entities.FeatureArgument;

public class PairTokens implements PairItems, FeatureArgument {

	// direction from main token
	public static String DIRECTION_FORWARD = "F";
	public static String DIRECTION_BACKWARD = "B";
	
	private Token token1;
	private Token token2;
	private boolean sameSentence;
	private boolean sameChunkPhrase;
	private int distanceTokens;
	private String direction;
	// penntreebank parser
	private String commonAncestorTag;
	private int indexCommonAncestorTag;
	private String nextLevelParserTag1;
	private String nextLevelParserTag2;
	private boolean sameSTagSentence;
	private int distanceDepthParser;
	// dependency parser
	private String dependencyTag; // hyphen-separated
	private int distanceDependencyTag;
	
	public PairTokens() {
		init();
	}
	
	public PairTokens(Token t1, Token t2) {
		init();
		this.token1 = t1;
		this.token2 = t2;
		this.comparePairTokens(t1,t2);
	}
	
	private void init() {
		this.sameSentence = false;
		this.sameChunkPhrase = false;
		this.distanceTokens = -1;
		this.direction = null;
		this.commonAncestorTag = null;
		this.nextLevelParserTag1 = null;
		this.nextLevelParserTag2 = null;
		this.sameSTagSentence = false;
		this.dependencyTag = null;
		this.distanceDependencyTag = -1;
		this.distanceDepthParser = -1;
	}
	
	public Token Token1() {
		return this.token1;
	}
	
	public Token Token2() {
		return this.token2;
	}
	
	public boolean SameSentence() {
		return this.sameSentence;
	}
	
	public boolean SameChunkPhrase() {
		return this.sameChunkPhrase;
	}
	
	public int DistanceTokens(){
		return this.distanceTokens;
	}
	
	public String Direction() {
		return this.direction;
	}
	
	public String CommonAncestorTag() {
		return this.commonAncestorTag + "-" + this.indexCommonAncestorTag;
	}
	
	public int DistanceDepthParser() {
		return this.distanceDepthParser;
	}
	
	public String NextLevelParserTag1() {
		return this.nextLevelParserTag1;
	}
	
	public String NextLevelParserTag2() {
		return this.nextLevelParserTag2;
	}
	
	public boolean SameSTagSentence() {
		return this.sameSTagSentence;
	}
	
	public String DependencyTag() {
		return this.dependencyTag;
	}
	
	public int DistanceDependencyTag() {
		return this.distanceDependencyTag;
	}
	
	public void setDistanceToken1(Token t) {
		int distance = calculateDistanceTokens(this.token1,t);
		if (distance<this.distanceTokens)
			this.distanceTokens = distance; 
	}
	
	public void setDistanceToken2(Token t) {
		int distance = calculateDistanceTokens(t,this.token2);
		if (distance<this.distanceTokens)
			this.distanceTokens = distance; 
	}
	
	public void addDependencyTagToken1(Token t) {
		if (this.token1.Sentence().Sequential()==t.Sentence().Sequential()) {
			String tag = this.token1.Sentence().getDepTagTokens(this.token1,t);
			if (tag!=null) {
				if (this.dependencyTag==null) {
					this.dependencyTag = tag;
					this.distanceDependencyTag = t.Sentence().getDistanceDepTags(tag);
				}
				else {
					this.dependencyTag += "-" + tag;
					int distance = t.Sentence().getDistanceDepTags(tag);
					if (distance<this.distanceDependencyTag)
						this.distanceDependencyTag = distance;
				}
			}
		}
	}
	
	public void addDependencyTagToken2(Token t) {
		if (t.Sentence().Sequential()==this.token2.Sentence().Sequential()) {
			String tag = t.Sentence().getDepTagTokens(t,this.token2);
			if (tag!=null) {
				if (this.dependencyTag==null) {
					this.dependencyTag = tag;
					this.distanceDependencyTag = t.Sentence().getDistanceDepTags(tag);
				}
				else {
					this.dependencyTag += "-" + tag;
					int distance = t.Sentence().getDistanceDepTags(tag);
					if (distance<this.distanceDependencyTag)
						this.distanceDependencyTag = distance;
				}
			}
		}
	}
	
	public void setCommonAncestorTag(String tagNumber, int distance) {
		String tag = tagNumber.substring(0,tagNumber.indexOf("-"));
		int index = new Integer(tagNumber.substring(tagNumber.indexOf("-")+1));
		if (isPhraseLevel(tag) && isClauseLevel(this.commonAncestorTag)) {
			this.commonAncestorTag = tag;
			this.indexCommonAncestorTag = index;
		}
		else if ( (isPhraseLevel(tag) && isPhraseLevel(this.commonAncestorTag)) || 
				(isClauseLevel(tag) && isClauseLevel(this.commonAncestorTag)) ) {
			if (tag.equals(this.commonAncestorTag)) {
				if (index<this.indexCommonAncestorTag)
					this.indexCommonAncestorTag = index;
			}
			else {
				if (distance<this.distanceDepthParser) {
					this.commonAncestorTag = tag;
					this.indexCommonAncestorTag = index;
				}
			}
		}
	}
	
	public void setDistanceDepthParser(int distance) {
		if (distance<this.distanceDepthParser)
			this.distanceDepthParser = distance;
	}
	
	private boolean isClauseLevel(String tag) {
		for (int i=0; i<CorporaConstant.ClauseTags.length; i++) {
			if (CorporaConstant.ClauseTags[i].equals(tag))
				return true;
		}
		return false;
	}
	
	private boolean isPhraseLevel(String tag) {
		for (int i=0; i<CorporaConstant.PhraseTags.length; i++) {
			if (CorporaConstant.PhraseTags[i].equals(tag))
				return true;
		}
		return false;
	}
	
	private void comparePairTokens(Token t1, Token t2) {
		//System.out.println("t1=" + t1.TokenText() + " t2=" + t2.TokenText());
		init();
		// same sentence
		if (t1.Sentence().Sequential()!=t2.Sentence().Sequential())
			return;
		else
			this.sameSentence = true;
		// same chunk phrase
		if (t1.ChunkTag()!=null && t2.ChunkTag()!=null && t1.ChunkTag().SeqPhrase()==t2.ChunkTag().SeqPhrase())
			this.sameChunkPhrase = true;
		else
			this.sameChunkPhrase = false;
		// token distance and direction
		this.distanceTokens = calculateDistanceTokens(t1,t2);
		if (t1.Sequential()>t2.Sequential())
			this.direction = DIRECTION_BACKWARD;
		else
			this.direction = DIRECTION_FORWARD;
		// parsers
		PennTreebankItem nodes1 = t1.PtbParserItems();
		PennTreebankItem nodes2 = t2.PtbParserItems();
		comparePennTreebankItem(nodes1,nodes2);
		t1.setParserTag(this.nextLevelParserTag1);
		t2.setParserTag(this.nextLevelParserTag2);
		// dependency parser
		if (t1.Sentence().Sequential()==t2.Sentence().Sequential()) {
			this.dependencyTag = t1.Sentence().getDepTagTokens(t1,t2);
			this.distanceDependencyTag = t1.Sentence().getDistanceDepTags(this.dependencyTag);
		}
	}
	
	private int calculateDistanceTokens(Token t1, Token t2) {
		return Math.abs(t1.Sequential()-t2.Sequential());
	}
	
	private void comparePennTreebankItem(PennTreebankItem nodes1, PennTreebankItem nodes2) {
		HashMap<String,Integer> indexTags1 = new HashMap<String,Integer>();
		HashMap<String,Integer> indexTags2 = new HashMap<String,Integer>();
		//nodes1.print(); nodes2.print();
		this.sameSTagSentence = true;
		int totalNode1 = -1;
		int totalNode2 = -1;
		for_node1:
		for (int i=(nodes1.Nodes().size()-1); i>=0; i--) {
			PennTreebankItemNode node1 = nodes1.Nodes().elementAt(i);
			addIndexTag(indexTags1,node1.Tag());
			//System.out.print("node1="); node1.print();
			totalNode1++;
			totalNode2 = -1;
			for_node2:
			for (int j=(nodes2.Nodes().size()-1); j>=0; j--) {
				PennTreebankItemNode node2 = nodes2.Nodes().elementAt(j);
				addIndexTag(indexTags2,node2.Tag());
				//System.out.print("node2=");node2.print();
				totalNode2++;
				//System.out.println(node1.toString() + "," + node2.toString());
				if (node2.Sequential()<node1.Sequential())
					break for_node2;
				if (node2.Sequential()==node1.Sequential()) {
					//System.out.println("i=" + i + " j=" + j);
					this.commonAncestorTag = node2.Tag();
					int index1 = getIndexTag(indexTags1,node1.Tag());
					int index2 = getIndexTag(indexTags1,node2.Tag());
					if (index1>index2)
						this.indexCommonAncestorTag = index1;
					else
						this.indexCommonAncestorTag = index2;
					if ((i+1)<nodes1.Nodes().size())
						this.nextLevelParserTag1 = nodes1.Nodes().get(i+1).Tag();
					if ((j+1)<nodes2.Nodes().size())
						this.nextLevelParserTag2 = nodes2.Nodes().get(j+1).Tag();
					break for_node1;
				}
				if (node2.Tag().equals("S"))
					this.sameSTagSentence = false;
			}
			if (node1.Tag().equals("S"))
				this.sameSTagSentence = false;
			
		}
		this.distanceDepthParser = totalNode1 + totalNode2;
	}
	
	private void addIndexTag(HashMap<String,Integer> tags, String tag) {
		if (tags.containsKey(tag)) {
			int index = tags.get(tag);
			index++;
			tags.put(tag,index);
		}
		else
			tags.put(tag,1);
	}
	
	private int getIndexTag(HashMap<String,Integer> tags, String tag) {
		return tags.get(tag);
	}
	
	public String toString() {
		String text = "sameSentence=" + this.sameSentence + 
			", commonAncestorTag=" + this.commonAncestorTag + 
			", nextLevelTagToken1=" + this.nextLevelParserTag1 + 
			", nextLevelTagToken2=" + this.nextLevelParserTag2 + 
			", sameSTagSentence=" + this.sameSTagSentence +
			", distanceDepthParser=" + this.distanceDepthParser +
			", dependencyTag=" + this.dependencyTag +
			", distanceDependencyTag=" + this.distanceDependencyTag + 
			", distanceTokens=" + this.distanceTokens;
		return text;
	}
	
	public void print() {
		System.out.println(this.toString());
	}
	
	public String getName() {
		return null;
	}
	
	public void addToName(String name) {
		
	}
	
	public static void main(String[] args) {
		
		/*PennTreebankItem nodes1 = new PennTreebankItem();
		nodes1.push(new PennTreebankItemNode(0,"S1"));
		nodes1.push(new PennTreebankItemNode(1,"S"));
		nodes1.push(new PennTreebankItemNode(5,"VP"));
		nodes1.push(new PennTreebankItemNode(7,"VP"));
		nodes1.push(new PennTreebankItemNode(9,"SBAR"));
		nodes1.push(new PennTreebankItemNode(11,"S"));
		nodes1.push(new PennTreebankItemNode(20,"VP"));
		nodes1.push(new PennTreebankItemNode(24,"VP"));
		nodes1.push(new PennTreebankItemNode(25,"VBN"));
		
		PennTreebankItem nodes2 = new PennTreebankItem();
		nodes2.push(new PennTreebankItemNode(0,"S1"));
		nodes2.push(new PennTreebankItemNode(1,"S"));
		nodes2.push(new PennTreebankItemNode(5,"VP"));
		nodes2.push(new PennTreebankItemNode(7,"VP"));
		nodes2.push(new PennTreebankItemNode(9,"SBAR"));
		nodes2.push(new PennTreebankItemNode(11,"S"));
		nodes2.push(new PennTreebankItemNode(20,"VP"));
		nodes2.push(new PennTreebankItemNode(24,"VP"));
		nodes2.push(new PennTreebankItemNode(29,"PP"));
		nodes2.push(new PennTreebankItemNode(31,"NP"));
		nodes2.push(new PennTreebankItemNode(33,"NN"));
		
		PennTreebankItem nodes3 = new PennTreebankItem();
		nodes3.push(new PennTreebankItemNode(0,"S1"));
		nodes3.push(new PennTreebankItemNode(1,"S"));
		nodes3.push(new PennTreebankItemNode(0,"S1"));
		nodes3.push(new PennTreebankItemNode(1,"S"));
		nodes3.push(new PennTreebankItemNode(5,"VP"));
		nodes3.push(new PennTreebankItemNode(7,"VP"));
		nodes3.push(new PennTreebankItemNode(9,"SBAR"));
		nodes3.push(new PennTreebankItemNode(11,"S"));
		nodes3.push(new PennTreebankItemNode(20,"VP"));
		nodes3.push(new PennTreebankItemNode(24,"VP"));
		nodes3.push(new PennTreebankItemNode(36,"PP"));
		nodes3.push(new PennTreebankItemNode(40,"PP"));
		nodes3.push(new PennTreebankItemNode(42,"NP"));
		nodes3.push(new PennTreebankItemNode(48,"PP"));
		nodes3.push(new PennTreebankItemNode(50,"NP"));
		nodes3.push(new PennTreebankItemNode(54,"PP"));
		nodes3.push(new PennTreebankItemNode(56,"NP"));
		nodes3.push(new PennTreebankItemNode(82,"PP"));
		nodes3.push(new PennTreebankItemNode(84,"NP"));
		nodes3.push(new PennTreebankItemNode(87,"PP"));
		nodes3.push(new PennTreebankItemNode(89,"NP"));
		nodes3.push(new PennTreebankItemNode(96,"SBAR"));
		nodes3.push(new PennTreebankItemNode(97,"S"));
		nodes3.push(new PennTreebankItemNode(98,"NP"));
		nodes3.push(new PennTreebankItemNode(99,"CD"));*/
		
		// 10089566, sentence=1, token=1 (Involvement)
		PennTreebankItem nodes3 = new PennTreebankItem();
		nodes3.push(new PennTreebankItemNode(0,"S1"));
		nodes3.push(new PennTreebankItemNode(1,"S"));
		nodes3.push(new PennTreebankItemNode(2,"NP"));
		nodes3.push(new PennTreebankItemNode(3,"NP"));
		nodes3.push(new PennTreebankItemNode(4,"NP"));
		nodes3.push(new PennTreebankItemNode(5,"NN"));
		
		// 10089566, sentence=1, token=11 (activation)
		PennTreebankItem nodes5 = new PennTreebankItem();
		nodes5.push(new PennTreebankItemNode(0,"S1"));
		nodes5.push(new PennTreebankItemNode(1,"S"));
		nodes5.push(new PennTreebankItemNode(20,"VP"));
		nodes5.push(new PennTreebankItemNode(22,"NP"));
		nodes5.push(new PennTreebankItemNode(23,"NN"));
		
		// 10089566, sentence=1, token=13 (IL-10)
		PennTreebankItem nodes4 = new PennTreebankItem();
		nodes4.push(new PennTreebankItemNode(0,"S1"));
		nodes4.push(new PennTreebankItemNode(1,"S"));
		nodes4.push(new PennTreebankItemNode(29,"VP"));
		nodes4.push(new PennTreebankItemNode(24,"PP"));
		nodes4.push(new PennTreebankItemNode(26,"NP"));
		nodes4.push(new PennTreebankItemNode(27,"NP"));
		nodes4.push(new PennTreebankItemNode(28,"JJ"));
		
		// 10089566, sentence=1, token=14 (up-regulation)
		PennTreebankItem nodes2 = new PennTreebankItem();
		nodes2.push(new PennTreebankItemNode(0,"S1"));
		nodes2.push(new PennTreebankItemNode(1,"S"));
		nodes2.push(new PennTreebankItemNode(20,"VP"));
		nodes2.push(new PennTreebankItemNode(24,"PP"));
		nodes2.push(new PennTreebankItemNode(26,"NP"));
		nodes2.push(new PennTreebankItemNode(27,"NP"));
		nodes2.push(new PennTreebankItemNode(29,"NN"));
		
		// 10089566, sentence=1, token=19 (gp41)
		PennTreebankItem nodes1 = new PennTreebankItem();
		nodes1.push(new PennTreebankItemNode(0,"S1"));
		nodes1.push(new PennTreebankItemNode(0,"S"));
		nodes1.push(new PennTreebankItemNode(20,"VP"));
		nodes1.push(new PennTreebankItemNode(24,"PP"));
		nodes1.push(new PennTreebankItemNode(26,"NP"));
		nodes1.push(new PennTreebankItemNode(35,"PP"));
		nodes1.push(new PennTreebankItemNode(37,"NP"));
		nodes1.push(new PennTreebankItemNode(38,"NP"));
		nodes1.push(new PennTreebankItemNode(39,"CD"));
		
		PairTokens app = new PairTokens();
		app.comparePennTreebankItem(nodes2,nodes1);
		app.print();
		
	}
	
}

