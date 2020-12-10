package biolockj.dataType.seq;

import java.io.File;
import java.io.FilenameFilter;

/**
 * The common fasta format. 
 * 
 * This implementation design was largely guided by the wikipedia page: https://en.wikipedia.org/wiki/FASTA_format
 * This page (as of 9 Dec 2020) credits David J. Lipman and William R. Pearson with developing the format, and references the FASTA suite of alignment tools.
 * 
 * A particular note about the format (taken from wikipedia):<br>
 * FASTA is pronounced "fast A", and stands for "FAST-All", because it works with any alphabet, an extension of the original "FAST-P" (protein) and "FAST-N" (nucleotide) alignment tools.
 * 
 * Note that this file format does not specify any particular {@link SequenceAlphabet}
 * 
 * Each sequence is described with two or more lines. The first line starts with a ">"
 * character and all following information on the line is the sequence name, no format restrictions. The following
 * line(s) are the the actual sequence.
 * 
 * @author Ivory Blakley
 *
 */
public class FastaFormat implements SequenceFormat {

	public FastaFormat() {}
	
	public FastaFormat(int maxLineLength) {
		if (maxLineLength == -1) {
			multiLineSeq = false;
		}else {
			multiLineSeq = true;
			sequenceLineLength = maxLineLength;
		}
	}
		
	public boolean multiLineSeq = true;
	
	/**
	 * IF a sequence is longer than the {@link #sequenceLineLength}, then it wraps to the next line.
	 */
	private int sequenceLineLength;
	
	public int getSequenceLineLength() {
		return sequenceLineLength;
	}

	protected void setSequenceLineLength( int sequenceLineLength ) {
		this.sequenceLineLength = sequenceLineLength;
	}

	@Override
	public FilenameFilter getFilenameFilter() {
		return new FilenameFilter() {
			
			@Override
			public boolean accept( File dir, String name ) {
				boolean accept = false;
				for (String ending : ends) {
					if (name.endsWith( ending ) ) accept = true;
				}
				return accept;
			}
			
		};
	}
	
	private String[] ends = {"fa", "fasta", "fna", "ffn", "faa", "frn"};

}
