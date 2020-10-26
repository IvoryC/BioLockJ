/**
 * @UNCC Fodor Lab
 * @author Michael Sioda
 * @email msioda@uncc.edu
 * @date Jan 20, 2019
 * @disclaimer This code is free software; you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any
 * later version, provided that any use properly credits the author. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details at http://www.gnu.org *
 */
package biolockj.module.report.taxa;

import java.io.File;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import biolockj.Log;
import biolockj.api.API_Exception;
import biolockj.exception.ModuleInputException;
import biolockj.module.BioModule;
import biolockj.module.JavaModuleImpl;
import biolockj.util.BioLockJUtil;
import biolockj.util.TaxaUtil;

/**
 * TBD
 */
public abstract class TaxaCountModule extends JavaModuleImpl {

	@Override
	public List<File> getInputFiles() throws ModuleInputException {
		if( getFileCache().isEmpty() ) {
			final List<File> files = new ArrayList<>();
			for( final File f: findModuleInputFiles() )
				if( TaxaTable.isTaxaTableFile( f ) ) files.add( f );

			cacheInputFiles( filterByProcessLevel( files ) );
		}

		return getFileCache();
	}

	/**
	 * Require taxonomy table module as prerequisite
	 */
	@Override
	public List<String> getPreRequisiteModules() throws Exception {
		final List<String> preReqs = super.getPreRequisiteModules();
		if( !BioLockJUtil.pipelineInputType( BioLockJUtil.PIPELINE_TAXA_COUNT_TABLE_INPUT_TYPE ) )
			preReqs.add( BuildTaxaTables.class.getName() );
		return preReqs;
	}
	
	/**
	 * All child classes from this class have this pre-req.
	 * @throws API_Exception 
	 */
	@Override
	public String getDetails() throws API_Exception {
		return super.getDetails() + "*If the pipeline input does not include at least one taxa table, then the BuildTaxaTables class is added by this module as a pre-requisite.*" 
						+ System.lineSeparator();
	}

	/**
	 * Check the module output directory for taxonomy table files generated by BioLockJ.
	 * 
	 * @param module BioModule
	 * @return TRUE if module generated taxonomy table files
	 */
	public boolean isTaxaModule( final BioModule module ) {
		try {
			final Collection<File> files = BioLockJUtil.removeIgnoredAndEmptyFiles(
				FileUtils.listFiles( module.getOutputDir(), HiddenFileFilter.VISIBLE, HiddenFileFilter.VISIBLE ) );

			for( final File f: files )
				if( TaxaTable.isTaxaTableFile( f ) ) return true;
		} catch( final Exception ex ) {
			Log.warn( getClass(), "Error occurred while inspecting module output files: " + module );
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isValidInputModule( final BioModule module ) {
		return isTaxaModule( module );
	}

	/**
	 * Pipelines may include taxa tables + normalized taxa tables (or log normalized taxa tables). Standard modules will
	 * always want the most processed modules (log-norm is top preference, then normalized, lastly raw counts). The
	 * purpose of includes less processes normalized tables is merely to provide the required input for the
	 * R_PlotEffectSize module (which find the correct tables in an R function).
	 * 
	 * @param files List of taxa table files
	 * @return List of taxa tables (only 1/level)
	 */
	protected static List<File> filterByProcessLevel( final List<File> files ) {
		final List<File> filteredFiles = new ArrayList<>();
		final Map<String, Set<File>> levelFiles = getTaxaFilesByLevel( files );
		for( final String level: levelFiles.keySet() ) {
			if( levelFiles.get( level ).size() == 1 ) {
				filteredFiles.add( levelFiles.get( level ).iterator().next() );
				continue;
			}

			File topFile = null;
			for( final File file: levelFiles.get( level ) ) {
				if( TaxaUtil.isLogNormalizedTaxaFile( file ) ) {
					topFile = file;
					break;
				}
				if( TaxaUtil.isNormalizedTaxaFile( file ) ) topFile = file;
				else if( topFile == null ) topFile = file;
			}

			filteredFiles.add( topFile );
		}

		return filteredFiles;
	}

	private static Map<String, Set<File>> getTaxaFilesByLevel( final List<File> files ) {
		final Map<String, Set<File>> levelFiles = new HashMap<>();
		for( final String level: TaxaUtil.getTaxaLevels() )
			for( final File file: files )
				if( file.getName().contains( level ) ) {
					Set<File> fileSet = levelFiles.get( level );
					if( fileSet == null ) {
						fileSet = new HashSet<>();
						levelFiles.put( level, fileSet );
					}
					fileSet.add( file );
				}
		return levelFiles;
	}

}
