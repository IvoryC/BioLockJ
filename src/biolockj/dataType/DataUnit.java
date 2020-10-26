package biolockj.dataType;

import biolockj.exception.BioLockJException;
import jdk.Exported;

/**
 * Types to represent the data that modules might use as inputs or outputs.
 * There does NOT need to be a 1-1 relationship with files on the file system,
 * a data type might be stored in multiple files, such as structured database, or paired sequence files.
 * 
 * When looking for inputs, modules should require the most abstract form that meets their needs.
 * 
 * When describing outputs, modules should use the most specific class that accurately describes their outputs.
 * 
 * @author Ivory Blakley
 *
 */
public interface DataUnit {
	
	/**
	 * A description to help a human decide if a given input is appropriate for a given method.
	 * @return the description
	 */
	public String getDescription();
	
	/**
	 * The constructor of an implementing class should never attempt to validate itself; the DataUnit instance may be
	 * used to represent data that does not yet exist. 
	 * 
	 * DataUnit should be validated when it is about to be used, OR any time after it {@link isReady} == true.
	 * 
	 * Calling {@link #isReady} within the method implementation is recommended. 
	 * In general, if it is not ready it cannot possibly be valid. In general, at a point when it is
	 * appropriate to call isValid, if isReady()==false then it is likely that something unexpected as gone wrong, and it will
	 * be more helpful to have the calling class get a response of 'false' and let that class create an error message.
	 * For problems that occur where isReady==true, the methods in the isValid method are probably better able to create
	 * a helpful message. 
	 * 
	 * @return
	 */
	public boolean isValid() throws BioLockJException;
	
	/**
	 * Object instance may used for modules to communicate about outputs they will produce.
	 * During check dependencies, anything being created by a module is expected to be isReady=false.
	 * Anything being supplied as an input is expected to be isReady=true from the start.
	 * When a module executes, all of its inputs are expected to be isReady=true.
	 * The relationship is captured by the {@link InputSource} class.  The {@link InputSource} 
	 * may throw an error if the module that this DataUnit is expected to come from has 
	 * completed ({@link InputSource#isReady()} but the DataUnit is still returning isReady=false.
	 * 
	 * This method should catch any exceptions and return false. 
	 * We fully expect that it might encounter null pointers, or other inconvenient issues.
	 * @return
	 */
	public boolean isReady();
	
	/**
	 * Experimental.  Not guaranteed in future versions.
	 * @param type
	 * @return
	 */
	@Deprecated
	public static String getTypeName(DataUnit type) {
		return type.getClass().getName();
	}

}
