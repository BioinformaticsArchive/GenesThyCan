package moara.util.corpora;

import java.util.ArrayList;

import moara.normalization.NormalizationConstant;

public class NamedEntity  extends Token {

	private ArrayList<NormalizedNamedEntity> normalized;
	private String typePrediction;
	private String typeMatching;
		
	public NamedEntity (String text, int start, int end) {
		super(text,start,end);
	}
	
	public String typePrediction() {
		if (this.typePrediction==null) {
			if (this.normalized.size()==1)
				return NormalizationConstant.PREDICTION_UNIQUE;
			else if (this.normalized.size()>1)
				return NormalizationConstant.PREDICTION_AMBIGUOUS;
		}
		return this.typePrediction;
	}
	
	public String typeMatching() {
		return this.typeMatching;
	}
	
	public ArrayList<NormalizedNamedEntity> normalized() {
		return this.normalized;
	}
	
	public void setTypeMatching(String type) {
		this.typeMatching = type;
	}
	
	public void addNormalizedEntity(NormalizedNamedEntity nne) {
		if (this.normalized==null)
			this.normalized = new ArrayList<NormalizedNamedEntity>();
		this.normalized.add(nne);
	}
	
}
