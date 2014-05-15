package moara.util.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class OutputFile {
	
	private String directory;
	private String fileName;
	private RandomAccessFile rw;
	
	public OutputFile(String directory, String fileName) {
		this.directory = directory;
		this.fileName = fileName;
	}
	
	public void init(String[] titles) {
		try {
			File err = new File(this.directory + "/" + this.fileName);
			if (err.exists())
				err.delete();
			err.createNewFile();
		    if (err.exists()) {
		     	this.rw = new RandomAccessFile(err,"rw");
		     	// titles
		     	for (int i=0; i<titles.length; i++) {
		     		rw.writeBytes("|" + titles[i]);
		     	}
		     	rw.writeBytes("|\n");
		    }
		}
		catch(IOException ex) { 
			System.err.println(ex);
		}
	}
	
	public void end() {
		try {
			rw.close();
		}
		catch(IOException ex) { 
			System.err.println(ex);
		}
	}
	
	public void print(String[] values) {
		try {
			for (int j=0; j<values.length; j++) {
     			rw.writeBytes("|" + values[j]);
     		}
     		rw.writeBytes("|\n");
		}
		catch(IOException ex) { 
			System.err.println(ex);
		}
	}
	
}
