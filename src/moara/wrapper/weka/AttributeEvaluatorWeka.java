package moara.wrapper.weka;

import weka.attributeSelection.*;

import java.lang.Exception;

public class AttributeEvaluatorWeka extends BaseWeka {

	private AttributeEvaluator evaluator;
	
	public AttributeEvaluatorWeka() {
		
	}
	
	public void build(String function) {
		System.out.println("instances=" + this.dataset.numInstances());
		System.out.println("attributes=" + this.dataset.numAttributes());
		try {
			if (function.equals(ConstantWeka.ATTREVAL_CHI_SQUARED))
				this.evaluator = new ChiSquaredAttributeEval();
			else if (function.equals(ConstantWeka.ATTREVAL_GAIN_RATION))
				this.evaluator = new GainRatioAttributeEval();
			else if (function.equals(ConstantWeka.ATTREVAL_INFO_GAIN))
				this.evaluator = new InfoGainAttributeEval();
			if (this.evaluator!=null)
				this.evaluator.buildEvaluator(this.dataset);
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public double evaluateAttribute(String feature) {
		try {
			return this.evaluator.evaluateAttribute(this.featureToIndex.get(feature));
		}
		catch (Exception e) {
			System.out.println(e);
		}
		return -1;
	}
	
}
