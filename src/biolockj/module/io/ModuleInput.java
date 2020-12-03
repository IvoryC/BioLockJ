package biolockj.module.io;

import biolockj.dataType.BasicInputFilter;
import biolockj.dataType.DataUnit;
import biolockj.dataType.DataUnitFilter;
import biolockj.dataType.ModuleOutputFilter;
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
public class ModuleInput{
	
	/**
	 * Standard way to define an input.
	 * This will create a ModuleInput that requires that the input be an instance of the same DataUnit class as the template.
	 * @param label used as {@link #label}
	 * @param description used as {@link #description}
	 * @param template a DataUnit instance that will be used as the template if the inputs must come from pipeline inputs rather than a module.
	 */
	public ModuleInput(String label, String description, DataUnit template) {
		this(label, description, template, new BasicInputFilter(template.getClass()));
	}
	
	/**
	 * Define an input.
	 * This allows the criteria to be defined independently of the template.
	 * @param label used as {@link #label}
	 * @param description used as {@link #description}
	 * @param template a DataUnit instance that will be used as the template if the inputs must come from pipeline inputs rather than a module.
	 * @param criteria a {@link DataUnitFilter} that determines if a given DatUnit object is a suitable input.
	 */
	public ModuleInput(String label, String description, DataUnit template, DataUnitFilter criteria) {
		this.label = label;
		this.description = description;
		this.template = template;
		this.criteria = criteria;
	}
	
	/**
	 * Define an input.
	 * @param label used as {@link #label}
	 * @param description used as {@link #description}
	 * @param template a DataUnit instance that will be used as the template if the inputs must come from pipeline inputs rather than a module.
	 * @param clazz The class of BioModule that this input must come from
	 */
	public ModuleInput(String label, String description, DataUnit template, Class<? extends BioModule> clazz) {
		this(label, description, template, new ModuleOutputFilter(clazz));
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
	 * Where will this input source data from.
	 */
	private InputSource source = null;
	
	public InputSource getSource() {
		return source;
	}
	
	/**
	 * If reading from pipeline input files (rather than module outputs), 
	 * this DataUnit object should be used as a template to create DataUnit objects representing the inputs.
	 */
	private DataUnit template;
	
	public DataUnit getTemplate() {
		return template;
	}
	
	/**
	 * Almost always true.  
	 * If no suitable InputSource can be found, does that warrant an error?
	 */
	public boolean required = true;
	
}