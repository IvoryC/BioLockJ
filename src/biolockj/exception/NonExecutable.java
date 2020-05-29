package biolockj.exception;

import biolockj.module.BioModule;
import biolockj.util.ModuleUtil;

/**
 * Paths to executable elements can be passed to BioLockJ via the config file as exe.<executable>=/path/to/executable.
 * This exception is thrown when such a path leads to a file that is not executable.
 * @author Ivory Blakley
 *
 */
public class NonExecutable extends SpecialPropertiesException {
	
	public NonExecutable( String property, String val ) {
		super( property, buildMsg( property, val ) );
	}
	
	public NonExecutable( String property, String val, BioModule module, Exception ex ) {
		super( property, buildMsg( property, val, module ) + System.lineSeparator() + ex.getMessage() );
	}
	
	public NonExecutable( String property, String val, BioModule module ) {
		super( property, buildMsg( property, val, module ) );
	}

	private static String buildMsg( String property, String val, BioModule module) {
		String msg;
		if( module == null ) msg = buildMsg( property, val );
		else {
			msg = "The module [" + ModuleUtil.displayName( module ) + "] calls for the executable given by [" + property + "=" + val +
				"]." + System.lineSeparator() + badNews + System.lineSeparator();
		}
		return msg;
	}
	private static String buildMsg( String property, String val) {
		return "The executable given by [" + property + "=" + val + "] is invalid." + System.lineSeparator()
						+ badNews + System.lineSeparator();
	}
	
	private static final String badNews = "The value is either a file path that does not exist or is not executable, or a value that is not found in the PATH.";
	
	private static final long serialVersionUID = 5311676871403752148L;

}
