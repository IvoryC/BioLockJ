package biolockj.module.dada2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import biolockj.api.API_Exception;
import biolockj.module.report.r.R_Module;
import biolockj.module.report.r.R_package;

public abstract class DADA2_Module extends R_Module {

	@Override
	protected Set<R_package> requiredRPackages() {
		Set<R_package> packages = super.requiredRPackages();
		R_package pack;
		try {
			pack = new R_package( "dada2", null, new URL("https://benjjneb.github.io/dada2/index.html"), true);
		}catch (MalformedURLException mal )
		{
			pack = new R_package( "dada2", null, null, true);
		}
		pack.setMinVersion( "3.10" );
		packages.add( pack );
		return packages;
	}

	@Override
	protected String getModulePrefix() {
		return "dada2";
	}

	@Override
	public String getDockerImageName() {
		return "dada2";
	}

	@Override
	public String getDockerImageOwner() {
		return "golob";
	}

	@Override
	public String getDockerImageTag() {
		return "1.14.1.ub.1804";
	}
	
	@Override
	public String getDetails() throws API_Exception {
		// TODO Auto-generated method stub
		return "See https://benjjneb.github.io/dada2/index.html <br>";
	}
	
}
