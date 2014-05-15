package moara.util.lexicon;

public class NucleicAcid {

	public static String ADN_A = "A";
	public static String ADN_G = "G";
	public static String ADN_T = "T";
	public static String ADN_C = "C";
	public static String ADN_U = "U";
	public String[] arrayNucleicAcid = {ADN_A,ADN_G,ADN_T,ADN_C,ADN_U};
	
	public NucleicAcid() {
		
	}
	
	public boolean isNucleicAcid(String token) {
		String[] a = arrayNucleicAcid;
		for (int j=0; j<token.length(); j++) {
			String s = token.substring(j,j+1);
			boolean found = false;
			for (int i=0; i<a.length; i++) {
				if (a[i].equals(s))
					found = true;;
			}
			if (!found)
				return false;
		}
		return true;
	}
	
}