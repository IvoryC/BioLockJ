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

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import biolockj.Config;
import biolockj.Constants;
import biolockj.exception.*;
import biolockj.module.report.r.R_Module;
import biolockj.util.*;

/**
 * Superclass for Java BioModules that will be called in separate instances of the application.
 */
public abstract class ScriptModuleImpl extends BioModuleImpl implements ScriptModule {
	
	public ScriptModuleImpl() {
		super();
		addGeneralProperty( Constants.SCRIPT_DEFAULT_HEADER );
		addGeneralProperty( Constants.SCRIPT_NUM_WORKERS );
		addGeneralProperty( Constants.SCRIPT_NUM_THREADS );
		addGeneralProperty( Constants.SCRIPT_PERMISSIONS );
		addGeneralProperty( Constants.SCRIPT_TIMEOUT );
		List<String> generalProps = BashScriptBuilder.listProps();
		generalProps.addAll( DockerUtil.listProps() );
		for (String prop : generalProps ) {
			addGeneralProperty( prop );
		}
	}

	/**
	 * Return an inner list of lines to execute for each sample. Return these as a list (outer list)
	 */
	@Override
	public abstract List<List<String>> buildScript( final List<File> files ) throws Exception;

	/**
	 * The default behavior is the same for paired or unpaired data. This method must be overridden to create separate
	 * scripts for paired datasets.
	 */
	@Override
	public List<List<String>> buildScriptForPairedReads( final List<File> files ) throws Exception {
		return buildScript( files );
	}

	/**
	 * Validate module dependencies:
	 * <ul>
	 * <li>Require {@link biolockj.Config}.{@value Constants#SCRIPT_PERMISSIONS} exists
	 * <li>Require {@link biolockj.Config}.{@value Constants#SCRIPT_NUM_WORKERS} is positive integer
	 * <li>Require {@link biolockj.Config}.{@value Constants#SCRIPT_NUM_THREADS} is positive integer
	 * <li>Verify {@link biolockj.Config}.{@value Constants#SCRIPT_TIMEOUT} is positive integer if set
	 * <li>Start the AWS DB sync to S3 if a novel DB has been configure and
	 * {@value biolockj.util.NextflowUtil#AWS_COPY_DB_TO_S3} is enabled
	 * </ul>
	 */
	@Override
	public void checkDependencies() throws Exception {
		isValidProp( Constants.SCRIPT_DEFAULT_HEADER);
		isValidProp( Constants.SCRIPT_PERMISSIONS );
		isValidProp( Constants.SCRIPT_NUM_WORKERS );
		isValidProp( Constants.SCRIPT_NUM_THREADS );
		isValidProp( Constants.SCRIPT_TIMEOUT );
		BashScriptBuilder.checkDependencies( this );
	}

	/**
	 * Build the nested list of bash script lines that will be used by {@link biolockj.util.BashScriptBuilder} to build
	 * the worker scripts. Pass{@link #getInputFiles()} to either {@link #buildScript(List)} or
	 * {@link #buildScriptForPairedReads(List)} based on
	 * {@link biolockj.Config}.{@value biolockj.Constants#INTERNAL_PAIRED_READS}.
	 */
	@Override
	public void executeTask() throws Exception {
		final List<List<String>> data =
			SeqUtil.hasPairedReads() ? buildScriptForPairedReads( getInputFiles() ): buildScript( getInputFiles() );
		BashScriptBuilder.buildScripts( this, data );

	}

	@Override
	public String[] getJobParams() throws Exception {
		if( getMainScript() == null ) return null;
		return new String[] { getMainScript().getAbsolutePath() };
	}

	/**
	 * Get the main script file in the bioModule script directory, with prefix:
	 * {@value biolockj.module.BioModule#MAIN_SCRIPT_PREFIX}. R_Modules not running in a docker container end in
	 * {@value Constants#R_EXT}, otherwise must end with {@value #SH_EXT}
	 *
	 * @return Main script file
	 */
	@Override
	public File getMainScript() {
		for( final File file: getScriptDir().listFiles() ) {
			if (file.getName().startsWith( MAIN_SCRIPT_PREFIX ) && file.getName().endsWith( SH_EXT )) {
				return file;
			}
		}
		return null;
	}

