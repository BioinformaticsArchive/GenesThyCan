package moara.wrapper.abner;

import abner.Trainer;

public class AbnerTrainer {

	public AbnerTrainer() {
		
	}
	
	public void trainBC2(String file, String model) {
		Trainer abner = new Trainer();
		abner.train(file,model);
	}
	
	public static void main(String[] args) {
		AbnerTrainer at = new AbnerTrainer();
		at.trainBC2("wrappers/abner_train_bc2_bc1yeastmousefly.in","wrappers/abner_bc2_bc1yeastmousefly.model");
	}
}
