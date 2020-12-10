package biolockj.dataType.seq;

import biolockj.dataType.BasicDataUnit;

public class FastqData extends BasicDataUnit implements SeqData {

	private FastqFormat format;
	private SequenceAlphabet alphabet;
	private boolean paired = false;
	
	public FastqData() {
		format = new FastqFormat("Solexa");
		alphabet = new NucleicAcid();
	}
	
	public FastqData(FastqFormat format, SequenceAlphabet alphabet, boolean paired) {
		this.format = format;
		this.alphabet = alphabet;
		this.paired = paired;
	}

	@Override
	public String getDescription() {
		return "Sequence data in FASTQ format. The sequence itself is encoded within the alphabet [" +
			alphabet.getCharacters().toString() + "], and the quality scores are encoded by characters [" +
			format.getQualScoreEncoding().getCharacters().toString() + "].";
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
