package biolockj.dataType.metatdata;

import java.util.List;
import org.apache.commons.lang.math.NumberUtils;

/**
 * An extension of {@link NumericMetaData} that additionally requires that the column include only count data, that is,
 * all values are integers >=0.
 * 
 * @author Ivory Blakley
 *
 */
public class CountField extends NumericMetaData {

	public CountField( String name ) {
		super( name );
	}

	@Override
	public String getDescription() {
		return "A metadata attribute with counts data. All non-null values are integers >= 0.";
	}
	
	@Override
	protected boolean verifyVals(List<String> vals) {
		if (vals.isEmpty()) return false;
		boolean allNumbers = true;
		for (String val : vals) {
			try {
				allNumbers = allNumbers && NumberUtils.createInteger( val ) >= 0;
			}catch(NumberFormatException nfe) {
				allNumbers = false;
				break;
			}
			if (!allNumbers) break;
		}
		return allNumbers;
	}

}
