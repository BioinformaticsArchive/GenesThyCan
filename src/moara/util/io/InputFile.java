package moara.util.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.StringTokenizer;

public class InputFile {

	private String directory;
	private String fileName;
	private HashMap<String,String[]> values;
	
	public InputFile(String directory, String fileName) {
		this.directory = directory;
		this.fileName = fileName;
		this.values = new HashMap<String,String[]>();
	}
	
	public HashMap<String,String[]> getValues() {
		return this.values;
	}
	
	public void read(String[] titles, int numKey) {
		try {
			RandomAccessFile r = new RandomAccessFile(this.directory + this.fileName,"r");
			String line = r.readLine();
			// skip titles
			line = r.readLine();
			while (line!=null) {
				StringTokenizer tokens = new StringTokenizer(line,"|");
				int index = 0;
				String key = "";
				String[] columns = new String[titles.length];
				while (tokens.hasMoreTokens()) {
					String token = tokens.nextToken();
					columns[index] = token;
					if (index<numKey)
						key += token + "_";
					index++;
				}
				key = key.substring(0,key.length()-1);
				this.values.put(key,columns);
				line = r.readLine();
			}
		}
		catch(IOException ex) { 
			System.err.println(ex);
		}
	}
	
	public String[] getColumns(String[] keyValues) {
		String key = "";
		for (int i=0; i<keyValues.length; i++) {
			key += keyValues[i] + "_";
		}
		key = key.substring(0,key.length()-1);
		return this.values.get(key);
	}
	
	public static void main(String[] args) {
		InputFile app = new InputFile("i2b2/","terms.txt");
		String[] titlesTerms = {"term","frequency","num_doc"};
		app.read(titlesTerms,1);
	}
	
}
