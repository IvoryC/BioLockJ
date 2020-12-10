package biolockj.dataType.seq;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import biolockj.Log;
import biolockj.dataType.BasicDataUnit;
import biolockj.dataType.DataUnit;
import biolockj.exception.BioLockJException;
import biolockj.exception.ModuleInputException;

/**
 * When representing an individual db, there should only be one file, and it should be the reference db directory.
 * @author Ivory Blakley
 *
 */
public class Bowtie2ReferenceGenome extends BasicDataUnit implements DataUnit, SeqData {

	/**
	 * Create a {@link Bowtie2ReferenceGenome} instance with all default values.
	 */
	public Bowtie2ReferenceGenome() {
		format = new FastaFormat() {} ;
		alphabet = new SequenceAlphabet() {};
	}
	
	/**
	 * Create a {@link Bowtie2ReferenceGenome} instance to represent existing database.
	 * @param dir the folder containing the components of the reference, including index files.
	 * @throws BioLockJException
	 */
	public Bowtie2ReferenceGenome(File dir) throws BioLockJException {
		setFiles( Arrays.asList( dir ) );
	}
	
	/**
	 * Define a {@link Bowtie2ReferenceGenome} instance with existing specifications for the format.
	 * @param format
	 * @param alphabet
	 */
	public Bowtie2ReferenceGenome(FastaFormat format, SequenceAlphabet alphabet) {
		this.format = format;
		this.alphabet = alphabet;
	}
	
	private SequenceAlphabet alphabet;
	
	@Override
	public SequenceAlphabet getAlphabet() {
		return alphabet;
	}
	
	private SequenceFormat format;

	@Override
	public SequenceFormat getFormat() {
		return format;
	}

	@Override
	public boolean isPaired() {
		return false;
	}

	@Override
	public String getDescription() {
		return "The product of the bowtie2-build command. Referenceing the bowtie2 software produced by Johns Hopkins University, see http://bowtie-bio.sourceforge.net/bowtie2/manual.shtml#the-bowtie2-build-indexer ";
	}

	@Override
	public List<DataUnit> getData( List<File> files, DataUnit template, boolean useAllFiles )
		throws ModuleInputException {
		Bowtie2ReferenceGenome base = (Bowtie2ReferenceGenome) template;
		List<DataUnit> data = new ArrayList<>();
		if ( files == null || files.isEmpty() ) throw new ModuleInputException("The files arg is required.");
		for (File dir : files) {
			if ( ! dir.isDirectory()) {
				if (useAllFiles) throw new ModuleInputException( "The required file type for a " + this.name() + " must be a directory. Found: " + dir.getAbsolutePath() );
				continue;
			}
			
			try {
				Bowtie2ReferenceGenome unit = new Bowtie2ReferenceGenome( (FastaFormat) base.getFormat(), base.getAlphabet() );
				unit.setFiles( Arrays.asList( dir ) );
				unit.canBeMultiple(false);
				if (unit.isValid()) {
					data.add( unit );
				}else {
					String msg = "File [" + dir.getName() + "] did not result in a valid [" + template.name() + "] object.";
					Log.debug(this.getClass(), msg);
					if (useAllFiles) throw new ModuleInputException(msg);
				}
			} catch( BioLockJException e ) {
				e.printStackTrace();
				throw new ModuleInputException( "Factory [" + this.getClass().getName() + "] could not create a new instance of: " + template.getClass().getName() );
			}
		}

		return data;
	}

	@Override
	public boolean isValid() throws BioLockJException {
		boolean valid = true;
		if (getFiles() == null) valid = false;
		if ( !canBeMultiple() && getFiles().size() > 1 ) valid = false;
		for (File dir : getFiles() ) {
			if ( ! dir.isDirectory() ) valid = false;
			else {
				if ( getIndexFiles(dir).length == 0) valid = false;
			}
		}
		return valid;
	}

	public File[] getIndexFiles(File dbRoot){
		return dbRoot.listFiles(new FilenameFilter() {
			@Override
			public boolean accept( File dir, String name ) {
				return name.endsWith( ".bt2" ) || name.endsWith( ".bt21" );
			}	
		});
	}
}
