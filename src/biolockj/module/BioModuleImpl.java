/**
 * @UNCC Fodor Lab
 * @author Michael Sioda
 * @email msioda@uncc.edu
 * @date Feb 9, 2017
 * @disclaimer This code is free software; you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any
 * later version, provided that any use properly credits the author. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details at http://www.gnu.org *
 */
package biolockj.module;

import java.io.File;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.SizeFileComparator;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import biolockj.*;
import biolockj.Properties;
import biolockj.api.API_Exception;
import biolockj.dataType.DataUnit;
import biolockj.dataType.DataUnitFilter;
import biolockj.dataType.SpecificModuleOutput;
import biolockj.dataType.UnknownPipelineInput;
import biolockj.exception.BioLockJException;
import biolockj.exception.ConfigFormatException;
import biolockj.exception.ConfigNotFoundException;
import biolockj.exception.ModuleInputException;
import biolockj.exception.PipelineFormationException;
import biolockj.module.io.InputSource;
import biolockj.module.io.InputSpecs;
import biolockj.module.io.OutputSpecs;
import biolockj.util.*;

/**
 * Superclass for standard BioModules (classifiers, parsers, etc). Sets standard behavior for many of the BioModule
 * interface methods.
 */
public abstract class BioModuleImpl implements BioModule, Comparable<BioModule> {

	public BioModuleImpl() {
		propDescMap = new HashMap<>();
		propTypeMap = new HashMap<>();
		propDefaultValMap = new HashMap<>();
	}
	
	/**
	 * HashMap with property name as key and the description for this property as the value.
	 */
	private final HashMap<String, String> propDescMap;
	
	/**
	 * HashMap with property name as key and the description for this property as the value.
	 */
	private final HashMap<String, String> propTypeMap;
	
	/**
	 * HashMap with property name as key and the default value for this property as the value.
	 */
	private final HashMap<String, String> propDefaultValMap;
	
	protected final void addNewProperty(String prop, String type, String desc) {
		addNewProperty(prop, type, desc, null);
	}
	
	protected final void addNewProperty(String prop, String type, String desc, String defaultValue) {
		propDescMap.put(prop, desc);
		propTypeMap.put(prop, type);
		propDefaultValMap.put( prop, defaultValue );
	}
	
	protected final void addGeneralProperty(String prop) {
		try {
			addNewProperty(prop, Properties.getPropertyType( prop ), Properties.getDescription( prop ));
		}catch(API_Exception ex) {
			addNewProperty(prop, "", "");
		}
	}
	protected final void addGeneralProperty(String prop, String additionalDescription) {
		try {
			addGeneralProperty(prop, Properties.getPropertyType( prop ), additionalDescription);
		}catch(API_Exception ex) {
			addNewProperty(prop, "", "See general property; " + additionalDescription);
		}
	}
	protected final void addGeneralProperty(String prop, String type, String additionalDescription) {
		try {
			addNewProperty(prop, type, Properties.getDescription( prop ) + " -> " + additionalDescription);
		}catch(API_Exception ex) {
			addNewProperty(prop, type, "See general property; " + additionalDescription);
		}
	}
	
	public final String getPropDefault(String prop) {
		return propDefaultValMap.get( prop );
	}
	
	/**
	 * If restarting or running a direct pipeline execute the cleanup for completed modules.
	 */
	@Override
	public abstract void checkDependencies() throws Exception;

	/**
	 * If metadata exists in module output directory, refresh MetaUtil.
	 */
	@Override
	public void cleanUp() throws Exception {
		Log.info( getClass(), "Clean up: " + getClass().getName() );
		if( getMetadata().isFile() ) {
			MetaUtil.setFile( getMetadata() );
			MetaUtil.refreshCache();
		}
	}

	@Override
	public int compareTo( final BioModule module ) {
		return getID().compareTo( module.getID() );
	}

