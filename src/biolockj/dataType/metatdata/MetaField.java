package biolockj.dataType.metatdata;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import biolockj.Log;
import biolockj.dataType.BasicDataUnit;
import biolockj.dataType.DataUnit;
import biolockj.exception.MetadataException;
import biolockj.exception.ModuleInputException;
import biolockj.util.MetaUtil;

public class MetaField extends BasicDataUnit{
	
	public MetaField(String name) {
		this.name = name;
		canBeMultiple(false);
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
		return "A metadata attribute \"" + name + "\". One value is associated with each of any number of samples (or other similar unit) in the pipepline.";
	}
	
	@Override
	public boolean isValid() throws MetadataException {
		return isReady() && !MetaUtil.getFieldValues( getName(), false ).isEmpty();
	}

	@Override
	public boolean isReady() {
		return MetaUtil.hasColumn( getName() );
	}

	/**
	 * Always uses the current metadata file
	 */
	@Override
	public void setFiles( List<File> files ) {
		Log.warn(this.getClass(), "The file associated with a metaField object is always the metadata file.");
	}

	@Override
	public List<File> getFiles() throws MetadataException {
		if (isReady()) {
			List<File> files = new ArrayList<>();
			files.add( MetaUtil.getMetadata() );
			return files;
		}
		return null;
	}
	
	@Override
	public List<DataUnit> getData( List<File> files, DataUnit template, boolean useAllFiles )
		throws ModuleInputException {
		List<DataUnit> data = new ArrayList<>();
		for (String field : MetaUtil.getFieldNames() ) {
			if (isAcceptableColumn(field)) {
				data.add( new MetaField(field) );
			}
		}
		return data;
	}
	
	/**
	 * Designed for child classes that have restrictions.
	 * @return
	 */
	protected boolean isAcceptableColumn(String field) {
		if ( ! canBeMultiple() && !getName().equals( field ) ) return false;
		return true;
	}

}
