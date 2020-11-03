package biolockj.dataType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import biolockj.exception.BioLockJException;
import biolockj.exception.MetadataException;
import biolockj.util.MetaUtil;

public class MetaField<T extends MetaField<?>> implements DataUnit<T>{
	
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

	@Override
	public boolean isIterable() {
		return false;
	}

	@Override
	public void setIterable( boolean iterable ) {	}

	/**
	 * A named column is always exactly 1.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iterable<T> iterate() throws BioLockJException {
		List<T> list = new ArrayList<>();
		list.add( (T) this );
		return list;
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
	
}
