package biolockj.dataType.seq;

import java.util.HashMap;
import java.util.Map;

public class NucleicAcid implements SequenceAlphabet {

	public NucleicAcid() {}
	
	public char[] getCharacters() {
		return "ACGTUWSMKRYBDHVNZX".toCharArray();
	}
	
	public static char[] expandAmbiguityChar(char c){
		return getExpansionMap().get( String.valueOf( c ) ).toCharArray();
	}
	
	private static Map<String, String> DNA_BASE_MAP = new HashMap<>();
	private static Map<String, String> getExpansionMap() {
		if (DNA_BASE_MAP.isEmpty()) {
			// IUPAC DNA BASE Substitutions
			// http://www.dnabaser.com/articles/IUPAC%20ambiguity%20codes.html
			DNA_BASE_MAP.put( "A", "A" );
			DNA_BASE_MAP.put( "C", "C" );
			DNA_BASE_MAP.put( "G", "G" );
			DNA_BASE_MAP.put( "T", "T" );
			DNA_BASE_MAP.put( "Y", "CT" );
			DNA_BASE_MAP.put( "R", "AG" );
			DNA_BASE_MAP.put( "W", "AT" );
			DNA_BASE_MAP.put( "S", "CG" );
			DNA_BASE_MAP.put( "K", "GT" );
			DNA_BASE_MAP.put( "M", "AC" );
			DNA_BASE_MAP.put( "D", "AGT" );
			DNA_BASE_MAP.put( "V", "ACG" );
			DNA_BASE_MAP.put( "H", "ACT" );
			DNA_BASE_MAP.put( "B", "CGT" );
			DNA_BASE_MAP.put( "N", "ACGT" );
			DNA_BASE_MAP.put( "X", "ACGT" );
			DNA_BASE_MAP.put( "Z", "" );
		}
		return DNA_BASE_MAP;
	}

}
