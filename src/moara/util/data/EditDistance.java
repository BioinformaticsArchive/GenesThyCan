package moara.util.data;

public class EditDistance {

	private double[][] matrix;
	
	public EditDistance() {
		
	}
	
	public double score(EditDistanceItem item1, EditDistanceItem item2, double maxCost) {
		double score = 0;
		item1.clear();
		item2.clear();
		// create scoring matrix
		int length1 = item1.getLenght();
		int length2 = item2.getLenght();
		this.matrix = new double[length1+1][length2+1];
	    // set initial value
	    this.matrix[0][0] = 0;
	    // set values of first column
	    for (int j=0; j<length1; j++) {
	    	this.matrix[j+1][0] = this.matrix[j][0] + item1.getInclusionCost(j);
	    }
	    // set value for the rest of the matrix
	    for (int i=0; i<length2; i++) {
	    	// set values of the first row
	    	this.matrix[0][i+1] = this.matrix[0][i] + item2.getExclusionCost(i);
	    	double minColumn = maxCost+1;
	    	for (int j=0; j<length1; j++) {
	    		this.matrix[j+1][i+1] =  min(
	    			this.matrix[j][i] + item2.getSubstitutionCost(i,item1,j),
	    			this.matrix[j+1][i] + item2.getExclusionCost(i),
	    			this.matrix[j][i+1] + item1.getInclusionCost(j)
	    		);
	    		// save min value of the column
	    		if (this.matrix[j+1][i+1]<minColumn)
	    			minColumn = this.matrix[j+1][i+1];
	    	}
	    	score = this.matrix[length1][i+1];
	    	// check max cost
	    	if (maxCost>0 && (minColumn>maxCost)) {
	    		//System.out.println("break " + minColumn + " " + maxCost);
	    		return -1;
	    		//break;
	    	}
	    }
	    // print final cost matrix
	    /*for (int i=0; i<this.matrix.length; i++) {
	    	System.out.print(i+1 + "\t");
	    	for (int j=0; j<this.matrix[i].length; j++)
	    		System.out.print(this.matrix[i][j] + "\t");
	    	System.out.println();
	    }*/
	    return score;
	}
	
	private double min(double a, double b, double c) {
		return Math.min(Math.min(a, b), c);
	}
	
}

