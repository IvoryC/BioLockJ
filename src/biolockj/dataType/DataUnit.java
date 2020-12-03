package biolockj.dataType;

import java.io.File;
import java.io.FilenameFilter;
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
public interface DataUnit {

	/**
	 * A description to help a human decide if a given input is appropriate for a given method.
	 * @return the description
	 */
	public String getDescription();
	
	/**
	 * Once data is attached (such as files, objects, meta data columns); 
	 * this method allows the class to check that a given instance actually upholds the class description.
	 * For example: a module may say it makes a column in the metadata, called "Counts", that has numeric data.
	 * That module returns a NumericMetaData object, that indicates column name "Counts".
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
	public default boolean isValid() throws BioLockJException{
		return isReady();
	}
	
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
	public default boolean isReady() {
		List<File> files = null;
		try {
			files = getFiles();
		} catch( Exception e ) {
			files = null;
		}
		boolean ready = files != null && !files.isEmpty();
		return ready;
	}
	
	//private List<File> files = null;
	
	/**
	 * This method is typically called by a DataUnitFactory class. This method should be "dumb". The place for ensuring
	 * that correct file types are added is in the DataUnitFactory class, or in the isValid method.
	 * 
	 * The List design accommodates things that have multiple files with a fixed relationship, such paired reads where
	 * one sample has a forward file and a reverse file.
	 * 
	 * At the point when setFiles is called, if there are multiple instances of this file type, each instance is modeled by a
	 * DataUnit instance.
	 * 
	 * @param files
	 */
	public void setFiles(List<File> files);
	//this.files = files;
	
	/**
	 * This will often return a list of exactly 1.
	 * You cannot getFiles() from a DataUnit object that is not isReady().
	 * You cannot getFiles() from a DataUnit object that canBeMultiple - that object must iterate() 
	 * to create a List of DataUnit objects of the same type, each of which 
	 * are isReady() and not canBeMultiple() and can call getFiles().
	 * Some implementations may do these steps and return all files, and the 
	 * calling class would have to call iterate() to be able to list files per individual object.
	 * @return
	 */
	public List<File> getFiles() throws BioLockJException;
	//return files;
	
	/**
	 * Return an object that can be used to produce instances of this data unit given one or more files. A class, clazz,
	 * implementing DataUnit may also implement DataUnitFilter<clazz>, and its implementation of this method could be as
	 * simple as: return this;
	 * 
	 * @return
	 */
	public default DataUnitFactory<?> getFactory(){
		return getFactory(this);
	}
	
	public static <T extends DataUnit> DataUnitFactory<T> getFactory(T template){
		return new DataUnitFactory<T>() {};
	}

	/**
	 * Returns a FilenameFilter object which accepts files of this data type. 
	 * 
	 * For example, a FastaSeqs class implementing
	 * DataUnit would return a filter that accepts filenames ending in .fa or .fasta, thus making it easy for the
	 * DataUnitUnitFactory class, or other classes, to pick out the relevant files and ignore 'readme.txt' and
	 * 'metadada.csv'.
	 * 
	 * The default implementation excludes hidden files, and excludes files that have no file extension (no "." in the name).
	 * 
	 * @return
	 */
	public default FilenameFilter getFilenameFilter() {
		return new FilenameFilter() {
			@Override
			public boolean accept( File dir, String name ) {
				//no hidden files, no files without extensions
				if ( name.startsWith( "." ) | !name.contains( "." ) ) return false; 
				else return true;
			}
		};
	}
		
	/**
	 * Many programs will produce one OR MORE of a given file type depending on the inputs they receive. Some
	 * programs accept exactly one object for each input and/or produce exactly one output.
	 * 
	 * For example FastQC takes in one sequence file and produces one summary, and if you give it many sequence files,
	 * it will produce many summary files in parallel. So a FastQC module would produce one output type, a DataUnit
	 * class that represents a FastQC summary, and it would return true for canBeMultiple() indicating to all downstream
	 * modules that it produces one type, and it will create any number of instances of that type.
	 * 
	 * Conversely, BuildTaxaTables produces exactly one TaxaTable object regardless of how many sequence files go into
	 * it; so it would return false.
	 * 
	 * An implementation could be smart enough to look at its own inputs and determine if multiple summaries will be
	 * produced. Most implementations will simply return true, because it is possible that multiple instances will be
	 * produced.
	 * 
	 * A module that can't handle multiple instances of an input type might have a DataUnitFilter that will disallow
	 * canBeMultiple(). Most methods that take exactly one input will iterate over however many there are and produce
	 * one instance of the output type for each instance of the input type.
	 * 
	 * @return
	 */
	public boolean canBeMultiple() ;
	
	/**
	 *  Allow the module that uses this to model its output to specify if it has the 
	 *  possibility of making more than one instance of this data type.
	 *  See {@link DataUnit#isIterable()}
	 * @param multiple can this output produce more than one instance of this output type
	 */
	public void canBeMultiple(boolean multiple) ;
	
}
