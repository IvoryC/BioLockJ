package biolockj.util.paths;

import biolockj.Log;
import biolockj.exception.DockerVolCreationException;
import biolockj.util.DockerUtil;

/**
 * Introduced with BioLockJ v1.3.18. Unlike the CommonDockerMapper made at the same time, this Mapper does not make any
 * special considerations for ":".  This simplification should be completely fine on unix-like systems.
 * 
 * @author Ivory Blakley
 *
 */
public class SimpleDockerMapper extends BasicDockerMapper {

	public SimpleDockerMapper() throws DockerVolCreationException {
		super();
	}
	
	//TODO: increment appropriately when changes are made.
	@Override
	public String version() {
		return "1.0.0";
	}
	
	//TODO: increment appropriately when changes are made.
	@Override
	public String builtForVersion() {
		return "v1.3.18";
	}
	
	@Override
	protected String formatHostPath(final String path) {
		final String HOST_MNT = "/host_mnt/";
		String hostPath;
		if (path.startsWith( HOST_MNT )) {
			hostPath = path.replaceFirst( HOST_MNT, "/" );
		}
		else hostPath = path ;
		Log.info(DockerUtil.class, "Host path from docker mounts: " + path);
		Log.info(DockerUtil.class, "Is represented in the form:   " + hostPath);
		return hostPath;
	}

}
