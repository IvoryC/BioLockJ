package biolockj.dataType.seq;

import biolockj.dataType.BasicDataUnit;

/**
 * This class is absurdly generic. It is designed to be used as a functional stand-in when while the developer is still
 * figuring out just what types of data their module can take / make.
 * 
 * @author Ivory Blakley
 *
 */
public class GenericSeqData extends BasicDataUnit implements SeqData{

	@Override
	public String getDescription() {
		return "Sequence data, in the broadest sense.";
	}

	@Override
	public SequenceAlphabet getAlphabet() {
		return new SequenceAlphabet() {};
	}

	@Override
	public SequenceFormat getFormat() {
		return new SequenceFormat() {};
	}

	@Override
	public boolean isPaired() {
		return false;
	}

}
