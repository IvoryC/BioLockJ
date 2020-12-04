package biolockj.module.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import biolockj.dataType.DataUnit;
import biolockj.exception.ModuleInputException;
import biolockj.util.ModuleUtil;

/**
 * This class allows modules and utility classes to model what a BioModule 
 * will use as its input, even at time when (like in checkDependencies) 
 * the input itself may not exist yet.
 * @author Ivory Blakley
 *
 */
public class InputSource {
	
	private final boolean isBioModule;
	private final ModuleOutput oput;
	private final List<File> files;
	private final String name;
	private DataUnit template = null;
	private List<DataUnit> data;
	
	
	/**
	 * Construct an InputSource based on the output a module in the pipeline will produce.
	 * @param oput
	 */
	public InputSource(ModuleOutput oput){
		isBioModule = true;
		this.oput = oput;
		this.files = new ArrayList<>(); // will be filled in after the module completes.
		this.template = oput.getTemplate();
		name = oput.getModule() + ":" + oput.getLabel();
	}
	
	/**
	 * Construct an InputSource based data that already exists upon pipeline launch.
	 * @param folder
	 * @param template
	 */
	public InputSource(List<File> inFiles, DataUnit template) throws ModuleInputException {
		isBioModule = false;
		this.oput = null;
		this.files = inFiles;
		this.template = template;
		if (inFiles.size() == 1) {
			name = inFiles.get( 0 ).getName();
		}else{
			name = template.toString() + " from [" + inFiles.size() + "] input files";
		}
		data = DataUnit.getFactory(template).getData( inFiles, template );
	}
	

	/**
	 * A list of DataUnit objects.  These may be the representation of pipeline input files, or of the output of other modules.
	 * @return
	 * @throws ModuleInputException
	 */
	public List<DataUnit> getData() throws ModuleInputException{
		if (isBioModule) {
			if ( ! ModuleUtil.isComplete( oput.getModule() )) {
				throw new ModuleInputException("The source module [" + oput.getModule() + "] has not completed.");
			}
			files.addAll( Arrays.asList( oput.getModule().getOutputDir().listFiles( template.getFilenameFilter() ) ) );
			data = DataUnit.getFactory(template).getData( files, template );
		}
		return data;
	}
	
	/**
	 * Is this input a BioLockJ module?
	 * @return true if the input comes from a modules output directory, false otherwise.
	 */
	public boolean isModule() {
		return isBioModule;
	}
	
	/**
	 * Check if a resource is ready. Is the module complete? or does the file exist ?
	 * If isReady() then other classes may access the data through getData().
	 * By the time another class needs the data, if !isReady() warrants an error.  
	 * But some optional actions, like messages might be done before then IFF isReady().
	 * @return
	 */
	public boolean isReady() {
		if (isBioModule) {
			return ModuleUtil.isComplete( oput.getModule() );
		}
		return true;
	}
	
	public ModuleOutput getModuleOutput() {
		if (isBioModule) {
			return oput;
		}else return null;
	}
	
	public DataUnit getTemplate() {
		return template;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
