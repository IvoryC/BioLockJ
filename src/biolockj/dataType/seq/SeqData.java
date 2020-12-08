package biolockj.dataTypes.seq;

import biolockj.dataType.DataUnit;

public interface SeqData extends DataUnit {
	
	SequenceAlphabet getAlphabet();
	
	SequenceFormat getFormat();
	
	boolean	isPaired();

}
