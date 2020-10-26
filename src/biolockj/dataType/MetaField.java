package biolockj.dataType;
import biolockj.exception.MetadataException;
import biolockj.util.MetaUtil;

public class MetaField implements DataUnit{
	
	public MetaField(String name) {
		this.name = name;
	}
	
	String name;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	};

	/**
	 * @return the description
	 */
	public String getDescription() {
		return "A metadata attribute. One value is associated with each of any number of sample (or other similar unit) in the pipepline.";
	}
	
	@Override
	public boolean isValid() throws MetadataException {
		return isReady() && !MetaUtil.getFieldValues( getName(), false ).isEmpty();
	}

	@Override
	public boolean isReady() {
		return MetaUtil.hasColumn( getName() );
	}
	
}
