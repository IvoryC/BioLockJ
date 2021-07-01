package biolockj.module.biobakery.metaphlan;

import java.util.Arrays;
import java.util.List;

public class M3Params extends M2Params {
	
	//Flags and argument names for metaphlan 3
	static final String FORCE = "--force";

	public M3Params() {}
	
	@Override
	public List<String> getFlagArgList(){
		return Arrays.asList( FORCE, NO_MAP, IGNORE_VIRUSES, IGNORE_EUKARYOTES, IGNORE_BACTERIA,
			IGNORE_ARCHEA, AVOID_DISQM, INSTALL, VERSION_ARG, VERSION_ARG_LONG, HELP_ARG, HELP_ARG_LONG );
	}
	
	public void check_param_names() throws UnrecognizedMetaphlanParameter {
		for (String name : map.keySet()) {
			if ( Arrays.asList( getNamedArgsList() ).contains( name ) && map.get(name) == null ) {
				throw new UnrecognizedMetaphlanParameter("The parameter [" + name + "] to metaphlan3 takes a value.");
			}
			if ( getFlagArgList().contains( name ) && map.get(name) != null ) {
				throw new UnrecognizedMetaphlanParameter("The parameter [" + name 
					+ "] to metaphlan3 does not take a value take a value; found value [" + map.get( name ) + "].");
			}
			if ( !getNamedArgsList().contains( name ) && !getFlagArgList().contains( name ) ) {
				throw new UnrecognizedMetaphlanParameter("The parameter [" + name 
					+ "] is not a known parameter for metaphlan3.");
			}
		}
	}
	
	public void check_halt_params() throws RejectedMetaphlanParameter {
		for (String name : getHaltArgList() ) {
			if (map.containsKey( name )) {
				throw new RejectedMetaphlanParameter("The parameter [" + name + "] will prevent metaphlan3 from running.");
			}
		}
	}
	

}
