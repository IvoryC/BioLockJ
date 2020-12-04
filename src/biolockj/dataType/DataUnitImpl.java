package biolockj.dataType;

import java.io.File;
import java.util.List;
import biolockj.exception.BioLockJException;

/**
 * The minimal assumed DataUnit implementation that could not be represented in default methods.
 * @author Ivory Blakley
 *
 */
public abstract class DataUnitImpl implements DataUnit {

	private List<File> files = null;
	
	private boolean multiple;

	@Override
	public void setFiles( List<File> files ) throws BioLockJException {
		this.files = files;
	}

	@Override
	public List<File> getFiles() throws BioLockJException {
		return files;
	}

	@Override
	public boolean canBeMultiple() {
		return multiple;
	}

	@Override
	public void canBeMultiple( boolean multiple ) {
		this.multiple = multiple;
	}

}
