package biolockj.exception;

import java.io.IOException;
import biolockj.Constants;
import biolockj.Log;
import biolockj.module.BioModule;
import biolockj.module.JavaModule;
import biolockj.util.DockerUtil;
import biolockj.util.ModuleUtil;

public class InsufficientMemoryException extends DisableableCheck  {
		
	public static final String ENABLING_PROP = Constants.CHECK_MEMORY;
	
	long minBytes;

	/**
	 * This will check the total memory available in docker or (if running locally) the available memory in for the JVM.
	 * This check can be suppressed by the user using the {@value #ENABLING_PROP} property.
	 * @param msg Modules message about this check
	 * @param module the current BioModule
	 * @param minBytes the minimum available memory required, in bytes.
	 */
	public InsufficientMemoryException( String msg, BioModule module, long minBytes ) {
		super( msg, module );
		this.minBytes = minBytes;
	}
	
	public InsufficientMemoryException( long minBytes ) {
		this( "", null, minBytes);
	}

	/**
	 * Property {@value #ENABLING_PROP}
	 */
	@Override
	public String getEnablingProp() {
		return ENABLING_PROP;
	}

	@Override
	protected String buildLocalMsg(String message)  {
		String msg = "Insufficient memory. Module " + ModuleUtil.displaySignature( getModule() ) +
						" is likely to fail." + System.lineSeparator() + super.buildLocalMsg( message );
		if (DockerUtil.inDockerEnv()) msg += System.lineSeparator() + "Please increase the total memory in docker settings.";
		return msg;
	}
	
	@Override
	public boolean failsTest() throws SpecialPropertiesException, ConfigNotFoundException, IOException, InterruptedException {
		boolean failure;
		long mem = 0;
		if (DockerUtil.inDockerEnv()) mem = DockerUtil.getTotalMem();
		else if (getModule() instanceof JavaModule ) {
			mem = Runtime.getRuntime().freeMemory();
			Log.info(getClass(), "Available JVM memory: " + mem);
//		}else {
//			//TODO: find some suitable check for running locally
		}
		if (mem == 0) {
			Log.warn(getClass(), "Failed to get valid value for total/available memory.");
			failure = false;
		}else if (mem < minBytes) {
			failure = true;
		}else {
			failure = false;
		}
		return failure;
	}
	
	private static final long serialVersionUID = 1L;

}
