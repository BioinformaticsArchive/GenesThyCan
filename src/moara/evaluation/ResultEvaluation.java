package moara.evaluation;

import java.lang.Comparable;

public class ResultEvaluation implements Comparable<ResultEvaluation> {

	// description
	private String name;
	// true positives
	private int truePos;
	// false positives
	private int falsePos;
	// false negatives
	private int falseNeg;
	// precision
	private double precision;
	// recall
	private double recall;
	// f-measure
	private double fmeasure;
	
	public ResultEvaluation() {
		this.truePos = 0;
		this.falsePos = 0;
		this.falseNeg = 0;
		this.precision = -1;
		this.recall = -1;
		this.fmeasure = -1;
	}
	
	public ResultEvaluation(int truePos, int falsePos, int falseNeg) {
		this.truePos = truePos;
		this.falsePos = falsePos;
		this.falseNeg = falseNeg;
		this.precision = -1;
		this.recall = -1;
		this.fmeasure = -1;
	}
	
	public String Name() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int TruePos() {
		return this.truePos;
	}
	
	public int FalsePos() {
		return this.falsePos;
	}
	
	public int FalseNeg() {
		return this.falseNeg;
	}
	
	public double Precision() {
		if (this.precision==-1)
			return getPrecision();
		else
			return this.precision;
	}
	
	public double Recall() {
		if (this.recall==-1)
			return getRecall();
		else
			return this.recall;
	}
	
	public double FMeasure() {
		if (this.fmeasure==-1)
			return getFMeasure();
		else
			return this.fmeasure;
	}
	
	public void addTruePos() {
		this.truePos++;
	}
	
	public void addFalsePos() {
		this.falsePos++;
	}
	
	public void addFalseNeg() {
		this.falseNeg++;
	}
	
	public void addTruePos(int tp) {
		this.truePos += tp;
	}
	
	public void addFalsePos(int fp) {
		this.falsePos += fp;
	}
	
	public void addFalseNeg(int fn) {
		this.falseNeg += fn;
	}
	
	public void setPrecision(double value) {
		this.precision = value;
	}
	
	public void setRecall(double value) {
		this.recall = value;
	}
	
	public void setFMeasure(double value) {
		this.fmeasure = value;
	}
	
	public double getPrecision() {
		if (this.truePos!=0 || this.falsePos!=0)
			this.precision = (double)this.truePos/(double)(this.truePos+this.falsePos);
		else
			this.precision = 0;
		return this.precision;
	}
	
	public double getRecall() {
		if (this.truePos!=0 || this.falseNeg!=0)
			this.recall = (double)this.truePos/(double)(this.truePos+this.falseNeg);
		else
			this.recall = 0;
		return this.recall;
	}
	
	public double getFMeasure() {
		if (this.precision==-1)
			getPrecision();
		if (this.recall==-1)
			getRecall();
		if (this.precision!=0 || this.recall!=0)
			this.fmeasure = (2*this.precision*this.recall)/(this.precision+this.recall);
		else
			this.fmeasure = 0;
		return this.fmeasure;
	}
	
	public int compareTo(ResultEvaluation re) {
		if (this.fmeasure>re.FMeasure())
			return 1;
		else if (this.fmeasure<re.FMeasure())
			return -1;
		else
			return 0;
	}
	
	public String toString() {
		String text = this.name + "\n";
		text += "TP=" + this.truePos + " FP=" + this.falsePos + " FN=" + this.falseNeg + "\n";
		text += "R=" + this.Recall() + " P=" + this.Precision() + " FM=" + this.FMeasure();
		return text;
	}
	
	public void print() {
		System.out.println(this.toString());
	}
	
}
