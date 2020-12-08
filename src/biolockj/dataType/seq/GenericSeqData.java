package biolockj.dataTypes.seq;

import java.io.File;
import java.io.FilenameFilter;
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
		return new SequenceAlphabet() {
			
			@Override
			public char[] getCharacters() {
				// Anything that actually wants to examine the alphabet, will surely use a less generic class.
				return "abcdefjhijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-.?1234567890".toCharArray();
			}
		};
	}

	@Override
	public SequenceFormat getFormat() {
		return new SequenceFormat() {
			
			@Override
			public FilenameFilter getFilenameFilter() {
				return new FilenameFilter() {
					
					@Override
					public boolean accept( File dir, String name ) {
						return true;
					}
				};
			}
		};
	}

	@Override
	public boolean isPaired() {
		return false;
	}

}
