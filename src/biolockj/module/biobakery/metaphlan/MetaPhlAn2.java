package biolockj.module.biobakery.metaphlan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import biolockj.Config;
import biolockj.Constants;
import biolockj.Properties;
import biolockj.api.ApiModule;
import biolockj.exception.ConfigFormatException;
import biolockj.exception.ConfigNotFoundException;
import biolockj.module.BioModule;
import biolockj.module.SeqModule;
import biolockj.util.BashScriptBuilder;
import biolockj.util.BioLockJUtil;
import biolockj.util.MetaUtil;
import biolockj.util.SeqUtil;

public class MetaPhlAn2 extends MetaPhlAn_Tool implements ApiModule {
	
	static final String CHECK_PARAMS = "metaphlan.checkParams";
	static final String CHECK_PARAMS_DESC = "Should BioLockJ check the user-provided parameters to metaphlan2.";
	
	protected M2Params params;
	
	protected String analysis = "rel_ab_w_read_stats";
	
	private static final String FUNCTION_NAME = "runMetaphlan";
	private static final String INFO_FUNCTION_NAME = "versionInfo";
	
	public MetaPhlAn2() {
		super();
		addNewProperty( EXE_METAPHLAN, Properties.EXE_PATH, "" );
		addNewProperty( EXE_BOWTIE2, Properties.EXE_PATH, "" );
		addNewProperty( EXE_BOWTIE2_BUILD, Properties.EXE_PATH, "" );
		addNewProperty( INDEX, Properties.STRING_TYPE, INDEX_DESC );
		addNewProperty( METAPHLAN_PARAMS, Properties.STRING_TYPE, METAPHLAN_PARAMS_DESC );
		addNewProperty( CHECK_PARAMS, Properties.BOOLEAN_TYPE, CHECK_PARAMS_DESC, "Y" );
		params = new M2Params();
	}
	
	@Override
	public void checkDependencies() throws Exception {
		super.checkDependencies();
		isValidProp(EXE_METAPHLAN);
		isValidProp(EXE_BOWTIE2);
		isValidProp(EXE_BOWTIE2_BUILD);
		isValidProp(INDEX);
		isValidProp(METAPHLAN_PARAMS);
		isValidProp(CHECK_PARAMS);
	}
	
	@Override
	public Boolean isValidProp( String property ) throws Exception {
		boolean isValid = super.isValidProp( property );
		switch(property) {
			case EXE_METAPHLAN:
				Config.getExe( this, EXE_METAPHLAN );
				isValid = true;
				break;
			case EXE_BOWTIE2:
				Config.getExe( this, EXE_BOWTIE2 );
				isValid = true;
				break;
			case EXE_BOWTIE2_BUILD:
				Config.getExe( this, EXE_BOWTIE2_BUILD );
				isValid = true;
				break;
			case INDEX:
				Config.requireString( this, INDEX );
				isValid = true;
				break;
			case METAPHLAN_PARAMS:
				getExtraParams();
				isValid = true;
				break;
			case CHECK_PARAMS:
				Config.requireBoolean( this, CHECK_PARAMS );
				isValid = true;
				break;
		}
		return isValid;
	}

	@Override
	public boolean isValidInputModule( BioModule module ) {
		return module instanceof SeqModule;
	}

	@Override
	public List<List<String>> buildScript( List<File> files ) throws Exception {
		List<List<String>> outer = new ArrayList<>();
		List<String> in = new ArrayList<>();
		in.add( INFO_FUNCTION_NAME );
		outer.add( in );
		for (File file : files ) {
			List<String> inner = new ArrayList<>();
			inner.add( FUNCTION_NAME + " " + outputFile(file).getAbsolutePath() + " " + file.getAbsolutePath() );
			outer.add( inner );
			MetaUtil.setSampleId( outputFile(file), MetaUtil.getSampleId( file ) );
		}
		return outer;
	}
	
