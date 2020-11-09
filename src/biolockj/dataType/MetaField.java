package biolockj.dataType;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import biolockj.exception.BioLockJException;
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
	public void setFiles( List<File> files ) {}

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
	public DataUnitFactory<?> getFactory() {
		return new DataUnitFactory<MetaField>() {

			@Override
			public Collection<MetaField> getActualData( List<File> files ) throws BioLockJException {
				List<MetaField> list = new ArrayList<MetaField>();
				list.add( MetaField.this );
				return list;
			}};
	}
	
}
