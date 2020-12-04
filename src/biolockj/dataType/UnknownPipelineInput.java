package biolockj.dataType;

/**
 * This class makes absolutely no assumptions about file content or format.
 * The file just has to exist, and we assume 1 file IS one data unit.
 * @author Ivory Blakley
 *
 */
public class UnknownPipelineInput extends BasicDataUnit {

	@Override
	public String getDescription() {
		return "Pipeline input. An input that is not an output of a module in the current pipeline.";
	}
	
}
