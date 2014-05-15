package moara.bio.entities;

import moara.bio.BioConstant;
import moara.util.corpora.NormalizedNamedEntity;

public class NormalizedGene extends NormalizedNamedEntity {

	public NormalizedGene() {
		super();
	}
	
	public String getNamespace() {
		return BioConstant.ENTREZ_GENE_LINK + this.Identifier();
	}
	
}
