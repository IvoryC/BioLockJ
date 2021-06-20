package biolockj.module.biobakery.metaphlan;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import biolockj.Constants;

public class M2Params {

	// Flags and argument names for metaphlan2
	static final String INPUT_TYPE = "--input_type";
	static final String FORCE = "--force";
	static final String MPA_PKL = "--mpa_pkl";
	static final String BOWTIE2DB = "--bowtie2db";
	static final String INDEX = "-x";
	static final String INDEX_LONG = "--index";
	static final String BT2_PS = "--bt2_ps";
	static final String BOWTIE2_EXE = "--bowtie2_exe";
	static final String BOWTIE2_BUILD = "--bowtie2_build";
	static final String BOWTIE2OUT = "--bowtie2out";
	static final String NO_MAP = "--no_map";
	static final String TMP_DIR = "--tmp_dir";
	static final String TAX_LEV = "--tax_lev";
	static final String MIN_CU_LEN = "--min_cu_len";
	static final String MIN_ALIGNMENT_LEN = "--min_alignment_len";
	static final String IGNORE_VIRUSES = "--ignore_viruses";
	static final String IGNORE_EUKARYOTES = "--ignore_eukaryotes";
	static final String IGNORE_BACTERIA = "--ignore_bacteria";
	static final String IGNORE_ARCHEA = "--ignore_archaea";
	static final String STAT_Q = "--stat_q";
	static final String IGNORE_MARKERS = "--ignore_markers";
	static final String AVOID_DISQM = "--avoid_disqm";
	static final String STAT = "--stat";
	static final String TYPE = "-t";
	static final String NREADS = "--nreads";
	static final String PRES_TH = "--pres_th";
	static final String CLADE = "--clade";
	static final String MIN_AB = "--min_ab";
	static final String OUT_FILE = "-o";
	static final String OUT_FILE_LONG = "--output_file";
	static final String ID_KEY = "--sample_id_key";
	static final String ID = "--sample_id";
	static final String SAM = "-s";
	static final String SAM_LONG = "--samout";
	static final String BIOM = "--biom";
	static final String BIOM_LONG = "--biom_output_file";
	static final String MDELIM = "--mdelim";
	static final String MDELIM_LONG = "--metadata_delimiter_char";
	static final String NPROC = "--nproc";
	static final String INSTALL = "--install";
	static final String READ_MIN_LEN = "--read_min_len";
	static final String VERSION_ARG = "-v";
	static final String VERSION_ARG_LONG = "--version";
	static final String HELP_ARG = "-h";
	static final String HELP_ARG_LONG = "--help";

	public final List<String> NAMED_ARGS = Arrays.asList( INPUT_TYPE, MPA_PKL, BOWTIE2DB, INDEX, BT2_PS, TMP_DIR,
		BOWTIE2_EXE, BOWTIE2_BUILD, BOWTIE2OUT, TAX_LEV, STAT_Q, IGNORE_MARKERS, TYPE, NREADS, PRES_TH, OUT_FILE,
		ID_KEY, ID, SAM, SAM_LONG, BIOM, BIOM_LONG, MDELIM, MDELIM_LONG, NPROC, READ_MIN_LEN, MIN_CU_LEN,
		MIN_ALIGNMENT_LEN, STAT, CLADE, MIN_AB );

	public final List<String> FLAG_ARGS = Arrays.asList( FORCE, NO_MAP, IGNORE_VIRUSES, IGNORE_EUKARYOTES, IGNORE_BACTERIA,
		IGNORE_ARCHEA, AVOID_DISQM, INSTALL, VERSION_ARG, VERSION_ARG_LONG, HELP_ARG, HELP_ARG_LONG );
	
	public final List<String> HALT_ARGS = Arrays.asList( INSTALL, VERSION_ARG, VERSION_ARG_LONG, HELP_ARG, HELP_ARG_LONG );
		
	public final List<String> AUTO_ARGS = Arrays.asList( OUT_FILE, NPROC, BOWTIE2DB, MPA_PKL );
	
	Map<String, String> map = new HashMap<>();
	
	public Map<String, String> getMap(){
		return map;
	}
	
	public M2Params() {
	}
	
	public void readParams( String extras ) {
		if( extras != null ) {
			String name = null;
			StringTokenizer toks = new StringTokenizer( extras, " " );
			while( toks.hasMoreElements() ) {
				String arg = toks.nextToken();
				if( arg.startsWith( "-" ) ) {
					name = arg;
					map.put( name, null );
				} else {
					map.put( name, arg );
					name = null;
				}
			}
		}
	}

	public void check_param_names() throws UnrecognizedMetaphlan2Parameter {
		for (String name : map.keySet()) {
			if ( Arrays.asList( NAMED_ARGS ).contains( name ) && map.get(name) == null ) {
				throw new UnrecognizedMetaphlan2Parameter("The parameter [" + name + "] to metaphlan2 takes a value.");
			}
			if ( FLAG_ARGS.contains( name ) && map.get(name) != null ) {
				throw new UnrecognizedMetaphlan2Parameter("The parameter [" + name 
					+ "] to metaphlan2 does not take a value take a value; found value [" + map.get( name ) + "].");
			}
			if ( !NAMED_ARGS.contains( name ) && !FLAG_ARGS.contains( name ) ) {
				throw new UnrecognizedMetaphlan2Parameter("The parameter [" + name 
					+ "] is not a known parameter for metaphlan2.");
			}
		}
	}
	
	public void check_halt_params() throws RejectedMetaphlan2Parameter {
		for (String name : HALT_ARGS ) {
			if (map.containsKey( name )) {
				throw new RejectedMetaphlan2Parameter("The parameter [" + name + "] will prevent metaphlan2 from running.");
			}
		}
	}
	
}