	/**
	 * Compared based on ID
	 */
	@Override
	public boolean equals( final Object obj ) {
		if( this == obj ) return true;
		if( obj == null ) return false;
		if( !( obj instanceof BioModuleImpl ) ) return false;
		final BioModuleImpl other = (BioModuleImpl) obj;
		if( getID() == null ) {
			if( other.getID() != null ) return false;
		} else if( !getID().equals( other.getID() ) ) return false;
		return true;
	}

	@Override
	public abstract void executeTask() throws Exception;
	
	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	// TODO: limit this access... maybe move BioModule and BioModuleImpl to the biolockj package
	public void setAlias( String alias ) throws PipelineFormationException {
		if (alias.contains( "." )) throw new PipelineFormationException( "Invalid alias: [" + alias + "]; an alias cannot contain a \".\" character." );
		if ( ! alias.substring( 0, 1 ).equals( alias.substring( 0, 1 ).toUpperCase() )) throw new PipelineFormationException("Invalid alias: [" + alias + "]; an alias must start with a capital letter.");
		this.alias = alias;
	}

	@Override
	public Integer getID() {
		return this.moduleId;
	}

	/**
	 * BioModule {@link #getInputFiles()} is called to initialize upon first call and cached.
	 */
	@Override
	public List<File> getInputFiles() throws ModuleInputException {
		if ( calledPrematurely() ) return null;
		if( getFileCache().isEmpty() ) {
			cacheInputFiles ( findModuleInputFiles() );
		}
		return getFileCache();
	}

	/**
	 * The methods to get specific input files should be called when the module executes, or after it has exacuted.
	 * Until then, the inputs are abstractly represented as InputSource's, which can be listed using the {@link #getInputSources} method.
	 * This method catches any times that the file-specific methods are called prematurely, so the method returns null.
	 * With future changes, the specific methods will be private, and any outside class that needs the info will get 
	 * it from a summary maintained by a utility class.
	 * @return
	 */
	private boolean calledPrematurely() {
		File flagFile = PipelineUtil.getPipelineStatusFlag();
		String flag = flagFile == null ? "formation/checkDependencies" : flagFile.getName();
		boolean premature = false;
		if (Pipeline.exeModule() != this) {
			Log.error( this.getClass(), "I am not the current module.");
			if( !ModuleUtil.isComplete( this ) ) {
				Log.error( this.getClass(), "I am not a completed module.");
				premature = true;
			}
		}
		if ( !flag.equals( Constants.BLJ_STARTED ) ) {
			Log.error( this.getClass(), "The pipeline is not in the execution phase.");
			premature = true;
		}
		if ( premature ) {
			Log.error( this.getClass(), "Pipeline flag: " + flag);
			Log.error( this.getClass(), "Current exe module: " + Pipeline.exeModule() );
			Log.error( this.getClass(), "That " + (Pipeline.exeModule() == this ? "is" : "is not") + " me." );
			String msg = "The exact input files cannot be determined until module [" + ModuleUtil.displaySignature( this ) + "] executes." 
							+ System.lineSeparator() 
							+ "Until then, the inputs are abstractly represented as InputSource's, which can be listed using the getInputSources() method.";
			Log.error(this.getClass(), msg);
			Throwable spot = new ModuleInputException(msg);
			spot.printStackTrace();
			//return true;
		}
		return false;
	}
	
	@Override
	public File getMetadata() {
		return new File( getOutputDir().getAbsolutePath() + File.separator + MetaUtil.getFileName() );
	}

	/**
	 * All BioModule work must be contained within the scope of its root directory.
	 */
	@Override
	public File getModuleDir() {
		return this.moduleDir;
	}

	/**
	 * Returns moduleDir/output which will be used as the next module's input.
	 */
	@Override
	public File getOutputDir() {
		return ModuleUtil.requireSubDir( this, OUTPUT_DIR );
	}

	/**
	 * By default, no post-requisites are required.
	 */
	@Override
	public List<String> getPostRequisiteModules() throws Exception {
		return new ArrayList<>();
	}

