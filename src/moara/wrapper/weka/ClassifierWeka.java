package moara.wrapper.weka;

import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;

public class ClassifierWeka extends BaseWeka {

	private String function;
	private Classifier classifier;
	
	public ClassifierWeka(String function) {
		this.function = function;
	}
	
	public void train() {
		//System.err.println("Total instances: " + dataset.numInstances());
		// build classifier
		if (this.dataset!=null) {
			// Random Forest
			if (function.equals(ConstantWeka.CLASSIFIER_RANDOM_FOREST)) {
				System.err.println("Training random forest...");
				this.classifier = new RandomForest();
			}
			// Support Vector Machines
			if (function.equals(ConstantWeka.CLASSIFIER_SVM)) {
				System.err.println("Training support vector machines...");
				this.classifier = new SMO();
			}
			// Logistic Regression
			if (function.equals(ConstantWeka.CLASSIFIER_LOGISTIC)) {
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
	
}
