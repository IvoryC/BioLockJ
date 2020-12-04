/**
 * @UNCC Fodor Lab
 * @author Michael Sioda
 * @email msioda@uncc.edu
 * @date May 15, 2018
 * @disclaimer This code is free software; you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any
 * later version, provided that any use properly credits the author. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details at http://www.gnu.org *
 */
package biolockj.module.report.r;

import java.io.File;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.*;
import biolockj.Config;
import biolockj.Constants;
import biolockj.Log;
import biolockj.Properties;
import biolockj.api.API_Exception;
import biolockj.api.ApiModule;
import biolockj.dataType.DataUnitFactory;
import biolockj.dataType.MetaField;
import biolockj.exception.BioLockJException;
import biolockj.exception.ConfigFormatException;
import biolockj.exception.ModuleInputException;
import biolockj.module.BioModule;
import biolockj.module.io.ModuleInput;
import biolockj.module.io.ModuleOutput;
import biolockj.module.io.SpecificModuleOutput;
import biolockj.module.io.InputSource;
import biolockj.module.io.ModuleIO;
import biolockj.module.report.taxa.TaxaTable;
import biolockj.util.BioLockJUtil;
import biolockj.util.MetaUtil;
import biolockj.util.ModuleUtil;
import biolockj.util.RMetaUtil;

/**
 * This BioModule is used to build the R script used to generate taxonomy statistics and plots.
 * 
 * @blj.web_desc R Statistics Calculator
 */
public class R_CalculateStats extends R_Module implements ApiModule, ModuleIO {
	
	public R_CalculateStats() {
		super();
		addGeneralProperty( Constants.R_RARE_OTU_THRESHOLD );
		addGeneralProperty( MetaUtil.META_FILE_PATH, "(required) Table whose columns (all columns, or those given through *" + RMetaUtil.R_REPORT_FIELDS + "*) which give grouping/design for statistical tests."  );
		addGeneralProperty( RMetaUtil.R_REPORT_FIELDS, "(recommended)" );
		addGeneralProperty( RMetaUtil.R_EXCLUDE_FIELDS, "(optional)" );
		addGeneralProperty( RMetaUtil.R_NOMINAL_FIELDS, "(optional)" );
		addGeneralProperty( RMetaUtil.R_NUMERIC_FIELDS, "(optional)" );
		addNewProperty( R_ADJ_PVALS_SCOPE, Properties.STRING_TYPE, "defines R p.adjust( n ) parameter is calculated. Options:  " + ADJ_PVAL_ATTRIBUTE + ", " + ADJ_PVAL_LOCAL + ", " + ADJ_PVAL_TAXA + ", " + ADJ_PVAL_ATTRIBUTE + ".");
		addNewProperty( R_PVAL_ADJ_METHOD, Properties.STRING_TYPE, "the p.adjust \"method\" parameter" );
		addNewProperty( ADDED_FIELDS, Properties.BOOLEAN_TYPE, ADDED_FIELDS_DESC, Constants.FALSE);
	}

	/**
	 * Validate configuration file properties used to build the R report:
	 * <ul>
	 * <li>super.checkDependencies()
	 * <li>Require {@value #R_ADJ_PVALS_SCOPE}
	 * <li>Require {@value #R_PVAL_ADJ_METHOD}
	 * </ul>
	 */
	@Override
	public void checkDependencies() throws Exception {
		super.checkDependencies();
		String scope = Config.requireString( this, R_ADJ_PVALS_SCOPE );
		if ( !scope.equals( ADJ_PVAL_ATTRIBUTE ) && !scope.equals( ADJ_PVAL_GLOBAL ) 
						&& !scope.equals( ADJ_PVAL_LOCAL ) && !scope.equals( ADJ_PVAL_TAXA ) ) throw new ConfigFormatException( R_ADJ_PVALS_SCOPE, getDescription(R_ADJ_PVALS_SCOPE) );
		Config.requireString( this, R_PVAL_ADJ_METHOD );
		RMetaUtil.classifyReportableMetadata( this );
		Config.getPositiveDoubleVal( this, Constants.R_RARE_OTU_THRESHOLD );
	}
	
	@Override
	protected Map<String, String> requiredRPackages() {
		Map<String, String> packages = super.requiredRPackages();
		packages.put("coin","https://CRAN.R-project.org");
		packages.put("Kendall","https://CRAN.R-project.org");
		return packages;
	}
	
