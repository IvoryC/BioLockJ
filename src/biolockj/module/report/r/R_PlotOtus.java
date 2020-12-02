/**
 * @UNCC Fodor Lab
 * @author Michael Sioda
 * @email msioda@uncc.edu
 * @date Feb 18, 2017
 * @disclaimer This code is free software; you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any
 * later version, provided that any use properly credits the author. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details at http://www.gnu.org *
 */
package biolockj.module.report.r;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import biolockj.Config;
import biolockj.Constants;
import biolockj.Log;
import biolockj.Properties;
import biolockj.api.ApiModule;
import biolockj.dataType.DataUnit;
import biolockj.exception.BioLockJException;
import biolockj.exception.ConfigNotFoundException;
import biolockj.exception.ConfigPathException;
import biolockj.exception.DockerVolCreationException;
import biolockj.exception.PipelineFormationException;
import biolockj.module.BioModule;
import biolockj.module.io.InputSource;
import biolockj.module.report.taxa.TaxaTable;
import biolockj.util.BioLockJUtil;
import biolockj.util.MetaUtil;
import biolockj.util.ModuleUtil;
import biolockj.util.TaxaUtil;

/**
 * This BioModule is used to build the R script used to generate OTU-metadata box-plots and scatter-plots for each
 * report field and taxonomy level.
 * 
 * @blj.web_desc R Plot OTUs
 */
public class R_PlotOtus extends R_Module implements ApiModule {
	
	public R_PlotOtus() {
		super();
		addNewProperty( R_PVAL_FORMAT, Properties.STRING_TYPE, "Sets the format used in R sprintf() function" );
		addNewProperty( COUNTS_IN_PROP, Properties.FILE_PATH, "Directory where taxa counts files are found if not available from pipeline modules" );
		addGeneralProperty( Constants.R_RARE_OTU_THRESHOLD );
		addGeneralProperty( Constants.R_COLOR_BASE );
		addGeneralProperty( Constants.R_COLOR_HIGHLIGHT );
		addGeneralProperty( Constants.R_COLOR_PALETTE);
		addGeneralProperty( Constants.R_COLOR_POINT );
		addGeneralProperty( Constants.R_PCH );
		addGeneralProperty( Constants.R_COLOR_FILE, "(recommended) see other color control options in General Properties." );
		addGeneralProperty( Constants.SET_SEED, "(recommended)");
	}

	@Override
	public void checkDependencies() throws Exception {
		super.checkDependencies();
		Config.getString( this, R_PVAL_FORMAT );
		Config.getPositiveDoubleVal( this, Constants.R_RARE_OTU_THRESHOLD );
		Config.getString( this, Constants.R_COLOR_BASE );
		Config.getString( this, Constants.R_COLOR_HIGHLIGHT );
		Config.getString( this, Constants.R_COLOR_PALETTE );
		Config.getString( this, Constants.R_COLOR_POINT );
		Config.getExistingFile(null, Constants.R_COLOR_FILE);
		Config.getString( this, Constants.R_PCH );
		Config.getPositiveInteger( this, Constants.SET_SEED );
	}
	
	@Override
	public List<List<String>> buildScript( List<File> files ) throws Exception {
		//Disregard the glommed together input files
		List<List<String>> outer = new ArrayList<>();
		TaxaTable tt = getInputTaxaTable();
		File statsDir = getInputSources().get( 1 ).getFile();
		getFunctionLib();
		File rscript = getModuleRScript();
		String metaFilePath = MetaUtil.getMetadata().getAbsolutePath();
		Log.debug(this.getClass(), "Using metadata file: " + metaFilePath);
		for (String level : TaxaUtil.getTaxaLevels() ) {
			List<String> inner = new ArrayList<>();
			if (tt.getTaxaTableFile( level ) == null) {
				Log.warn(this.getClass(), "Skipping level " + level + " because no counts table was found.");
			}else {
				inner.add( Config.getExe( this, Constants.EXE_RSCRIPT ) + " " + rscript.getAbsolutePath() 
				+ " " + level 
				+ " " + tt.getTaxaTableFile( level ).getAbsolutePath() 
				+ " " + metaFilePath 
				+ " " + statsDir.getAbsolutePath() );
				outer.add( inner );
			}
		}
		return outer;
	}
	
