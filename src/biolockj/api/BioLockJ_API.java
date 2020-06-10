package biolockj.api;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import biolockj.Config;
import biolockj.Properties;
import biolockj.exception.BioLockJException;
import biolockj.exception.ConfigException;
import biolockj.module.BioModule;
import biolockj.util.BioLockJUtil;
import biolockj.util.ModuleUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;

public class BioLockJ_API {

	// bash script
	private static final String BASH_ENTRY = "biolockj-api";
	
	// query 
	private static final String LIST_MODULES = "listModules";
	private static final String LIST_API_MODULES = "listApiModules";
	private static final String LIST_PROPS = "listProps";
	private static final String LIST_ALL_PROPS = "listAllProps";
	private static final String VALIDATE_PROP = "isValidProp";
	private static final String PROP_TYPE = "propType";
	private static final String PROP_VALUE = "propValue";
	private static final String PROP_DESC = "describeProp";
	private static final String PROP_INFO = "propInfo";
	private static final String MODULE_INFO = "moduleInfo";
	private static final String HELP = "help";

	//options
	private static final String EXT_MODS_ARG = "external-modules";
	private static final String MODULE_ARG = "module";
	private static final String PROP_ARG = "property";
	private static final String VALUE_ARG = "value";
	private static final String CONFIG_ARG = "config";
	private static final String DEBUG_ARG = "verbose";
	
	private static boolean verbose = false;

	private BioLockJ_API() {}

	public static void main( String[] args ) throws Exception {
		try {
			HashMap<String, String> options = new HashMap<>();
			String query = null;
			for (String arg : args) {
				int sep = arg.indexOf( "=" );
				if (sep == -1) {
					if (query == null) query = arg;
					else throw new API_Exception("All options after the query must be named." + System.lineSeparator() + "Cannot process [ " + arg + " ].");
				}else {
					options.put( arg.substring( 0, sep ).trim(), arg.substring( sep + 1 ).trim() );
				}
			}
			if ( query == null ) throw new API_Exception("Cannot determine query.");
			if ( options.get(EXT_MODS_ARG) != null) System.err.print("The [" + EXT_MODS_ARG + "] option may not have been handled correctly.");
			if ( options.get(DEBUG_ARG) != null ) setVerbose( true );
			
			String reply = "";
			switch( query ) {
				case LIST_MODULES:
					System.out.println("READY TO LIST MODULES!"); //TODO: remove this
					unsupportedOption(query, options, new String[0]);
					reply = listToString( listModules() );
					break;
				case LIST_API_MODULES:
					unsupportedOption(query, options, new String[0]);
					reply = listToString( listApiModules( ) );
					break;
				case LIST_PROPS:
					unsupportedOption(query, options, new String[] {MODULE_ARG} );
					if ( options.get( MODULE_ARG ) == null ) {
						reply = listToString( listProps( ) );
					}else {
						reply = listToString( listModuleProps( options.get( MODULE_ARG ) ) );
					}
					break;
				case LIST_ALL_PROPS:
					unsupportedOption(query, options, new String[0]);
					Set<String> allProps = new HashSet<>();
					allProps.addAll(listProps( ));
					for (String mod : listApiModules()) {
						ApiModule tmp = (ApiModule) ModuleUtil.createModuleInstance( mod );
						allProps.addAll( tmp.listProps() );
					}
					ArrayList<String> allPropsOrderd = new ArrayList<>(allProps);
					Collections.sort(allPropsOrderd);
					reply = listToString( allPropsOrderd );
					break;
				case VALIDATE_PROP:
					unsupportedOption(query, options, new String[] {MODULE_ARG, PROP_ARG, VALUE_ARG} );
					requiredOption(query, options, new String[] {PROP_ARG, VALUE_ARG} );
					List<String> modules = options.get( MODULE_ARG ) == null ? new ArrayList<>() : Arrays.asList( options.get( MODULE_ARG ).split( "," ) );
					Boolean isGood = isValidProp(options.get(PROP_ARG), options.get(VALUE_ARG), modules );
					if (isGood == null) reply = "null";
					else reply = isGood.toString();
					break;
				case PROP_TYPE:
					unsupportedOption(query, options, new String[] {MODULE_ARG, PROP_ARG} );
					requiredOption(query, options, new String[] {PROP_ARG} );
					if ( options.get( MODULE_ARG ) == null ) {
						reply = Properties.getPropertyType( options.get( PROP_ARG ) );
					}else {
						reply = propTypeModule( options.get( PROP_ARG ), options.get( MODULE_ARG ) );
					}
					break;
				case PROP_DESC:
					unsupportedOption(query, options, new String[] {MODULE_ARG, PROP_ARG} );
					requiredOption(query, options, new String[] {PROP_ARG} );
					if ( options.get( MODULE_ARG ) == null ) {
						reply = Properties.getDescription( options.get( PROP_ARG ) );
					}else {
						reply = propDescModule( options.get( PROP_ARG ), options.get( MODULE_ARG ) );
					}
					break;
				case PROP_VALUE:
					unsupportedOption(query, options, new String[] {PROP_ARG, CONFIG_ARG, MODULE_ARG} );
					requiredOption(query, options, new String[] {PROP_ARG} );
					BioModule mod = null;
					if ( options.get( MODULE_ARG ) != null ) {
						mod = ModuleUtil.createModuleInstance( options.get( MODULE_ARG ) );
					}
					reply = propValue( options.get( PROP_ARG ), options.get( CONFIG_ARG ), mod);
					break;
				case PROP_INFO:
					unsupportedOption(query, options, new String[0]);
					reply = propInfo().toString(2);
					break;
				case MODULE_INFO:
					unsupportedOption(query, options, new String[0]);
					reply = moduleInfo().toString(2);
					break;
				case HELP:
					reply = getHelp();
					break;
				default:
					throw new API_Exception( "\"" + query + "\" is not a recognized query term." );
			}
			System.out.println( reply );
		} catch( API_Exception | BioLockJException ex ) {
			System.err.println( ex.getMessage() );
			System.err.println( "See help menu using: " + BASH_ENTRY + " " + HELP);
		} catch( Exception e ) {
			System.err.println( "An unexpected error occurred; your query could not be processed." );
			throw e;
		}
	}
	