	@Override
	public List<List<String>> buildScript( final List<File> files ) throws Exception {
		List<List<String>> outer = new ArrayList<>();
		TaxaTable tt = getInputTaxaTable(); //ignore the files arg
		getFunctionLib();
		File rscript = getModuleRScript();
		String metaFilePath = MetaUtil.getMetadata().getAbsolutePath();
		Log.debug(this.getClass(), "Using metadata file: " + metaFilePath);
		for (String level : Config.getList( this, Constants.REPORT_TAXONOMY_LEVELS )) {
			Log.debug(this.getClass(), "Building command for taxonomic level: " + level);
			if ( tt.getTaxaTableFile( level ) != null ) {
				List<String> inner = new ArrayList<>();
				//TODO add a call to the ScriptBuilder to get the ignored-new-line character/string
				inner.add( Config.getExe( this, Constants.EXE_RSCRIPT ) + " " + rscript.getAbsolutePath() 
					+ " " + level 
					+ " " + tt.getTaxaTableFile( level ).getAbsolutePath() 
					+ " " + metaFilePath );
				outer.add( inner );
			}else {
				Log.info(this.getClass(), "No input file found for level [" + level + "].");
			}
		}
		return outer;
	}
	
	@Override
	protected List<File> findModuleInputFiles() throws ModuleInputException {
		List<File> files = new ArrayList<>();
		TaxaTable t = getInputTaxaTable();
		try {
			files.addAll( t.getFiles() );
			files.add( MetaUtil.getMetadata() );
		} catch( BioLockJException ex ) {
			throw new ModuleInputException( this, ex );
		}
		return files;
	}
	
	private TaxaTable getInputTaxaTable() throws ModuleInputException {
		TaxaTable tt;
		@SuppressWarnings("unchecked")
		InputSource<TaxaTable> is = ((InputSource<TaxaTable>) tableInput.getSource());
		try {
			tt = is.getData().get( 0 );
		} catch( BioLockJException ex ) {
			ex.printStackTrace();
			throw new ModuleInputException (this, is, ex);
		}
		return tt;
	}

	/**
	 * Get the stats file for the given fileType and taxonomy level.
	 *
	 * @param module Calling module
	 * @param level Taxonomy level
	 * @param isParametric Boolean TRUE to query for parametric file
	 * @param isAdjusted Boolean TRUE to query for adjusted p-value file
	 * @return File Table of statistics or null
	 * @throws Exception if errors occur
	 */
	@Deprecated

	public static File getStatsFile( final BioModule module, final String level, final Boolean isParametric,
		final Boolean isAdjusted ) throws Exception {
		final String querySuffix = "_" + level + "_" + getSuffix( isParametric, isAdjusted ) + TSV_EXT;
		final Set<File> results = new HashSet<>();
		final IOFileFilter ff = new WildcardFileFilter( "*" + querySuffix );
		for( final File dir: getStatsFileDirs( module ) ) {
			final Collection<File> files = FileUtils.listFiles( dir, ff, HiddenFileFilter.VISIBLE );
			if( files.size() > 0 ) results.addAll( files );
		}

		final int count = results.size();
		if( count == 0 ) return null;
		else if( count == 1 ) {
			final File statsFile = results.iterator().next();
			Log.info( R_CalculateStats.class, "Return stats file: " + statsFile.getAbsolutePath() );
			return statsFile;
		}

		throw new Exception( "Only 1 " + R_CalculateStats.class.getSimpleName() + " output file with suffix = \"" +
			querySuffix + "\" should exist.  Found " + count + " files --> " + results );

	}

	/**
	 * Get the file name suffix used to specify types of statistics.
	 * 
	 * @param isParametric boolean get the Parametric rather than the non-parametric suffix. If null, get the r-squared
	 * suffix.
	 * @param isAdjusted boolean get the adjusted rather than the non-adjusted suffix
	 * @return file name suffix
	 * @throws Exception if errors occur
	 */
	public static String getSuffix( final Boolean isParametric, final Boolean isAdjusted ) throws Exception {
		if( isParametric == null ) return R_SQUARED_VALS;
		else if( isParametric && isAdjusted != null && isAdjusted ) return P_VALS_PAR_ADJ;
		else if( isParametric ) return P_VALS_PAR;
		else if( !isParametric && isAdjusted != null && isAdjusted ) return P_VALS_NP_ADJ;
		else if( !isParametric ) return P_VALS_NP;

		throw new Exception( "BUG DETECTED! Logic error in getSuffix( isParametric, isAdjusted)" );
	}

