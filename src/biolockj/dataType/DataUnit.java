package biolockj.dataType;

import java.io.File;
import java.util.List;
import biolockj.exception.BioLockJException;
import biolockj.module.io.InputSource;

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
public interface DataUnit<T extends Object> {
	
	/**
	 * A description to help a human decide if a given input is appropriate for a given method.
	 * @return the description
	 */
	public String getDescription();
	
	/**
	 * Once data is attached (such files, objects, meta data columns; 
	 * this method allows the class to check that a given instance actually upholds the class description.
	 * For example: a module may say it makes an column in the metadata, called "Counts", that has numeric data.
	 * That module return a NumericMetaData object, that indicated column name counts.
	 * Once that column has been made (or should have been made) this isValid method of the NumericMetaData class
	 * can check that there actually is a metadata column named Counts, that contains numeric data.
	 * 
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
	 * Object instance may be used for modules to communicate about outputs they will produce.
	 * During check dependencies, anything being created by a module is expected to be isReady=false.
	 * Anything being supplied as an input is expected to be isReady=true from the start.
	 * When a module executes, all of its inputs are expected to be isReady=true.
	 * The relationship is captured by the {@link InputSource} class.  The {@link InputSource} 
	 * may throw an error if the module that this DataUnit is expected to come from has 
	 * completed ({@link InputSource#isReady()} but the DataUnit is still returning isReady=false )
	 * 
	 * This method should catch any exceptions and return false. 
	 * We fully expect that it might encounter null pointers, or other inconvenient issues.
	 * @return
	 */
	public boolean isReady();
	
	/**
	 * Many programs will produce one OR MORE of a given file type depending on how many inputs they receive.
	 * Some programs accept exactly one object for each input and/or produce exactly one output.
	 * For many programs, this distinction is handled by accepting a single file as an input, 
	 * and multiple files are given as inputs, the program iterates over all of them.
	 * For example FastQC takes in one sequence file and produces one summary, and if you 
	 * give it many sequence files, it will produce many summary files in parallel. So a FastQC module 
	 * would produce one output type, a DataUnit class that represents a FastQC summary, and it would 
	 * return true for isIterable() indicating to all downstream modules that it produces one type, 
	 * and it will create any number of instances of that type.  An implementation might be smart enough 
	 * to look at its own inputs and determine if multiple summaries will be produced.
	 * In terms of inputs, a DataUnitFilter might be designed to disallow isIterable();
	 * BioModule logic should include some kind of handing of iterable forms of their inputs, 
	 * most likely just iterating over them with the same logic used for a single instance.
	 * @return
	 */
	public boolean isIterable();

	/**
	 *  Allow the module that uses this to model its output to specify if it has the 
	 *  possibility of making more than one instance of this data type.
	 *  See {@link DataUnit#isIterable()}
	 * @param iterable TODO
	 */
	public void setIterable(boolean iterable);
	
	/**
	 * For most file types, whatever process created it may have created several.
	 * Before the outputs are created, we model them as if there will be exactly 1.
	 * After the outputs are created, the this method is used to create new objects each of which 
	 * models one output instance. This method needs to be able to identify one output from another
	 * in a folder that contains many instances.  In most cases, there is a 1-to-1 file to DataUnit 
	 * ratio, so this is pretty straight forward. For any not straight forward cases, this method 
	 * needs to be smart enough to make the distinctions. This method should also be smart enough 
	 * to ignore irrelevant files in the same directory.
	 * It is recommended that for each output DataUnit object, this method should 
	 *  - call setFiles() with appropriate files.
	 *  - setIterable(false);
	 * If isReady == false, return null or throw error.
	 * Make NO CALLs to isValid(); isValid probably calls isIterable() and iterate().
	 * If isIterable() == false, then this method should return a Collection of length 1, 
	 * where the one object is itself.
	 * 
	 * See {@link DataUnit#isIterable()}
	 * @return
	 */
	public Iterable<T> iterate() throws BioLockJException;
	
	/**
	 * This will often accept only a list of exactly 1.
	 * The List aspect accommodates things that have multiple files with a fixed relationship, 
	 * such paired reads where one sample has a forward file and a reverse file.
	 * If there are multiple instances of this unit type, this object will need to use iterate() before assigning files.
	 * @param files
	 */
	public void setFiles(List<File> files);
	
	/**
	 * This will often return a list of exactly 1.
	 * You cannot getFiles() from a DataUnit object that is not isReady().
	 * You cannot getFiles() from a DataUnit object that isIterable - that object must iterate() 
	 * to create a List of DataUnit objects of the same type, each of which 
	 * are isReady() and not isIterable() and can call getFiles().
	 * Some implementations may do these steps and return all files, and the 
	 * calling class would have to call iterate() to be able to list files per individual object.
	 * @return
	 */
	public List<File> getFiles() throws BioLockJException;

}
