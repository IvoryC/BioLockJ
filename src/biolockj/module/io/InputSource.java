package biolockj.module.io;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import biolockj.dataType.DataUnit;
import biolockj.exception.BioLockJException;
import biolockj.module.BioModule;
import biolockj.util.ModuleUtil;

/**
 * This class allows modules and utility classes to model what a BioModule 
 * will use as its input, even at time when (like in checkDependencies) 
 * the input itself may not exist yet.
 * @author Ivory Blakley
 *
 */
public class InputSource {
	
	/**
	 * Construct an InputSource based on the output a module in the pipeline will produce.
	 * @param oput
	 */
	public InputSource(ModuleOutput<?> oput){
		isBioModule = true;
		this.oput = oput;
		this.file = null;
		this.template = oput.getDataType();
		name = oput.getModule() + ":" + oput.getLabel();
	}
	
	/**
	 * Construct an InputSource based data that already exists upon pipeline launch.
	 * @param folder
	 * @param template
	 */
	public InputSource(File folder, DataUnit template){
		isBioModule = false;
		this.oput = null;
		this.file = folder;
		this.template = template;
		name = folder.getName() + ( folder.isDirectory() ? " folder" : "");
	}
	
	private final boolean isBioModule;
	private final ModuleOutput<?> oput;
	private final File file;
	private final String name;
	
	private DataUnit template = null;
	
	/**
	 * Is this input a BioLockJ module?
	 * @return true if the input comes from a modules output directory, false otherwise.
	 */
	public boolean isModule() {
		return isBioModule;
	}
	
	/**
	 * Check if a resource is ready. Is the module complete? or does the file exist ?
	 * @return
	 */
	public boolean isReady() {
		boolean ready;
		if (isBioModule) {
			ready = ModuleUtil.isComplete( oput.getModule() );
		}else {
			ready = file.exists();
		}
		return ready;
	}
	
	/**
	 * Get the file path for the input.
	 * @return the output folder (if a BioModule), or the file path.
	 */
	public File getFile() {
		if (isBioModule) {
			return oput.getModule().getOutputDir();
		}else {
			return file;
		}
	}
	
	public BioModule getBioModule() {
		if (isBioModule) {
			return oput.getModule();
		}else return null;
	}
	
	public ModuleOutput<?> getOutputSpecs() {
		if (isBioModule) {
			return oput;
		}else return null;
	}
	
	public String getName() {
		return name;
	}
	
	public DataUnit getInputType() {
		return template;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
