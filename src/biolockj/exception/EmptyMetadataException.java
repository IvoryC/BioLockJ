package biolockj.exception;

public class EmptyMetadataException extends MetadataException {

	public EmptyMetadataException( String msg ) {
		super( message + System.lineSeparator() + msg);
	}
	
	public EmptyMetadataException() {
		super( message );
	}
	
	private static final String message = "The metadata table does not contain any samples." 
					+ System.lineSeparator() + "This can happen if the metadata is not initiallized correctly, or if all samples are filtered out of the pipeline.";

	private static final long serialVersionUID = 1L;
}
