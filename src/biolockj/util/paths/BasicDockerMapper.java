package biolockj.util.paths;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONObject;
import biolockj.BioLockJ;
import biolockj.Log;
import biolockj.exception.DockerVolCreationException;
import biolockj.util.DockerUtil;
import biolockj.util.RuntimeParamUtil;

/**
 * Introduced with BioLockJ v1.3.18.
 * @author Ivory Blakley
 *
 */
public abstract class BasicDockerMapper implements DockerMountMapper {
	
	private TreeMap<String, String> volumeMap;

	public BasicDockerMapper() throws DockerVolCreationException {
		Log.debug( this.getClass(), "This docker mapper, version [" + this.version() + "] of [" + this.getClass().getName() +
			"], was designed with BioLockJ version: " + this.builtForVersion() );
		makeVolMap();
	}
	
	/**
	 * This map is a link between file paths inside the container (the containerized path) and paths outside the
	 * container (the decontainerized path). This program and all scripts are (presumably) running inside the docker
	 * container, and need to use the containerized path. The user, and thus the logs and the config file(s), are
	 * outside of the docker container, and need to use the decontainerized path.
	 * 
	 * In this map is built using the Mounts map from docker.
	 * 
	 * Each value in this map is the containerized path; which is the destination (or "target") in the docker mounts
	 * map.
	 * 
	 * Each key in this map is a representation of the original host file path; however the form of this path varies on
	 * different host systems (mac vs windows) and different versions of docker. The representation of a unix-like path
	 * is exactly the format that is produce by unix commands such as pwd. The path begins with "/" and uses "/"
	 * separators. The representation of windows file paths varies between docker versions. The representation in this
	 * map is:<br> <lower case drive>/<path using '\' separators>
	 * 
	 * @throws DockerVolCreationException
	 */
	protected void makeVolMap() throws DockerVolCreationException {
		StringBuilder sb = new StringBuilder();
		String s = null;
		try {
			Process p = Runtime.getRuntime().exec( getDockerInfoCmd() );
			final BufferedReader br = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
			while( ( s = br.readLine() ) != null ) {
				sb.append( s );
			}
			p.waitFor();
			p.destroy();
		} catch( IOException | InterruptedException e ) {
			e.printStackTrace();
			throw new DockerVolCreationException( e );
		}
		String json = sb.toString();
		JSONArray fullArr = new JSONArray( json );
		JSONObject obj = fullArr.getJSONObject( 0 );
		if( !obj.has( "Mounts" ) ) throw new DockerVolCreationException();
		JSONArray arr = obj.getJSONArray( "Mounts" );
		volumeMap = new TreeMap<>();
		for( int i = 0; i < arr.length(); i++ ) {
			JSONObject mount = arr.getJSONObject( i );
			String source = formatHostPath(mount.get( "Source" ).toString());
			String destination = formatInnerPath(mount.get( "Destination" ).toString());
			volumeMap.put( source, destination );
			Log.info( DockerUtil.class, "Host directory: " + source );
			Log.info( DockerUtil.class, "is mapped to container directory: " + destination );
		}
		Log.info( DockerUtil.class, volumeMap.toString() );
	}
	
	/**
	 * Given a path as it appears in the Docker inspect Mount host info, convert the path to a form suitable for the map as a key.
	 * @param path A path taken from the host part of a Mount entry in the docker info.
	 * @return
	 */
	protected String formatHostPath(final String path) {
		return path;
	}
	
	/**
	 * Given a path as it appears in the target portion of a Mount entry, convert the path to a form suitable for the map a value. 
	 * @param path A path taken from the target/destination part of a Mount entry in the docker info.
	 * @return
	 */
	protected String formatInnerPath(final String path) {
		return path;
	}
	
	/**
	 * Give a path as it appears in the config file, convert it to a form suitable for matching to map keys.
	 * @param path A path taken from the config file. Relative paths should already be converted to absolute paths.
	 * @return
	 */
	protected String formatConfigPath(final String path) {
		return path;
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getDockerInfoCmd() throws IOException, DockerVolCreationException {
		// return "curl --unix-socket /var/run/docker.sock http:/v1.38/containers/" + getHostName() + "/json";
		return "docker inspect " + DockerUtil.getContainerId();
	}

	@Override
	public TreeMap<String, String> getMap() {
		return volumeMap;
	}

	@Override
	public String asInnerPath( String path ) throws DockerVolCreationException {
		Log.debug( DockerUtil.class, "Containerizing path: " + path );
		if( !DockerUtil.inDockerEnv() ) return path;
		if( path == null || path.isEmpty() ) return null;
		
		String hostPath = formatConfigPath(path);
		
		String pipelineKey = null;
		for( String key: volumeMap.keySet() ) {
			if( volumeMap.get( key ).equals( DockerUtil.DOCKER_PIPELINE_DIR ) ) pipelineKey = key;
			if( DockerUtil.inAwsEnv() && volumeMap.get( key ).equals( DockerUtil.DOCKER_BLJ_MOUNT_DIR ) ) pipelineKey = key;
			// if the config file is null; we must not be running a pipeline; this is the API calling, and all mounts are under the workspace getting set up.
			if( RuntimeParamUtil.getConfigFile(false) == null && volumeMap.get( key ).equals( DockerUtil.PURE_DOCKER_PIPELINE_DIR ) ) pipelineKey = key;
		}
		if( pipelineKey == null && BioLockJ.getPipelineDir() != null ) {
			throw new DockerVolCreationException( "no pipeline dir !" );
		}
		if( pipelineKey != null && isParentDir(pipelineKey, hostPath) )
			return hostPath.replaceFirst( pipelineKey, volumeMap.get( pipelineKey ) );
		
		String innerPath = path;
		String bestMatch = null;
		int bestMatchLen = 0;
		for( String s: volumeMap.keySet() ) {
			if( isParentDir(s, hostPath) && s.length() > bestMatchLen ) {
				bestMatch = String.valueOf( s );
				bestMatchLen = s.length();
			}
		}
		if( bestMatch != null ) {
			innerPath = hostPath.replaceFirst( bestMatch, volumeMap.get( bestMatch ) );
		}
		Log.debug( DockerUtil.class, "Containerized path to: " + innerPath );
		return innerPath;
	}
	
	protected boolean isParentDir(String parent, String child){
		if( child.equals( parent ) ) {
			return true;
		}
		if( child.startsWith( parent ) 
						&& child.length() > parent.length() 
						&& child.charAt( parent.length() )==File.separatorChar) {
			return true;
		}
		return false;
	}

	@Override
	public String asOuterPath( String innerPath) throws DockerVolCreationException {
		String hostPath = innerPath;
		if( DockerUtil.inDockerEnv() ) {
			for( String s: volumeMap.keySet() ) {
				if (isParentDir(volumeMap.get( s ), innerPath)) {
					hostPath = hostPath.replaceFirst( volumeMap.get( s ), s );
					break;
				}
			}
		}
		return hostPath;
	}

}
