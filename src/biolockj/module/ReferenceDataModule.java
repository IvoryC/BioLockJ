package biolockj.module;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import biolockj.dataType.DataUnit;
import biolockj.exception.BioLockJException;
import biolockj.exception.ModuleInputException;
import biolockj.module.io.ModuleInput;
import biolockj.util.ModuleUtil;

/**
 * This outlines a mechanism analogous to the ModuleIO interface. While it makes sense that modules will need to
 * getInputFiles() and define the types they can take, it also makes sense to make distinction between the reference
 * data in a pipeline and the throughput data.
 * 
 * @author Ivory Blakley
 *
 */
public interface ReferenceDataModule extends BioModule {
	
	/**
	 * Analogous to {@link biolockj.module.io.ModuleIO#getInputTypes}. This follows the same structure, and should be used in parallel to getInputTypes 
	 * allowing modules to distinguish reference data from throughput data.
	 * 
	 * @return
	 */
	public List<ModuleInput> getReferenceTypes();
	
	/**
	 * Determine an InputSource for each of the modules reference requirements.
	 * In most cases, the reference is specified through a property that defined in the module.
	 * Most modules will define their own {@link #assignReferenceSources()} method to use that property.
	 * 
	 */
	public default void assignReferenceSources() throws BioLockJException{
		for ( ModuleInput ref : getReferenceTypes() ) {
			ModuleUtil.findInputModule(this, ref);
			if (ref.getSource() == null && ref.isRequired()) 
				throw new ModuleInputException("Module " + this + " failed to find a reference data for reference [" + ref.getLabel() + "].");
		}
	}
		
	/**
	 * Analogous {@link biolockj.module.io.ModuleIO#getInputFiles}.
	 */
	public default List<File> getReferenceFiles() throws ModuleInputException {
		final List<File> files = new ArrayList<>();
		final List<DataUnit> data = new ArrayList<>();
		try {
			for( ModuleInput input: getReferenceTypes() ) {
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
	
	@Override
	public default void checkDependencies() throws Exception {
		ModuleUtil.checkRefData( this );
	}

}
