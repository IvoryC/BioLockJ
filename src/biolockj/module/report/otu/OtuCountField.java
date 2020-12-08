package biolockj.module.report.otu;

import biolockj.Constants;
import biolockj.dataType.metatdata.CountField;
import biolockj.exception.MetadataException;
import biolockj.util.MetaUtil;

public class OtuCountField extends CountField {

	public OtuCountField( String name ) {
		super( name );
	}
	
	/**
	 * @return the description
	 */
	@Override
	public String getDescription() {
		return "A metadata attribute with OTU counts data. The column name ends with \"" + Constants.OTU_COUNT + "\" All non-null values are integers >= 0.";
	}
	
	public boolean isValid() throws MetadataException {
		if ( !super.isValid() ) return false;
		if ( ! getName().endsWith( Constants.OTU_COUNT )) return false;
		return verifyVals( MetaUtil.getFieldValues( getName(), true ) );
	}
	
	@Override
	protected boolean isAcceptableColumn(String field) {
		if (!field.endsWith( Constants.OTU_COUNT )) return false;
		boolean accept = false;
		try {
			accept = verifyVals( MetaUtil.getFieldValues( getName(), true ) );
		} catch( MetadataException e ) {
			e.printStackTrace();
		}
		return accept;
	}

}