	@Override
	public List<List<String>> buildScriptForPairedReads( List<File> files ) throws Exception {
		List<List<String>> outer = new ArrayList<>();
		final Map<File, File> pairs = SeqUtil.getPairedReads( files );
		List<String> in = new ArrayList<>();
		in.add( INFO_FUNCTION_NAME );
		outer.add( in );
		for (File file : pairs.keySet() ) {
			List<String> inner = new ArrayList<>();
			inner.add( FUNCTION_NAME + " " + outputFile(file).getAbsolutePath() + " " + file.getAbsolutePath() + " " + pairs.get( file ).getAbsolutePath() );
			outer.add( inner );
		}
		return outer;
	}
	
	private File outputFile(File infile) throws Exception {
		String id = MetaUtil.getSampleId( infile );
		if (id == null) id = infile.getName();
		return new File(getOutputDir(), id + "_" + getExtraParams().get(M2Params.TYPE) + ".txt");
	}
	
	@Override
	public List<String> getWorkerScriptFunctions() throws Exception {
		List<String> list = super.getWorkerScriptFunctions();
		String continued = BashScriptBuilder.continueLine();
		String continueing = "   ";
		Map<String, String> argMap = getExtraParams();		
		list.add( "function " + FUNCTION_NAME + "() {");
		list.add( "OUT=$1" );
		list.add( "IN=$2" );
		list.add( "IN_RV=$3" );//only used with paired reads
		list.add( Config.getExe( null, EXE_METAPHLAN ) + " " + M2Params.TYPE + " " + analysis + continued );
		list.add( continueing + "$IN $IN_RV" + continued);
		list.add( continueing + M2Params.OUT_FILE_LONG + " $OUT" + continued);
		for (String name : params.NAMED_ARGS ) {
			if (argMap.containsKey( name ) ) {
				list.add( continueing + name + " " + argMap.get( name ) + continued );
			}
		}
		for (String flag : params.FLAG_ARGS ) {
			if (argMap.containsKey( flag )) {
				list.add( continueing + flag + continued );
			}
		}
		list.add( continueing + M2Params.INDEX_LONG + " " + Config.requireString( this, INDEX ) + continued );
		list.add( continueing + M2Params.NPROC + " " + Config.getPositiveInteger( this, Constants.SCRIPT_NUM_THREADS ) );
		list.add( "}" );
		list.add( "" );
		list.add( "function " + INFO_FUNCTION_NAME + "(){" );
		String infoFile = (new File(getLogDir(), "metaphlan--help.txt")).getAbsolutePath();
		list.add( Config.getExe( this, EXE_METAPHLAN ) + " --version > " + infoFile );
		list.add( "echo '' >> " + infoFile );
		list.add( Config.getExe( this, EXE_METAPHLAN ) + " --help >> " + infoFile );
		list.add( "}" );
		return list;
	}
	
	private Map<String, String> getExtraParams() throws ConfigFormatException, ConfigNotFoundException {
		if( params.getMap().isEmpty() ) {
			params.readParams(Config.getString( this, METAPHLAN_PARAMS ));
			if( Config.getBoolean( this, CHECK_PARAMS ) ) {
				params.check_param_names();
				params.check_halt_params();
				check_auto_params();
			}
		}
		Map<String, String> argMap = params.getMap();
		if (argMap.containsKey( M2Params.TYPE )) {
			analysis = argMap.get( M2Params.TYPE );
		}else {
			argMap.put( M2Params.TYPE, analysis );
		}
		if (!argMap.containsKey( M2Params.INPUT_TYPE )) {
			argMap.put( M2Params.INPUT_TYPE, getInputType() );
		}
		if (!argMap.containsKey( M2Params.TMP_DIR )) {
			argMap.put( M2Params.TMP_DIR, getTempDir().getAbsolutePath() );
		}
		if (!argMap.containsKey( M2Params.BOWTIE2OUT )) {
			argMap.put( M2Params.BOWTIE2OUT, "../temp/$(basename $IN).bowtie2out.txt");
		}
		return argMap;
	}	
	
