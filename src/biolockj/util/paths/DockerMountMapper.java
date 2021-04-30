package biolockj.util.paths;

import java.util.TreeMap;
import biolockj.exception.DockerVolCreationException;

/**
 * Introduced with BioLockJ v1.3.18.
 * @author Ivory Blakley
 *
 */
public interface DockerMountMapper extends PathMapper {
	
	/**
	 * The map at represents the connection of inner path to outer path.
	 * Value is the inner path, the path form that can be used inside the docker container.
	 * Key is the outer path, the path form that represents the path on the host machine.
	 * @return
	 */
	public TreeMap<String, String> getMap() throws DockerVolCreationException;

	/**
	 * Convert path to inner path form.
	 * @param path A path represented as a String, representing the outer path form
	 * @return inner path form
	 */
	public String asInnerPath(String path) throws DockerVolCreationException;
	
	/**
	 * Convert path to outer path form.
	 * @param path A path represented as a String, representing the inner path form
	 * @return outer path form.
	 */
	public String asOuterPath(String path) throws DockerVolCreationException;

	/**
	 * Changes to a mapper class should be accompanied by a increment in version.
	 * The is a string and open form, but using the major.minor.patch semantic versioning is recommended.
	 * @return
	 */
	public String version();
	
	/**
	 * The BioLockJ version that a class was built for and tested with.
	 * @return the current or previous BioLockJ version
	 */
	public String builtForVersion();
}
