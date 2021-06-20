package biolockj.module.biobakery.metaphlan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import biolockj.Config;
import biolockj.Constants;
import biolockj.Log;
import biolockj.Properties;
import biolockj.api.API_Exception;
import biolockj.api.ApiModule;
import biolockj.util.BashScriptBuilder;

public class MetaPhlAn_DB extends MetaPhlAn_Tool implements ApiModule{
	
	public MetaPhlAn_DB() {
		addNewProperty( EXE_METAPHLAN, Properties.EXE_PATH, "" );
		addNewProperty( EXE_BOWTIE2, Properties.EXE_PATH, "" );
		addNewProperty( EXE_BOWTIE2_BUILD, Properties.EXE_PATH, "" );
		addNewProperty( BOWTIE2DB, Properties.FILE_PATH, BOWTIE2DB_DESC );
		addNewProperty( INDEX, Properties.STRING_TYPE, INDEX_DESC );
	}
	
	@Override
	public void checkDependencies() throws Exception {
		super.checkDependencies();
		isValidProp(EXE_METAPHLAN);
		isValidProp( BOWTIE2DB );
		if( Config.getString( this, BOWTIE2DB ) == null ) Log.warn( this.getClass(),
			"Setting the database folder explicitly is recommended. Found [" + BOWTIE2DB + "= ]." );
		isValidProp( INDEX );
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
			case BOWTIE2DB:
				Config.getExistingDir( this, BOWTIE2DB );
				isValid = true;
				break;
			case INDEX:
				Config.requireString( this, INDEX );
				isValid = true;
				break;
		}
		return isValid;
	}

	@Override
	public List<List<String>> buildScript( List<File> files ) throws Exception {
		String continued = BashScriptBuilder.continueLine();
		String continueing = "       ";
		List<List<String>> outer = new ArrayList<>();
		List<String> inner = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		sb.append( Config.getExe( this, EXE_METAPHLAN ) + " " + M2Params.INSTALL + continued);
		if (Config.getString( this, BOWTIE2DB ) != null) {
			sb.append( continueing + M2Params.BOWTIE2DB + " " + Config.getExistingDir( this, BOWTIE2DB ) + continued);
		}
		if (Config.getString( this, EXE_BOWTIE2 ) != null) {
			sb.append( continueing + M2Params.BOWTIE2_EXE + " " + Config.getExe( this, EXE_BOWTIE2 ) + continued);
		}
		if (Config.getString( this, EXE_BOWTIE2_BUILD ) != null) {
			sb.append( continueing + M2Params.BOWTIE2_BUILD + " " + Config.getExe( this, EXE_BOWTIE2_BUILD ) + continued);
		}
		sb.append( continueing + M2Params.INDEX_LONG + " " + Config.requireString( this, INDEX ) );
		inner.add( sb.toString() );
		outer.add( inner );
		return outer;
	}

	@Override
	public String getDescription() {
		return "Install the reference database required by [MetaPhlAn](https://github.com/biobakery/MetaPhlAn).";
	}
	
	@Override
	public String getDetails() throws API_Exception {
		return "Use this module to verify that a given index of the database is present or to download/build it if it is not.  Downloading requires internet access." +
			Constants.markDownReturn + "The command run by this module will be something like:" +
			Constants.markDownReturn +
			"`metaphlan --install --bowtie2db /path/to/metaphlan/metaphlan_databases/ --index mpa_v30_CHOCOPhlAn_201901`";
	}

	@Override
	public String getCitationString() {
		return "The BioLockJ module was developed by Ivory Blakley to facilitate using MetaPhlan2." +
			System.lineSeparator() + citeMetaphlan();
	}

}
