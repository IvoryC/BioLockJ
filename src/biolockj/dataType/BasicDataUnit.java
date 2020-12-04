package biolockj.dataType;

/**
 * Basic implementation to allow a a single class to implement both DataUnit and DataUnitFactory.
 * This is often more concise than implementing each part as a separate object.
 * @author Ivory Blakley
 * @param <T>
 *
 * @param <T>
 */
public abstract class BasicDataUnit extends DataUnitImpl implements DataUnitFactory {

	@Override
	public DataUnitFactory getFactory() {
		return this;
	}
	
}