	/**
	 * By default, no prerequisites are required.
	 */
	@Override
	public List<String> getPreRequisiteModules() throws Exception {
		return new ArrayList<>();
	}

	/**
	 * Returns summary message to be displayed by Email module so must not contain confidential info. ModuleUtil
	 * provides summary metrics on output files
	 */
	@Override
	public String getSummary() throws Exception {
		Log.info( SummaryUtil.class, "Building module summary for: " + ModuleUtil.displaySignature( this ) );
		return SummaryUtil.getOutputDirSummary( this );
	}

	/**
	 * Returns moduleDir/temp for intermediate files. If {@link biolockj.Constants#RM_TEMP_FILES} =
	 * {@value biolockj.Constants#TRUE}, this directory is deleted after pipeline completes successfully.
	 */
	@Override
	public File getTempDir() {
		return ModuleUtil.requireSubDir( this, TEMP_DIR );
	}
	
	/**
	 * Returns moduleDir/temp for intermediate files. If {@link biolockj.Constants#RM_TEMP_FILES} =
	 * {@value biolockj.Constants#TRUE}, this directory is deleted after pipeline completes successfully.
	 */
	@Override
	public File getLogDir() {
		return ModuleUtil.requireSubDir( this, LOG_DIR );
	}
	
	/**
	 * Returns moduleDir/temp for intermediate files. If {@link biolockj.Constants#RM_TEMP_FILES} =
	 * {@value biolockj.Constants#TRUE}, this directory is deleted after pipeline completes successfully.
	 */
	@Override
	public File getResourceDir() {
		return ModuleUtil.requireSubDir( this, RES_DIR );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( getID() == null ? 0: getID().hashCode() );
		return result;
	}

	/**
	 * This method must be called immediately upon instantiation.
	 * 
	 * @throws Exception if errors occur
	 */
	@Override
	public void init() throws Exception {
		this.moduleId = nextId++;
		this.moduleDir = new File(
			Config.pipelinePath() + File.separator + ModuleUtil.displaySignature( this ) );

		if( !this.moduleDir.isDirectory() ) {
			this.moduleDir.mkdirs();
			Log.info( getClass(), "Construct module [ " + ModuleUtil.displayID( this ) + " ] for new " +
				this.moduleDir.getAbsolutePath() );
		} else Log.info( getClass(), "Construct module [ " + ModuleUtil.displayID( this ) + " ] for existing " +
			this.moduleDir.getAbsolutePath() );
	}
	
	/**
	 * When {@link #findInputSources(BioModule)} is called, this method determines if the previousModule output is valid 
	 * input for the current BioModule. The default implementation of this method returns TRUE for any module.
	 * The default implementation of {@link #findInputSources()} calls this method through {@link #findInputSources(BioModule, boolean)} 
	 * which allows the user to specify the input module.
	 * 
	 * This is the appropriate method to override in child classes.
	 * 
	 */
	@Override
	public boolean isValidInputModule( final BioModule module ) {
		return true;
	}
	
	/**
	 * When {@link #findInputSources(BioModule)} is called, this method determines if the previousModule output is valid 
	 * input for the current BioModule. This is a wrapper for {@link #isValidInputModule(BioModule)} that allows the user 
	 * to configure a specific input module using the {@link biolockj.Config} property {@value biolockj.Constants#MODULE_INPUT_PROP}.
	 */
	protected final boolean isValidInputModule(final BioModule module, boolean validateConfiguredInput) {
		boolean isValid;
		String specificInputProp = Config.getModulePropName( this, Constants.MODULE_INPUT_PROP );
		if ( Config.getString( this, specificInputProp ) == null ) {
			isValid = isValidInputModule(module);
		}else {
			boolean matched = false; 
			for (String target : Config.getList( this, specificInputProp ) ) {
				if ( ModuleUtil.displayName( module ).equals( target ) ||
								module.getClass().getSimpleName().equals( target ) ) matched = true;
			}
			if (validateConfiguredInput) {
				isValid = matched && isValidInputModule(module);
			}else {
				isValid = matched;
				if (matched && !isValidInputModule(module)) {
					Log.warn(this.getClass(), "Module " + ModuleUtil.displaySignature( this ) 
					+ " is being forced to use an input module that is NOT considered a valid input." + System.lineSeparator()
					+ "See configured property [ " + specificInputProp + " = " + Config.getString( this, specificInputProp ) + " ]" + System.lineSeparator()
					+ "And make sure you know what you are doing!!!");
				}
			}
		}
		return isValid;
	}
	
