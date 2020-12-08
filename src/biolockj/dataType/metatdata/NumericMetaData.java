package biolockj.dataType.metatdata;

import java.util.List;
import org.apache.commons.lang.math.NumberUtils;
import biolockj.exception.MetadataException;
import biolockj.util.MetaUtil;

public class NumericMetaData extends MetaField {

	public NumericMetaData( String name ) {
		super( name );
	}

	/**
	 * @return the description
	 */
	@Override
	public String getDescription() {
		return "A metadata attribute with numeric data.";
	}
	
	public boolean isValid() throws MetadataException {
		if ( !super.isValid() ) return false;
		return verifyVals( MetaUtil.getFieldValues( getName(), true ) );
	}
	
	protected boolean verifyVals(List<String> vals) {
		if (vals.isEmpty()) return false;
		boolean allNumbers = true;
		for (String val : vals) {
			if ( !NumberUtils.isNumber( val ) ) allNumbers = false;
		}
		return allNumbers;
	}
	
	@Override
	protected boolean isAcceptableColumn(String field) {
		boolean accept = false;
		try {
			accept = verifyVals( MetaUtil.getFieldValues( getName(), true ) );
		} catch( MetadataException e ) {
			e.printStackTrace();
		}
		return accept;
	}
	
}
