package biolockj.module.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import biolockj.dataType.DataUnit;
import biolockj.exception.BioLockJException;
import biolockj.exception.ModuleInputException;
import biolockj.module.BioModule;
import biolockj.util.ModuleUtil;

public interface ModuleIO extends BioModule {

	/**
	 * A human and technical description of the modules input requirements.
	 * @return
	 */
	public List<ModuleInput> getInputTypes() throws ModuleInputException;

	/**
	 * Determine an InputSource for each of the modules InputSpecs.
	 */
	public default void assignInputSources() throws BioLockJException{
		ModuleUtil.assignInputSources(this);
	}

	/**
	 * A human and technical definition of the module output types.
	 * @return
	 */
	public List<ModuleOutput> getOutputTypes() throws ModuleInputException;
	
	/**
	 * This does not override {@link biolockj.module.BioModuleImpl#getInputFiles()}.
	 * But it may eventually replace it.  And in the mean time, it is a convenient reference while migrating modules.
	 */
	@Override
	public default List<File> getInputFiles() throws ModuleInputException {
		final List<File> files = new ArrayList<>();
		final List<DataUnit> data = new ArrayList<>();
		try {
			for( ModuleInput input: getInputTypes() ) {
				data.addAll( input.getSource().getData() );
			}
			for( DataUnit du: data ) {
				files.addAll( du.getFiles() );
			}
		} catch( Exception e ) {
			throw new ModuleInputException( this, e );
		}
		return files;
	}

}