	private TaxaTable getInputTaxaTable() throws BioLockJException {
		List<File> files = new ArrayList<>();
		for (File f : getInputSources().get( 0 ).getFile().listFiles()) {
			if (TaxaTable.isTaxaTableFile( f )) files.add( f );
		}
		TaxaTable tt = new TaxaTable();
		tt.setFiles( files );
		return tt;
	}
	
	@Override
	protected List<InputSource> findInputSources() throws BioLockJException {
		List<InputSource> sources = new ArrayList<>();
		sources.add(findTaxaTableSource()); // 0: taxa count tables
		sources.add(findStatsTableSource()); // 1: stats table dir
		return sources;
	}
	
	private InputSource findTaxaTableSource() throws PipelineFormationException, ConfigPathException, ConfigNotFoundException, DockerVolCreationException{
		boolean foundSource = false;
		BioModule prevMod = ModuleUtil.getPreviousModule( this );
		InputSource in = null;
		while( !foundSource ) {
			if (prevMod == null) {
				File taxaDir = Config.requireExistingDir( this, COUNTS_IN_PROP );
				for (File f : taxaDir.listFiles() ) {
					if (TaxaTable.isTaxaTableFile( f )) {
						foundSource = true;
					}
				}
				if (foundSource) {
					in = new InputSource( taxaDir );
				}else{
					throw new PipelineFormationException( "Failed to find taxa table input" );
				}
			}else {
				Collection<DataUnit> outs = prevMod.getOutputTypes();
				if ( !outs.isEmpty() ) {
					for ( DataUnit out : outs ) {
						if (out instanceof TaxaTable) {
							foundSource = true;
							in = new InputSource(prevMod);
						}
					}
				}
			}
			prevMod = ModuleUtil.getPreviousModule( prevMod );
		}
		return in;
	}
	
	/**
	 * This module is a visualization that specifically requires the R_CalculateStats output.
	 * The R_CalculateStats is a pre-requisite.
	 * @return
	 * @throws PipelineFormationException
	 */
	private InputSource findStatsTableSource() throws PipelineFormationException {
		boolean foundSource = false;
		BioModule prevMod = ModuleUtil.getPreviousModule( this );
		InputSource in = null;
		while( !foundSource ) {
			if (prevMod == null) {
				throw new PipelineFormationException( "Failed to find R_CalculateStats module." );
			}else {
				if (prevMod instanceof R_CalculateStats) {
					foundSource = true;
					in = new InputSource(prevMod);
				}
			}
			prevMod = ModuleUtil.getPreviousModule( prevMod );
		}
		return in;
	}

	/**
	 * Returns {@link #getStatPreReqs()} unless the pipeline input is already a stats table.
	 */
	@Override
	public List<String> getPreRequisiteModules() throws Exception {
		List<String> preReqs = super.getPreRequisiteModules();
		preReqs.add( R_CalculateStats.class.getName() );
		return preReqs;
	}
	
	private static final String COUNTS_IN_PROP = "r_PlotOtus.countsDir";

	@Override
	public String getDescription() {
		return "Generate OTU-metadata box-plots and scatter-plots for each reportable metadata field and each *report.taxonomyLevel* configured";
	}

	@Override
	public String getCitationString() {
		return "BioLockJ " + BioLockJUtil.getVersion() + System.lineSeparator() + "Module created by Mike Sioda and developed by Ivory Blakley";
	}

	@Override
	protected String getModuleRScriptName() {
		return "R_PlotOtus.R";
	}

	@Override
	protected String getModulePrefix() {
		return "r_PlotOtus";
	}
	
	private static final String R_PVAL_FORMAT = "r.pValFormat";

}
