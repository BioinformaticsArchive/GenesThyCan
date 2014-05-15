package moara.util.lexicon;

public class AminoAcid {

	// amino acids symbols
	public static String AMINO_ACID_Ala = "Ala";
	public static String AMINO_ACID_Arg = "Arg";
	public static String AMINO_ACID_Asn = "Asn";
	public static String AMINO_ACID_Asp = "Asp";
	public static String AMINO_ACID_Cys = "Cys";
	public static String AMINO_ACID_Glu = "Glu";
	public static String AMINO_ACID_Gln = "Gln";
	public static String AMINO_ACID_Gly = "Gly";
	public static String AMINO_ACID_His = "His";
	public static String AMINO_ACID_Ile = "Ile";
	public static String AMINO_ACID_Leu = "Leu";
	public static String AMINO_ACID_Lys = "Lys";
	public static String AMINO_ACID_Met = "Met";
	public static String AMINO_ACID_Phe = "Phe";
	public static String AMINO_ACID_Pro = "Pro";
	public static String AMINO_ACID_Ser = "Ser";
	public static String AMINO_ACID_Thr = "Thr";
	public static String AMINO_ACID_Trp = "Trp";
	public static String AMINO_ACID_Tyr = "Tyr";
	public static String AMINO_ACID_Val = "Val";
	public String[] arrayCode = {AMINO_ACID_Ala,AMINO_ACID_Arg,AMINO_ACID_Asn,
		AMINO_ACID_Asp,AMINO_ACID_Cys,AMINO_ACID_Glu,AMINO_ACID_Gln,AMINO_ACID_Gly,
		AMINO_ACID_His,AMINO_ACID_Ile,AMINO_ACID_Leu,AMINO_ACID_Lys,AMINO_ACID_Met,
		AMINO_ACID_Phe,AMINO_ACID_Pro,AMINO_ACID_Ser,AMINO_ACID_Thr,AMINO_ACID_Trp,
		AMINO_ACID_Tyr,AMINO_ACID_Val};
	public String[] arrayName = {"alanine","arginine","asparagine",
		"aspartic acid","cysteine","glutamic acid","glutamine","glycine",
		"histidine","isoleucine","leucine","lysine","methionine",
		"phenylalanine","proline","serine","threonine","tryptophan",
		"tyrosine","valine"};
	
	public AminoAcid() {
		
	}
	
	public boolean isAminoAcidCode(String token) {
		String[] a = arrayCode;
		for (int i=0; i<a.length; i++) {
			if (a[i].equals(token))
				return true;
		}
		return false;
	}
	
	public boolean isAminoAcidName(String token) {
		String[] a = arrayName;
		for (int i=0; i<a.length; i++) {
			if (a[i].equals(token))
				return true;
		}
		return false;
	}
}
