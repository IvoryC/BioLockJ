/**
 * @UNCC Fodor Lab
 * @author Michael Sioda
 * @email msioda@uncc.edu
 * @date June 20, 2017
 * @disclaimer This code is free software; you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any
 * later version, provided that any use properly credits the author. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details at http://www.gnu.org *
 */
package biolockj.module.seq;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import biolockj.*;
import biolockj.api.ApiModule;
import biolockj.dataType.DataUnit;
import biolockj.dataType.DataUnitFilter;
import biolockj.dataType.seq.FastqData;
import biolockj.dataType.seq.FastqFormat;
import biolockj.dataType.seq.SeqData;
import biolockj.exception.BioLockJException;
import biolockj.exception.ModuleInputException;
import biolockj.module.ReferenceDataModule;
import biolockj.module.ScriptModuleImpl;
import biolockj.module.SeqModule;
import biolockj.module.io.InputSource;
import biolockj.module.io.ModuleInput;
import biolockj.module.io.ModuleOutput;
import biolockj.util.*;

/**
 * This BioModule runs biobakery kneaddata program to remove contaminated DNA.<br>
 * Multiple contaminent DNA databases can be used to filter reads simultaniously.<br>
 * Common contaminents include Human, Viral, and Plasmid DNA.<br>
 * 
 * @blj.web_desc Knead Data Sanitizer
 */
public class KneadData extends ScriptModuleImpl implements ApiModule, SeqModule, ReferenceDataModule {

	public KneadData() {
		super();
		addNewProperty( EXE_KNEADDATA, "", "" );
		addNewProperty( EXE_KNEADDATA_PARAMS, Properties.STRING_TYPE, "Optional parameters passed to kneaddata" );
		addNewProperty( KNEAD_DBS, Properties.FILE_PATH_LIST, "Path to database(s) for KneadData program" );
	}

	@Override
	public List<List<String>> buildScript( final List<File> files ) throws Exception {
		final List<List<String>> data = new ArrayList<>();
		for( final File seqFile: files ) {
			if( SeqUtil.hasPairedReads() && !SeqUtil.isForwardRead( seqFile.getName() ) ) continue;

			final ArrayList<String> lines = new ArrayList<>();

			if( SeqUtil.hasPairedReads() )
				lines.add( sanatize( seqFile, SeqUtil.getPairedReads( files ).get( seqFile ) ) );
			else lines.add( sanatize( seqFile, null ) );

			lines.addAll( buildScriptLinesToMoveValidSeqsToOutputDir( SeqUtil.getSampleId( seqFile ) ) );

			data.add( lines );
		}

		return data;
	}

	@Override
	public void checkDependencies() throws Exception {
		super.checkDependencies();		
		SeqModule.super.checkDependencies();
		ReferenceDataModule.super.checkDependencies();
		getParams();
	}

	@Override
	public String getSummary() throws Exception {
		final StringBuffer sb = new StringBuffer();
		try {
			for (String line : SummaryUtil.addRefDataSummaryLines( this ) ) {
				sb.append( line + RETURN );
			}
		} catch( final Exception ex ) {
			final String msg = "Unable to complete module summary: " + ex.getMessage();
			sb.append( msg + RETURN );
			Log.warn( getClass(), msg );
		}

		return super.getSummary() + sb.toString();
	}

	/**
	 * This method generates the worker script function: {@value #FUNCTION_SANATIZE}.
	 */
	@Override
	public List<String> getWorkerScriptFunctions() throws Exception {
		final List<String> lines = super.getWorkerScriptFunctions();
		lines.add( "function " + FUNCTION_SANATIZE + "() {" );
		lines.add( Config.getExe( this, EXE_KNEADDATA ) + " " + getParams() + OUTPUT_FILE_PREFIX_PARAM + " $1 " +
			INPUT_PARAM + " $2 " + ( SeqUtil.hasPairedReads() ? INPUT_PARAM + " $3 ": "" ) + OUTPUT_PARAM + " " +
			getTempDir().getAbsolutePath() );
		lines.add( "}" + RETURN );
		return lines;
	}

