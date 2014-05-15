import java.util.List;

import uk.ac.man.PathNER.PathNER;
import uk.ac.man.PathNER.PathNER.MergedMatch;
import uk.ac.man.Utils.ArgParser;


public class TestPathNER {

	/**
	 * @param args
	 */
public static void main(String[] args) throws Exception{
		
		PathNER pathNER = new PathNER();
		pathNER.initGate();
		
		//Handling command line parameters
		ArgParser ap = new ArgParser(args);
		pathNER.reportNumber = ap.getInt("report", 5);
		String goldTestFileName = ap.get("test");
		boolean debug_flag = ap.getBoolean("debug", false);
		
		if(debug_flag){
			pathNER.writeGateDoc = true;
			pathNER.verbose = true;
		}
		
		pathNER.verbose = true;
		
		System.out.println("\n\n***********************************");
		System.out.println("Testing PathNer on a text file");
		System.out.println("***********************************\n\n\n");
		
		String inFile = "pathmen_file_test.txt";
		
		
		String outFile = "file_test_result.txt";
		List<MergedMatch> result = pathNER.testOnFile(inFile, outFile);
		
		System.out.println(result.size() + " mentions found!");
		
		System.out.println("\n\n***********************************");
		System.out.println("PathNer text file test completed!");
		System.out.println("***********************************\n\n\n");
		
	}

}
