package moara.evaluation;

import java.util.ArrayList;

public class ApproximateRandomization {

	private ArrayList<ObservationSet> system1;
	private ArrayList<ObservationSet> system2;
	private ArrayList<ObservationSet> pseudo1;
	private ArrayList<ObservationSet> pseudo2;
	private int numberShuffles = 9999;
	private int numberGreaterEqual;
	private double significanceLevel;
	
	public ApproximateRandomization() {
		this.numberGreaterEqual = 0;	
	}
	
	public double SignificanceLevel() {
		return this.significanceLevel;
	}
	
	public void calculate() {
		for (int i=0; i<this.numberShuffles; i++) {
			for (int j=0; j<this.system1.size(); j++) {
				ObservationSet set1 = this.system1.get(j);
				ObservationSet set2 = this.system2.get(j);
				if (keepValues()) {
					this.pseudo1.add(set1);
					this.pseudo2.add(set2);
				}
				else {
					this.pseudo1.add(set2);
					this.pseudo2.add(set1);
				}
			}
			// calculate test function
			
		}
		this.significanceLevel = (double)(this.numberGreaterEqual+1)/(double)(this.numberShuffles+1);
	}
	
	private boolean keepValues() {
		double value = Math.random();
		if (value<=0.5)
			return true;
		else
			return false;
	}
	
	
}
