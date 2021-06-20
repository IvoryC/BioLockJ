package biolockj.module.biobakery.metaphlan;

import biolockj.exception.ConfigFormatException;

public class UnrecognizedMetaphlan2Parameter extends ConfigFormatException {

	public UnrecognizedMetaphlan2Parameter( String property, String msg ) {
		super( MetaPhlAn2.METAPHLAN_PARAMS, msg + System.lineSeparator() + optOut);
	}
	
	public UnrecognizedMetaphlan2Parameter( String msg ) {
		super( MetaPhlAn2.METAPHLAN_PARAMS, msg + System.lineSeparator() + optOut);
	}
	
	private static final String optOut =
		"Please review the usage of metaphlan2.py. If you are confident in your parameters, you may disable this check by setting " +
			MetaPhlAn2.CHECK_PARAMS + " = N in your config file";
	
}