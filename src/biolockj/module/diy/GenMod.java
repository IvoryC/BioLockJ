/**
 * @UNCC Fodor Lab
 * @author Michael Sioda
 * @email msioda@uncc.edu
 * @date June 19, 2017
 * @disclaimer This code is free software; you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any
 * later version, provided that any use properly credits the author. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details at http://www.gnu.org *
 */
package biolockj.module.diy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.io.FileUtils;
import biolockj.Config;
import biolockj.Constants;
import biolockj.Log;
import biolockj.Properties;
import biolockj.api.API_Exception;
import biolockj.api.ApiModule;
import biolockj.api.BuildDocs;
import biolockj.exception.BioLockJException;
import biolockj.exception.ConfigPathException;
import biolockj.module.ScriptModuleImpl;
import biolockj.module.getData.InputDataModule;
import biolockj.module.io.InputSource;
import biolockj.util.BioLockJUtil;

/**
 * This BioModule allows users to call in their own scripts into BLJ
 * 
 * @blj.web_desc Allows User made scripts into the BLJ pipeline
 */
public class GenMod extends ScriptModuleImpl implements ApiModule, InputDataModule {

	public GenMod() {
		super();
		addNewProperty( LAUNCHER, Properties.STRING_TYPE, LAUNCHER_DESC );
		addNewProperty( PARAM, Properties.STRING_TYPE, PARAM_DESC );
		addNewProperty( SCRIPT, Properties.FILE_PATH, SCRIPT_DESC );
		addNewProperty( RESOURCES, Properties.FILE_PATH_LIST, RESOURCES_DESC );
	}

	@Override
	public List<List<String>> buildScript( final List<File> files ) throws Exception {
		final List<List<String>> data = new ArrayList<>();
		final ArrayList<String> lines = new ArrayList<>();
		transferResources();
		lines.add( getLauncher() + transferScript() + getScriptParams() );
		data.add( lines );
		Log.info( GenMod.class, "Core command: " + data );
		return data;
	}

	@Override
	public void checkDependencies() throws Exception {
		super.checkDependencies();
		isValidProp(LAUNCHER);
		isValidProp(PARAM);
		isValidProp(SCRIPT);
		isValidProp(RESOURCES);
	}
	
	@Override
	public Boolean isValidProp( String property ) throws Exception {
	    Boolean isValid = super.isValidProp( property );
	    switch(property) {
	        case LAUNCHER:
	        	getLauncher();
	            isValid = true;
	            break;
	        case SCRIPT:
	        	Config.requireExistingFile( this, SCRIPT );
	            isValid = true;
	            break;
	        case PARAM:
	        	Config.getString(this, PARAM);
	            isValid = true;
	            break;
	        case RESOURCES:
	        	Config.getExistingFileList( this, RESOURCES );
	            isValid = true;
	            break;
	    }
	    return isValid;
	}

	protected String getLauncher() throws Exception {
		String launcher = Config.getString( this, LAUNCHER );
		if( launcher != null ) {
			launcher = Config.getExe( this, Constants.EXE_PREFIX + launcher ) + " ";
			Log.debug( GenMod.class, "Launcher used: " + launcher );
		} else {
			launcher = "";
			Log.debug( GenMod.class, "No Launcher provided" );
		}
		return launcher;

	}

	protected String getScriptParams() {
		final String param = Config.getString( this, PARAM );
		if( param == null ) {
			Log.debug( GenMod.class, "No param provided" );
			return "";
		}

		Log.debug( GenMod.class, "param provided: " + param );
		return " " + param;

	}

	protected String transferScript() throws ConfigPathException, IOException, Exception {
		final File original = Config.requireExistingFile( this, SCRIPT );
		FileUtils.copyFileToDirectory( original, getResourceDir() );
		final File copy = new File( getResourceDir() + File.separator + original.getName() );
		copy.setExecutable( true, false );
		Log.debug( GenMod.class, "Users script saved to: " + copy.getAbsolutePath() );
		return copy.getAbsolutePath();
	}
	
	protected void transferResources() throws ConfigPathException, IOException, Exception {
		if( Config.getString( this, RESOURCES ) != null ) {
			for( File file: Config.getExistingFileList( this, RESOURCES ) ) {
				FileUtils.copyFileToDirectory( file, getResourceDir() );
				Log.info( this.getClass(),
					"Copied resource " + file.getAbsolutePath() + " to module resource folder: " + getResourceDir() );
			}
		}
	}
	
	/**
	 * Completely open-ended. User-supplied script must find any input it needs.
	 */
	@Override
	protected List<InputSource> findInputSources() throws BioLockJException {
		return new ArrayList<InputSource>();
	}
	
	@Override
	public Set<String> getInputDataTypes() {
		return new TreeSet<String>();
	}
	
	/**
	 * To run the GenMod in docker, the user must supply a docker image name.
	 */
	@Override
	public String getDockerImageName() {
		return "blj_basic";
	}
	
	@Override
	public String getDescription() {
		return "Allows user to add their own scripts into the BioLockJ pipeline.";
	}
	
	@Override
	public String getDetails() throws API_Exception {
		return BuildDocs.copyFromModuleResource( this, "GenModDetails.md" );
	}

	@Override
	public String getCitationString() {
		return "BioLockJ " + BioLockJUtil.getVersion( );
	}

	/**
	 * {@link biolockj.Config} property: {@value #LAUNCHER}<br>
	 * {@value #LAUNCHER_DESC}
	 */
	protected static final String LAUNCHER = "genMod.launcher";
	private static final String LAUNCHER_DESC = "Define executable language command if it is not included in your $PATH";
	
	/**
	 * {@link biolockj.Config} property: {@value #PARAM}<br>
	 * {@value #PARAM_DESC}
	 */
	protected static final String PARAM = "genMod.param";
	private static final String PARAM_DESC = "parameters to pass to the user's script";
	
	/**
	 * {@link biolockj.Config} property: {@value #SCRIPT}<br>
	 * {@value #SCRIPT_DESC}
	 */
	protected static final String SCRIPT = "genMod.scriptPath";
	private static final String SCRIPT_DESC = "path to user script";
	
	/**
	 * {@link biolockj.Config} property: {@value #RESOURCES}<br>
	 * {@value #RESOURCES_DESC}
	 */
	protected static final String RESOURCES = "genMod.resources";
	private static final String RESOURCES_DESC = "path to one or more files to be copied to the module resource folder.";

}
