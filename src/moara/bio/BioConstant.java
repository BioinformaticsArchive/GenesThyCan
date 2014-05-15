package moara.bio;

public class BioConstant {

	// organism types of names
	static public String ORGANISM_SYNONYM = "synonym";
	static public String ORGANISM_ACRONYM = "acronym";
	static public String ORGANISM_MISSPELLING = "misspelling";
	static public String ORGANISM_SCIENTIFIC = "scientific name";
	static public String ORGANISM_COMMON_NAME = "common name";
	static public String ORGANISM_INCLUDES = "includes";
	static public String ORGANISM_GENBANK = "genbank common name";
	static public String ORGANISM_GENBANK_SYNONYM = "genbank synonym";
	static public String ORGANISM_GENBANK_ACRONYM = "genbank acronym";
	static public String ORGANISM_GENBANK_ANAMORPH = "genbank anamorph";
	static public String ORGANISM_MISNOMER = "misnomer";
	static public String ORGANISM_ANAMORPH = "anamorph";
	static public String ORGANISM_TELEOMORPH = "teleomorph";
	static public String ORGANISM_EQUIVALENT = "equivalent name";
	static public String ORGANISM_BLAST = "blast name";
	static public String ORGANISM_IN_PART = "in-part";
	static public String ORGANISM_AUTHORITY = "authority";
	static public String ORGANISM_UNPUBLISHED = "unpublished name";
	// derived from scientific name
	static public String ORGANISM_DERIVED = "derived";
	
	// BioThesaurus lexicon type
	static public String BIOTHESAURUS_BIOMEDICAL = "B";
	static public String BIOTHESAURUS_CHEMICAL = "C";
	static public String BIOTHESAURUS_ENGLISH = "E";
	static public String BIOTHESAURUS_ENZYME = "Z";
	static public String BIOTHESAURUS_GENERAL = "G";
	static public String BIOTHESAURUS_SINGLE_WORD = "S";
	static public String BIOTHESAURUS_MANUAL = "M";
	
	// type of synonyms
	public static String SYNONYM_LIST = "L";
	public static String SYNONYM_DESCRIPTION = "D";
	public static String SYNONYM_PRODUCT = "P";
	public static String SYNONYM_DERIVED = "R";
	public static String SYNONYM_ORDERED = "O";
	
	// status of synonym
	public static String SYNONYM_ALL = "A";
	public static String SYNONYM_RELIABLE = "R";
	
	// Uniprot databases
	public static String UNIPROT_SWISSPROT = "uniprot_swissprot";
	public static String UNIPROT_TREMBL = "uniprot_trembl";
	public static String ENTREZ_GENE = "ncbi";
	
	// database links
	public static String UNIPROT_LINK = "http://www.uniprot.org/uniprot/";
	public static String ENTREZ_GENE_LINK = "http://www.ncbi.nlm.nih.gov/gene/";
	
}