	/**
	 * Returns moduleDir/script which contains all scripts generated by the module.
	 */
	@Override
	public File getScriptDir() {
		return ModuleUtil.requireSubDir( this, Constants.SCRIPT_DIR );
	}

	/**
	 * This method returns all of the lines from any failure files found in the script directory.
	 * 
	 * @return List of script errors
	 * @throws Exception if errors occur reading failure files
	 */
	@Override
	public List<String> getScriptErrors() throws Exception {
		final List<String> errors = new ArrayList<>();
		for( final File script: getScriptDir().listFiles() ) {
			if( !script.getName().endsWith( Constants.SCRIPT_FAILURES ) ) continue;
			final BufferedReader reader = BioLockJUtil.getFileReader( script );
			try {
				for( String line = reader.readLine(); line != null; line = reader.readLine() )
					errors.add( script.getName() + " | " + line );
			} finally {
				reader.close();
			}
		}

		return errors;
	}

	/**
	 * Returns summary message to be displayed by Email module so must not contain confidential info. ModuleUtil
	 * provides summary metrics on output files
	 */
	@Override
	public String getSummary() throws Exception {
		return super.getSummary() + ( hasScripts() ? SummaryUtil.getScriptDirSummary( this ): "" );
	}

	/**
	 * Default behavior is for scripts to run indefinitely (no timeout).
	 */
	@Override
	public Integer getTimeout() throws ConfigFormatException, ModuleInputException {
		return Config.getPositiveInteger( this, Constants.SCRIPT_TIMEOUT );
	}

	@Override
	public List<String> getWorkerScriptFunctions() throws Exception {
		return new ArrayList<>();
	}

	/**
	 * Return all collectionProperty values separated by a space. If numThreadsParam is not null, append the numThreads
	 * param and value.
	 * 
	 * @param params Runtime parameter
	 * @param numThreadsParam Number of threads parameter name
	 * @return all runtime parameters
	 * @throws ConfigException if Configuration errors occur
	 */
	protected String getRuntimeParams( final List<String> params, final String numThreadsParam )
		throws ConfigException {
		String threadsParam = numThreadsParam == null ? "": numThreadsParam + " " + getNumThreads() + " ";
		String paramVals = params == null || params.isEmpty() ? "": BioLockJUtil.join( params );
		if( threadsParam.isEmpty() && paramVals.isEmpty() ) return "";
		if( paramVals.isEmpty() ) paramVals = "";
		if( threadsParam.isEmpty() ) threadsParam = "";
		String returnVal = threadsParam + paramVals;
		if( !returnVal.endsWith( " " ) ) returnVal += " ";
		return returnVal;
	}

	/**
	 * Check if module produced any scripts
	 * 
	 * @return boolean TRUE if script dir exists
	 */
	protected boolean hasScripts() {
		return new File( getModuleDir().getAbsolutePath() + File.separator + Constants.SCRIPT_DIR ).exists();
	}

	private Integer getNumThreads() throws ConfigFormatException, ConfigNotFoundException {
		return Config.requirePositiveInteger( this, Constants.SCRIPT_NUM_THREADS );
	}

	@Override
	public Boolean isValidProp( String property ) throws Exception {
		Boolean isValid = super.isValidProp( property );
		switch(property) {
			case Constants.SCRIPT_DEFAULT_HEADER:
				isValid = true;
				break;
			case Constants.SCRIPT_NUM_WORKERS:
				Config.requirePositiveInteger( this, Constants.SCRIPT_NUM_WORKERS );
				isValid = true;
				break;
			case Constants.SCRIPT_NUM_THREADS:
				Config.requirePositiveInteger( this, Constants.SCRIPT_NUM_THREADS );
				isValid = true;
				break;
			case Constants.SCRIPT_PERMISSIONS:
				Config.requireString( this, Constants.SCRIPT_PERMISSIONS );
				isValid = true;
				break;
			case Constants.SCRIPT_TIMEOUT:
				Config.getPositiveInteger( this, Constants.SCRIPT_TIMEOUT );
				isValid = true;
				break;
		}
		return isValid;
	}
	
}
