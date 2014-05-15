package moara.util.text;

public class TermSpaceModel {
	
	// term
	private String term;
	// frequency in the docs
	private int frequency;
	// feature selection (measure) value
	private double measureValue;
	// number of documents
	private int numDoc;
	
	public TermSpaceModel(String term) {
		this.term = term;
		this.frequency = 0;
		this.numDoc = 0;
	}
	
	public String Term() {
		return this.term;
	}
	
	public int Frequency() {
		return this.frequency;
	}
	
	public double MeasureValue() {
		return this.measureValue;
	}
	
	public int NumDoc() {
		return this.numDoc;
	}
	
	public void setMeasureValue(double value) {
		this.measureValue = value;
	}
	
	public void setFrequency(int value) {
		this.frequency = value;
	}
	
	public void setNumDoc(int numDoc) {
		this.numDoc = numDoc;
	}
	
	public void addFrequency() {
		this.frequency++;
	}
	
	public void addFrequency(int value) {
		this.frequency += value;
	}
	
	public String toString() {
		return this.term;
	}
	
}
