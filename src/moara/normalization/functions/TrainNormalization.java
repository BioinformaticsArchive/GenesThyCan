package moara.normalization.functions;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.SQLException;
import java.util.StringTokenizer;
import moara.bio.BioConstant;
import moara.gene.entities.Gene;
import moara.gene.dbs.DBGene;
import moara.gene.dbs.DBGeneGO;
import moara.gene.dbs.DBGeneProduct;
import moara.gene.dbs.DBGeneSynonym;
import moara.gene.dbs.DBGeneTerm;
import moara.gene.dbs.DBGeneTextTerm;
import moara.gene.functions.GeneTextIndexing;
import moara.bio.dbs.DBOrganism;
import moara.bio.entities.Organism;
import moara.normalization.dbs.DBGeneSynonymFeature;
import moara.normalization.dbs.DBFeaturePair;

public class TrainNormalization {

	private Organism organism;
	private boolean useML;
	private String shortName;
	private RandomAccessFile rwInfo;
	private RandomAccessFile rwGo;
	
	public TrainNormalization(Organism organism) {
		this.organism = organism;
		this.useML = false;
	}
	
	public void useMachineLearningNormalization() {
		this.useML = true;
	}
	
	public void truncateOrganismData() {
		try {
			// clean gene info
			System.err.println("Truncating gene information...");
			DBGene dbGene = new DBGene(this.organism);
			dbGene.deleteGene();
			// clean gene synonyms
			System.err.println("Truncating gene synonyms...");
			DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(this.organism);
			dbGeneSynonym.deleteGeneSynonym();
			// clean gene_go info
			System.err.println("Truncating gene GO information...");
			DBGeneGO dbGeneGO = new DBGeneGO(this.organism);
			dbGeneGO.deleteGeneGO();
			// clean gene products
			System.err.println("Truncating gene products...");
			DBGeneProduct dbGeneProduct = new DBGeneProduct();
			dbGeneProduct.deleteGeneProduct(this.organism);
			// clean gene_terms
			System.err.println("Truncating gene terms...");
			DBGeneTerm dbGeneTerm = new DBGeneTerm(this.organism);
			dbGeneTerm.truncateTerm();
			// clean gene text terms 
			System.err.println("Truncating gene text terms...");
			DBGeneTextTerm dbGeneTextTerm = new DBGeneTextTerm(this.organism);
			dbGeneTextTerm.truncateTermGene();
			// clean gene synonym feature
			System.err.println("Truncating gene synonym features...");
			DBGeneSynonymFeature dbGeneSynonymFeature = new DBGeneSynonymFeature(this.organism);
			dbGeneSynonymFeature.trucateSynonymFeature();
			// clean feature pairs
			System.err.println("Truncating feature pairs...");
			DBFeaturePair dbFeaturePair = new DBFeaturePair(this.organism);
			dbFeaturePair.truncateFeaturePair();
		}
		catch(SQLException e) { 
        	System.err.println(e);
        }
	}
	
