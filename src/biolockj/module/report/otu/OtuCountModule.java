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
package biolockj.module.report.otu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import biolockj.Log;
import biolockj.exception.ModuleInputException;
import biolockj.module.BioModule;
import biolockj.module.JavaModuleImpl;
import biolockj.module.io.ModuleIO;
import biolockj.module.io.ModuleInput;
import biolockj.module.io.ModuleOutput;
import biolockj.module.report.taxa.TaxaTable;
import biolockj.util.OtuUtil;

/**
 * OtuCount modules reads OTU count assignment tables (1 file/sample) with 2 columns.<br>
 * See {@link biolockj.module.report.otu.OtuTable}
 */
public abstract class OtuCountModule extends JavaModuleImpl implements ModuleIO {

	/**
	 * Check the module to determine if it generated OTU count files.
	 * This method was deprecated when the OtoCountModule framework moved to the ModuleIO system.
	 * 
	 * @param module BioModule
	 * @return TRUE if module generated OTU count files
	 */
	@Deprecated
	protected boolean isOtuModule( final BioModule module ) {
		try {
			final File[] files = module.getOutputDir().listFiles();

			for( final File f: files )
				if( OtuUtil.isOtuFile( f ) ) return true;
		} catch( final Exception ex ) {
			Log.warn( getClass(), "Error occurred while inspecting module output files: " + module );
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public List<ModuleInput> getInputTypes() {
		List<ModuleInput> inputs = new ArrayList<>();
		inputs.add( new ModuleInput("OTU tables", "Usually the output of a paser module that follows a classifier module.", new OtuTable()) );
		return inputs;
	}

	@Override
	public List<ModuleOutput> getOutputTypes(){
		List<ModuleOutput> outputs = new ArrayList<>();
		outputs.add( new ModuleOutput(this, "A taxa table that is merged result of all input OTU tables.", new TaxaTable()) );
		return outputs;
	}
	
	@Override
	public List<File> getInputFiles() throws ModuleInputException {
		return ModuleIO.super.getInputFiles();
	}

}
