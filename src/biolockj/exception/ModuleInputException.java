package biolockj.exception;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import biolockj.dataType.InputSource;
import biolockj.module.BioModule;
import biolockj.util.ModuleUtil;

/**
 * This Exception is thrown when a BioModule encounters a problem while identifying 
 * its specific input files from the abstract representation of where to look.
 * @author ieclabau
 *
 */
public class ModuleInputException extends BioLockJException {

	public ModuleInputException( String msg ) {
		super( msg );
	}
	
	public ModuleInputException( BioModule current, InputSource is ) {
		super( buildMessage( current, is ) );
		this.cause = most_recent_cause + "";
	}
	
	private static String most_recent_cause = "unknown";
	private String cause = "unknown";
	
	public String getGeneralCause() {
		return cause;
	}
	
	private static String buildMessage( BioModule current, InputSource is ) {
		String msgStart = "Module [" + ModuleUtil.displaySignature( current ) + 
						"] cannot take its inputs because";
		String msgMid;
		String msgEnd;
		if ( is.isModule() ) {
			BioModule source = is.getBioModule();
			msgMid = msgStart + " module [" + ModuleUtil.displaySignature( source ) + "]";
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
			File sourceFile = is.getFile();
			msgMid = msgStart + " input source [" + is.getName() + "]";
			if ( ! sourceFile.exists() ) {
				most_recent_cause = NONEXISTING_FILE;
				msgEnd = msgMid + most_recent_cause;
			}else {
				if ( sourceFile.isDirectory() ) {
					int numDirFiles = FileUtils.listFiles( sourceFile,
						HiddenFileFilter.VISIBLE, HiddenFileFilter.VISIBLE ).size();
					if ( numDirFiles == 0 ) {
						most_recent_cause = EMPTY_DIR;
						msgEnd = msgMid + most_recent_cause;
					}else{
						most_recent_cause = HAS_FILES;
						msgEnd = msgMid + " contains " + numDirFiles + most_recent_cause;
					}
				} else {
					most_recent_cause = FILE_EXISTS;
					msgEnd = msgMid + most_recent_cause;
				}
			}
		}
		return msgEnd;
	}
	
	private static final String NO_OUTPUT_FILES = " completed without creating any output files.";
	private static final String OUTPUTS_EXIST = " output files, but there was a problem.";
	private static final String INCOMPLETE_MODULE = " has not completed.";
	private static final String NONEXISTING_FILE = " does not exist.";
	private static final String EMPTY_DIR = " is an empty directory.";
	private static final String HAS_FILES = " files, but there was a problem.";
	private static final String FILE_EXISTS = " is an existing file, but there was a problem.";

}
