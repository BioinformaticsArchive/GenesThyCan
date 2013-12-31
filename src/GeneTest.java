
import genetukit.api.GNProcessor;
import genetukit.api.GNResultItem;

import java.io.BufferedReader;
import java.util.Properties;

/*
 * The test is now runnable. 
 * Things to note:
 * 1. the dragon tool jar library needs to be updated to the latest version
 * 2. The config file needs to be modified for database access
 */
public class GeneTest {
	public static void getEnv(){
		   BufferedReader br;
	       Process p;
	        Properties envVars;
	        Runtime r;
	        String line, OS, key, value;
	        int idx;

	        try{
	            r = Runtime.getRuntime();
	            envVars = new Properties();
	            OS = System.getProperty("os.name").toLowerCase();
	            
	            System.out.println(OS);
	           
	            if (OS.indexOf("windows 9") > -1) {
	               // p = r.exec("command.com /c set");
	            	
	            	System.out.println("command.com /c set");
	            	
	            } else if ( (OS.indexOf("nt") > -1) || (OS.indexOf("windows") > -1)) {
	                // thanks to JuanFran for the xp fix!
	               // p = r.exec("cmd.exe /c set");
	            	System.out.println("cmd.exe /c set");
	            } else {
	                // our last hope, we assume Unix (thanks to H. Ware for the fix)
	               // p = r.exec("env");
	            	System.out.println("env");
	            }
	        }
	        catch(Exception e){
	            e.printStackTrace();
	        }
	}
	public static void testGeneTUKit(){

		boolean useBanner = true;
		GNProcessor processor = new GNProcessor();
		processor.open(useBanner);
		GNProcessor.FileType fileType = GNProcessor.FileType.NXML;
		GNResultItem[] items = processor.process("2858709.nxml", fileType);
		processor.close();
		//Output GN results:
		for(int j=0; j<items.length; j++)
		{
		    StringBuffer sb = new StringBuffer();
		    sb.append(items[j].getID());
		    sb.append("\t");
		    for(int k=0; k<items[j].getGeneMentionList().size(); k++)
		    {
		        if(k!=0) sb.append("|");
		        sb.append(items[j].getGeneMentionList().get(k));
		    }
		    sb.append("\t");
		    sb.append(items[j].getSpeciesID());
		    sb.append("\t");
		    sb.append(items[j].getScore());
		    System.out.println(sb.toString());
		}
	}
	public static void main(String[] args){
	
		testGeneTUKit();
	}
}
