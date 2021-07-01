package biolockj.module.biobakery.metaphlan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import biolockj.Config;
import biolockj.Properties;
import biolockj.api.ApiModule;
import biolockj.module.BioModule;
import biolockj.util.BashScriptBuilder;

public class Merge_MetaPhlAn_Tables extends MetaPhlAn_Tool implements ApiModule {
	
	static final String EXE_MERGE = "exe.merge_metaphlan_tables.py";
	
	private static final String FUNCTION_NAME = "doMerge";
	
	public Merge_MetaPhlAn_Tables() {
		addNewProperty( EXE_MERGE, Properties.EXE_PATH, "" );
	}
	
	@Override
	public List<List<String>> buildScript( List<File> files ) throws Exception {
		List<List<String>> outer = new ArrayList<>();
		List<String> inner = new ArrayList<>();
		inner.add( FUNCTION_NAME );
		outer.add( inner );
		return outer;
	}
	
	private File outputFile() throws Exception {
		return new File(getOutputDir(), "merged_metaphlan_profile.txt");
	}
	
	@Override
	public List<String> getWorkerScriptFunctions() throws Exception {
		List<String> list = super.getWorkerScriptFunctions();
		String continued = BashScriptBuilder.continueLine();
		String continueing = "      ";
		list.add( "function " + FUNCTION_NAME + "() {");
		list.add( Config.getExe( this, EXE_MERGE ) + " " + continued );
		for (File input : getInputFiles() ) {
			list.add( continueing + input.getAbsolutePath() + " " + continued );
		}
		list.add( continueing + "> " + outputFile().getAbsolutePath() );
		list.add( "}" );
		list.add( "" );
		return list;
	}
	
	@Override
	public boolean isValidInputModule( BioModule module ) {
		return module instanceof MetaPhlAn2;
	}
	
	@Override
	public String getDescription() {
		return "Run the merge_metaphlan_tables.py utility from [MetaPhlAn](https://github.com/biobakery/MetaPhlAn).";
	}

	@Override
	public String getCitationString() {
		return "The BioLockJ module was developed by Ivory Blakley to facilitate using MetaPhlan2." +
		System.lineSeparator() + citeMetaphlan();
	}


}
