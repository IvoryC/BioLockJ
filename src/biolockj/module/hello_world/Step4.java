package biolockj.module.hello_world;

import java.util.List;
import biolockj.module.BioModule;
import biolockj.module.SeqModule;

/**
 * Specify the acceptable input data type for this module.
 * Currently, the way to specify this is through the isValidInputModule method.  
 * Test a given module to see if it is an acceptable source of intput data.
 * 
 * Your description / details should describe what type of output it makes.
 * 
 * Many modules are connected to some other module. They jointly perform a single task, but it still makes sense to
 * break that task into multiple modules. They will reference each as pre- or post-requisite modules.
 * 
 * @author Ivory Blakley
 *
 */
public abstract class Step4 extends Step3 {

	@Override
	public List<String> getPostRequisiteModules() throws Exception {
		// TODO Auto-generated method stub
		return super.getPostRequisiteModules();
	}

	@Override
	public List<String> getPreRequisiteModules() throws Exception {
		// TODO Auto-generated method stub
		return super.getPreRequisiteModules();
	}


	@Override
	public boolean isValidInputModule( BioModule module ) {
		// return module instanceof SeqModule;
		return super.isValidInputModule( module );
	}

}