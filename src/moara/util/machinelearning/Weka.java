package moara.util.machinelearning;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.lang.ClassNotFoundException;
//import java.net.URL;
import moara.util.Constant;
import weka.core.*;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.Classifier;

public abstract class Weka {
	
	private String function;
	protected Classifier classifier;
	protected FastVector attributes;
	protected Instances dataset;
	private RandomAccessFile rwArff;
	
	public Weka(String function) {
		this.function = function;
	}
	
	public void saveModel(String file) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream("models/" + file + ".model"));
			oos.writeObject(this.classifier);
			System.err.println("Saving model..." + file);
			oos.flush();
			oos.close();
		}
		catch(IOException ex) { 
			System.err.println(ex);
		}
	}
	
	public void readModel(String file) {
		try {
			file = "models/" + file + ".model";
			System.err.println("Reading model..." + file);
			// directory is referenced with respect to where this class is
			//URL input = Weka.class.getResource(file);
			//System.err.println("input weka " + input);
			//ObjectInputStream ois = new ObjectInputStream(input.openStream());
			FileInputStream input = new FileInputStream(new File(file));
			ObjectInputStream ois = new ObjectInputStream(input);
			Classifier cls = (Classifier)ois.readObject();
			this.classifier = cls;
			ois.close();
		}
		catch(IOException ex) { 
			System.err.println(ex);
		}
		catch(ClassNotFoundException ex) { 
			System.err.println(ex);
		}
	}
	
	public void train() {
		// initiate attribute
		initAttributes();
		// create training set
		createTrainingSet();
		// train
		trainWeka(dataset);
	}
	
	public void train(Instances dataset) {
		// train
		trainWeka(dataset);
	}
	
	private void trainWeka(Instances dataset) {
		//System.err.println("Total instances: " + dataset.numInstances());
		// build classifier
		if (dataset!=null) {
			// Random Forest
			if (function.equals(Constant.ML_RANDOM_FOREST)) {
				System.err.println("Training random forest...");
				this.classifier = new RandomForest();
			}
			// Support Vector Machines
			if (function.equals(Constant.ML_SVM)) {
				System.err.println("Training support vector machines...");
				this.classifier = new SMO();
			}
			// Logistic Regression
			if (function.equals(Constant.ML_LOGISTIC)) {
				System.err.println("Training logistic regression...");
				this.classifier = new SimpleLogistic();
			}
			try {
				this.classifier.buildClassifier(dataset);
			}
			catch(Exception ex) { 
				System.err.println(ex);
			}
		}
	}
	
	public String classify(Instance inst) {
		try {
			Instances dataset = new Instances("test",this.attributes,1);
			dataset.setClassIndex(this.attributes.size()-1);
			inst.setDataset(dataset);
			int category = 0;
			category = (new Double(this.classifier.classifyInstance(inst))).intValue();
			return inst.classAttribute().value(category);
		}
		catch(Exception ex) { 
			System.err.println(ex);
		}
		return null;
	}
	
	public Instances getDataset() {
		return this.dataset;
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
	
	protected abstract void initAttributes();
	
	protected abstract void createTrainingSet();
	
}
