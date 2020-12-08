package biolockj.dataType;

import biolockj.Pipeline;
import biolockj.exception.ModuleInputException;
import biolockj.module.BioModule;
import biolockj.util.ModuleUtil;

/**
 * This effectively allows you to side-step describing a type.
 * It says nothing about the file format, or content.
 * It defines a DataUnit simply as the output of a specific module.
 * This is ideal when multiple modules are effectively one unit, but it was most practical to define that unit using more than one module.
 * @author Ivory Blakley
 *
 */
public class SpecificModuleOutputUnit<T extends BioModule> extends BasicDataUnit {

	private T fromModule = null;

	public SpecificModuleOutputUnit(Class<T> clazz) throws ModuleInputException {
		try {
			fromModule = clazz.newInstance();
		} catch( InstantiationException | IllegalAccessException e ) {
			e.printStackTrace();
			throw new ModuleInputException( e );
		}
	}
	
	public SpecificModuleOutputUnit(T madeByModule) {
		fromModule = madeByModule;
	}
	
	public T getCreatingModule() {
		return fromModule;
	}

	@Override
	public String getDescription() {
		String desc;
		try {
			String s = getCreatingModule().getClass().getName();
			desc = "Output is not described; it is defined as being whatever a " + s + " module puts in its output directory.";
		}catch(Exception e) {
			desc = "The output of a specific module, whatever that might be.";
		}
		return desc;
	}

	@Override
	public boolean isReady() {
		if (Pipeline.getModules().contains( fromModule )) return ModuleUtil.isComplete( fromModule );
		return super.isReady();
	}

}
