package biolockj.dataType;

import java.io.File;
import java.util.Collection;
import java.util.List;
import biolockj.exception.ModuleInputException;

/**
 * Basic implementation to allow a a single class to implement both DataUnit and DataUnitFactory.
 * @author Ivory Blakley
 *
 * @param <T>
 */
public abstract class BasicDataUnit extends DataUnitImpl implements DataUnitFactory<DataUnit> {

	@Override
	public Collection<DataUnit> getData( List<File> files, DataUnit template ) throws ModuleInputException {
		return DataUnitFactory.super.getData( files, (DataUnit) this );
	}

	@Override
	public DataUnitFactory<DataUnit> getFactory() {
		return this;
	}

}