	public boolean isValidInputDir(File dir) {
		return dir != null && dir.isDirectory() && dir.listFiles().length > 0;
	}
	
	/**
	 * When {@link #findInputSources} is called, this method determines if a given input directory is a valid 
	 * input for the current BioModule. This is a wrapper for {@link #isValidInputDir(File)} that allows the user 
	 * to configure a specific input directory using the {@link biolockj.Config} property {@value biolockj.Constants#INPUT_DIRS}.
	 * @param dir The directory to consider
	 * @param validateConfiguredInput Should the input be validated even if it is specified using the module-specific input property. If false, the user can over-ride the modules built-in methods for determining appropriate inputs.
	 */
	public boolean isValidInputDir(File dir, boolean validateConfiguredInput) {
		boolean isValid;
		String specificInputProp = Config.getModulePropName( this, Constants.INPUT_DIRS );
		if ( Config.getString( this, specificInputProp ) == null ) {
			isValid = isValidInputDir(dir);
		}else {
			boolean matched = false; 
			for (String target : Config.getList( this, specificInputProp ) ) {
				File targetDir = new File(target);
				if ( dir.getAbsolutePath().equals( targetDir.getAbsolutePath() ) ) {
					matched = true;
				}
			}
			if (validateConfiguredInput) {
				isValid = matched && isValidInputDir(dir);
			}else {
				isValid = matched;
				if (matched && !isValidInputDir(dir)) {
					Log.warn(this.getClass(), "Module " + ModuleUtil.displaySignature( this ) 
					+ " is being forced to use an input folder that is NOT considered a valid input." + System.lineSeparator()
					+ "See configured property [ " + specificInputProp + " = " + Config.getString( this, specificInputProp ) + " ]" + System.lineSeparator()
					+ "And make sure you know what you are doing!!!");
				}
			}
		}
		return isValid;
	}
	
	@Override
	public String toString() {
		String val = getClass().getName();
		try {
			val = ModuleUtil.displaySignature( this );
		} catch( final Exception ex ) {
			Log.warn( getClass(), "Unable to find ID for: " + val);
		}
		return val;
	}

	/**
	 * Cache the input files for quick access on subsequent calls to {@link #getInputFiles()}
	 * 
	 * @param files Input files
	 */
	protected void cacheInputFiles( final Collection<File> files ) {
		this.inputFiles.clear();
		this.inputFiles.addAll( files );
		Collections.sort( this.inputFiles );
		if( !this.inputFiles.isEmpty() && SeqUtil.isSeqFile( this.inputFiles.get( 0 ) ) )
			sortCachedInputFilesForEvenBatchSize();
		else printInputFiles();
	}

