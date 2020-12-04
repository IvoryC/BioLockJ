package biolockj.exception;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import biolockj.module.BioModule;
import biolockj.module.io.InputSource;
import biolockj.util.ModuleUtil;

/**
 * This Exception is thrown when a BioModule encounters a problem while identifying 
 * its specific input files from the abstract representation of where to look.
 * @author Ivory Blakley
 *
 */
public class ModuleInputException extends BioLockJException {

	public ModuleInputException( String msg ) {
		super( msg );
	}
	
	public ModuleInputException( Exception ex ) {
		super( "There was a problem setting inputs." + System.lineSeparator() + "Underlying Exception Type: " + ex.getClass().getSimpleName() +
			System.lineSeparator() + "Message:" + ex.getMessage() );
		this.cause = ex;
	}
	
	public ModuleInputException( BioModule current, Exception ex ) {
		super( "Module " + current + " has a problem with its inputs." + System.lineSeparator() + "Underlying Exception Type: " + ex.getClass().getSimpleName() +
			System.lineSeparator() + "Message:" + ex.getMessage() );
		this.cause = ex;
	}
	
	public ModuleInputException( BioModule current, InputSource<?> is, Exception ex ) {
		super( buildMessage( current, is ) + System.lineSeparator() + "Underlying Exception Type: " + ex.getClass().getSimpleName() +
			System.lineSeparator() + "Message:" + ex.getMessage() );
		this.cause = ex;
	}
	
	public ModuleInputException( BioModule current, InputSource<?> is ) {
		super( buildMessage( current, is ) );
	}
	
	private static String most_recent_cause = "unknown";
	private Exception cause = null;
	
	public Exception getGeneralCause() {
		return cause;
	}
	
	private static String buildMessage( BioModule current, InputSource<?> is ) {
		String msgStart = "Module [" + current + "] cannot take its inputs";
		String msgMid;
		String msgEnd;
		if ( is.isModule() ) {
			BioModule source = is.getModuleOutput().getModule();
			msgMid = msgStart + " because module [" + ModuleUtil.displaySignature( source ) + "]";
			if (ModuleUtil.isComplete( source ) ) {
				int numOutFiles = FileUtils.listFiles( source.getOutputDir(),
					HiddenFileFilter.VISIBLE, HiddenFileFilter.VISIBLE ).size();
				if (numOutFiles == 0) {
					most_recent_cause = NO_OUTPUT_FILES;
					msgEnd = msgMid + most_recent_cause;
				}else {
					most_recent_cause = OUTPUTS_EXIST;
					msgEnd = msgMid + " created " + numOutFiles + most_recent_cause;
				}
			}else {
				most_recent_cause = INCOMPLETE_MODULE;
				msgEnd = msgMid + most_recent_cause;
			}
		}else {
			msgEnd = msgStart + ". There may be a problem with the input files.";
		}
		return msgEnd;
	}
	
	private static final String NO_OUTPUT_FILES = " completed without creating any output files.";
	private static final String OUTPUTS_EXIST = " output files, but there was a problem.";
	private static final String INCOMPLETE_MODULE = " has not completed.";
	
	private static final long serialVersionUID = 1857464960L;

}
