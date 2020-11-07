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
public class OutputSpecs<T extends DataUnit>{
	
	public OutputSpecs(String label, T type) {
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

	private final T type;

	public T getDataType() {
		return type;
	}
				
	private boolean iterable = true;
	
	/**
	 * Many programs will produce one OR MORE of a given file type depending on how many inputs they receive.
	 * Some programs accept exactly one object for each input and/or produce exactly one output.
	 * For many programs, this distinction is handled by accepting a single file as an input, 
	 * and multiple files are given as inputs, the program iterates over all of them.
	 * For example FastQC takes in one sequence file and produces one summary, and if you 
	 * give it many sequence files, it will produce many summary files in parallel. So a FastQC module 
	 * would produce one output type, a DataUnit class that represents a FastQC summary, and it would 
	 * return true for isIterable() indicating to all downstream modules that it produces one type, 
	 * and it will create any number of instances of that type.  An implementation might be smart enough 
	 * to look at its own inputs and determine if multiple summaries will be produced.
	 * In terms of inputs, a DataUnitFilter might be designed to disallow isIterable();
	 * BioModule logic should include some kind of handing of iterable forms of their inputs, 
	 * most likely just iterating over them with the same logic used for a single instance.
	 * @return
	 */
	public boolean isIterable() {
		return iterable;
	}
	
	/**
	 *  Allow the module that uses this to model its output to specify if it has the 
	 *  possibility of making more than one instance of this data type.
	 *  See {@link DataUnit#isIterable()}
	 * @param iterable can this output produce more than one of this output type
	 */
	public void setIterable(boolean iterable) {
		this.iterable = iterable;
	}
	
}