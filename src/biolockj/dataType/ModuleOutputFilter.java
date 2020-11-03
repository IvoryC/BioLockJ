package biolockj.dataType;

import biolockj.module.BioModule;

public class ModuleOutputFilter implements DataUnitFilter {

	Class<? extends BioModule> clazz;
	
	public ModuleOutputFilter(Class<? extends BioModule> clazz) {
		this.clazz = clazz;
	}

	@Override
	public boolean accept( DataUnit data ) {
		boolean accept = false;
		if (SpecificModuleOutput.class.isInstance( data )) {
			if (clazz.isInstance( ((SpecificModuleOutput<?>) data).getCreatingModule() ) ) {
				accept = true;
			};
		}
		return accept;
	}

}
