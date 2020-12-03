package biolockj.module.io;

import biolockj.dataType.DataUnit;
import biolockj.module.BioModule;

/**
 * This is the object that BioModules use to communicate about their outputs.
 * A {@link DataUnit} object is how modules talk about files they produce or use an inputs.
 * This object is a container for one DataUnit, and label.
 * The label is how this modules documentation (especially the getDetails() method) references each output type. 
 * To give more technical info about an output, use a more specific DataUnit object class; and/or implement more DataUnit interfaces.
 * To give humans more info about, add to the modules Details section, reference the label.
 * 
 * @author Ivory Blakley
 *
 */
public class ModuleOutput<T extends DataUnit>{
	
	public ModuleOutput(BioModule module, String label, T type) {
		this.module = module;
		this.label = label;
		this.type = type;
	}
	
	/**
	 * The object represents an output that is generated by exactly one module.
	 */
	private final BioModule module;
	
	/**
	 * Get the {@link #module}
	 * @return
	 */
	public BioModule getModule() {
		return module;
	}
	
	/**
	 * A label for this output. Should be one or a few words, 
	 * this will be used in logs, messages and the user guide.
	 */
	private final String label;

	/**
	 * Get the {@link #label}
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	private final T type;

	public T getDataType() {
		return type;
	}
	
}