package biolockj.module.biobakery.metaphlan;

import biolockj.exception.ConfigFormatException;

public class UnrecognizedMetaphlanParameter extends ConfigFormatException {

	public UnrecognizedMetaphlanParameter( String property, String msg ) {
		super( MetaPhlAn_Tool.METAPHLAN_PARAMS, msg + System.lineSeparator() + optOut);
	}
	
	public UnrecognizedMetaphlanParameter( String msg ) {
		super( MetaPhlAn_Tool.METAPHLAN_PARAMS, msg + System.lineSeparator() + optOut);
	}
	
	private static final String optOut =
		"Please review the usage of metaphlan.py. If you are confident in your parameters, you may disable this check by setting " +
			MetaPhlAn_Tool.CHECK_PARAMS + " = N in your config file";
	
}