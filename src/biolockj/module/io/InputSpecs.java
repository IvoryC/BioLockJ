package biolockj.module.io;

import java.util.ArrayList;
import java.util.List;
import biolockj.dataType.BasicInputFilter;
import biolockj.dataType.DataUnit;
import biolockj.dataType.DataUnitFilter;
import biolockj.dataType.ModuleOutputFilter;
import biolockj.exception.ModuleInputException;
import biolockj.module.BioModule;

/**
 * This is the object that {@link BioModule}s use to describe and find their inputs.
 * A {@link DataUnit} object is how modules talk about files they produce or use an inputs.
 * An {@link InputSource} object models the relationship between a {@link DataUnit} and where it comes from.
 * A BioModule must find a suitable InputSource to meet each of its required InputSpecs.
 * 
 * @author Ivory Blakley
 *
 */
public class InputSpecs{
	
	/**
	 * Define an input.
	 * @param label used as {@link #label}
	 * @param description used as {@link #description}
	 * @param dataUnitClass The required class for this input, the string will have to be matched to a class
	 * @param criteria a {@link DataUnitFilter} that determines if a given DatUnit object is a suitable input.
	 */
	public InputSpecs(String label, String description, String dataUnitClass, DataUnitFilter criteria) {
		this.label = label;
		this.description = description;
		this.dataUnitClass = dataUnitClass;
		this.criteria = criteria;
	}
	
	/**
	 * Define an input.
	 * @param label used as {@link #label}
	 * @param description used as {@link #description}
	 * @param clazz The required class of data for this input
	 */
	public InputSpecs(String label, String description, Class<? extends DataUnit<?>> clazz) {
		this(label, description, clazz.toString(), new BasicInputFilter(clazz));
	}
	
	/**
	 * Define an input.
	 * @param label used as {@link #label}
	 * @param description used as {@link #description}
	 * @param clazz The class of module that this input must come from
	 */
	public InputSpecs(String label, String description, String dataUnitClass, Class<? extends BioModule> clazz) {
		this(label, description, dataUnitClass, new ModuleOutputFilter(clazz));
	}
	
	/**
	 * One or a few words, this will be used in logs and error messages.
	 * Analogous to an argument name.
	 */
	private final String label;

	/**
	 * Get the {@link #label}.
	 * @return
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Human readable description. Used in user guide. 
	 * Should include the 'in English' form of the {@link #criteria}.
	 * A DataUnit description describes the format, and sometimes content type.
	 * This describes the role that data has in the modules execution.
	 */
	private final String description;

	/**
	 * Get the {@link #description}. 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * An object dedicated to determining if a given DataUnit is a suitable match for this input.
	 */
	private final DataUnitFilter criteria;

	/**
	 * Get the {@link #criteria} object for this input.
	 * @return
	 */
	public DataUnitFilter getFilter() {
		return criteria;
	}

	/**
	 * BioModule methods should assign a source to meet each InputSpecs during check dependencies.
	 * If canTakeMultiple==false, then source will have a length of 1.
	 */
	public List<InputSource> source = new ArrayList<>();
	List<InputSource> getInputSources() {
		return source;
	}
	
	public void addInputSource(InputSource in) throws ModuleInputException {
		if ( canIterate || source.isEmpty() ) {
			source.add( in );
		}else {
			throw new ModuleInputException( "Input [" + label + "] can only take one source." );
		}
	}
	
	/**
	 * If reading from pipeline input files (rather than module outputs), 
	 * what DataUnit class should be used to interpret the inputs.
	 */
	public String dataUnitClass = null;
	
	/**
	 * Is this module capable of taking multiple matches for this input 
	 * and still doing its thing for each/all of them ?
	 */
	public boolean canIterate = true;
	
	/**
	 * Almost always true.  
	 * If no suitable InputSource can be found, does that warrant an error?
	 */
	public boolean required = true;
	
}