	/**
	 * Called upon first access of input files to return sorted list of files from all
	 * {@link biolockj.Config}.{@value biolockj.Constants#INPUT_DIRS}<br>
	 * Hidden files (starting with ".") are ignored<br>
	 * Call {@link #isValidInputModule(BioModule)} on each previous module until acceptable input files are found<br>
	 * 
	 * @return Set of input files
	 * @throws BioLockJException 
	 */
	protected List<File> findModuleInputFiles() throws ModuleInputException {
		final List<File> moduleInputFiles = new ArrayList<>();
		if ( calledPrematurely() ) return null;
		Log.debug( getClass(), "Initialize input files..." );
		List<InputSource> sources;
		try {
			sources = getInputSources();
		} catch( BioLockJException e ) {
			e.printStackTrace();
			throw new ModuleInputException("A problem occured while determining the input sources for module: " + ModuleUtil.displaySignature( this ));
		}
		if ( sources != null && ! sources.isEmpty() ) {
			for (InputSource is : sources ) {
				if ( ! is.isReady() ) throw new ModuleInputException(this, is);
				File file = is.getFile();
				final Collection<File> files = new ArrayList<>();
				if ( file.isDirectory() ) {
					files.addAll( FileUtils.listFiles( file,
						HiddenFileFilter.VISIBLE, HiddenFileFilter.VISIBLE ) ) ;
				}else if ( file.exists() ){
					files.add( file );
				}else {
					throw new ModuleInputException(this, is);
				}
				final Collection<File> goodFiles = BioLockJUtil.removeIgnoredAndEmptyFiles( files );
				Log.debug( getClass(), "# Files found in input source [" + is.getName() + "]: " + goodFiles.size() );
				//suggested for child class implementations
				//			if (goodFiles.size() == 0) {
				//				throw new ModuleInputException(this, is);
				//			}
				moduleInputFiles.addAll( goodFiles );
			}
		}
		return moduleInputFiles;
	}
	
	protected List<InputSource> findInputSources() throws BioLockJException {
		List<InputSource> inputSources = new ArrayList<>();
		Log.debug( getClass(), "Initialize input sources..." );
		boolean validInput = false;
		BioModule previousModule = ModuleUtil.getPreviousModule( this );
		while( !validInput )
			if( previousModule == null ) {
				Log.debug( getClass(),
					"No prior pipeline module is a valid input module.  Return pipleline input from: " + Constants.INPUT_DIRS );
				List<File> inputDirs = Config.getExistingFileList( this, Constants.INPUT_DIRS );
				if (inputDirs != null && ! inputDirs.isEmpty() ) {
					for (File input : Config.getExistingFileList( this, Constants.INPUT_DIRS ) ) {
						if (isValidInputDir(input)) inputSources.add( new InputSource(input) );
					}
				}
				validInput = true;
			} else {
				Log.debug( getClass(),
					"Check previous module for valid input type: " + ModuleUtil.displaySignature( previousModule ) );
				validInput = isValidInputModule( previousModule, true );
				if( validInput ) {
					InputSource input = new InputSource(previousModule);
					Log.debug( getClass(),
						"Found valid input module: " + input.getName() );
					inputSources.add( input );
				} else {
					previousModule = ModuleUtil.getPreviousModule( previousModule );
				}
			}
		if(inputSources.isEmpty()) {
			Log.warn(this.getClass(), "No suitable input sources were found!");
			throw new PipelineFormationException("Failed to find valid input source for module [" + ModuleUtil.displaySignature( this ) + "].");
		}
		return inputSources;
	}

	

	@Override
	public List<InputSource> getInputSources() throws BioLockJException {
		if( inputSources == null ) {
			inputSources = new ArrayList<>();
			for (InputSpecs inspec : inputSpecs ) {
				if (inspec.source == null) assignInputSources();
				inputSources.addAll( inspec.source );
			}
		}
		return inputSources;
	}
	
	/**
	 * Determine a suitable input source for each inputSpec.
	 * @throws BioLockJException 
	 */
	public void assignInputSources() throws BioLockJException {
		ModuleUtil.assignInputModules(this, getInputSpecs());
		for (InputSpecs inspec : getInputSpecs() ) {
			if (inspec.source == null) {
				assignInputDir( inspec );
			}
		}
	}
	
