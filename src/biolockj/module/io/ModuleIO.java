package biolockj.module.io;

import java.util.Collection;
import biolockj.module.BioModule;

public interface ModuleIO extends BioModule {

	/**
	 * A human and technical description of the modules input requirements.
	 * @return
	 */
	public Collection<InputSpecs> getInputSpecs();

	/**
	 * Determine an InputSource for each of the modules InputSpecs.
	 */
	public void assignInputSources();

	/**
	 * A human and technical definition of the module output types.
	 * @return
	 */
	public Collection<OutputSpecs<?>> getOutputSpecs();
	
	

}
