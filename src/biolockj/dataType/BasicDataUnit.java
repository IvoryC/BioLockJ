package biolockj.dataType;

import java.io.File;
import java.util.Collection;
import java.util.List;
import biolockj.exception.BioLockJException;

public abstract class BasicDataUnit<T extends BasicDataUnit> implements DataUnit, DataUnitFactory<T> {
	
	private List<File> files = null;

	@Override
	public void setFiles( List<File> files ) {
		this.files = files;
	}

	@Override
	public List<File> getFiles() {
		return files;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> getActualData( List<File> files ) throws BioLockJException {
		return getActualData(files, (T) this);
	}

}
