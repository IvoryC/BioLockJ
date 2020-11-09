package biolockj.module.io;

import java.util.List;
import biolockj.exception.BioLockJException;
import biolockj.module.BioModule;

public interface ModuleIO extends BioModule {

	/**
	 * A human and technical description of the modules input requirements.
	 * @return
	 */
	public List<InputSpecs> getInputSpecs();

	/**
	 * Determine an InputSource for each of the modules InputSpecs.
	 */
	public void assignInputSources() throws BioLockJException;

	/**
	 * A human and technical definition of the module output types.
	 * @return
	 */
	public List<OutputSpecs<?>> getOutputSpecs();
	
	

}