	/**
	 * Analyze file name for key strings to determine if file is a stats file output by this module.
	 *
	 * @param file Ambiguous file
	 * @return TRUE if file name is formatted as if output by this module
	 */
	public static boolean isStatsFile( final File file ) {
		for( final String suffix: statSuffixSet )
			if( file.getName().contains( suffix ) && file.getName().endsWith( TSV_EXT ) ) return true;
		return false;
	}

	@Deprecated
	private static List<File> getStatsFileDirs( final BioModule module ) throws Exception {
		final BioModule statsModule = ModuleUtil.getModule( module, R_CalculateStats.class.getName(), false );
		if( statsModule != null ) {
			final List<File> dirs = new ArrayList<>();
			dirs.add( statsModule.getOutputDir() );
			return dirs;
		}

		return BioLockJUtil.getInputDirs();
	}

	/**
	 * This {@value #R_PVAL_ADJ_METHOD} option can be set in {@link biolockj.Config} file: {@value #ADJ_PVAL_ATTRIBUTE}
	 */
	protected static final String ADJ_PVAL_ATTRIBUTE = "ATTRIBUTE";

	/**
	 * This {@value #R_PVAL_ADJ_METHOD} option can be set in {@link biolockj.Config} file: {@value #ADJ_PVAL_GLOBAL}
	 */
	protected static final String ADJ_PVAL_GLOBAL = "GLOBAL";

	/**
	 * This {@value #R_PVAL_ADJ_METHOD} option can be set in {@link biolockj.Config} file: {@value #ADJ_PVAL_LOCAL}
	 */
	protected static final String ADJ_PVAL_LOCAL = "LOCAL";

	/**
	 * This {@value #R_PVAL_ADJ_METHOD} option can be set in {@link biolockj.Config} file: {@value #ADJ_PVAL_TAXA}
	 */
	protected static final String ADJ_PVAL_TAXA = "TAXA";

	/**
	 * Non parametric p-value identifier: {@value #P_VALS_NP}
	 */
	protected static final String P_VALS_NP = "nonParametricPvals";

	/**
	 * Non parametric adjusted p-value identifier: {@value #P_VALS_NP_ADJ}
	 */
	protected static final String P_VALS_NP_ADJ = "adjNonParPvals";

	/**
	 * Parametric p-value identifier: {@value #P_VALS_PAR}
	 */
	protected static final String P_VALS_PAR = "parametricPvals";

	/**
	 * Parametric adjusted p-value identifier: {@value #P_VALS_PAR_ADJ}
	 */
	protected static final String P_VALS_PAR_ADJ = "adjParPvals";

	/**
	 * {@link biolockj.Config} String property: {@value #R_ADJ_PVALS_SCOPE} defines R p.adjust( n ) parameter. There are
	 * 4 supported options:
	 * <ol>
	 * <li>{@value #ADJ_PVAL_GLOBAL} n = number of all pVal tests for all fields and all taxonomy levels
	 * <li>{@value #ADJ_PVAL_ATTRIBUTE} n = number of all pVal tests for 1 field at all taxonomy levels
	 * <li>{@value #ADJ_PVAL_TAXA} n = number of all pVal tests for all fields at 1 taxonomy level
	 * <li>{@value #ADJ_PVAL_LOCAL} n = number of all pVal tests for 1 field at 1 taxonomy level
	 * </ol>
	 */
	protected static final String R_ADJ_PVALS_SCOPE = "r_CalculateStats.pAdjustScope";

	/**
	 * {@link biolockj.Config} String property: {@value #R_PVAL_ADJ_METHOD} defines p.adjust( method ) parameter.
	 * p.adjust.methods = c("holm", "hochberg", "hommel", "bonferroni", "BH", "BY", "fdr", "none")
	 */
	protected static final String R_PVAL_ADJ_METHOD = "r_CalculateStats.pAdjustMethod";

	/**
	 * R^2 identifier: {@value #R_SQUARED_VALS}
	 */
	protected static final String R_SQUARED_VALS = "rSquaredVals";

	private static final Set<String> statSuffixSet = new HashSet<>();

	static {
		statSuffixSet.add( P_VALS_NP );
		statSuffixSet.add( P_VALS_NP_ADJ );
		statSuffixSet.add( P_VALS_PAR );
		statSuffixSet.add( P_VALS_PAR_ADJ );
		statSuffixSet.add( R_SQUARED_VALS );
	}

	@Override
	public String getDescription() {
		return "Generate a basic summary statistics table.";
	}
	
	@Override
	public String getDetails() throws API_Exception {
		return "Generate a summary statistics table with [adjusted and unadjusted] [parameteric and non-parametirc] p-values and r<sup>2</sup> values for each reportable metadata field and each *report.taxonomyLevel* configured.";
	}