	public void cleanGeneData() {
		try {
			// clean gene info
			System.err.println("Cleaning gene information...");
			DBGene dbGene = new DBGene(this.organism);
			dbGene.dropTableGene();
			dbGene.executeCommit();
			// clean gene synonyms
			System.err.println("Cleaning gene synonyms...");
			DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(this.organism);
			dbGeneSynonym.dropTableGeneSynonym();
			dbGeneSynonym.executeCommit();
			// clean gene_go info
			System.err.println("Cleaning gene GO information...");
			DBGeneGO dbGeneGO = new DBGeneGO(this.organism);
			dbGeneGO.dropTableGeneGO();
			dbGeneGO.executeCommit();
			// clean gene products
			System.err.println("Cleaning gene products...");
			DBGeneProduct dbGeneProduct = new DBGeneProduct();
			dbGeneProduct.deleteGeneProduct(this.organism);
			dbGeneProduct.executeCommit();
			// clean gene_terms
			System.err.println("Cleaning gene terms...");
			DBGeneTerm dbGeneTerm = new DBGeneTerm(this.organism);
			dbGeneTerm.dropTableGeneTerm();
			dbGeneTerm.executeCommit();
			// clean gene text terms 
			System.err.println("Cleaning gene text terms...");
			DBGeneTextTerm dbGeneTextTerm = new DBGeneTextTerm(this.organism);
			dbGeneTextTerm.dropTableGeneTextTerm();
			dbGeneTextTerm.executeCommit();
			// clean gene synonym feature
			System.err.println("Cleaning gene synonym features...");
			DBGeneSynonymFeature dbGeneSynonymFeature = new DBGeneSynonymFeature(this.organism);
			dbGeneSynonymFeature.dropTableGeneSynonymFeature();
			dbGeneSynonymFeature.executeCommit();
			// clean feature pairs
			System.err.println("Cleaning feature pairs...");
			DBFeaturePair dbFeaturePair = new DBFeaturePair(this.organism);
			dbFeaturePair.dropTableFeaturePair();
			dbFeaturePair.executeCommit();
		}
		catch(SQLException e) { 
        	System.err.println(e);
        }
	}
	
	private void printOrganismInfo(String directory, String line) {
		try {
			if (this.rwInfo==null)
				this.rwInfo = new RandomAccessFile(directory+"/"+this.shortName+"_info.txt","rw");
			this.rwInfo.writeBytes(line+"\n");
		}
		catch(IOException e) { 
        	System.err.println(e);
        }
	}
	
	private void printOrganismGO(String directory, String line) {
		try {
			if (this.rwGo==null)
				this.rwGo = new RandomAccessFile(directory+"/"+this.shortName+"2go.txt","rw");
			this.rwGo.writeBytes(line+"\n");
		}
		catch(IOException e) { 
        	System.err.println(e);
        }
	}
	
	// NCBI gene_info file
	private void readGeneFile(String directory) {
		try {
			// clean old genes and synonyms
			DBGene dbGene = new DBGene(this.organism);
			dbGene.deleteGene();
			dbGene.executeCommit();
			DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(this.organism);
			dbGeneSynonym.deleteGeneSynonymType(BioConstant.SYNONYM_LIST);
			dbGeneSynonym.executeCommit();
			// read file
			//RandomAccessFile r = new RandomAccessFile(directory+"/" + this.shortName + "_info.txt","r");
			RandomAccessFile r = new RandomAccessFile(directory+"/gene_info","r");
            int count = 0;
            boolean found = false;
            String lastOrg = "xxx";
            String line = r.readLine();
            while (line!=null) {
                if (found && !line.startsWith(lastOrg))
                	break;
                StringTokenizer tokens = new StringTokenizer(line,"\t");
                if (tokens.hasMoreTokens()) {
                    // organism ID
                	String orgCode = tokens.nextToken().trim();
                	if (orgCode.equals(this.organism.Code())) {
                		if (!found)
                			found = true;
                		//System.err.println(line);
                		String id = tokens.nextToken().trim();
                		Gene gene = new Gene(id);
                		String symbol = tokens.nextToken().trim();
                		gene.setSymbol(symbol);
                        String locus = tokens.nextToken().trim();
                        if (locus.equals("-"))
                        	locus = "";
                        gene.setName(locus);
                        String synonyms = tokens.nextToken().trim();
                        synonyms += "|"+symbol+"|"+locus;
                        StringTokenizer ids = new StringTokenizer(tokens.nextToken(),"|");
                        while (ids.hasMoreTokens())
                        	gene.addId(ids.nextToken());
                        tokens.nextToken(); //chromosome
                        tokens.nextToken(); // map location
                        String description = tokens.nextToken();
                        gene.setDescription(description);
                        gene.setPhenotype("");
                        // insert gene
                        dbGene.insertGene(gene);
                        // save synonyms
                        if (synonyms.length()>0 && !synonyms.equals("-"))
                        	saveSynonyms(id,synonyms);
                        printOrganismInfo(directory,line);
                    }
                	if (!lastOrg.equals(orgCode))
                		System.err.println("{" + orgCode + "}");
                	lastOrg = orgCode;
                }
            	count++;
				if ((count%10000)==0)
					System.err.print(count + "...");
				if ((count%100000)==0)
					System.err.println("");
				line = r.readLine();
            }
            r.close();
            if (this.rwInfo!=null)
				this.rwInfo.close();
        }
        catch (IOException e) {
            System.err.println(e);
        }
        catch(SQLException e) { 
        	System.err.println(e);
        }
    }
	
