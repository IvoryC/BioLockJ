package biolockj.module.dada2;

import biolockj.api.API_Exception;

public class DadaMerge extends DADA2_Module {

	@Override
	protected String getModuleRScriptName() {
		return "dada2.R";
	}
	
	@Override
	public String getDetails() throws API_Exception {
		// TODO Auto-generated method stub
		return super.getDetails() + "<br>";
	}

}
