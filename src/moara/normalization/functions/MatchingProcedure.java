package moara.normalization.functions;

import java.util.HashMap;
import moara.normalization.entities.GenePrediction;

public class MatchingProcedure {

	protected boolean print;
	protected HashMap<String,GenePrediction> predicted;
		
	public MatchingProcedure() {
		this.print = false;
	}
	
	public HashMap<String,GenePrediction> Predicted() {
		return this.predicted;
	}
	
	public void setPrint(boolean print) {
		this.print = print;
	}
	
	public void newMatching() {
		this.predicted = new HashMap<String,GenePrediction>();
	}
	
}