	/**
	 * If the user passed in options that this querry does not support, throw an informative error.
	 * @param query
	 * @param options
	 * @param supportedOpts
	 * @throws API_Exception
	 */
	private static void unsupportedOption(String query, HashMap<String, String> options, String[] supportedOpts) throws API_Exception{
		List<String> supported = new ArrayList<>();
		supported.addAll( Arrays.asList( supportedOpts ) );
		if ( !supported.contains( DEBUG_ARG ) ) supported.add( DEBUG_ARG );
		for (String opt : options.keySet()) {
			if ( !supported.contains( opt ) && options.get( opt ) != null) {
				throw new API_Exception("The query [" + query + "] does not support the [--" + opt + "] option."
					+ System.lineSeparator() + "Found [ " + opt + " = " + options.get( opt ) + " ].");
			}
		}
	}
	
	/**
	 * If the user failed to pass in the options that this query requires, throw an informative error.
	 * @param query
	 * @param options
	 * @param supportedOpts
	 * @throws API_Exception
	 */
	private static void requiredOption(String query, HashMap<String, String> options, String[] requiredOpts) throws API_Exception{
		List<String> reqrd = new ArrayList<>();
		reqrd.addAll( Arrays.asList( requiredOpts ) );
		for (String opt : reqrd) {
			if ( options.get( opt ) == null) {
				throw new API_Exception("The query [" + query + "] requires the [--" + opt + "] option.");
			}
		}
	}
	
	public static void setVerbose(boolean writeLots) {
		verbose = writeLots;
	}
	
	private static String listToString( Iterable<? extends Object> elements ) {
		ArrayList<String> output = new ArrayList<>();
		for (Object elem : elements ) {
			if (elem instanceof Class) {
				output.add( ( (Class<?>) elem).getName() );
			}else if (elem instanceof CharSequence) {
				output.add( ( (CharSequence) elem).toString() );
			}else {
				output.add( elem.toString() );
			}
		}
		return( String.join( System.lineSeparator(), output ) );
	}
	
	
	
	
	
	
	
