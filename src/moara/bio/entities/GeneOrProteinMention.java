package moara.bio.entities;

import moara.util.corpora.NamedEntity;

public class GeneOrProteinMention extends NamedEntity {

	public GeneOrProteinMention (String text, int start, int end) {
		super(text,start,end);
	}
	
}
