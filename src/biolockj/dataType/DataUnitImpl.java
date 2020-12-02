package biolockj.dataType;

import java.io.File;
import java.util.List;

/**
 * The minimal assumed DataUnit implementation that could not be represented in default methods.
 * @author Ivory Blakley
 *
 */
public abstract class DataUnitImpl implements DataUnit {

	private List<File> files = null;
	
	private boolean multiple;

	@Override
	public void setFiles( List<File> files ) {
		this.files = files;
	}

	@Override
	public List<File> getFiles() {
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