	/////////
	
	
	
	/**
	 * Return a list of all the modules on the class path.
	 * 
	 * @param prefix - Single argument passed to the constructor of the Reflections class.
	 * @return
	 * @throws Exception
	 */
	public static List<String> listModules( String prefix ) throws Exception {
		List<String> allBioModules = new ArrayList<>();

		// Reflections reflections = supressStdOut_createReflections(prefix);
		//
		// Set<Class<? extends BioModule>> subTypes = reflections.getSubTypesOf( BioModule.class );
		// for (Class<? extends BioModule> st : subTypes ) {
		// try {
		// BioModule tmp = (BioModule) Class.forName( st.getName() ).newInstance();
		// allBioModules.add( tmp.getClass().getName() );
		// }catch(InstantiationException | ExceptionInInitializerError ex) {
		// //System.err.println("The class [" + st.getName() + "] is in scope, but cannot be instantiated.");
		// }
		// }

		
//		getClasses("biolockj.module.diy");			
//		System.out.println("That was my best-chance test.");
		
		for( Package p: Package.getPackages() ) {
			System.out.println("Consider package: " + p.getName());
			if( p.getName().contains( prefix ) ) {
				System.out.println("This packges has the prefix...");
				//Class[] classes = getClasses( p.getName() );
				Class[] classes = getClasses( p.getName() );
				for( Class c: classes ) {
					System.out.println("Consider class: " + c.getName());
					System.out.println( c.getName() );
					if( BioModule.class.isAssignableFrom( c ) ) {
						System.out.println("This class implements BioModule");
						try {
							BioModule tmp = (BioModule) Class.forName( c.getName() ).newInstance();
							allBioModules.add( tmp.getClass().getName() );
							System.out.println("We created an instace without errors, so we added that biomodule to the list.");
						} catch( InstantiationException | ExceptionInInitializerError ex ) {
							// System.err.println("The class [" + c.getName() + "] is in scope and implements BioModule,
							// but cannot be instantiated.");
							System.out.println("We can't make an instance of that class.");
						}
					}
				}
			}
		}

		////
		Collections.sort( allBioModules );
		System.out.println("We found " + allBioModules.size() + " BioModules in scope.");
		return allBioModules;
	}

	public static List<String> listModules() throws Exception {
		System.out.println("Defaulting to prefix='' ");
		return listModules( "" );
	}

	/* 
	 * This method was taken originally from http://snippets.dzone.com/posts/show/4831, which was archived by the
	 * Internet Archive, as linked to now. The snippet is also available at
	 * https://dzone.com/articles/get-all-classes-within-package. 
	 * Found by way of 'Ahmed Ashour's post stack overflow topic: https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
	 */
	private static Class[] getClasses( String packageName ) throws ClassNotFoundException, IOException, URISyntaxException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace( '.', '/' );
		Enumeration<URL> resources = classLoader.getResources( path );
		
		List<File> dirs = new ArrayList<File>();

		while( resources.hasMoreElements() ) {
			URL resource = resources.nextElement();
			System.out.println("Found resource: " + resource );
			File file = new File( resource.getFile() );
			// File file = Paths.get(resource.toURI()).toFile();
			// File file = Paths.get(classLoader.getResource( resource.getFile() ).toURI()).toFile() ;
			// FileSystem fileSystem = FileSystems.newFileSystem(resource.toURI(), Collections.<String, Object>emptyMap());//resource, Collections.<String, Object>emptyMap());
			// File file = new File( resource.getFile().replaceFirst( ".*!/", "" ) );
			
			System.out.println("Pass that as file: " + file );
			
			dirs.add( file );
			System.out.println("file exists: " + (file.exists() ? "yes" : "no") );
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for( File directory: dirs ) {
			System.out.println("Package " + packageName + " has resource: " + directory);
			classes.addAll( findClasses( directory, packageName ) );
		}
		return classes.toArray( new Class[ classes.size() ] );
	}

