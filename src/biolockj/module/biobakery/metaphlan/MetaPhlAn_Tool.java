package biolockj.module.biobakery.metaphlan;

import java.util.Arrays;
import java.util.List;
import biolockj.Config;
import biolockj.Properties;
import biolockj.exception.ConfigConflictException;
import biolockj.module.BioModule;
import biolockj.module.ScriptModuleImpl;
import biolockj.util.BashScriptBuilder;
import biolockj.util.BioLockJUtil;
import biolockj.util.DockerUtil;
import biolockj.util.ModuleUtil;

/**
 * This class holds the common properties shared by multiple modules that use metaphlan.
 * @author Ivory Blakley
 *
 */
public abstract class MetaPhlAn_Tool extends ScriptModuleImpl {
	
	static final String EXE_METAPHLAN = "exe.metaphlan";
	
	static final String EXE_BOWTIE2 = "exe.bowtie2";
	
	static final String EXE_BOWTIE2_BUILD = "exe.bowtie2build";
	
	/**
	 * {@value CHECK_PARAMS_DESC}: {@value CHECK_PARAMS}
	 */
	static final String CHECK_PARAMS = "metaphlan.checkParams";
	static final String CHECK_PARAMS_DESC = "Should BioLockJ check the user-provided parameters to metaphlan.";
	
	/**
	 * {@value METAPHLAN_PARAMS_DESC}: {@value METAPHLAN_PARAMS}
	 */
	static final String METAPHLAN_PARAMS = "metaphlan.params";
	static final String METAPHLAN_PARAMS_DESC = "Additional parameters to use with metaphlan2. Several options are handled specially. See details.";
	
	/**
	 * {@value CONSTISTENT_MODS_DESC}: {@value CONSTISTENT_MODS}
	 */
	static final String CONSTISTENT_MODS = "metaphlan.consistentModules";
	static final String CONSTISTENT_MODS_DESC = "Ensure same core settings for modules in the in the metaphlan2 family in the same pipeline.";
	
	/**
	 * {@value BOWTIE2DB_DESC}: {@value BOWTIE2DB}
	 */
	static final String BOWTIE2DB = "metaphlan.bowtie2db";
	static final String BOWTIE2DB_DESC =
		"Path to the directory containing the bowtie2 reference database, passed to metaphlan via the " +
			M2Params.BOWTIE2DB + " argument.";
	
	/**
	 * {@value MPA_PKL_DESC}: {@value MPA_PKL}
	 */
	static final String MPA_PKL = "metaphlan.mpa_pkl";
	static final String MPA_PKL_DESC = "This file path is passed to metaphlan via the " + M2Params.MPA_PKL + " parameter.";
	
	/**
	 * {@value MPA_DIR_DESC}: {@value MPA_DIR}
	 */
	static final String MPA_DIR = "metaphlan.mpa_dir";
	static final String MPA_DIR_DESC = "The path to the metaphlan directory. To use the mpa_dir environment variable, use `${mpa_dir}`.";
	
	/**
	 * {@value INDEX_DESC}: {@value INDEX}
	 */
	static final String INDEX = "metaphlan.dbIndex";
	static final String INDEX_DESC = "The version of the database to use, passed to metaphlan via the " + M2Params.INDEX_LONG + " parameter. Specifying this value is recommended. Example: mpa_v30_CHOCOPhlAn_201901";

	static final String[] CONSISTENT_PROPS = {DockerUtil.DOCKER_HUB_USER, DockerUtil.DOCKER_IMG, DockerUtil.DOCKER_IMG_VERSION,
		MPA_DIR, BOWTIE2DB, INDEX, EXE_METAPHLAN, EXE_BOWTIE2, EXE_BOWTIE2_BUILD, MPA_PKL, BashScriptBuilder.CLUSTER_MODULES };

	public MetaPhlAn_Tool() {
		super();
		addNewProperty( MPA_DIR, Properties.FILE_PATH, MPA_DIR_DESC );
		addNewProperty( CONSTISTENT_MODS, Properties.BOOLEAN_TYPE, CONSTISTENT_MODS_DESC, "Y" );
	}
	
	@Override
	public void checkDependencies() throws Exception {
		super.checkDependencies();
		isValidProp( MPA_DIR );
		if( Config.getBoolean( this, CONSTISTENT_MODS ) ) {
			List<BioModule> family = ModuleUtil.getModules( this, false );
			family.removeIf( mod -> !( mod instanceof MetaPhlAn_Tool ) );
			for( BioModule mod: family ) {
				for( String property: CONSISTENT_PROPS ) {
					if( !match(Config.getString( this, property ), Config.getString( mod, property )) ) {
						String msg =
							"Modules that use the same software should use the same settings for these properties: " +
								BioLockJUtil.getCollectionAsString( Arrays.asList( CONSISTENT_PROPS ) ) +
								System.lineSeparator() + "Found different values for property [" + property + "]" +
								System.lineSeparator() + ModuleUtil.displaySignature( this ) + " will use value: " +
								Config.getString( this, property ) + System.lineSeparator() +
								ModuleUtil.displaySignature( mod ) + " will use value: " +
								Config.getString( mod, property ) + System.lineSeparator() +
								"If you are SURE you want these values to be different, you can use [ " +
								CONSTISTENT_MODS + " = N ] to disable this check.";
						throw new ConfigConflictException( property, msg );
					}
				}
			}
		}
	}
	
	private boolean match(String val1, String val2) {
		boolean match;
		if (val1 == null && val2 == null ) match = true;
		else if (val1 == null && val2 != null ) match = false;
		else if (val2 == null && val1 != null ) match = false;
		else if (val1.equals( val2 )) match = true;
		else match = false;
		return match;
	}
	
	@Override
	public Boolean isValidProp( String property ) throws Exception {
		boolean isValid = super.isValidProp( property );
		switch(property) {
			case MPA_DIR:
				Config.getExistingDir( this, MPA_DIR );
				isValid = true;
				break;
			case CONSTISTENT_MODS:
				Config.requireBoolean( this, CONSTISTENT_MODS );
				isValid = true;
				break;
		}
		return isValid;
	}

	@Override
	public String getDockerImageName() {
		return "metaphlan";
	}

	@Override
	public String getDockerImageOwner() {
		return "biobakery";
	}

	@Override
	public String getDockerImageTag() {
		return "3.0.7";
	}
	
	protected static String citeMetaphlan() {
		return "To cite MetaPhlan2, please cite: " + System.lineSeparator()
		+ "Integrating taxonomic, functional, and strain-level profiling of diverse microbial communities with bioBakery 3 Francesco Beghini, Lauren J McIver, Aitor Blanco-Míguez, Leonard Dubois, Francesco Asnicar, Sagun Maharjan, Ana Mailyan, Paolo Manghi, Matthias Scholz, Andrew Maltez Thomas, Mireia Valles-Colomer, George Weingart, Yancong Zhang, Moreno Zolfo, Curtis Huttenhower, Eric A Franzosa, Nicola Segata. eLife (2021)";
	}

}
