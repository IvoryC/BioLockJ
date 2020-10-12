package biolockj.module.report.r;

import java.net.URL;

public class R_package {
	
	public R_package(String name, URL webpage) {
		this(name, null, webpage);
	}
	
	public R_package(String name, String repo) {
		this(name, repo, null);
	}
	
	public R_package(String name, String repo, URL webpage) {
		this(name, repo, null, false);
	}

	public R_package(String name, String repo, URL webpage, boolean useBiocManagerInstall) {
		this.name = name;
		this.repository = repo;
		this.webpage = webpage;
		this.useBiocManager = useBiocManagerInstall;
	}

	/*
	 * The package name.
	 */
	private String name;
	
	/*
	 * Repository to use to download the package using the install.packages command.
	 */
	private String repository;
	
	/*
	 * URL or similar, giving more information about the package.
	 */
	private URL webpage;
	
	private String minVersion = null;
	
	boolean useBiocManager = false;
	
	String installCmd = null;
	
	public void setMinVersion(String minVersion) {
		this.minVersion = minVersion;
	}
	public String getMinVersion(String minVersion) {
		return minVersion;
	}

	public String getInstallCmd() {
		if (installCmd != null ) return installCmd;
		return makePackageInstallCmd();
	}

	public void setInstallCmd( String installCmd ) {
		this.installCmd = installCmd;
	}

	public String getMinVersion() {
		return minVersion;
	}
	
	public String getName() {
		return name;
	}

	public URL getWebpage() {
		return webpage;
	}
	
	private String makePackageInstallCmd() {
		if ( useBiocManager ) {
			String versionArg = minVersion == null ? "" : ", version = " + minVersion;
			installCmd = "if (!requireNamespace(\"BiocManager\", quietly = TRUE)){install.packages(\"BiocManager\")};" + System.lineSeparator() +
							"BiocManager::install(\"" + name +  versionArg + "\")";
		} else if ( repository==null || repository.isEmpty() ) installCmd = "Rscript -e 'install.packages(\"" + name + "\"); '";   
		else installCmd = "Rscript -e 'install.packages(\"" + name + "\", repos=\""+ repository + "\"); '";
		return installCmd; 
	}
	
}
