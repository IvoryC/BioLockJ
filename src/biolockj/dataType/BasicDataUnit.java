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
public abstract class BasicDataUnit<T extends DataUnitImpl> extends DataUnitImpl implements DataUnitFactory<T> {

	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> getData( List<File> files, T template ) throws ModuleInputException {
		return DataUnitFactory.super.getData( files, (T) this );
	}

	@Override
	public DataUnitFactory<?> getFactory() {
		return this;
	}

}