	public void check_auto_params() throws RejectedMetaphlan2Parameter {
		Map<String, String> map = params.getMap();
		if (map.containsKey( M2Params.OUT_FILE ) | map.containsKey( M2Params.OUT_FILE_LONG )) {
			throw new RejectedMetaphlan2Parameter( "Do not include [" + M2Params.OUT_FILE + "] in the parameters; the output file is set automatically by BioLockJ. Output will be in the modules output folder." );
		}
		if (map.containsKey( M2Params.NPROC )) {
			throw new RejectedMetaphlan2Parameter( M2Params.NPROC, Constants.SCRIPT_NUM_THREADS );
		}
		if (map.containsKey( M2Params.BOWTIE2DB )) {
			throw new RejectedMetaphlan2Parameter(M2Params.BOWTIE2DB, MetaPhlAn2.BOWTIE2DB);
		}
		if (map.containsKey( M2Params.MPA_PKL )) {
			throw new RejectedMetaphlan2Parameter( M2Params.MPA_PKL, MetaPhlAn2.MPA_PKL);
		}
		if (map.containsKey( M2Params.INDEX ) | map.containsKey( M2Params.INDEX_LONG )) {
			throw new RejectedMetaphlan2Parameter( M2Params.INDEX_LONG, MetaPhlAn2.INDEX);
		}
	}
	
	private String getInputType() throws ConfigNotFoundException {
		String type = SeqUtil.getSeqType();
		if( type == null | ( !type.equals( "fastq" ) && !type.equals( "fasta" ) ) )
			throw new ConfigNotFoundException( METAPHLAN_PARAMS,
				"Please specify the " + M2Params.INPUT_TYPE + " parameter to metaphlan." );
		return type;
	}
	
	@Override
	public List<String> getPreRequisiteModules() throws Exception {
		List<String> list = super.getPreRequisiteModules();
		list.add( MetaPhlAn_DB.class.getCanonicalName() );
		return list;
	}
	
	@Override
	public String getDescription() {
		return "Profile the composition of microbial communities using [MetaPhlAn](https://github.com/biobakery/MetaPhlAn).";
	}

	@Override
	public String getDetails() {
		StringBuilder sb = new StringBuilder();
		sb.append(
			"[MetaPhlAn](https://github.com/biobakery/MetaPhlAn) is one of many tools in the BioBakery project." );
		sb.append(
			"This module facilitates using MetaPhlAn2 as part of a BioLockJ pipeline." + Constants.markDownReturn );
		sb.append(
			"Arguments to be passed to MetaPhlAn can be added via the _" + METAPHLAN_PARAMS + "_ property." );
		sb.append( Constants.markDownReturn +
			"The input and output locations will be specified automatically by BioLockJ.  " );
		sb.append( "Outputs are named using the <sample id>_<analysis type>.txt ." );
		sb.append( "The MetaPhlAn2 analsyis type is " + analysis + " by default, but can be specified in _" +
			METAPHLAN_PARAMS + "_. " );
		sb.append( M2Params.NPROC + " will be set based on the _" + Constants.SCRIPT_NUM_THREADS +
			"_ property and cannot be included in the _" + METAPHLAN_PARAMS + "_ value. " );
		sb.append( Constants.markDownReturn + "If _" + CHECK_PARAMS +
			"=Y_ then BioLockJ will review the params for metaphlan to check for common problems." );
		sb.append( "BioLockJ will check that if any of these arguments are used there is a value: " +
			BioLockJUtil.getCollectionAsString( params.NAMED_ARGS ) );
		sb.append( "BioLockJ will check that if any of these arguments are used there is no following value: " +
			BioLockJUtil.getCollectionAsString( params.FLAG_ARGS ) );
		sb.append( "These properties are set using specific BioLockJ properties: " +
			BioLockJUtil.getCollectionAsString( params.AUTO_ARGS ) );
		sb.append(
			"BioLockJ will also check to ensure there are no arguments that do not match the recognized set (these are typically mistakes) and no arugments that would prevent the program from running (such as -v or -h )." );
		sb.append( "This check is done during check dependences, before the first module starts." );
		return sb.toString();
	}
	
	@Override
	public String getCitationString() {
		return "The BioLockJ module was developed by Ivory Blakley to facilitate using MetaPhlan2." +
			System.lineSeparator() + citeMetaphlan();
	}
	

}
