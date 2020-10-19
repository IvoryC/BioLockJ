package biolockj.module;

import java.io.File;
import biolockj.util.ModuleUtil;

/**
 * This class allows modules and utility classes to model what a BioModule 
 * will use as its input, even at time when (like in checkDependencies) 
 * the input itself may not exist yet.
 * @author ieclabau
 *
 */
public class InputSource {
	InputSource(BioModule module){
		isBioModule = true;
		this.module = module;
		this.file = null;
		name = ModuleUtil.displaySignature( module );
	}
	InputSource(File folder){
		isBioModule = false;
		this.file = folder;
		this.module = null;
		name = folder.getName() + ( folder.isDirectory() ? " folder" : "");
	}
	private final boolean isBioModule;
	private final BioModule module;
	private final File file;
	private final String name;
	
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
			ready = ModuleUtil.isComplete( module );
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
			return module.getOutputDir();
		}else {
			return file;
		}
	}
	
	public BioModule getBioModule() {
		if (isBioModule) {
			return module;
		}else return null;
	}
	
	public String getName() {
		return name;
	}
}
