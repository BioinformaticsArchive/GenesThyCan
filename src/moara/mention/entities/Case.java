package moara.mention.entities;

import moara.mention.MentionConstant;

public class Case {

	 // category of the case
	 protected String category;
	 // frequency
	 protected int frequency;
	 // sequential
	 protected int sequential;
	 // type of case
	 protected String typeCase;
	 // code of try case
	 protected String tryCode;
	 // features of the case
	 protected Feature feature;
	 // positive frequency
	 protected int positive;
	 // negative frequency
	 protected int negative;
	 // type of word in case: complete or partial
	 protected String typeWordCase;
	 // start position of word
	 protected int start;
	 // end position of mention
	 protected int end;
	     
	 public Case() {
	     this.positive = 0;
	     this.negative = 0;
	     this.typeWordCase = MentionConstant.WORD_CASE_COMPLETE;
	 }
	 
	 public String Category() {
		return this.category;
	 }
	 
	 public int Frequency() {
		return this.frequency;
	 }
	 
	 public int Sequential() {
		return this.sequential;
	 }
	 
	 public String TypeCase() {
		return this.typeCase;
	 }
	 
	 public String TryCode() {
		return this.tryCode;
	 }
	 
	 public Feature Feature() {
		return this.feature;
	 }
	 
	 public int Positive() {
		return this.positive;
	 }
	 
	 public int Negative() {
		return this.negative;
	 }
	 
	 public String TypeWordCase() {
		return this.typeWordCase;
	 }
	 
	 public int Start() {
		return this.start;
	 }
	 
	 public int End() {
		return this.end;
	 }
	 
	 public void setCategory(String category) {
	 	this.category = category;
	 }
	 
	 public void setFrequency(int frequency) {
	 	this.frequency = frequency;
	 }
	 
	 public void setSequential(int sequential) {
	 	this.sequential = sequential;
	 }
	 
	 public void setTryCode(String tryCode) {
	 	this.tryCode = tryCode;
	 }
	 
	 public void setPositive(int positive) {
	 	this.positive = positive;
	 }
	 
	 public void setNegative(int negative) {
	 	this.negative = negative;
	 }
	 
	 public void setTypeWordCase(String typeWordCase) {
	 	this.typeWordCase = typeWordCase;
	 }
	 
	 public void setTypeCase(String typeCase) {
	 	this.typeCase = typeCase;
	 }
	 
	 public void setFeature(Feature feature) {
	 	this.feature = feature;
	 }
	 
	 public void setStart(int start) {
	 	this.start = start;
	 }
	 
	 public void setEnd(int end) {
	 	this.end = end;
	 }

}