	/**
	 * Move 1 file named /"Sample_ID.fastq/" if module input consists of forward reads only.<br>
	 * If module input contains paired reads, move 2 files named /"Sample_ID_paired_1.fastq/" and
	 * /"Sample_ID_paired_2.fastq/" to the module output directory (after renaming them to BioLockJ standards).
	 *
	 * @param sampleId Sample ID
	 * @return Script lines to move the file or files
	 * @throws Exception if errors occur building lines
	 */
	protected List<String> buildScriptLinesToMoveValidSeqsToOutputDir( final String sampleId ) throws Exception {
		final List<String> lines = new ArrayList<>();
		final String fileSuffix = fastqExt();
		if( SeqUtil.hasPairedReads() ) {
			final String fwSuffix = Config.requireString( this, Constants.INPUT_FORWARD_READ_SUFFIX );
			final String rvSuffix = Config.requireString( this, Constants.INPUT_REVERSE_READ_SUFFIX );
			final File fwOutFile =
				new File( getOutputDir().getAbsolutePath() + File.separator + sampleId + fwSuffix + fileSuffix );
			final File rvOutFile =
				new File( getOutputDir().getAbsolutePath() + File.separator + sampleId + rvSuffix + fileSuffix );
			lines.add(
				"mv " + getSanatizedFile( sampleId, false ).getAbsolutePath() + " " + fwOutFile.getAbsolutePath() );
			lines.add(
				"mv " + getSanatizedFile( sampleId, true ).getAbsolutePath() + " " + rvOutFile.getAbsolutePath() );
		} else {
			final File outFile = new File( getOutputDir().getAbsolutePath() + File.separator + sampleId + fileSuffix );
			lines.add( "mv " + getSanatizedFile( sampleId, null ).getAbsolutePath() + " " + outFile.getAbsolutePath() );
		}

		return lines;
	}

	/**
	 * Return sanitized sequence data file.
	 *
	 * @param sampleId Sample ID
	 * @param isRvRead Boolean TRUE to return the file containing reverse reads
	 * @return File with sanitized sequences
	 */
	protected File getSanatizedFile( final String sampleId, final Boolean isRvRead ) {
		String suffix = "";
		if( SeqUtil.hasPairedReads() ) suffix += isRvRead ? RV_OUTPUT_SUFFIX: FW_OUTPUT_SUFFIX;

		return new File( getTempDir().getAbsolutePath() + File.separator + sampleId + suffix + fastqExt() );
	}

	private String getDBs() throws ModuleInputException {
		String dbs = "";
		for ( File db : getReferenceFiles() ) {
			dbs += DB_PARAM + " " + db.getAbsolutePath() + " ";
		}
		return dbs;
	}

	private String getParams() throws Exception {
		String params = getRuntimeParams( Config.getList( this, EXE_KNEADDATA_PARAMS ), NUM_THREADS_PARAM ) + getDBs();
		if( !params.contains( BYPASS_TRIM_PARAM ) && !params.contains( TRIMMOMATIC_PARAM ) && DockerUtil.inDockerEnv() )
			params += DOCKER_TRIM_PARAM + " ";

		return params;
	}

	private static String fastqExt() {
		return "." + Constants.FASTQ;
	}

	private static String sanatize( final File seqFile, final File rvRead ) throws Exception {
		return FUNCTION_SANATIZE + " " + SeqUtil.getSampleId( seqFile ) + " " + seqFile.getAbsolutePath() +
			( rvRead == null ? "": " " + rvRead.getAbsolutePath() );
	}
	
	@Override
	public String getDockerImageName() {
		if (Config.getString( this, KNEAD_DBS ) != null )
			return "knead_data_dbfree";
		else
			return "kraken_classifier";
	}

	/**
	 * KneadData executable: {@value #EXE_KNEADDATA}
	 */
	protected static final String EXE_KNEADDATA = "exe.kneaddata";

