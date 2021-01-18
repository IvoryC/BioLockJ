package biolockj.module;

import java.util.ArrayList;
import java.util.List;
import biolockj.Constants;
import biolockj.Log;
import biolockj.api.ApiModule;
import biolockj.util.ModuleUtil;
import biolockj.exception.PipelineFormationException;
import biolockj.exception.StopModuleException;
import biolockj.module.io.ModuleIO;
import biolockj.module.io.ModuleInput;
import biolockj.module.io.ModuleOutput;
/**
 * Stop a pipeline.
 * @author Ivory Blakley
 *
 */
public class Stop extends BioModuleImpl implements ApiModule, ModuleIO{

	public Stop() throws PipelineFormationException {
		// Takes no properties.
		// Always use all-caps alias.
		setAlias("STOP");
	}

	@Override
	public String getDockerImageName() {
		return Constants.MAIN_DOCKER_IMAGE;
	}

	@Override
	public void checkDependencies() throws Exception {
		Log.warn(this.getClass(), getMessage());
		System.out.println(getMessage());
	}

	@Override
	public void executeTask() throws Exception {
		Log.warn(this.getClass(), getMessage());
		throw new StopModuleException(this);
	}

	@Override
	public String getDescription() {
		return "Stop a pipeline.";
	}
	
	@Override
	public String getDetails() {
		return "This module immediatley stops a pipeline. " +
			"<br>This is useful when troubleshooting a pipeline, or while a pipeline is a work-in-progress.  " +
			"Any downstream modules will be checked in the checkDependencies phase, but will not be reached during the module execution phase." +
			"<br>This module and the current pipeline will be flagged as `" + Constants.BLJ_FAILED + "`." + 
			"<br>To progress a pipeline past this module, remove this module from the BioModule run order, and restart the pipeline.";
	}

	@Override
	public String getCitationString() {
		return "Module created by Ivory Blakley";
	}
	
	private String getMessage(){
		return "This pipeline is configured to stop upon completing module: " + ModuleUtil.getPreviousModule( this );
	}

	@Override
	public List<ModuleInput> getInputTypes() {
		// takes no input
		return new ArrayList<ModuleInput>();
	}

	@Override
	public List<ModuleOutput> getOutputTypes() {
		// makes no output
		return new ArrayList<ModuleOutput>();
	}

}
