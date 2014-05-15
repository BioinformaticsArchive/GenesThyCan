package moara.mention.entities;

import java.util.ArrayList;

public class CollectionGeneMention {

	private ArrayList<Mention> mentions;
	
	public CollectionGeneMention() {
		this.mentions = new ArrayList<Mention>();
	}
	
	public CollectionGeneMention(int size) {
		this.mentions = new ArrayList<Mention>(size);
	}
	
	public CollectionGeneMention(ArrayList<Mention> gms) {
		this.mentions = new ArrayList<Mention>(gms.size());
		for (int i=0; i<gms.size(); i++)
			this.addMention(gms.get(i));
	}
	
	public ArrayList<Mention> Mentions() {
		return this.mentions;
	}
	
	public void addMention(Mention gm) {
		this.mentions.add(gm);
	}
	
}