	/**
	 * {@link biolockj.Config} property containing parameters for {@value #EXE_KNEADDATA}:
	 * {@value #EXE_KNEADDATA_PARAMS}
	 */
	protected static final String EXE_KNEADDATA_PARAMS = "kneaddata.kneaddataParams";

	/**
	 * Name of the bash function used to decompress gzipped files: {@value #FUNCTION_SANATIZE}
	 */
	protected static final String FUNCTION_SANATIZE = "sanatizeData";

	/**
	 * {@link biolockj.Config} required property to the contaminent databases {@value #KNEAD_DBS}:
	 */
	protected static final String KNEAD_DBS = "kneaddata.dbs";
	
	private static final String DEFAULT_DB_IN_DOCKER = "/mnt/efs/db";

	private static final String BYPASS_TRIM_PARAM = "--bypass-trim";
	private static final String DB_PARAM = "-db";
	private static final String DOCKER_TRIM_PARAM = "--trimmomatic /app/Trimmomatic-0.38";
	private static final String FW_OUTPUT_SUFFIX = "_paired_1";
	private static final String INPUT_PARAM = "-i";
	private static final String NUM_THREADS_PARAM = "-t";
	private static final String OUTPUT_FILE_PREFIX_PARAM = "--output-prefix";
	private static final String OUTPUT_PARAM = "-o";
	private static final String RV_OUTPUT_SUFFIX = "_paired_2";
	private static final String TRIMMOMATIC_PARAM = "--trimmomatic ";
	
	@Override
	public String getDescription() {
		return "Run the Biobakery [KneadData](https://github.com/biobakery/kneaddata) program to remove contaminated DNA.";
	}

	@Override
	public String getCitationString() {
		return "https://github.com/biobakery/kneaddata" + System.lineSeparator() + "Module developed by Mike Sioda";
	}
	
	@Override
	public Boolean isValidProp( String property ) throws Exception {
		Boolean isValid = super.isValidProp( property );
		switch(property) {
			case EXE_KNEADDATA:
				isValid = Properties.isValidExeProp( this, EXE_KNEADDATA );
				break;
		}
		return isValid;
	}

	@Override
	public List<ModuleInput> getReferenceTypes() {
		List<ModuleInput> refs = Arrays.asList( refDB );
		return refs;
	}

	@Override
	public void assignReferenceSources() throws BioLockJException {
		List<File> configedDbs = null;
		if ( Config.getString( this, KNEAD_DBS ) != null) {
			configedDbs = Config.requireExistingDirs( this, KNEAD_DBS );
			refDB.setSource( new InputSource( configedDbs, refDB.getTemplate() ) );
		}else if ( DockerUtil.inDockerEnv() ){
			configedDbs = Arrays.asList( new File( DEFAULT_DB_IN_DOCKER ) );
			refDB.setSource( new InputSource( configedDbs, refDB.getTemplate() ) );
		}else {
			ModuleUtil.findInputModule(this, refDB);
		}
	}

	@Override
	public List<File> getReferenceFiles() throws ModuleInputException {
		return ReferenceDataModule.super.getReferenceFiles();
	}

	@Override
	public List<ModuleInput> getInputTypes() {
		List<ModuleInput> inputs = new ArrayList<>();
		inputs.add( inSeqs );
		return SeqModule.super.getInputTypes();
	}

	@Override
	public List<ModuleOutput> getOutputTypes() {
		List<ModuleOutput> outputs = new ArrayList<>();
		outputs.add( new ModuleOutput(this, "Filerted data", new FastqData() ) );
		return outputs;
	}
	
	private final ModuleInput refDB = new ModuleInput( "Reference genome", 
		"Sequences to remove from the sample data, must be a valide KneadData database.", 
		new KneadDataDB() );
	
	private final ModuleInput inSeqs = new ModuleInput( "Sequence data", "Fastq files", new FastqData(), new DataUnitFilter() {
		@Override
		public boolean accept( DataUnit data ) {
			return data instanceof SeqData && ( (SeqData) data ).getFormat() instanceof FastqFormat;
		}
	} ) ;
	
}
