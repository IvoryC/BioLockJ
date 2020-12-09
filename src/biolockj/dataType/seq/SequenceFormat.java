package biolockj.dataType.seq;

import java.io.File;
import java.io.FilenameFilter;

public interface SequenceFormat {
	
	/**
	 * Formats generally have some file name conventions, typically a file extension.
	 * This method may be used to help pick out appropriate files.
	 * @return
	 */
	public default FilenameFilter getFilenameFilter() {
		return new FilenameFilter() {
			
			//This accept-all default allows for cases when file name restrictions are not known.
			@Override
			public boolean accept( File dir, String name ) {
				return true;
			}
			
		};
	}

}
