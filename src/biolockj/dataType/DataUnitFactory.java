package biolockj.dataType;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import biolockj.exception.ModuleInputException;

public interface DataUnitFactory {

	/**
	 * Interpret one or more files as DataUnit objects.
	 * See {@link #getData(List, boolean)}
	 * @param files a list of existing files on the file system
	 * @return a collection of one or more DataUnit objects
	 */
	public default <T extends DataUnit> List<T> getData(List<File> files, T template) throws ModuleInputException {
		return getData(files, template, false);
	}
	
	/**
	 * Interpret one or more files as DataUnit objects.
	 * This default implementation assumes one data unit corresponds to exactly 1 file.
	 * And that a directory does not represent an instance of it.
	 * 
	 * @param files a list of existing files on the file system
	 * @param useAllFiles if true, throw an error if any file cannot be used as part of the intended data type.
	 * @return a collection of one or more DataUnit objects
	 */
	@SuppressWarnings("unchecked")
	public default <T extends DataUnit> List<T> getData(List<File> files, T template, boolean useAllFiles) throws ModuleInputException {
		if ( files == null || files.isEmpty() ) throw new ModuleInputException("The files arg is required.");
		FilenameFilter filter = template.getFilenameFilter();
		List<T> data = new LinkedList<>();
		for (File file : files) {
			if (file.isDirectory()) continue;
			if (filter.accept( file.getParentFile(), file.getName() )) {
				if (useAllFiles) throw new ModuleInputException( "File name [" + file.getName() + "] is not an acceptable name for a data type: " + template.getClass().getName() );
				else continue;
			}
			List<File> unitFiles = new ArrayList<>();
			T unit;
			try {
				unit = (T) template.getClass().newInstance();
			} catch( InstantiationException | IllegalAccessException e ) {
				e.printStackTrace();
				throw new ModuleInputException( "Factory [" + this.getClass().getName() + "] could not create a new instance of: " + template.getClass().getName() );
			}
			unit.setFiles( unitFiles );
			unitFiles.add( file );
			data.add( unit );
		}
		return data;
	}
	
}