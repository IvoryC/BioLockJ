package biolockj.dataType;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import biolockj.exception.BioLockJException;
import biolockj.exception.ModuleInputException;

public abstract class BasicDataUnit<T extends BasicDataUnit<?>> implements DataUnit<T> {
	
	private List<File> files = null;
	private boolean iterable = true;

	@Override
	public boolean isValid() throws BioLockJException {
		return isReady() && ! isIterable();
	}

	@Override
	public boolean isReady() {
		return files != null;
	}

	@Override
	public boolean isIterable() {
		return iterable;
	}

	@Override
	public void setIterable(boolean iterable) {
		this.iterable = iterable;
	}

	/**
	 * This implementation assumes one data unit corresponds to exactly 1 file.
	 * And that a directory does not represent an instance of it.
	 */
	@Override
	public Collection<T> iterate() throws BioLockJException {
		if ( files != null || files.isEmpty() ) throw new ModuleInputException("Cannot iterate before files are set.");
		if ( files.size() == 1 && files.get( 0 ).isDirectory() ) {
			setFiles( Arrays.asList( files.get( 0 ).listFiles( getFilenameFilter() )) );
		}
		List<T> list = new LinkedList<>();
		for (File file : files) {
			if (file.isDirectory()) continue;
			try {
				List<File> inner = new ArrayList<>();
				T unit = (T) this.clone();
				unit.setFiles( inner );
				unit.setIterable(false);
				inner.add( file );
				list.add( unit );
			} catch( CloneNotSupportedException e ) {
				e.printStackTrace();
				throw new ModuleInputException( "There was a problem in distinguishing the individual " 
				+ this.getClass().getName() + " files; see file: " + file.getAbsolutePath() );
			}
		}
		return list;
	}

	@Override
	public void setFiles( List<File> files ) {
		this.files = files;
	}

	@Override
	public List<File> getFiles() {
		return files;
	}
	
	public FilenameFilter getFilenameFilter() {
		return new FilenameFilter() {
			@Override
			public boolean accept( File dir, String name ) {
				//no hidden files, no files without extensions
				if ( name.startsWith( "." ) | !name.contains( "." ) ) return false; 
				else return true;
			}
		};
	}

}
