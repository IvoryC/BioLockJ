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
import java.util.List;
import biolockj.exception.ModuleInputException;
import biolockj.exception.PipelineFormationException;

/**
 * Classes that implement this interface are eligible to be included in a BioLockJ pipeline.<br>
 * Use the <b>#BioModule</b> tag with the class name in the {@link biolockj.Config} file to include a module.<br>
 * The {@link biolockj.Pipeline} class executes BioModules in the order provided in the {@link biolockj.Config}
 * file.<br>
 * <p>
 * <b>BioModule Directory Structure</b><br>
 * <table summary="BioModule Directories" cellpadding="4">
 * <tr>
 * <th>Directory</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>output</td>
 * <td>Contains all module output files</td>
 * </tr>
 * <tr>
 * <td>temp</td>
 * <td>Holds intermediate files generated by the module, but are not to be passed on to the next module. This directory
 * will deleted after pipeline execution if {@value biolockj.Constants#RM_TEMP_FILES} =
 * {@value biolockj.Constants#TRUE}</td>
 * </tr>
 * </table>
 */
public interface BioModule {
	/**
	 * During pipeline initialization, all configured BioModules will run this method to validate dependencies.
	 *
	 * @throws Exception thrown if missing or invalid dependencies are found
	 */
	public void checkDependencies() throws Exception;

	/**
	 * This method executes after execution to update Config modified by the module or other cleanup operations.
	 *
	 * @throws Exception thrown if any runtime error occurs
	 */
	public void cleanUp() throws Exception;

	/**
	 * This is the main method called when it is time for the BioModule to complete its task.
	 *
	 * @throws Exception thrown if the module is unable to complete is task
	 */
	public void executeTask() throws Exception;

	/**
	 * Some BioModules may be added to a pipeline multiple times so must be identified by an ID.<br>
	 * This is the same value as the directory folder prefix when run.<br>
	 * The 1st module ID is 0 (or 00 if there are more than 10 modules.
	 * 
	 * @return Module ID
	 */
	public Integer getID();
	
	/**
	 * Some BioModules may be added to a pipeline multiple times.<br>
	 * The user may provide an alias for a module in the run order, 
	 * thus allowing the user direct properties to an individual instance of a module.
	 * 
	 * @return Module ID
	 */
	public String getAlias(); 

	public void setAlias( String alias ) throws PipelineFormationException; 

	/**
	 * Each BioModule takes the previous BioModule output as input:<br>
	 * BioModule[ n ].getInputFiles() = BioModule[ n - 1 ].getOutputDir().listFiles()<br>
	 * Special cases:<br>
	 * <ul>
	 * <li>The 1st BioModule return all files in {@value biolockj.Constants#INPUT_DIRS}<br>
	 * <li>If previous BioModule BioModule[ n - 1 ] is a MetadataModule, forward it's input + output file: BioModule[ n
	 * ].getInputFiles() = BioModule[ n -1 ].getInputFiles() + BioModule[ n -1 ].getOutputFiles()
	 * </ul>
	 * 
	 * @return Input files
	 * @throws ModuleInputException when there is a problem with the input files
	 */
	public List<File> getInputFiles() throws ModuleInputException;

	/**
	 * Updated/new metadata files are saved to the module output directory (if created by the module). Use param = FALSE
	 * to return an empty file objects in preparation for building a new metadata file.
	 *
	 * @return Updated or new module/metadata/metadataFile.tsv is created, otherwise null
	 */
	public File getMetadata();

	/**
	 * Each BioModule generates sub-directory under $DOCKER_PROJ
	 *
	 * @return BioModule root directory
	 */
	public File getModuleDir();

	/**
	 * Output files destined as input for the next BioModule is created in this directory.
	 *
	 * @return File directory containing the primary BioModule output
	 */
	public File getOutputDir();

	/**
	 * {@link biolockj.Pipeline} calls this method when building the list of pipeline BioModules to execute. Any
	 * BioModules returned by this method will be added to the pipeline after the current BioModule. If multiple
	 * post-requisites are found, the modules will be added in the order listed.
	 * 
	 * @return List of BioModules
	 * @throws Exception if invalid Class names are returned as post-requisites
	 */
	public List<String> getPostRequisiteModules() throws Exception;

	/**
	 * {@link biolockj.Pipeline} calls this method when building the list of pipeline BioModules to execute. Any
	 * BioModules returned by this method will be added to the pipeline before the current BioModule. If multiple
	 * prerequisites are returned, the modules will be added in the order listed.
	 * 
	 * @return List of BioModule Class Names
	 * @throws Exception if invalid Class names are returned as prerequisites
	 */
	public List<String> getPreRequisiteModules() throws Exception;

