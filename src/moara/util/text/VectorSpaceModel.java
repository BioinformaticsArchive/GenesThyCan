package moara.util.text;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class VectorSpaceModel {
	
	private HashMap<String,TermSpaceModel> terms;
	
	public VectorSpaceModel() {
		
	}
	
	public HashMap<String,TermSpaceModel> Terms() {
		return this.terms;
	}
	
	public void buildVectorModel(ArrayList<String> tokens) {
		createVectorModel(tokens);
	}
	
	public void builtVectorModelText(ArrayList<String> tokens) {
		createVectorModel(tokens);
		removeInvalidTerms();
		calculateMeasureValue(this.terms);
	}
	
	private void createVectorModel(ArrayList<String> tokens) {
		this.terms = new HashMap<String,TermSpaceModel>(tokens.size());
		for (int i=0; i<tokens.size(); i++) {
			TermSpaceModel tvm;
			if (!this.terms.containsKey(tokens.get(i)))
				tvm = new TermSpaceModel(tokens.get(i));
			else
				tvm = this.terms.get(tokens.get(i));
			tvm.addFrequency();
			this.terms.put(tvm.Term(),tvm);
		}
	}
	
	public void calculateMeasureValue(HashMap<String,TermSpaceModel> terms) {
		int numDoc = getNumDoc();
		if (terms!=null)
			this.terms = terms;
		int totalTermsDoc = getTotalTermsDoc();
		Iterator<TermSpaceModel> iter = this.terms.values().iterator();
		while (iter.hasNext()) {
			TermSpaceModel tvm = iter.next();
			double weight = calculateTFIDFTerm(tvm,numDoc,totalTermsDoc);
			tvm.setMeasureValue(weight);
		}
	}
	
	private int getTotalTermsDoc() {
		int total = 0;
		Iterator<TermSpaceModel> iter = this.terms.values().iterator();
		while (iter.hasNext()) {
			TermSpaceModel tvm = iter.next();
			total += tvm.Frequency();
		}
		return total;
	}
	
	private double calculateTFIDFTerm(TermSpaceModel tvm, int numDoc, int totalTermsDoc) {
		double tf = (double)tvm.Frequency()/(double)totalTermsDoc;
		int N = getNumDocByTerm(tvm.Term());
		//System.err.println(tvm.Term() + " " + tvm.Frequency() + " " + 
		//	totalTermsDoc + " " + numDoc + " " + N);
				
		if (N==0)
			return -1;
		else {
			double idf = Math.log((double)numDoc/(double)N);
			return (double)tf*idf;
		}
	}
	
	private void removeInvalidTerms() {
		ArrayList<String> training = getTrainingTerms();
		if (training==null)
			return;
		ArrayList<String> keys = new ArrayList<String>(this.terms.size());
		Iterator<String> iter = this.terms.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			//System.err.println(key + " " + totalTerms.contains(key));
			if (!training.contains(key))
				keys.add(key);
		}
		keys.trimToSize();
		for (int i=0; i<keys.size(); i++) {
			this.terms.remove(keys.get(i));
		}
	}
	
	protected abstract int getNumDoc();
	
	protected abstract int getNumDocByTerm(String term);
	
	protected abstract ArrayList<String> getTrainingTerms();
	
	protected abstract void removeLowFrequencyTerms(int frequency);
	
	protected abstract void removeFewDocsTerms(int numDoc);
	
}