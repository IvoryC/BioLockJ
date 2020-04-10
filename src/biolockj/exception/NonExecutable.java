package biolockj.exception;

import biolockj.module.BioModule;

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
	
	public NonExecutable( String property, String val, Exception ex ) {
		super( property, buildMsg( property, val ) );
		ex.printStackTrace();
	}
	
	public NonExecutable( String property, String val, BioModule module ) {
		super( property, buildMsg( property, val, module ) );
	}

	private static String buildMsg( String property, String val, BioModule module) {
		if (module == null) buildMsg( property, val );
		return "The module [" + module.getAlias() + "] calls for the executable given by [" + property + "=" + val + "]." + System.lineSeparator()
						+ badNews + System.lineSeparator();
	}
	private static String buildMsg( String property, String val) {
		return "The executable given by [" + property + "=" + val + "] is invalid." + System.lineSeparator()
						+ badNews + System.lineSeparator();
	}
	
	private static final String badNews = "The file given by that path either does not exist, is not on the PATH, or is not executable.";
	
	private static final long serialVersionUID = 5311676871403752148L;

}