	/**
	 * If a given input requirement cannot be satisfied by other modules.
	 * The default is not very smart.
	 * @param inspec
	 * @throws BioLockJException 
	 */
	public void assignInputDir(InputSpecs inspec) throws BioLockJException {
		List<File> validDirs = new ArrayList<>();
		List<File> inputDirs = Config.getExistingFileList( this, Constants.INPUT_DIRS );
		if( inputDirs == null || inputDirs.isEmpty() ) {
			throw new ModuleInputException( "No input dirs available for module [" + ModuleUtil.displaySignature( this ) + "]." );
		}else{
			for( File inFile: inputDirs ) {
				DataUnit<?> inData = null;
				try {
					inData = (DataUnit<?>) Class.forName( inspec.dataUnitClass ).newInstance();
				} catch( Exception e ) {
					e.printStackTrace();
					Log.error(this.getClass(), "Failed attempt to instantiate a DataUnit of type: " + inspec.dataUnitClass );
				}
				if (inData == null) break;
				if ( inspec.getFilter().accept( inData ) && inData.isValid() ) {
					validDirs.add( inFile );
				}
			}
		}
		if (validDirs.size() > 1) {
			throw new ModuleInputException( "Too many input dirs for module [" + ModuleUtil.displaySignature( this ) + "]." );
		}else if (validDirs.size() == 0) {
			throw new ModuleInputException( "The input given for [" + ModuleUtil.displaySignature( this ) + "] were not suitable for input [" + inspec.getLabel() + "]." );
		}else {
			inspec.source.add( new InputSource( validDirs.get(0) ) );
		}
	}
	
	protected List<InputSpecs> inputSpecs = null;
	
	public List<InputSpecs> getInputSpecs() {
		if (inputSpecs == null) defineInputSpecs();
		return inputSpecs;
	}
	
	/**
	 * Create an instance of InputSpecs that has no technical criteria.
	 * 
	 * If this default DataUnitFilter is used to determine module inputs,
	 * then the most recent module to produce any output will be used.
	 * Use this when the input is intended to be VERY open ended, or when 
	 * other methods are used to actually determine the modules inputs.
	 * 
	 * If there are no previous modules, then this dataUnitClass will 
	 * accept any file as a an input file.
	 * 
	 * This default may be removed in the future.  
	 * All extending classes should provide their own, more meaningful, input specifications.
	 */
	protected void defineInputSpecs() {
		inputSpecs = new ArrayList<>();
		inputSpecs.add( new InputSpecs("input", "any input", UnknownPipelineInput.class.getName(), 
			new DataUnitFilter() {

			@Override
			public boolean accept( @SuppressWarnings("rawtypes") DataUnit data ) {
				return true;
			}

		}) );
	}
	
	protected List<OutputSpecs> outputSpecs = null;
	
	@Override
	public Collection<OutputSpecs> getOutputSpecs(){
		if (outputSpecs == null) defineOutputSpecs();
		return outputSpecs;
	}
	
	/**
	 * By default, the output type of each module is an instance of SpecificModuleOutput
	 * that refers specifically to this module.
	 * The default may be removed in the future.  
	 * All extending classes should provide their own, preferably more descriptive, output specifications.
	 */
	protected void defineOutputSpecs() {
		Collection<OutputSpecs> outputs = new LinkedList<>();
		outputs.add( new OutputSpecs("module output", new SpecificModuleOutput<>(this)) );
	}

	
	
	/**
	 * Get cached input files
	 * 
	 * @return List of input files
	 */
	protected List<File> getFileCache() {
		return this.inputFiles;
	}

	private void printInputFiles() {
		Log.info( getClass(), "# Input Files: " + getFileCache().size() );
		for( int i = 0; i < getFileCache().size(); i++ )
			Log.info( getClass(), "Input File [" + i + "]: " + this.inputFiles.get( i ).getAbsolutePath() );
	}

	private void sortCachedInputFilesForEvenBatchSize() {
		try {
			final Map<Integer, List<File>> map = new HashMap<>();
			final int numWorkers = ModuleUtil.getNumWorkers( this );
			final List<File> sortedList = new SizeFileComparator().sort( new ArrayList<>( this.inputFiles ) );
			for( int i = 0; i < sortedList.size(); i++ ) {
				final List<File> files =
					map.get( i % numWorkers ) == null ? new ArrayList<>(): map.get( i % numWorkers );
				files.add( sortedList.get( i ) );
				map.put( i % numWorkers, files );
			}
			this.inputFiles.clear();
			for( final Integer batchNum: map.keySet() )
				this.inputFiles.addAll( map.get( batchNum ) );
			Log.info( getClass(),
				"List seqFiles sorted for equal batch sizes " + BioLockJUtil.printLongFormList( this.inputFiles ) );
		} catch( ConfigFormatException | ConfigNotFoundException ex ) {
			Log.error( getClass(), "Failed sort by size, return alphabetical list instead", ex );
		}
	}

