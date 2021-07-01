package biolockj.module.biobakery.metaphlan;

import biolockj.exception.ConfigFormatException;

public class RejectedMetaphlanParameter extends ConfigFormatException {

	public RejectedMetaphlanParameter( String m2arg, String property ) {
		this( redirectMsg(m2arg, property) );
	}
	
	public RejectedMetaphlanParameter( String msg ) {
		super( MetaPhlAn2.METAPHLAN_PARAMS, msg + System.lineSeparator() + maybeGenMod);
	}
	
	private static String redirectMsg(String m2arg, String prop) {
		return "Do not include [" + m2arg + "] in the parameters; BioLockJ will set this value based on property _" + prop + "_.";
	}
	
	private static final String maybeGenMod = "If the restrictions of this module prevent you from doing what you need to do, considered writing a script for use with the GenMod module.";
}