	private void saveSynonyms(String id, String synonyms) {
		DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(this.organism);
		try {
			StringTokenizer tokens = new StringTokenizer(synonyms,"|");
			while (tokens.hasMoreTokens()) {
				String synonym = tokens.nextToken().trim();
				dbGeneSynonym.insertGeneSynonymType(id,BioConstant.SYNONYM_LIST,synonym,"");
			}
			dbGeneSynonym.executeCommit();
		}
		catch(SQLException e) { 
        	System.err.println(e);
        	dbGeneSynonym.executeRollback();
        }
	}
	
	// NCBI gene2go file
	private void readGeneGOFile(String directory) {
		DBGeneGO dbGeneGO = new DBGeneGO(this.organism);
		try {
			// clean old GOs
			dbGeneGO.deleteGeneGO();
			dbGeneGO.executeCommit();
			// read file
			RandomAccessFile r = new RandomAccessFile(directory+"/gene2go","r");
			//RandomAccessFile r = new RandomAccessFile(directory+"/" + this.shortName + "2go.txt","r"); 
			int count = 0;
			boolean found = false;
            String lastOrg = "xxx";
            String line = r.readLine();
            while (line!=null) {
            	if (found && !line.startsWith(lastOrg))
                	break;
            	StringTokenizer tokens = new StringTokenizer(line,"\t");
                if (tokens.hasMoreTokens()) {
                    // organism ID
                	String orgCode = tokens.nextToken().trim();
                	if (orgCode.equals(this.organism.Code())) {
                		if (!found)
                			found = true;
                		String id = tokens.nextToken().trim();
                		String go = tokens.nextToken().trim();
                		// insert gene-GO
                		dbGeneGO.insertGeneGO(id,go);
                		printOrganismGO(directory,line);
                	}
                	/*if (!lastOrg.equals(orgCode))
                		System.err.println("{" + orgCode + "}");*/
                	lastOrg = orgCode;
                }
            	count++;
                if ((count%10000)==0)
					System.err.print(count + "...");
				if ((count%100000)==0)
					System.err.println("");
				line = r.readLine();
			}
            r.close();
            dbGeneGO.executeCommit();
            if (this.rwGo!=null)
				this.rwGo.close();
        }
        catch (IOException e) {
            System.err.println(e);
        }
        catch(SQLException e) { 
        	System.err.println(e);
        	dbGeneGO.executeRollback();
        }
    }
	
	private void orderDictionary() {
		DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(this.organism);
		try {
			// verify gene synonyms
			int total = dbGeneSynonym.getTotalSynonymType(BioConstant.SYNONYM_LIST);
			if (total==0) {
				System.err.println("No synonyms found for the organism. You must " +
					"first import the synonyms.");
			}
			else {
				GeneSynonymSelection gss = new GeneSynonymSelection();
				gss.orderGeneSynonyms(this.organism);
				dbGeneSynonym.executeCommit();
			}
		}
		catch(SQLException ex) { 
			System.err.println(ex);
			dbGeneSynonym.executeRollback();
		}
	}
	
