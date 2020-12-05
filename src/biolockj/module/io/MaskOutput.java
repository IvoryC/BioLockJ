package biolockj.module.io;

import java.util.ArrayList;
import java.util.List;
import biolockj.Config;
import biolockj.Log;
import biolockj.Properties;
import biolockj.api.ApiModule;
import biolockj.dataType.DataUnit;
import biolockj.dataType.DataUnitFilter;
import biolockj.dataType.SpecificModuleOutputUnit;
import biolockj.exception.BioLockJException;
import biolockj.module.BioModule;
import biolockj.module.JavaModuleImpl;
import biolockj.util.BioLockJUtil;

public class MaskOutput extends JavaModuleImpl implements ApiModule, ModuleIO {

	public MaskOutput() {
		addNewProperty( MAST_MOD, Properties.STRING_TYPE, MAST_MOD_DESC );
		addNewProperty( AS_TYPE, Properties.LIST_TYPE, AS_TYPE_DESC );
	}
	
	@Override
	public void runModule() throws Exception {
		// do nothing
	}
	
	@Override
	public void checkDependencies() throws Exception {
		super.checkDependencies();
		Config.getString( this, MAST_MOD );
		if ( other == null ) throw new BioLockJException("Failed to find upstream module called \"" + Config.getString( this, MAST_MOD ) + "\".");
		Log.info(this.getClass(), "The actual output will come from module: " + other);
		Log.info(this.getClass(), "The output will appear to other modules as type(s): " + BioLockJUtil.getCollectionAsString( getUnits() ));
	}

	@Override
	public List<ModuleInput> getInputTypes() {
		List<ModuleInput> inputs = new ArrayList<ModuleInput>();
		inputs.add( new ModuleInput( "output to mask", "the output of some upstream module", 
			new SpecificModuleOutputUnit<BioModule>( other ),
			new DataUnitFilter() {
				
				@Override
				public boolean accept( DataUnit data ) {
					boolean accept = false;
					if (data instanceof SpecificModuleOutputUnit) {
						BioModule oth = ((SpecificModuleOutputUnit<?>) data).getCreatingModule();
						accept = oth.getAlias().equals(Config.getString( MaskOutput.this, MAST_MOD ));
						if (accept) MaskOutput.this.other = oth;
					}
					return accept;
				}
			}) );
		return inputs;
	}

	@Override
	public List<ModuleOutput> getOutputTypes()  {
		List<ModuleOutput> outputs = new ArrayList<>();
		try {
			for (DataUnit unit : getUnits() ) {
				outputs.add( new ModuleOutput(this, "mask", unit ) );
			}
		} catch( BioLockJException e ) {
			// catch failure from getUnits in checkDependencies()
			e.printStackTrace();
		}
		return outputs;
	}
	
	private List<DataUnit> getUnits() throws BioLockJException{
		List<DataUnit> units = new ArrayList<>();
		for (String type : Config.getList( this, AS_TYPE )) {
			try {
				Class.forName( type );
			} catch( ClassNotFoundException e ) {
				e.printStackTrace();
				throw new BioLockJException( "Failed to creat DataUnit class of type: " + type );
			}
		}
		return units;
	}

	@Override
	public String getDescription() {
		return "Make the outputs of some module appear as some type.";
	}
	
	@Override
	public String getDetails() {
		return "Sometimes valid inputs for some downstream module are created by some upstream module, but there is a mis-match between the sought inputs and the declared outputs. The " +
			this.getClass().getSimpleName() +
			" module comes to the rescue!  This module produces no output; but it declares output types based on configurable property _" +
			AS_TYPE +
			"_ and instead of directing downstream modules to its own output directory, it directs them to the output directory of some upstream module, based on _" +
			MAST_MOD + "_.";
	}

	@Override
	public String getCitationString() {
		return "Module created by Ivory Blakley";
	}
	
	BioModule other;
	
	private static final String MAST_MOD = "maskOutput.maskModule";
	private static final String MAST_MOD_DESC = "The alias of the upstream module whose outputs should be masked.";
	
	private static final String AS_TYPE = "maskOutput.asType";
	private static final String AS_TYPE_DESC = "The DataUnit type(s) to declare for other modules to use.";

}
