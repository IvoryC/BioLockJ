package biolockj.exception;

import biolockj.Config;
import biolockj.Constants;
import biolockj.Log;
import biolockj.Pipeline;
import biolockj.module.BioModule;

/**
 * A BioLockJException that extending OverrideableCheck has an associated property to override the exception. If the
 * Exception is thrown inappropriately, the check can be disabled, and this can be done for individual modules.
 * 
 * Its usage generally follows this pattern, where implementing class OCheck extends (a child of) BioLockJException,
 * 
 * //in some mdoule
 * checkDependencies(){ 
 * 		Check ex = new Check(this, "Module needs such-n-such thing."); 
 * 		if ( <such-n-such is lacking> ) { 
 * 			if (ex.shouldThrow()) throw ex; 
 * 			} 
 * 		}
 * 
 * 
 * Some extending class may include a method to run the corresponding test.
 * 
 * 
 * @author Ivory Blakley
 *
 */
public abstract class DisableableCheck extends BioLockJException {
	
	protected BioModule module;
	
	protected String localMessage;
	
	public DisableableCheck( String msg ) throws ConfigFormatException {
		this( msg, null );
	}
	
	public DisableableCheck( String msg, BioModule module ) {
		super( msg );
		this.module = module;
		if (module == null && Pipeline.exeModule() != null) this.module = Pipeline.exeModule();
		localMessage = buildLocalMsg(msg);
	}

	/**
	 * Get the name of the property that enables/disables this check.
	 * Often of the form like: pipeline.checkMemory, pipeline.checkExe, pipeline.check_____ .
	 * @return
	 */
	public abstract String getEnablingProp();
	
	/**
	 * Get the BioModule that was associated with this instance.  Could be null.
	 * @return
	 */
	protected BioModule getModule() {
		return module;
	}
		
	/**
	 * Build a standardized message around the module-specific message.
	 * @param message A message supplied by a module
	 * @return
	 * @throws ConfigFormatException 
	 */
	protected String buildLocalMsg(String message) {
		return message + System.lineSeparator() + getPropMessage();
	}
	
	/**
	 * Does the current configuration enable this exception?
	 * @return
	 * @throws ConfigFormatException 
	 */
	public boolean isEnabled() {
		boolean enabled = true;
		try {
			enabled = Config.getBoolean( getModule(), getEnablingProp(), Constants.TRUE);
		} catch( ConfigFormatException e ) {
			e.printStackTrace();
		}
		return enabled;
	}
	
	protected String getPropMessage() {
		String propName = Config.getModulePropName( getModule(), getEnablingProp() );
		String msg;
		if (isEnabled()) {
			msg = "This error can be reduced to a warning by setting [ " + propName + "=" + Constants.FALSE + " ].";
		}else {
			msg = "This error has been reduced to a warning because of the property [ " + propName + "=" + Constants.FALSE + " ].";
		}
		return msg;
	}
	
	/**
	 * Call this method when you believe you should throw this exception.
	 * If the exception is disabled, it will return false, and log a warning.
	 * If the exception is enabled, it will return true, and the exception should be thrown.
	 * 
	 * if (ex.shouldThrow()) throw ex; 
	 * 
	 * where ex is an implementing instance.
	 * @throws Exception 
	 * 
	 */
	public final boolean shouldThrow() {
		boolean failure;
		try {
			failure = failsTest();
		} catch( Exception e ) {
			failure = true;
		}
		if( failure ) {
			if( isEnabled() ) {
				return true;
			} else {
				Log.warn( this.getClass(), getMessage() );
				return false;
			}
		} else {
			return false;
		}
		
	}
	
	/**
	 * This method is meant to called internally through the shouldThrow() method. 
	 * However calling the method directly allows the module to handle any exceptions directly.
	 * The shouldThrow() will catch any exceptions thrown by this method and simply move forward with failure=true;
	 * @return
	 * @throws Exception
	 */
	public abstract boolean failsTest() throws Exception;
	
	@Override
	public String getMessage() {
		return localMessage;
	}

}
