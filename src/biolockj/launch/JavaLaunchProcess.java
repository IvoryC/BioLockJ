package biolockj.launch;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import biolockj.exception.ConfigPathException;
import biolockj.exception.DockerVolCreationException;
import biolockj.util.RuntimeParamUtil;

public class JavaLaunchProcess extends LaunchProcess {
	
	private File BLJ_JAR_FILE = null;
	static final String BioLockJ_CLASS = "biolockj.BioLockJ";

	public JavaLaunchProcess( String[] args ) throws DockerVolCreationException, EndLaunch, ConfigPathException {
		super(args);
		checkJarFile();
	}

	public static void main(String[] args) throws Exception {
		if( inTestMode() ) showArgs(JavaLaunchProcess.class.getSimpleName(), args);
		JavaLaunchProcess launcher = new JavaLaunchProcess(args);
		launcher.runCommand();
	}
	
	@Override
	String createCmd() throws EndLaunch {
		StringBuilder command = new StringBuilder();
		command.append( "java -cp " + BLJ_JAR_FILE.getAbsolutePath());
		if (getFlag( EXT_MODS_ARG )) {
			command.append(":" + getParameter( EXT_MODS_ARG ) + "/*" );
		}
		command.append( " " + BioLockJ_CLASS );
		command.append( " " + RuntimeParamUtil.BLJ_PROJ_DIR + " " + BLJ_PROJ_DIR.getAbsolutePath() );
		if (getFlag( PASSWORD_ARG ) ) command.append( " " + RuntimeParamUtil.PASSWORD + " " + getParameter( PASSWORD_ARG ));
		if (getFlag( RESTART_ARG ) ) command.append( " " + RuntimeParamUtil.RESTART_DIR + " " + getRestartDir().getAbsolutePath());
		if (getFlag( PRECHECK_ARG ) || getFlag( UNUSED_PROPS_ARG )) command.append( " -precheck" );
		if (getFlag( UNUSED_PROPS_ARG ) ) { command.append( " " + RuntimeParamUtil.UNUSED_PROPS_FLAG ); }
		if ( getConfigFile() != null ) command.append( " " + RuntimeParamUtil.CONFIG_FILE + " " + getConfigFile().getAbsolutePath() );
		if ( ! getFlag( FG_ARG )) {
			command.insert( 0, "nohup " );
			//command.append( " >/dev/null 2>&1 &" );
		}
		return command.toString();
	}

	@Override
	void runCommand() throws Exception {
		String cmd = createCmd();
		if (inTestMode()) {System.out.println( LaunchProcess.BIOLOCKJ_TEST_MODE_VALUE + " " + cmd); throw new EndLaunch( 0 );}
		
		super.runCommand();
		
		System.out.print( "Initializing BioLockJ" );
		try {
			final Process p = Runtime.getRuntime().exec( cmd ); 
			confirmStart(p);
		} catch( final Exception ex ) {
			System.err.println( "Problem occurred running command: " + cmd);
			ex.printStackTrace();
		}
		printInfo();
	}
	
	/**
	 * inspired by MarcoS's response on https://stackoverflow.com/questions/5243233/set-running-time-limit-on-a-method-in-java
	 * @param p
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	private void confirmStart(Process p) throws IOException, InterruptedException {
		long startTime = System.currentTimeMillis();
		int maxtime = 15; 
		Timer timer = new Timer(true);
		if( !getFlag( FG_ARG ) ) {
			timer.schedule( new TimerTask() {
				@Override
				public void run() {
					System.out.print( "." );
				}
			}, 1 );
		}
		
		if ( getFlag( WAIT_ARG ) ) {
			timer.schedule( new TimerTask() {
				@Override
				public void run() {
					System.out.println( "The normal timeout has been disabled." );
				}
			}, maxtime );
		}else {
			timer.schedule(new InterruptTimerTask(Thread.currentThread()), maxtime);	
		}
		try {
			watchProcess(p);
		} catch (InterruptedException e) {
			if (System.currentTimeMillis() - startTime >= maxtime - 1 )
				System.out.println( "Reached max wait time: " + maxtime + " seconds." );
			else throw e;
		}
		timer.cancel();
	}
	
	
	private void checkJarFile() throws EndLaunch {
		BLJ_JAR_FILE=new File( new File(BLJ_DIR, "dist"), "BioLockJ.jar");
		if ( ! BLJ_JAR_FILE.exists()) {
			msgs.add( "Error: Required env variable BLJ_JAR [\"${BLJ}\"] must be a file on the filesystem. ");
			msgs.add("If you are a developer, you may need to build the program.");
			throw new EndLaunch( 1, msgs);
		}
	}
	
	/*
	 * A TimerTask that interrupts the specified thread when run.
	 */
	protected class InterruptTimerTask extends TimerTask {

	    private Thread theTread;

	    public InterruptTimerTask(Thread theTread) {
	        this.theTread = theTread;
	    }

	    @Override
	    public void run() {
	        theTread.interrupt();
	    }

	}
	
}
