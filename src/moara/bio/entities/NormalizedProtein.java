package moara.bio.entities;

import moara.bio.BioConstant;
import moara.util.corpora.NormalizedNamedEntity;

public class NormalizedProtein extends NormalizedNamedEntity {

	public NormalizedProtein() {
		super();
	}
	
	public String getNamespace() {
		return BioConstant.UNIPROT_LINK + this.Identifier();
	}
	
}
