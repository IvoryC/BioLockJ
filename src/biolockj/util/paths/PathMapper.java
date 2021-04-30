package biolockj.util.paths;

import java.util.Map;
import biolockj.exception.BioLockJException;

public interface PathMapper {
		
	/**
	 * The map at represents the connection of inner path to outer path.
	 * Value is the inner path.
	 * Key is the outer path.
	 * @return
	 */
	public Map<String, String> getMap() throws BioLockJException;

	/**
	 * Convert path to inner path form.
	 * @param path A path represented as a String, representing the outer path form
	 * @param mustConvert should failure to convert the path result in an exception; 
	 * if false, the path returned may be identical to the input path
	 * @return inner path form
	 */
	public String asInnerPath(String path) throws BioLockJException;
	
	/**
	 * Convert path to outer path form.
	 * @param path A path represented as a String, representing the inner path form
	 * @param mustConvert should failure to convert the path result in an exception; 
	 * if false, the path returned may be identical to the input path
	 * @return outer path form.
	 */
	public String asOuterPath(String path) throws BioLockJException;
	
}