	@Override
	public String getCitationString() {
		return "BioLockJ " + BioLockJUtil.getVersion() + System.lineSeparator() + "Module created by Mike Sioda and developed by Ivory Blakley";
	}

	@Override
	protected String getModuleRScriptName() {
		return "R_CalculateStats.R";
	}

	@Override
	protected String getModulePrefix() {
		return "r_CalculateStats";
	}
	
	/**
	 * {@value #ADDED_FIELDS_DESC}
	 */
	private static final String ADDED_FIELDS = "r_CalculateStats.useAddedFields";
	private static final String ADDED_FIELDS_DESC = "Should the reports include tests using metadata columns added by pipeline modules.";
	
	@Override
	public void assignInputSources() throws BioLockJException {
		ModuleUtil.assignInputSources(this, tableInput );

		metaInput.setSource( new InputSource<MetaField>( Arrays.asList( MetaUtil.getMetadata() ),
			(MetaField) metaInput.getTemplate() ) );
		
		if (Config.requireBoolean( this, ADDED_FIELDS ) ) {
			Log.debug(this.getClass(), "Fields added by other modules will be included in " + this + " output.");
			Log.debug(this.getClass(), "This will not include metadata fields added by modules that do not declare a MetaField output.");
			ModuleUtil.assignInputSources(this, additional );
		}else {
			Log.debug(this.getClass(), "Fields added by other modules will NOT be included in " + this + " output.");
			inputTypes.remove( additional );
		}
		
	}
	
	/**
	 * Determine the fields that were given in the meta data.
	 * @return
	 * @throws BioLockJException
	 */
	private Set<String> selectMetaFields() throws BioLockJException{
		RMetaUtil.classifyReportableMetadata( this );
		Set<String> fields = new HashSet<>();
		fields.addAll( Config.getList( null, RMetaUtil.R_REPORT_FIELDS ) );
		if (fields.isEmpty()) {
			fields.addAll( Config.getList( this, RMetaUtil.R_NOMINAL_FIELDS ) );
			fields.addAll( Config.getList( this, RMetaUtil.R_NUMERIC_FIELDS ) );
			fields.addAll( RMetaUtil.getBinaryFields( null ) );
		}
		fields.removeAll( Config.getList( this, RMetaUtil.R_EXCLUDE_FIELDS ) );
		return fields;
	}

	DataUnitFactory<MetaField> pickMetaFields = new DataUnitFactory<MetaField>() {

		@Override
		public List<MetaField> getData( List<File> files, MetaField template, boolean useAllFiles )
			throws ModuleInputException {
			List<MetaField> fields = new ArrayList<>();
			try {
				for( String field: selectMetaFields() ) {
					fields.add( new MetaField( field ) );
				}
			} catch( BioLockJException e ) {
				e.printStackTrace();
				throw new ModuleInputException(e.getMessage());
			}
			return fields;
		}
		
	};
	
	private void defineInputs() {
		inputTypes = new ArrayList<>();
		
		tableInput = new ModuleInput("taxa table", 
			"A taxa table, values should already be normalized. May include mutliple tables to represent multiple taxonomic levels.", 
			new TaxaTable() );
		inputTypes.add( tableInput );

		metaInput = new ModuleInput( "test design", 
			"One or more metadata columns defining groups to use for statistical tests, see property _" + RMetaUtil.R_REPORT_FIELDS + "_.",
			new MetaField( "[meta data field name]" ) {

				@Override
				public DataUnitFactory<MetaField> getFactory() {
					return pickMetaFields;
				}

			} ) ;
		inputTypes.add( metaInput );
		
		additional = new ModuleInput( "test design from pipeline",
			"One or more metadata columns created by pipeline modules defining groups to use for statistical tests, see property _" + RMetaUtil.R_REPORT_FIELDS + "_.",
			new MetaField( "[meta data field name]" ));
		additional.setRequired( false );
		inputTypes.add( additional );
		
	}
	
	@Override
	public List<ModuleInput> getInputTypes(){
		if (inputTypes==null) defineInputs();
		return inputTypes;
	}
	
	List<ModuleInput> inputTypes = null;
	
	ModuleInput tableInput;
	ModuleInput metaInput;
	ModuleInput additional;

	@Override
	public List<ModuleOutput> getOutputTypes() {
		List<ModuleOutput> list = new ArrayList<>();
		list.add( new SpecificModuleOutput<R_CalculateStats>( this ) );
		return list;
	}

}
