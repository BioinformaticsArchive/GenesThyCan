package moara.util.corpora;

public class ChunkTag {

	private String tag;
	private String positionTag;
	private int seqPhrase;
	
	public ChunkTag(String tag, String positionTag, int seqPhrase) {
		this.tag = tag;
		this.positionTag = positionTag;
		this.seqPhrase = seqPhrase;
	}
	
	public String Tag() {
		return this.tag;
	}
	
	public String PositionTag() {
		return this.positionTag;
	}
	
	public int SeqPhrase() {
		return this.seqPhrase;
	}
	
}
