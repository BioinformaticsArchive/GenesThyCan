package moara.wrapper.weka;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Instance;

public class BaseWeka {

	protected FastVector attributes;
	protected Instances dataset;
	protected HashMap<String,Integer> featureToIndex;
	private int indexAttribute;
	
	public BaseWeka() {
		
	}
	
	public void initAttributes(int total) {
		this.attributes = new FastVector(total);
		this.featureToIndex = new HashMap<String,Integer>(total);
	}
	
	public void addNumericAttribute(String feature) {
		Attribute attribute = new Attribute(feature);
		this.attributes.addElement(attribute);
		this.featureToIndex.put(feature,this.indexAttribute);
		this.indexAttribute++;
	}
	
	public void addNominalAttribute(String feature, ArrayList<String> values) {
		Attribute attribute;
		if (values!=null) {
			FastVector nominalvalues = new FastVector(values.size());
			for (int i=0; i<values.size(); i++)
				nominalvalues.addElement(values.get(i));
			attribute = new Attribute(feature,nominalvalues);
		}
		else {
			FastVector nominalvalues = new FastVector();
			attribute = new Attribute(feature,nominalvalues);
		}
		this.attributes.addElement(attribute);
		this.featureToIndex.put(feature,this.indexAttribute);
		this.indexAttribute++;
	}
	
	public void addValueToNominalAttribute(String feature, String value) {
		Enumeration<Attribute> atts = this.attributes.elements();
		while (atts.hasMoreElements()) {
			Attribute attribute = atts.nextElement();
			if (attribute.name().equals(feature))
				attribute.addStringValue(value);
		}
	}
	
	public void initDataset(String name, int total, ArrayList<String> classes) {
		this.dataset = new Instances(name,this.attributes,total);
		for (int i=0; i<classes.size(); i++) {
			this.dataset.setClassIndex(this.featureToIndex.get(classes.get(i)));
		}
	}
	
	public void addInstanceToDataset(HashMap<String,String> fvs) {
		Instance inst = new Instance(this.attributes.size());
		Iterator<String> iter = fvs.keySet().iterator();
		while (iter.hasNext()) {
			String feature = iter.next();
			Attribute attribute = (Attribute)this.attributes.elementAt(this.featureToIndex.get(feature));
			if (attribute.isNumeric())
				inst.setValue(attribute,new Integer(fvs.get(feature)));
			else
				inst.setValue(attribute,fvs.get(feature));
		}
		inst.setDataset(this.dataset);
 		this.dataset.add(inst);
	}
	
}
