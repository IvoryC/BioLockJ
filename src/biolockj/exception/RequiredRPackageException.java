package biolockj.exception;

import java.util.Set;
import biolockj.module.report.r.R_package;

public class RequiredRPackageException extends BioLockJException {

	/**
	 * Create exception based on which R package is missing.
	 * @param rpackage name of an R package that is required but cannot be loaded
	 */
	public RequiredRPackageException( R_package rpackage, String message ) {
		super( missingPackageMessage(rpackage) + System.lineSeparator() + message);
	}
	
	/**
	 * Create exception based on which R packages are missing, and the URLs of repos to download them from.
	 * @param names Set of Strings given one or more names of packages that are required but cannot be loaded
	 * @param repos Map of package names to url where the package could be installed from.
	 */
	public RequiredRPackageException( Set<R_package> rpackages) {
		super( missingMultipleMessage( rpackages ));
	}
	
	private static String missingPackageMessage(R_package rpackage) {
		return "Missing R library [" + rpackage.getName() + "]. Please install this package.  Suggested install command: " + System.lineSeparator() 
		+ rpackage.getInstallCmd(); 
	}
	
	private static String missingMultipleMessage(Set<R_package> rpackages){
		StringBuffer sb = new StringBuffer();
		for (R_package pack : rpackages) {
			sb.append( missingPackageMessage( pack) + System.lineSeparator() );
		}
		return sb.toString();
	}
	
	private static final long serialVersionUID = -8321547735003700687L;

}
