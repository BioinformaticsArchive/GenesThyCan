package moara.bio.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.SQLException;
import java.util.StringTokenizer;
import moara.bio.dbs.DBBioThesaurus;
import moara.bio.BioConstant;
import moara.util.text.StringUtil;

public class ReadBioThesaurus {

	public ReadBioThesaurus() {
		
	}
	
	public void read(String file, String type) {
		DBBioThesaurus dbBioThesaurus = new DBBioThesaurus();
		try {
			// clean old synonyms
			dbBioThesaurus.deleteBioThesaurus(type);
			// read synonyms 
			StringUtil su = new StringUtil();
			RandomAccessFile r = new RandomAccessFile(file,"r");
            int count = 0;
            String line = r.readLine();
            while (line!=null) {
            	//System.out.println(line);
            	StringTokenizer tokens = new StringTokenizer(line,": ");
                String info = ".";
                String frequencyString = "";
                String first = tokens.nextToken();
                if (!su.isNumeral(first)) {
                	info = first;
                	String second = tokens.nextToken();
                	if (!su.isNumeral(second)) {
                		info += " " + second;
                		frequencyString = tokens.nextToken();
                	}
                	else
                		frequencyString = second;
                }
                else
                	frequencyString = first;
                int frequency = (new Integer(frequencyString)).intValue();
                // term
                if (tokens.hasMoreTokens()) {
                	String term = tokens.nextToken().replace("'","");
                    dbBioThesaurus.insertBioThesaurus(term,frequency,type,info);
                }
                count++;
				if ((count%1000)==0) {
					System.out.println(count + "...");
				}
                line = r.readLine();
            }
            r.close();
            dbBioThesaurus.executeCommit();
        }
        catch (IOException e) {
            System.out.println(e);
            dbBioThesaurus.executeRollback();
        }
        catch(SQLException e) { 
        	System.out.println(e);
        	dbBioThesaurus.executeRollback();
        }
	}
	
	public static void main(String[] args) {
		ReadBioThesaurus app = new ReadBioThesaurus();
		app.read("normalization/biothesaurus/single_word_name_2008_08_05.txt",
			BioConstant.BIOTHESAURUS_SINGLE_WORD);
	}
	
}
