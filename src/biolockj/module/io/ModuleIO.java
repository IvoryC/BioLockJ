package biolockj.module.io;

import java.util.List;
import biolockj.exception.BioLockJException;
import biolockj.module.BioModule;
import biolockj.util.ModuleUtil;

public interface ModuleIO extends BioModule {

	/**
	 * A human and technical description of the modules input requirements.
	 * @return
	 */
	public List<ModuleInput> getInputTypes();

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
	public List<ModuleOutput> getOutputTypes();
	
	

}