	/**
	 * Gets the BioModule execution summary, this is sent as part of the notification email, if configured.<br>
	 * Summary should not include data content, to avoid unintentional publication of confidential information.<br>
	 * However, meta-data such as number/size of files can be helpful during debug.<br>
	 *
	 * @return Summary of BioModule execution
	 * @throws Exception if any error occurs
	 */
	public String getSummary() throws Exception;

	/**
	 * Contains intermediate files generated by the module but not used by the next BioModule.<br>
	 * The files may contain supplementary information or data that may be helpful during debug or recovery.<br>
	 * If {@value biolockj.Constants#RM_TEMP_FILES} = {@value biolockj.Constants#TRUE}, successful pipelines delete this
	 * directory.<br>
	 *
	 * @return File directory of files typically not useful long term
	 */
	public File getTempDir();
	
	/**
	 * Retains records of the process of running the module.<br>
	 * The files are intended to be small and stored long term with successful pipelines.<br>
	 * @return
	 */
	public File getLogDir();

	/**
	 * The resource sub-direcotry contains files that the module may depend on.<br>
	 * Unlike temp or log, these files are not created by the module.    
	 * Unlike output, these files are not intended to be available to other modules.<br>
	 * Examples of files that might go in resource/ include:<br>
	 *  * library files that are used by a user-supplied script
	 *  * files that are packaged in the jar file of a module, and copied to be available to scripts.
	 *  * information that can be determined in the java layer, and is made available to non-java scripts.
	 *  <br> Like all subdirs, this is deleted in incomplete modules during a re-start.
	 *  Depending on the module, the files may be deleted upon module completion in the cleanup() method.
	 * @return
	 */
	public File getResourceDir();
	
	/**
	 * Initialize a new module to generate a unique ID and module directory.
	 * 
	 * @throws Exception if errors occur
	 */
	public void init() throws Exception;
	
	/**
	 * If a property is null based on the config files (including all defaults and standard.properties) but 
	 * a module is passed to the Config class as the context for getting that module, the Config class 
	 * can query the module for a value for the property.
	 * In general, this is used for the properties defined in this module, but it CAN also be used for general properties.
	 * @param property
	 * @return
	 */
	public String getPropDefault(String prop);

	/**
	 * BioModules {@link #getInputFiles()} method typically, but not always, return the previousModule output files.
	 * This method checks the output directory from the previous module to check for input deemed acceptable by the
	 * current module. The conditions coded in this method will be checked on each previous module in the pipeline until
	 * acceptable input is found. If no previous module produced acceptable input, the files under
	 * {@link biolockj.Config}.{@value biolockj.Constants#INPUT_DIRS} are returned.<br>
	 * <br>
	 * This method can be overridden by modules that need input files generated prior to the previous module.
	 * 
	 * @param previousModule BioModule that ran before the current BioModule
	 * @return boolean TRUE if the previousModule output is acceptable input for the current BioModule
	 */
	public boolean isValidInputModule( BioModule previousModule );
	
	/**
	 * Get the name of the docker hub user that owns the image to use for this module.
	 * Docker images from docker hub are specified using this syntax: <owner>/<image>:<tag>
	 * @return
	 */
	public String getDockerImageOwner();
	
	/**
	 * Get the docker image to use for this module.  
	 * The owner and version/tag are specificed separately; this is just the image name.
	 * Docker images from docker hub are specified using this syntax: <owner>/<image>:<tag>
	 * @return
	 */
	public String getDockerImageName();
	
	/**
	 * Get the version / tag to use for the docker image.
	 * Docker images from docker hub are specified using this syntax: <owner>/<image>:<tag>
	 * @return
	 */
	public String getDockerImageTag();

	/**
	 * Script prefix appended to start of file name to indicate the main script in the script directory.<br>
	 * Non-AWS pipelines execute worker scripts via executing the main shell script - named with the prefix:
	 * {@value #MAIN_SCRIPT_PREFIX}
	 */
	public static final String MAIN_SCRIPT_PREFIX = "MAIN_";

	/**
	 * Name of the output sub-directory: {@value #OUTPUT_DIR}
	 */
	public static final String OUTPUT_DIR = "output";

	/**
	 * Name of the temporary sub-directory: {@value #TEMP_DIR}
	 */
	public static final String TEMP_DIR = "temp";
	
	/**
	 * Name of the temporary sub-directory: {@value #LOG_DIR}
	 */
	public static final String LOG_DIR = "log";
	
	/**
	 * Name of the temporary sub-directory: {@value #RES_DIR}
	 */
	public static final String RES_DIR = "resources";
}
