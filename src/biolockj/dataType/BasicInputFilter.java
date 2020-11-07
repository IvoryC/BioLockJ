package biolockj.dataType;

public class BasicInputFilter implements DataUnitFilter {

	public BasicInputFilter(Class<? extends DataUnit> clazz) {
		this.clazz = clazz;
	}
	
	private Class<? extends DataUnit> clazz; 

	@Override
	public boolean accept( DataUnit data ) {
		return clazz.isInstance( data );
	}

}
