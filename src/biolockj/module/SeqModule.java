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
package biolockj.module;

import java.util.ArrayList;
import java.util.List;
import biolockj.dataType.BasicInputFilter;
import biolockj.dataType.seq.GenericSeqData;
import biolockj.dataType.seq.SeqData;
import biolockj.module.io.ModuleIO;
import biolockj.module.io.ModuleInput;
import biolockj.module.io.ModuleOutput;

/**
 * Classes that implement this interface requires sequence files for input.<br>
 */
public interface SeqModule extends ScriptModule, ModuleIO {

	@Override
	public default List<ModuleInput> getInputTypes() {
		List<ModuleInput> inputs = new ArrayList<>();
		inputs.add( new ModuleInput( "Sequence data", "Sequence data.", new GenericSeqData(),
			new BasicInputFilter( SeqData.class ) ) );
		return inputs;
	}
		
	@Override
	public default List<ModuleOutput> getOutputTypes() {
		List<ModuleOutput> outputs = new ArrayList<>();
		outputs.add( new ModuleOutput(this, "Sequence data", new GenericSeqData()) );
		return outputs;
	}
	
}
