package biolockj.module.dada2;

import biolockj.api.API_Exception;

public class QualityProfile extends DADA2_Module {

	@Override
	protected String getModuleRScriptName() {
		return "makeDada2QualityPlots.R";
	}

	@Override
	public String getDetails() throws API_Exception {
		return super.getDetails() + "<br>The plotQualityProfile function creates plots to help the researcher determine optimal parameters for trimming and filtering sequence files.";
	}

}
