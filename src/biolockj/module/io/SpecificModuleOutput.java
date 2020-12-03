package biolockj.module.io;

import biolockj.dataType.SpecificModuleOutputUnit;
import biolockj.module.BioModule;

public class SpecificModuleOutput<T extends BioModule> 
extends ModuleOutput<SpecificModuleOutputUnit<T>> {

	public SpecificModuleOutput( T module ) {
		super( module, "module output", new SpecificModuleOutputUnit<T>(module) );
	}

}