	private void getFeaturesSynonyms() {
		DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(this.organism);
		try {
			// verify gene synonyms
			int total = dbGeneSynonym.getTotalSynonymType(BioConstant.SYNONYM_ORDERED);
			if (total==0) {
				System.err.println("No synonyms found for the organism. You must " +
					"first import the synonyms.");
			}
			else {
				GeneSynonymFeature gsf = new GeneSynonymFeature();
				gsf.extractFeaturesSynonyms(organism);
				dbGeneSynonym.executeCommit();
			}
		}
		catch(SQLException ex) { 
			System.err.println(ex);
			dbGeneSynonym.executeRollback();
		}
	}
	
	private void createTables() {
		try {
			// gene table
			System.err.println("Creating organism tables...");
			DBGene dbGene = new DBGene(this.organism);
			dbGene.createTableGene();
			dbGene.executeCommit();
			// gene go table
			DBGeneGO dbGeneGO = new DBGeneGO(this.organism);
			dbGeneGO.createTableGeneGO();
			dbGeneGO.executeCommit();
			// gene synonyms table 
			DBGeneSynonym dbGeneSynonym = new DBGeneSynonym(this.organism);
			dbGeneSynonym.createTableGeneSynonym();
			dbGeneSynonym.executeCommit();
			// gene terms table
			DBGeneTerm dbGeneTerm = new DBGeneTerm(this.organism);
			dbGeneTerm.createTableGeneTerm();
			dbGeneTerm.executeCommit();
			// gene text terms table
			DBGeneTextTerm dbGeneTextTerm = new DBGeneTextTerm(this.organism);
			dbGeneTextTerm.createTableGeneTextTerm();
			dbGeneTextTerm.executeCommit();
			if (this.useML) {
				// create gene synonym feature
				DBGeneSynonymFeature dbGeneSynonymFeature = new DBGeneSynonymFeature(this.organism);
				dbGeneSynonymFeature.createTableGeneSynonymFeature();
				dbGeneSynonymFeature.executeCommit();
				// create feature pairs
				DBFeaturePair dbFeaturePair = new DBFeaturePair(this.organism);
				dbFeaturePair.createTableFeaturePair();
				dbFeaturePair.executeCommit();
			}
		}
		catch(SQLException ex) { 
			System.err.println(ex);
		}
	}
	
	public void train(String name, String directory) {
		try {
			// update short name
			this.shortName = name;
			System.err.println("Updating short name...");
			DBOrganism dbOrganism = new DBOrganism();
			dbOrganism.updateShortName(this.organism,name);
			dbOrganism.executeCommit();
			// create tables and indexes
			//truncateOrganismData();
			createTables();
			// read genes
			System.err.println("Reading gene information...");
			readGeneFile(directory);
			// read gene go
			readGeneGOFile(directory);
			// process gene texts
			System.err.println("Processing gene text...");
			GeneTextIndexing index = new GeneTextIndexing(this.organism);
			index.processGeneText();
			// order dictionary of synonyms
			orderDictionary();
			// features synonym
			if (this.useML)
				getFeaturesSynonyms();
		}
		catch(SQLException ex) { 
			System.err.println(ex);
		}
	}
	
	public static void main(String[] args) {
		TrainNormalization app = new TrainNormalization(new Organism("4932"));
		//app.useMachineLearningNormalization();
		app.train("yeast","normalization");
		//app.cleanGeneData();
		
		
		/*
		try {
			DBGeneSynonymFeature dbGeneSynonymFeature = new DBGeneSynonymFeature(app.dbNorm,
				app.dbMoara,app.organism);
			dbGeneSynonymFeature.createTableGeneSynonymFeature();
			// create feature pairs
			DBFeaturePair dbFeaturePair = new DBFeaturePair(app.dbNorm,app.dbMoara,app.organism);
			dbFeaturePair.createTableFeaturePair();
			app.getFeaturesSynonyms();
		}
		catch(SQLException ex) { 
			System.err.println(ex);
			app.dbMoara.executeRollback();
			app.closeDatabase();
		}*/
		
		// app.orderDictionary();
	} 
	
}
