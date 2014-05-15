package moara.mention.entities;

import moara.mention.MentionConstant;

public class VariablesProcessing {

	public String word;
	public String wordBefore1;
	public String wordBefore2;
	public String wordBefore3;
	public String wordBefore4;
	
	public String tag;
	public String tagBefore1;
	public String tagBefore2;
	public String tagBefore3;
	public String tagBefore4;
	
	public String category;
	public String categoryBefore1;
	public String categoryBefore2;
	public String categoryBefore3;
	public String categoryBefore4;
	
	public VariablesProcessing(String categoryBeforeNotMention) {
		this.word = new String();
		this.wordBefore1 = new String();
		this.wordBefore2 = new String();
		this.wordBefore3 = new String();
		this.wordBefore4 = new String();
		this.tag = new String();
		this.tagBefore1 = new String();
		this.tagBefore2 = new String();
		this.tagBefore3 = new String();
		this.tagBefore4 = new String();
		this.category = new String();
		this.categoryBefore1 = categoryBeforeNotMention;
		this.categoryBefore2 = categoryBeforeNotMention;
		this.categoryBefore3 = categoryBeforeNotMention;
		this.categoryBefore4 = categoryBeforeNotMention;
	}
	
	public void updateVariables() {
		this.wordBefore4 = this.wordBefore3;
		this.wordBefore3 = this.wordBefore2;
		this.wordBefore2 = this.wordBefore1;
		this.wordBefore1 = this.word;
		this.tagBefore4 = this.tagBefore3;
		this.tagBefore3 = this.tagBefore2;
		this.tagBefore2 = this.tagBefore1;
		this.tagBefore1 = this.tag;
		this.categoryBefore4 = this.categoryBefore3;
		this.categoryBefore3 = this.categoryBefore2;
		this.categoryBefore2 = this.categoryBefore1;
		this.categoryBefore1 = this.category;
	}
	
	public void updateVariablesBefore() {
		this.wordBefore4 = this.wordBefore3;
		this.wordBefore3 = this.wordBefore2;
		this.tagBefore4 = this.tagBefore3;
		this.tagBefore3 = this.tagBefore2;
		this.categoryBefore4 = this.categoryBefore3;
		this.categoryBefore3 = this.categoryBefore2;
	}
	
	public Feature createCaseFeature(String[] features) {
		Feature f = new Feature(features);
		for (int i=0; i<features.length; i++) {
			String featureName = features[i];
			if (featureName.equals(MentionConstant.FEATURE_ORIGINAL_WORD))
				f.setStrValue(featureName,this.wordBefore2);
			else if (featureName.equals(MentionConstant.FEATURE_WORD))
				f.setStrValue(featureName,this.wordBefore2);
			else if (featureName.equals(MentionConstant.FEATURE_WORD_BEFORE_1))
				f.setStrValue(featureName,this.wordBefore3);
			else if (featureName.equals(MentionConstant.FEATURE_WORD_BEFORE_2))
				f.setStrValue(featureName,this.wordBefore4);
			else if (featureName.equals(MentionConstant.FEATURE_WORD_AFTER_1))
				f.setStrValue(featureName,this.wordBefore1);
			else if (featureName.equals(MentionConstant.FEATURE_WORD_AFTER_2))
				f.setStrValue(featureName,this.word);
			else if (featureName.equals(MentionConstant.FEATURE_CATEGORY_BEFORE_1))
				f.setStrValue(featureName,this.categoryBefore3);
			else if (featureName.equals(MentionConstant.FEATURE_CATEGORY_BEFORE_2))
				f.setStrValue(featureName,this.categoryBefore4);
			else if (featureName.equals(MentionConstant.FEATURE_POSTAG))
				f.setStrValue(featureName,this.tagBefore2);
			else if (featureName.equals(MentionConstant.FEATURE_POSTAG_BEFORE_1))
				f.setStrValue(featureName,this.tagBefore3);
			else if (featureName.equals(MentionConstant.FEATURE_POSTAG_BEFORE_2))
				f.setStrValue(featureName,this.tagBefore4);
			else if (featureName.equals(MentionConstant.FEATURE_POSTAG_AFTER_1))
				f.setStrValue(featureName,this.tagBefore1);
			else if (featureName.equals(MentionConstant.FEATURE_POSTAG_AFTER_2))
				f.setStrValue(featureName,this.tag);
			else if (featureName.equals(MentionConstant.FEATURE_FORMAT)) {
				f.setStrValue(featureName,this.wordBefore2);
				f.setStrValue(MentionConstant.FEATURE_WORD,wordBefore2);
			}
			else if (featureName.equals(MentionConstant.FEATURE_FORMAT_BEFORE_1))
				f.setStrValue(featureName,this.wordBefore3);
			else if (featureName.equals(MentionConstant.FEATURE_FORMAT_BEFORE_2))
				f.setStrValue(featureName,this.wordBefore4);
			else if (featureName.equals(MentionConstant.FEATURE_FORMAT_AFTER_1))
				f.setStrValue(featureName,this.wordBefore1);
			else if (featureName.equals(MentionConstant.FEATURE_FORMAT_AFTER_2))
				f.setStrValue(featureName,this.word);
			else if (featureName.equals(MentionConstant.FEATURE_SUFFIX3))
				f.setStrValue(featureName,this.wordBefore2);
			else if (featureName.equals(MentionConstant.FEATURE_SUFFIX4))
				f.setStrValue(featureName,this.wordBefore2);
			else if (featureName.equals(MentionConstant.FEATURE_PREFIX3))
				f.setStrValue(featureName,this.wordBefore2);
			else if (featureName.equals(MentionConstant.FEATURE_PREFIX4))
				f.setStrValue(featureName,this.wordBefore2);
		}
		return f;
	}
	
	public Feature createCaseSuffix4() {
		String[] features = {MentionConstant.FEATURE_FORMAT,
				MentionConstant.FEATURE_SUFFIX4,
				MentionConstant.FEATURE_CATEGORY_BEFORE_1};
		Feature f = new Feature(features);
		String suffix = this.wordBefore2.substring(this.wordBefore2.length()-4,this.wordBefore2.length());
		f.setStrValue(MentionConstant.FEATURE_FORMAT,suffix);
		f.setStrValue(MentionConstant.FEATURE_WORD,this.wordBefore2);
		f.setStrValue(MentionConstant.FEATURE_SUFFIX4,this.wordBefore2);
		f.setStrValue(MentionConstant.FEATURE_CATEGORY_BEFORE_1,this.categoryBefore3);
		return f;
	}
	
}