	private final List<File> inputFiles = new ArrayList<>();
	private File moduleDir = null;
	private Integer moduleId;
	private String alias = null;
	
	
	public String getTitle() {
		return this.getClass().getSimpleName();
	}
	
	public List<String> getMenuPlacement() {
		String classPath = this.getClass().getName();
		StringTokenizer st = new StringTokenizer( classPath, "." );
		ArrayList<String> arrayList = new ArrayList<>();
		String part;
		while (st.hasMoreElements()) {
			part = st.nextToken();
			arrayList.add(part);
		}
		return arrayList ;
	}


	protected final HashMap<String, String> getPropDescMap() {
		return propDescMap;
	}
	public final String getDescription( String prop ) throws API_Exception {
		if (prop.startsWith( Constants.EXE_PREFIX ) || prop.startsWith( Constants.HOST_EXE_PREFIX )
						&& listProps().contains( prop ) ) {
			return Properties.getDescription( prop ) ;
		}
		HashMap<String, String> descriptions = getPropDescMap();
		return descriptions.get( prop );
	}
	public final List<String> listProps() {
		List<String> props = new ArrayList<>(getPropDescMap().keySet());
		Collections.sort(props);
		return props;
	}
	
	protected final HashMap<String, String> getPropTypeMap() {
		return propTypeMap;
	}
	public final String getPropType( String prop ) throws API_Exception {
		if (prop.startsWith( Constants.EXE_PREFIX ) || prop.startsWith( Constants.HOST_EXE_PREFIX ) 
						&& listProps().contains( prop )) {
			return Properties.getPropertyType(prop);
		}
		HashMap<String, String> types = getPropTypeMap();
		return types.get( prop );
	}
	
	public Boolean isValidProp( String property ) throws Exception {
		Boolean isValid = null;
		if ( listProps().contains( property ) ) isValid = true;
		return isValid;
	}	
	
	public String getDetails() throws API_Exception {
		return "";
	}
	
	public String getDockerImageOwner() {
		return Constants.MAIN_DOCKER_OWNER;
	}

	public String getDockerImageTag() {
		return DockerUtil.getDefaultImageTag();
	}
	
	private List<InputSource> inputSources = null;

	/**
	 * BioLockJ gzip file extension constant: {@value #GZIP_EXT}
	 */
	public static final String GZIP_EXT = Constants.GZIP_EXT;

	/**
	 * BioLockJ log file extension constant: {@value #LOG_EXT}
	 */
	public static final String LOG_EXT = Constants.LOG_EXT;
	
	/**
	 * BioLockJ PDF file extension constant: {@value #PDF_EXT}
	 */
	public static final String PDF_EXT = Constants.PDF_EXT;

	/**
	 * Return character constant *backslash-n*
	 */
	public static final String RETURN = Constants.RETURN;

	/**
	 * BioLockJ shell script file extension constant: {@value #SH_EXT}
	 */
	public static final String SH_EXT = Constants.SH_EXT;

	/**
	 * BioLockJ tab character constant: {@value #TAB_DELIM}
	 */
	public static final String TAB_DELIM = Constants.TAB_DELIM;

	/**
	 * BioLockJ tab delimited text file extension constant: {@value #TSV_EXT}
	 */
	public static final String TSV_EXT = Constants.TSV_EXT;

	/**
	 * BioLockJ tab delimited text file extension constant: {@value #TXT_EXT}
	 */
	public static final String TXT_EXT = Constants.TXT_EXT;

	private static Integer nextId = 0;

}
