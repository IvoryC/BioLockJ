package biolockj.module.report.otu;

import biolockj.dataType.BasicDataUnit;

/**
 * This is conceptual place-holder. The modules in the otu package were made with teh concept of an OTU table although
 * there was not a programmatically enforced formal definition. This class holds the original English definition, and
 * allows the otu modules to migrate to the ModuleIO system.
 * 
 * @author Ivory Blakley
 *
 */
public class OtuTable extends BasicDataUnit {

	@Override
	public String getDescription() {
		return "A table with two columns:<br> * Col1: Full OTU pathway spanning top to bottom level <br> Col2: Count (# of reads) for the sample.";
	}

}
