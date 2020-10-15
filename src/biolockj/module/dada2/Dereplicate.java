package biolockj.module.dada2;

import biolockj.api.API_Exception;

public class Dereplicate extends DADA2_Module {

	@Override
	protected String getModuleRScriptName() {
		return "dereplicateSeqs.R";
	}
	
	@Override
	public String getDetails() throws API_Exception {
		// TODO Auto-generated method stub
		return super.getDetails() + "<br>";
	}

}
