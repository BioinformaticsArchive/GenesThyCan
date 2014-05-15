package moara.wrapper.secondstring;

import java.util.ArrayList;
import java.util.List;

import moara.util.Constant;
import moara.util.text.StringDistance;

import com.wcohen.ss.BasicStringWrapperIterator;
import com.wcohen.ss.JaroWinkler;
import com.wcohen.ss.Level2JaroWinkler;
import com.wcohen.ss.Levenstein;
import com.wcohen.ss.MongeElkan;
import com.wcohen.ss.SmithWaterman;
import com.wcohen.ss.SoftTFIDF;
import com.wcohen.ss.api.StringWrapper;

public class SecondStringStringDistance extends StringDistance {

	// classifier SoftTFIDF
	public SoftTFIDF soft;
	
	public SecondStringStringDistance(int type) {
		super(type);
	}
	
	public void train(ArrayList<String> tokens) {
		if (this.type==Constant.DISTANCE_SOFT_TFIDF)
			trainSoftTFIDF(tokens);
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public double score(String token1, String token2) {
		double score = 0;
		// Levenstein
		if (this.type==Constant.DISTANCE_LEVENSTEIN) {
			Levenstein lev = new Levenstein();
			score = lev.score(token1,token2);
		}
		// Smith-Waterman
		else if (this.type==Constant.DISTANCE_SMITH_WATERMAN) {
			SmithWaterman sw = new SmithWaterman();
			score = sw.score(token1,token2);
		}
		// Monge-Elkan
		else if (this.type==Constant.DISTANCE_MONGE_ELKAN) {
			MongeElkan me = new MongeElkan();
			score = me.score(token1,token2);
		}
		// Jaro-Winkler
		else if (this.type==Constant.DISTANCE_JARO_WINKLER) {
			JaroWinkler jw = new JaroWinkler();
			score = jw.score(token1,token2);
		}
		// SoftTFIDF
		else if (this.type==Constant.DISTANCE_SOFT_TFIDF) {
			//SoftTFIDF soft = new SoftTFIDF(new Level2JaroWinkler());
			if (this.soft == null){
				this.soft = new SoftTFIDF(new Level2JaroWinkler());
			}
			
			score = this.soft.score(token1,token2);
		}
		return score;
	}
	
	private SoftTFIDF trainSoftTFIDF(ArrayList<String> tokens) {
		SoftTFIDF soft = new SoftTFIDF(new Level2JaroWinkler());
		List<StringWrapper> list = new ArrayList<StringWrapper>();
		for (int i=0; i<tokens.size(); i++) {
			list.add(soft.prepare(tokens.get(i)));
		}
		soft.train(new BasicStringWrapperIterator(list.iterator()));
		
		this.soft = soft;
		
		return soft;
	}
	
}
