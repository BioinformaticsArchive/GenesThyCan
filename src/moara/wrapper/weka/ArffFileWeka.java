package moara.wrapper.weka;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import weka.core.Attribute;
import weka.core.Instance;

public class ArffFileWeka extends BaseWeka {

	private RandomAccessFile rwArff;
	
	public ArffFileWeka() {
		
	}
	
	public void initArffFile(String file) {
		try {
			File err = new File(file);
			if (err.exists())
				err.delete();
			err.createNewFile();
		    if (err.exists())
		     	this.rwArff = new RandomAccessFile(err,"rw");
		    this.rwArff.writeBytes("@RELATION test\n");
		    // attributes
	     	for (int i=0; i<this.dataset.numAttributes(); i++) {
	     		Attribute att = this.dataset.attribute(i);
	     		String name = att.name();
	     		String type = "";
	     		if (att.isNumeric())
	     			type = "NUMERIC";
	     		else if (att.isNominal()) {
	     			type = "{";
	     			for (int j=0; j<att.numValues(); j++)
	     				type += att.value(j) + ",";
	     			type = type.substring(0,type.length()-1) + "}";
	     		}
	     		this.rwArff.writeBytes("@ATTRIBUTE " + name + " " + type + "\n");
	     	}
	     	this.rwArff.writeBytes("@DATA\n");
		}
		catch(IOException ex) {
			System.err.println(ex);
		}
	}
	
	public void writeArffFile(String file) {
		initArffFile(file);
		for (int i=0; i<this.dataset.numInstances(); i++) {
     		Instance inst = this.dataset.instance(i);
     		writeInstance(inst);
     	}
		endArffFile();
	}
	
	public void endArffFile() {
		try {
			this.rwArff.close();
		}
		catch(IOException ex) {
			System.err.println(ex);
		}
	}
	
	public void writeInstance(Instance inst) {
		try {
			String instance = "{";
     		for (int j=0; j<inst.numAttributes(); j++) {
     			Attribute att = inst.attribute(j);
     			if (!inst.isMissing(j)) {
     				instance += j + " ";
	     			if (att.isNumeric())
	     				instance += inst.value(j) + ",";
	     			else
	     				instance += inst.stringValue(j) + ",";
     			}
     		}
     		this.rwArff.writeBytes(instance.substring(0,instance.length()-1)+"}\n");
		}
		catch(IOException ex) {
			System.err.println(ex);
		}
	}
	
}
