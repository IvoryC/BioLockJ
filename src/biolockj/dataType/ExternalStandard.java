package biolockj.dataType;

/**
 * A broad interface for any class that represents a tool, or file format, or standard of any kind.
 * 
 * @author Ivory Blakley
 *
 */
public interface ExternalStandard {
	
	/**
	 * What standard are you talking about.
	 * This could be "FASTQ" or "IUPAC nucleic acid notation"
	 * @return
	 */
	String whatIs();
	
	/**
	 * Who defines this standard.
	 * This could "John Stewart" or "IUPAC" or "The Baker Lab at NYC" or ...
	 * @return
	 */
	String whoSays();
	
	/**
	 * Where is the official documentation for this standard.
	 * This could be a URL for a user guide, or a link to a publication, or a citation string for some reference, or ...
	 * Cornish-Bowden A. Nomenclature for incompletely specified bases in nucleic acid sequences: recommendations 1984. Nucleic Acids Res. 1985 May 10;13(9):3021-30. doi: 10.1093/nar/13.9.3021. PMID: 2582368; PMCID: PMC341218.
	 * @return
	 */
	String whereDocs();

}
