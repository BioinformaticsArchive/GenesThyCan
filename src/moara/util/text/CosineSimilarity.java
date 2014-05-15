package moara.util.text;

import java.util.HashMap;
import java.util.Iterator;

public class CosineSimilarity {

	private double similarity;
	private String commonTerms = ""; //Chengkun
	private int numCommonTerms;
	
	public CosineSimilarity() {
		
	}
	
	public double Similarity() {
		return this.similarity;
	}
	
	public String CommonTerms() {
		return this.commonTerms;
	}
	
	public int NumCommonTerms() {
		return this.numCommonTerms;
	}
	
	// smaller vector first
	public void calculateDistance(HashMap<String,TermSpaceModel> list1, 
			HashMap<String,TermSpaceModel> list2) {
		// length of vector
		double length1 = lengthList(list1);
		double length2 = lengthList(list2);
		Iterator<TermSpaceModel> iter = list1.values().iterator();
		while (iter.hasNext()) {
			TermSpaceModel tvm1 = iter.next();
			String term1 = tvm1.Term();
			double weight1 = tvm1.MeasureValue();
			if (list2.containsKey(term1)) {
				TermSpaceModel tvm2 = list2.get(term1);
				double weight2 = tvm2.MeasureValue();
				this.similarity += weight1*weight2;
				this.commonTerms += term1 + " ";
				this.numCommonTerms++;
			}
		}
		if (length1==0 || length2==0)
			this.similarity = 0;
		else
			this.similarity = this.similarity/(length1*length2);
		if (this.commonTerms!=null)
			this.commonTerms.trim();
	}
	
	private double lengthList(HashMap<String,TermSpaceModel> list) {
		double sum = 0;
		Iterator<TermSpaceModel> iter = list.values().iterator();
		while (iter.hasNext()) {
			TermSpaceModel tvm = iter.next();
			double weight = tvm.MeasureValue();
			sum += weight*weight;
		}
		return Math.sqrt(sum);
	}
	
}
