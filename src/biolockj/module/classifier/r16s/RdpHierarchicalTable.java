package biolockj.module.classifier.r16s;

import biolockj.dataType.BasicDataUnit;
import biolockj.dataType.DataUnit;

public class RdpHierarchicalTable extends BasicDataUnit implements DataUnit {

	public RdpHierarchicalTable() {}

	@Override
	public String getDescription() {
		return "Tab-delimited output file containing the assignment count for each taxon in the hierarchical format.";
	}

}