	/* 
	 * This method was taken originally from http://snippets.dzone.com/posts/show/4831, which was archived by the
	 * Internet Archive, as linked to now. The snippet is also available at
	 * https://dzone.com/articles/get-all-classes-within-package. 
	 * Found by way of 'Ahmed Ashour's post stack overflow topic: https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
	 */
	private static List<Class> findClasses( File directory, String packageName ) throws ClassNotFoundException {
		System.out.println("Finding classes in dir: " + directory.getPath() + "; package: " + packageName);
		List<Class> classes = new ArrayList<Class>();
		if( !directory.exists() ) { System.out.println("directory does not exist."); return classes; }
		File[] files = directory.listFiles();
		System.out.println("There are " + files.length + " files in this package.");
		for( File file: files ) {
			if( file.isDirectory() ) {
				System.out.println("The file [" + file.getName() + "] is a directory.........look for classes...");
				assert !file.getName().contains( "." );
				classes.addAll( findClasses( file, packageName + "." + file.getName() ) );
			} else if( file.getName().endsWith( ".class" ) ) {
				classes.add(
					Class.forName( packageName + '.' + file.getName().substring( 0, file.getName().length() - 6 ) ) );
			}
		}
		return classes;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
//	
//	/**
//	 * Return a list of all the modules on the class path.
//	 * @param prefix - Single argument passed to the constructor of the Reflections class.
//	 * @return
//	 * @throws Exception 
//	 */
//	public static List<String> listModules(String prefix) throws Exception {
//		List<String> allBioModules = new ArrayList<>();
//
//		Reflections reflections = supressStdOut_createReflections(prefix);
//		
//		Set<Class<? extends BioModule>> subTypes = reflections.getSubTypesOf( BioModule.class );
//		for (Class<? extends BioModule> st : subTypes ) {
//			try {
//				BioModule tmp = (BioModule) Class.forName( st.getName() ).newInstance();
//				allBioModules.add( tmp.getClass().getName() );
//			}catch(InstantiationException | ExceptionInInitializerError ex) {
//				//System.err.println("The class [" + st.getName() + "] is in scope, but cannot be instantiated.");
//			}	
//		}
//		
//		Collections.sort(allBioModules);
//		return allBioModules;
//	}
//	public static List<String> listModules() throws Exception {
//		return listModules("");
//	}
//	
//	private static Reflections supressStdOut_createReflections(String prefix) {
//		PrintStream classicOut = System.out;
//		PrintStream classicErr = System.err;
//		
//		if (verbose) {
//			System.setOut( classicErr );
//		}else {
//			System.setOut(new PrintStream(new OutputStream() {
//				  public void write(int b) {}
//				}));
//			System.setErr( new PrintStream(new OutputStream() {
//				  public void write(int b) {}
//				}));
//		}
//		
//		Reflections reflections = new Reflections(prefix);
//
//		System.setOut(classicOut);
//		System.setErr(classicErr);
//		
//		return reflections;
//	}
	
	/**
	 * Like listModules, but only include modules that implement the ApiModule interface.
	 * @param args
	 * @return
	 * @throws Exception 
	 */
	public static List<String> listApiModules() throws Exception {
		List<String> allBioModules = listModules();
		List<String> apiModules = new ArrayList<>();
		for (String mod : allBioModules ) {
			BioModule tmp = ModuleUtil.createModuleInstance( mod );
			if (tmp instanceof ApiModule) {
				apiModules.add( tmp.getClass().getName() );
			}
		}
		return apiModules;
	}
	
	
	public static List<String> listProps() throws API_Exception{
		List<String> allProps = new ArrayList<>();
		allProps.addAll( Properties.getPropTypeMap().keySet() );
		Collections.sort(allProps);
		return allProps;
	}
	public static List<String> listModuleProps(String mod) throws Exception {
		List<String> allProps = new ArrayList<>();
		BioModule tmp = ModuleUtil.createModuleInstance( mod );
		if (tmp instanceof ApiModule) {
			allProps.addAll( ( (ApiModule) tmp ).listProps() );
		}
		return allProps;
	}
	
	/**
	 * Returns true if the 
	 * @param prop - a config property
	 * @param val - a proposed value for prop
	 * @param modules - if not empty, the property/val pair is validated by one or more modules, in addition to the biolockj backbone.
	 * @return
	 * @throws Exception 
	 */
	public static Boolean isValidProp(String prop, String val, List<String> modules) throws Exception {
		Config.initBlankProps();
		Config.setConfigProperty( prop, val );
		
		Boolean isValid = Properties.isValidProp(prop);
		
		if (modules.size() > 0) {
			for (String mod : modules) {
				BioModule tmp = ModuleUtil.createModuleInstance( mod );
				if (tmp instanceof ApiModule) {
					Boolean modVote;
					try {
						modVote = ( (ApiModule) tmp).isValidProp( prop );
					}catch(ConfigException e) {
						modVote = false;
					}
					if (isValid == null) isValid = modVote;
					else if (modVote != null) isValid = modVote && isValid;
				}
			}
		}
		return isValid;
	}
	
	/**
	 * Returns a string describing the format type that required for the value of the property given.
	 * The first String in args is taken as the property of interest.
	 * The second string is a module 
	 * the property type is queried for that module rather than the BioLockJ backbone.
	 * @param args - 
	 * @return
	 * @throws API_Exception 
	 */
	public static String propTypeModule(String prop, String module) throws API_Exception {
		String type = "";
		try {
			BioModule tmp = ModuleUtil.createModuleInstance( module );
			if (tmp instanceof ApiModule) {
				type = ( (ApiModule) tmp ).getPropType( prop );
			}
		}catch(ClassNotFoundException ex ) {
			throw new API_Exception("Module class [" + module + "] could not be found.");
		} catch( Exception e ) {
			throw new API_Exception("There was ap problem getting the type for ["+ prop + "] from class [" + module + "].");
		}
		return type; 
	}
	
	public static String propDescModule(String prop, String module) throws API_Exception {
		String desc = "";
		try {
			BioModule tmp = ModuleUtil.createModuleInstance( module );
			if (tmp instanceof ApiModule) {
				desc = ( (ApiModule) tmp ).getDescription( prop );
			}
		}catch(ClassNotFoundException ex ) {
			throw new API_Exception("Module class [" + module + "] could not be found.");
		} catch( Exception e ) {
			throw new API_Exception("There was ap problem getting the description for ["+ prop + "] from class [" + module + "].");
		}
		return desc; 
	}
	
	/**
	 * Returns the value for that property (first String)
	 * If a second argument is given, it is assumed to be the primary config file.
	 * @param property - the property to get the value of
	 * @param config - (can be null) the config file to read in to establish property values
	 * @param module - (can be null) a module to pass in a context when getting the value
	 * @return
	 * @throws Exception 
	 */
	public static String propValue(String property, String config, BioModule module) throws Exception {
		if (config != null) initConfig(config);
		else initConfig();
		String value = Config.getString( null, property );
		if ( value == null && module != null && module instanceof ApiModule) {
			value = ((ApiModule) module).getPropDefault( property ); //may still be null
		}
		return value;
	}
	public static String propValue(String property, BioModule module) throws Exception {
		return propValue(property, null, module);
	}
	public static String propValue(String property) throws Exception {
		return propValue(property, null, null);
	}
	
	private static void initConfig(String path) throws Exception {
		File config = new File(path);
		if (!config.exists()) throw new API_Exception( "Cannot find configuration file: " + path );
		Config.partiallyInitialize(config);
	}
	private static void initConfig() throws Exception {
		File tempConfig = File.createTempFile( "tempconfig", "properties" );
		Config.partiallyInitialize(tempConfig);
		tempConfig.delete();
	}
	
	/**
	 * For each of the properties registered to the Properties class, 
	 * report the property name, type, description and default value.
	 * @return
	 * @throws Exception
	 */
	public static JSONArray propInfo() throws Exception {
		HashMap<String, String> propDescMap = Properties.getPropDescMap();
		List<String> props = new ArrayList<>(propDescMap.keySet());
		Collections.sort(props);
		initConfig();
		JSONArray jarray = new JSONArray();
		for (String prop : props) {
			HashMap<String, String> propAtts = new HashMap<>();
			propAtts.put("property", prop);
			propAtts.put("type", Properties.getPropertyType( prop ));
			propAtts.put("description", Properties.getDescription( prop ));
			propAtts.put("default", propValue(prop));
			JSONObject jobj = new JSONObject( propAtts );
			jarray.put( jobj );
		}
		return jarray;
	}
	
	/**
	 * Returns a json formatted list of all modules and for each module that 
	 * implements the gui interface, it lists the props used by the module,
	 * and for each prop the info.
	 * @param args
	 * @return
	 * @throws Exception 
	 */
	public static JSONArray moduleInfo() throws Exception {
		initConfig();
		List<String> modules = listApiModules();
		JSONArray modsArray = new JSONArray();
		for (String mod : modules) {
			BioModule tmp = biolockj.util.ModuleUtil.createModuleInstance( mod );
			HashMap<String, Object> modAtts = new HashMap<>();
			
			modAtts.put("usage", "#BioModule " + mod );		
			try {
				modAtts.put("pre-req modules", tmp.getPreRequisiteModules() );
				modAtts.put("post-req modules", tmp.getPostRequisiteModules() );
			}catch(Exception e) {}
			
			if (tmp instanceof ApiModule) {
				modAtts.put("description", ((ApiModule) tmp).getDescription() );
				modAtts.put("details", ((ApiModule) tmp).getDetails() );
				modAtts.put("citation", ((ApiModule) tmp).getCitationString() );
				modAtts.put("title", ((ApiModule) tmp).getTitle() );
				modAtts.put("menuPlacement", ((ApiModule) tmp).getMenuPlacement() );
				
				List<String> props = listModuleProps( mod );
				JSONArray modProps = new JSONArray();
				for (String prop : props) {
					HashMap<String, String> propAtts = new HashMap<>();
					propAtts.put("property", prop);
					propAtts.put("type", Properties.getPropertyType( prop ));
					propAtts.put("description", Properties.getDescription( prop ));
					propAtts.put("default", propValue(prop));
					modProps.put( propAtts );
				}
				modAtts.put( "properties", modProps );
			}

			JSONObject jModAtts = new JSONObject( modAtts );
			modsArray.put( jModAtts );
		}
		return modsArray; //return json; convert to string in switch case.
	}
	
	/**
	 * Print the help menu explaining how to use this tool.
	 * @return
	 */
	private static String getHelp() {
		String EXT_MODS_OPTION = "--" + EXT_MODS_ARG + " <dir>";
		String MODULE_OPTION = "--" + MODULE_ARG + " <module_path>";
		String PROP_OPTION = "--" + PROP_ARG + " <property>";
		String VALUE_OPTION = "--" + VALUE_ARG + " <value>";
		String CONFIG_OPTION = "--" + CONFIG_ARG + " <file>";
		String DEBUG_OPTION = "--" + DEBUG_ARG;
		
		StringBuffer sb = new StringBuffer();
		sb.append( "BioLockJ API " + BioLockJUtil.getVersion( ) + " - UNCC Fodor Lab" +System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "Usage:" +System.lineSeparator() );
		sb.append( BASH_ENTRY + " <query> [options...]" +System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "For some uses, redirecting stderr is recommended:" +System.lineSeparator() );
		sb.append( BASH_ENTRY + " <query> [options...]  2> /dev/null" +System.lineSeparator() );
		sb.append( "Options shown in [ ] are optional for a given query." +System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "Use " + BASH_ENTRY + " without args to get help menu." +System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "Options:" + System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "  " + EXT_MODS_OPTION +System.lineSeparator() );
		sb.append( "        path to a directory containing additional modules" +System.lineSeparator() );
		sb.append( "  " + MODULE_OPTION +System.lineSeparator() );
		sb.append( "        class path for a specific module" +System.lineSeparator() );
		sb.append( "  " + PROP_OPTION +System.lineSeparator() );
		sb.append( "        a specific property" +System.lineSeparator() );
		sb.append( "  " + VALUE_OPTION +System.lineSeparator() );
		sb.append( "        a vlue to use for a specific property" +System.lineSeparator() );
		sb.append( "  " + CONFIG_OPTION +System.lineSeparator() );
		sb.append( "        file path for a configuration file giving one or more property values" +System.lineSeparator() );
		sb.append( "  " + DEBUG_OPTION +System.lineSeparator() );
		sb.append( "        flag indicating that all messages should go to standard err, including some that are typically disabled." +System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "query:" + System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "  " + LIST_MODULES + " [ " + EXT_MODS_OPTION + " ]" +System.lineSeparator() );
		sb.append( "        Returns a list of classpaths to the classes that extend BioModule." +System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "  " + LIST_API_MODULES + " [" + EXT_MODS_OPTION + " ]" +System.lineSeparator() );
		sb.append( "        Like listModules but limit list to modules that implement the ApiModule interface." +System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "  " + LIST_PROPS + " [ " + MODULE_OPTION + " ]" +System.lineSeparator() );
		sb.append( "        Returns a list of properties." +System.lineSeparator() );
		sb.append( "        If no args, it returns the list of properties used by the BioLockJ backbone." +System.lineSeparator() );
		sb.append( "        If a modules is given, then it returns a list of all properties used by" +System.lineSeparator() );
		sb.append( "        that module." +System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "  " + LIST_ALL_PROPS + " [ " + EXT_MODS_OPTION + " ]" +System.lineSeparator() );
		sb.append( "        Returns a list of all properties, include all backbone properties and all module properties." +System.lineSeparator());
		sb.append( "        Optionally supply the path to a directory containing additional modules to include their properties." +System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "  " + PROP_TYPE + " " + PROP_OPTION + " [ " + MODULE_OPTION + " [ " + EXT_MODS_OPTION + " ] ]" +System.lineSeparator() );
		sb.append( "        Returns the type expected for the property: String, list, integer, positive number, etc." +System.lineSeparator() );
		sb.append( "        If a module is supplied, then the modules propType method is used." +System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "  " + PROP_DESC + " " + PROP_OPTION + " [ " + MODULE_OPTION + " [ " + EXT_MODS_OPTION + " ] ]" +System.lineSeparator() );
		sb.append( "        Returns a description of the property." +System.lineSeparator() );
		sb.append( "        If a module is supplied, then the modules getDescription method is used." +System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "  " + PROP_VALUE + " " + PROP_OPTION + " [ " + CONFIG_OPTION + " ] [ "+ MODULE_OPTION + " ]" +System.lineSeparator() );
		sb.append( "        Returns the value for that property given that config file (optional) or " +System.lineSeparator() );
		sb.append( "        no config file (ie the default value)" +System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "  " + VALIDATE_PROP + " " + PROP_OPTION + " " + VALUE_OPTION + " [ " + MODULE_OPTION + "  [" + EXT_MODS_OPTION + "] ]" +System.lineSeparator() );
		sb.append( "        T/F/NA. Returns true if the value (val) for the property (prop) is valid;" +System.lineSeparator() );
		sb.append( "        false if prop is a property but val is not a valid value," +System.lineSeparator() );
		sb.append( "        and NA if prop is not a recognized property." +System.lineSeparator() );
		sb.append( "        IF a module is supplied, then additionally call the validateProp(key, value)" +System.lineSeparator() );
		sb.append( "        for that module, or for EACH module if a comma-separated list is given." +System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "  " + PROP_INFO + System.lineSeparator() );
		sb.append( "        Returns a json formatted list of the general properties (listProps)" +System.lineSeparator() );
		sb.append( "        with the type, descrption and default for each property" +System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "  " + MODULE_INFO + " [" + EXT_MODS_OPTION + "]" +System.lineSeparator() );
		sb.append( "        Returns a json formatted list of all modules and for each module that " +System.lineSeparator() );
		sb.append( "        implements the ApiModule interface, it lists the props used by the module," +System.lineSeparator() );
		sb.append( "        and for each prop the type, descrption and default." +System.lineSeparator() );
		sb.append( System.lineSeparator() );
		sb.append( "  " + HELP + "  (or no args)" +System.lineSeparator() );
		sb.append( "        Print help menu." +System.lineSeparator() );
		sb.append( System.lineSeparator() );

		return sb.toString(); 
	}

}
