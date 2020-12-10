package biolockj.dataType.seq;

import biolockj.dataType.BasicDataUnit;

public class FastaData extends BasicDataUnit implements SeqData {

	protected FastaFormat format;
	protected SequenceAlphabet alphabet;
	protected boolean paired;
	
	public FastaData() {
		format = new FastaFormat();
		alphabet = new NucleicAcid();
	}
	
	public FastaData(FastaFormat format, SequenceAlphabet alphabet, boolean paired) {
		this.format = format;
		this.alphabet = alphabet;
		this.paired = paired;
	}

	@Override
	public String getDescription() {
		String maxLine = "";
		if (format.multiLineSeq) {
			maxLine = "A sequence may include newline characters."; 
			if (format.getSequenceLineLength() > 0) 
				maxLine += " Each line of sequence will include up to " + format.getSequenceLineLength() + " characters.";
		}
		else maxLine = "A sequence is on exactly one line.";
		
		return "Sequence data in FASTA format. The sequence itself is encoded within the alphabet [" +
						alphabet.getCharacters().toString() + "]." + maxLine;
	}

	@Override
	public SequenceAlphabet getAlphabet() {
		return alphabet;
	}

	@Override
	public SequenceFormat getFormat() {
		return format;
	}

	@Override
	public boolean isPaired() {
		return paired;
	}

}
