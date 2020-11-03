package biolockj.module.io;

import biolockj.dataType.DataUnit;

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
public class OutputSpecs{
	
	public OutputSpecs(String label, DataUnit type) {
		this.label = label;
		this.type = type;
	}
	
	/**
	 * A label for this output. Should be one or a few words, 
	 * this will be used in logs, messages and the user guide.
	 */
	private final String label;

	public String getLabel() {
		return label;
	}

	private final DataUnit type;

	public DataUnit getDataType() {
		return type;
	}
	
}