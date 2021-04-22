package biolockj.util.paths;

import java.io.File;
import org.apache.commons.io.FilenameUtils;
import biolockj.Log;
import biolockj.exception.DockerVolCreationException;
import biolockj.util.DockerUtil;

/**
 * Introduced with BioLockJ v1.3.18. This represents the logic that had been used before version v1.3.18, but it is now
 * represented as a separate class implementing the DockerMountMapper interface.
 * 
 * @author Ivory Blakley
 *
 */
public class CommonDockerMapper extends BasicDockerMapper {

	public CommonDockerMapper() throws DockerVolCreationException {
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
	
	/**
	 * In the docker json file, the host path is sometimes represented as the exact path that the user might see on
	 * their host machine, and sometimes it is pre-prended with /host_mnt/, and other other differences. Presumably the
	 * version of docker is what dictates the difference in path appearance.
	 * 
	 * @param path
	 * @return
	 */
	@Override
	protected String formatHostPath(final String path) {
		final String HOST_MNT = "/host_mnt/";
		String hostPath;
		if (path.startsWith( HOST_MNT )) {
			if ( path.contains( ":" )) { // windows case
				hostPath = path.replaceFirst( HOST_MNT, "" );
			}else { // mac case
				hostPath = path.replaceFirst( HOST_MNT, "/" );
			}
		}
		else hostPath = path ;
		if ( path.contains( ":" ) ) {
			// added conversion for compatibility with older docker map format
			hostPath = convertWindowsPath(hostPath);
		}
		Log.info(DockerUtil.class, "Host path from docker mounts: " + path);
		Log.info(DockerUtil.class, "Is represented in the form:   " + hostPath);
		return hostPath;
	}
	
	protected String formatConfigPath(final String path) {
		if (isWindowsHostPath(path)) return convertWindowsPath(path);
		return path;
	}
	
	protected String convertWindowsPath(final String path) {
		String drive = path.substring( 0, path.indexOf( ":" ) ).toLowerCase();
		String dirPath = path.substring( path.indexOf( ":" ) + 1 );
		String mountPath = File.separator + drive + FilenameUtils.separatorsToSystem( dirPath );
		Log.debug( DockerUtil.class, "Converted Windows path [" + path + "] to docker mount form: " + mountPath );
		return mountPath ;
	}
	
	protected boolean isWindowsHostPath(final String path) {
		return (path.contains( ":" ) 
						&& path.indexOf( ":" ) == path.lastIndexOf( ":" )
						&& FilenameUtils.separatorsToWindows(path).equals(path));
	}
	

}
