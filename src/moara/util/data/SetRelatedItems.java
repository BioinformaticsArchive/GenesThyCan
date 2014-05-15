package moara.util.data;

import java.util.HashMap;
import java.util.Iterator;

import moara.tagger.basic.entities.FeatureArgument;

public class SetRelatedItems implements FeatureArgument {

	protected ItemSet features;
	protected ItemSet[] items;
	protected HashMap<String,PairItems> pairs;
	//private int index;
	
	public SetRelatedItems(int length) {
		init(length);
	}
	
	private void init(int length) {
		this.items  = new ItemSet[length];
		this.pairs = new HashMap<String,PairItems>(getTotalPairs(length));
		//this.index = 0;
	}
	
	protected int getTotalPairs(int length) {
		return (length*(length-1))/2;
	}
	
	public ItemSet Features() {
		return this.features;
	}
	
	public ItemSet[] Items() {
		return this.items;
	}
	
	public HashMap<String,PairItems> Pairs() {
		return this.pairs;
	}
	
	public PairItems getPair(int index1, int index2) {
		return this.pairs.get(getKey(index1,index2));
	}
	
	public PairItems getPair(String key) {
		return this.pairs.get(key);
	}
	
	public void setFeature(ItemSet f) {
		this.features = f;
		/*if (this.features==null) {
			this.features = new ItemSet[1];
			this.features[0] = f;
		}
		else {
			ItemSet[] temp = new ItemSet[this.features.length];
			for (int i=0; i<this.features.length; i++)
				temp[i] = this.features[i];
			this.features = new ItemSet[temp.length+1];
			for (int i=0; i<temp.length; i++)
				this.features[i] = temp[i];
			this.features[this.features.length-1] = f;
		}*/
	}
	
	/*public void addItem(ItemSet item) {
		item.addToName(getItemComplement(this.index));
		this.items[this.index] = item;
		this.index++;
	}*/
	
	public void setItem(int index, ItemSet item, boolean updateName) {
		if (updateName)
			item.addToName(getItemComplement(index));
		this.items[index] = item;
	}
	
	public String getItemComplement(int index) {
		return (new Integer(index)).toString();
	}
	
	public void addPair(int index1, int index2, PairItems pair, boolean updateName) {
		addPair(getKey(index1,index2),pair,updateName);
	}
	
	public void addPair(String key, PairItems pair, boolean updateName) {
		int[] indexes = getIndexesKey(key);
		if (updateName)
			pair.addToName(getPairComplement(indexes[0],indexes[1]));
		this.pairs.put(key,pair);
	}
	
	public String getPairComplement(int index1, int index2) {
		return index1 + "-" + index2;
	}
	
	protected String getKey(int index1, int index2) {
		return index1 + "," + index2;
	}
	
	private int[] getIndexesKey(String key) {
		int[] indexes = new int[2];
		indexes[0] = new Integer(key.substring(0,key.indexOf(",")));
		indexes[1] = new Integer(key.substring(key.indexOf(",")+1));
		return indexes;
	}
	
	public int getNumItems() {
		int total = 0;
		for (int i=0; i<this.items.length; i++) {
			if (this.items[i]!=null)
				total++;
		}
		return total;
	}
	
	public String toString() {
		String text = "";
		if (this.features!=null)
			text += this.features.toString() + "\n";
		/*if (this.features!=null)
			for (int i=0; i<this.features.length; i++)
				text += this.features[i].toString() + "\n";*/
		for (int i=0; i<this.items.length; i++) {
			if (this.items[i]!=null)
				text += this.getItemComplement(i) + ":" + this.items[i].toString() + "\n";
		}
		Iterator<String> iter = this.pairs.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (this.pairs.get(key)!=null) {
				int index1 = new Integer(key.substring(0,key.indexOf(",")));
				int index2 = new Integer(key.substring(key.indexOf(",")+1));
				text += this.getPairComplement(index1, index2) + ":" + this.pairs.get(key).toString() + "\n";
			}
		}
		return text;
	}
	
	public void print() {
		System.out.println(this.toString());
	}
	
}
