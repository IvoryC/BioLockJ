package biolockj.dataType;

import java.io.File;
import biolockj.module.BioModule;
import biolockj.util.MetaUtil;
import biolockj.util.ModuleUtil;

/**
 * This class allows modules and utility classes to model what a BioModule 
 * will use as its input, even at time when (like in checkDependencies) 
 * the input itself may not exist yet.
 * @author ieclabau
 *
 */
public class InputSource {
	public InputSource(BioModule module){
		isMetaDataColumn = false;
		isBioModule = true;
		this.module = module;
		this.file = null;
		this.column = null;
		name = ModuleUtil.displaySignature( module );
	}
	public InputSource(File folder){
		isMetaDataColumn = false;
		isBioModule = false;
		this.file = folder;
		this.module = null;
		this.column = null;
		name = folder.getName() + ( folder.isDirectory() ? " folder" : "");
	}
	public InputSource(BioModule module, String columnName){
		isMetaDataColumn = true;
		isBioModule = module != null;
		this.file = null;
		this.module = module;
		this.column = columnName;
		name = "metadata column [" + columnName + "]" + ( isBioModule ? (" from module" + ModuleUtil.displaySignature( module ) ) : "");
	}
	
	private final boolean isBioModule;
	private final boolean isMetaDataColumn;
	private final BioModule module;
	private final String column;
	private final File file;
	private final String name;
	
	private DataUnit data = null;
	
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
		if (module != null) {
			ready = ModuleUtil.isComplete( module );
		}else if (isMetaDataColumn){
			ready = MetaUtil.hasColumn( column );
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
	
	public String getMetaColumnName() {
		return column;
	}
	
	public Class<? extends DataUnit> getInputType() {
		return typeClass;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
