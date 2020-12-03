package biolockj.dataType;

import biolockj.module.BioModule;

/**
 * Many of the original BioLockJ modules specified a module type they could use as input rather than a data type.
 * This class, along with {@link SpecificModuleOutput} offers a convenient avenue for migrating that design into the DataUnit design.
 * 
 * @author Ivory Blakley
 *
 */
public class ModuleOutputFilter implements DataUnitFilter {

	Class<? extends BioModule> clazz;
	
	public ModuleOutputFilter(Class<? extends BioModule> clazz) {
		this.clazz = clazz;
	}

	@Override
	public boolean accept( DataUnit data ) {
		boolean accept = false;
		if (SpecificModuleOutput.class.isInstance( data )) {
			BioModule source = ((SpecificModuleOutput<?>) data).getCreatingModule();
			if (clazz.isInstance( source ) ) {
				accept = true;
			};
		}
		return accept;
	}

}
