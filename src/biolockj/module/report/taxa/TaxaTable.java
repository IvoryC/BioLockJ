package biolockj.module.report.taxa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import biolockj.Constants;
import biolockj.Log;
import biolockj.dataType.DataUnit;
import biolockj.exception.BioLockJException;
import biolockj.util.BioLockJUtil;
import biolockj.util.TaxaUtil;

/**
 * A taxa table file is tab-delimited, has a named row for each sample and a named column for each taxon/OTU. 
 * There may be multiple tables representing the same samples, with up to one file for each taxonomic level.
 * 
 * When the files are actually read in, they are read as a {@link TaxaLevelTable} object.
 * 
 * @author Ivory Blakley
 *
 */
public class TaxaTable implements DataUnit {

	List<String> levels;
	
	/*
	 * Map of taxonomic level to taxa table file.
	 */
	private Map<String, File> files = null;
	
	@Override
	public String getDescription() {
		return "A taxa table file is tab-delimited, has a named row for each sample and a named column for each taxon/OTU. There may be multiple tables representing the same samples, with up to one file for each taxonomic level.";
	}

	@Override
	public boolean isValid() throws BioLockJException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * If any one of the files exists, it is ready to be examined.
	 */
	@Override
	public boolean isReady() {
		if (files == null) return false;
		boolean ready = false;
		for( File file : files.values() ) {
			if ( file.exists() ) ready = true;
		}
		return ready;
	}
	
	public Collection<String> getLevels(){
		return files.keySet();
	}
	
	public File getTaxaTableFile(String level) {
		return files.get( level );
	}
	
	/**
	 * Given a collection of files, determine the taxa level to assign to each and store as a map of level to file.
	 * @param inFiles
	 * @return
	 * @throws BioLockJException 
	 */
	public boolean setFiles(Collection<File> inFiles) throws BioLockJException {
		files = new HashMap<>();
		//Log.debug(this.getClass(), "Number of files passed in as files list: " + files.size());
		//Log.debug(this.getClass(), "Taxa levels to search for are: " + BioLockJUtil.getCollectionAsString( TaxaUtil.getTaxaLevels()) );
		for( final String level: TaxaUtil.getTaxaLevels() ) {
			for( final File file: inFiles ) {
				if( file.getName().contains( "_" + level + "." ) ) {
					File levelFile = files.get( level );
					if( levelFile != null ) {
						//we expect to find no more than one file per level
						Log.error(this.getClass(), "Found multiple input files for level [" + level + "].");
						Log.error(this.getClass(), "Determined level for file [" + levelFile + "] to be: " + level + ".");
						Log.error(this.getClass(), "Determined level for file [" + file + "] to be: " + level + ".");
						throw new BioLockJException("Found multiple input files for level [" + level + "].");
					}
					files.put(level, file);
					Log.debug(this.getClass(), "Determined file for level [" + level + "] is: " + file);
				}
			}
		}
		Log.debug(this.getClass(), "Found input files for levels: " + getLevels());
		return true;
	}
	
	/**
	 * Check the file name to determine if it is a taxonomy table file.
	 * 
	 * @param file File to test
	 * @return boolean TRUE if file is a taxonomy count file
	 */
	public static boolean isTaxaTableFile( final File file ) {
		if( file.getName().contains( "_" + TAXA_TABLE + "_" ) ) for( final String level: TaxaUtil.getTaxaLevels() )
			if( file.getName().endsWith( level + Constants.TSV_EXT ) ) return true;
		return false;
	}

	/**
	 * Read a table of counts, formatted with samples as rows (ids in first column) and taxa as columns (ids in header).
	 * @param taxaTable
	 * @return Map linking sample name to map that links taxa IDs to value.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws BioLockJException
	 */
	public static TaxaLevelTable readTaxaTable(final File taxaTable) throws FileNotFoundException, IOException, BioLockJException{
		TaxaLevelTable data = new TaxaLevelTable(TaxaUtil.getTaxonomyTableLevel( taxaTable ));
		final List<String> otuNames = new ArrayList<>();
		boolean foundBigValues = false;
		
		final BufferedReader reader = BioLockJUtil.getFileReader( taxaTable );
		try {
			otuNames.addAll( TaxaUtil.getOtuNames( reader.readLine() ) );
			String nextLine = reader.readLine();
	
			while( nextLine != null ) {
				final StringTokenizer st = new StringTokenizer( nextLine, TaxaUtil.DELIM );
				final String sampleID = st.nextToken();
				final HashMap<String, Double> rowValues = data.newSampleRow( sampleID );
				int i = 0;
				while( st.hasMoreTokens() ) {
					final String nextToken = st.nextToken();
					if( nextToken.length() > 0 ) {
						Double cellValue = new Double( nextToken );
						if ( (cellValue + 1) <= cellValue ) foundBigValues = true;
						rowValues.put( otuNames.get( i ), cellValue);
					}
					i++;
				}
				if ( rowValues.size() != otuNames.size() ) {
					throw new BioLockJException("Header included [" + otuNames.size() + "] taxa, but the row for sample [" + 
				sampleID + "] has [" + rowValues.size() + "] values.");
				}
	
				nextLine = reader.readLine();
			}
		} finally {
			if( reader != null ) reader.close();
		}
		if (foundBigValues) Log.warn(TaxaUtil.class, "Values in this table may be larger than can be precisely stored as a Double.");
		return data;
	}

	/**
	 * Included in the file name of each file output. One file per sample is output by the ParserModule.
	 */
	public static final String TAXA_TABLE = "taxaCount";

}
