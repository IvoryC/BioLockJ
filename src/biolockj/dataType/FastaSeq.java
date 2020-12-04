package biolockj.dataType;

import java.io.File;
import java.io.FilenameFilter;
import biolockj.Log;
import biolockj.exception.BioLockJException;

public class FastaSeq extends BasicDataUnit {

	public FastaSeq() {	}

	@Override
	public String getDescription() {
		return "A fasta file. This file type has one or more sequences. Each sequence is represented as a name line followed by the sequence line. The name line begins with the greater than character '>' ";
	}

	@Override
	public boolean isValid() throws BioLockJException {
		// read the first 500 lines 
		return true;
	}

	@Override
	public FilenameFilter getFilenameFilter() {

		return new FilenameFilter() {

			@Override
			public boolean accept( File dir, String name ) {
				boolean isFa = false;
				for (String sfx : fileSuffix) {
					if (name.endsWith( sfx )) isFa = true;
				}
				if (!isFa) Log.debug(this.getClass(), "The file [" + name + "] does not have a normal fasta file suffix.");
				return isFa;
			}
			
		};
	}

	public static final String FA = ".fa";
	public static final String FASTA = ".fasta";
	
	public static final String[] fileSuffix = {FA, FASTA};

}
