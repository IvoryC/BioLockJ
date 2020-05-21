package biolockj.module.diversity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import biolockj.Log;
import biolockj.api.ApiModule;
import biolockj.legacy.utils.OtuWrapper;
import biolockj.module.BioModule;
import biolockj.module.JavaModuleImpl;
import biolockj.module.report.taxa.BuildTaxaTables;
import biolockj.module.report.taxa.TaxaLevelTable;
import biolockj.module.report.taxa.TransformTaxaTables;
import biolockj.util.BioLockJUtil;
import biolockj.util.TaxaUtil;

public class ShannonDiversity extends TransformTaxaTables implements ApiModule
{

	@Override
	public void runModule() throws Exception
	{
		Log.debug(this.getClass(),"In ShannonDiversity");
		
		List<File> inputFiles = getInputFiles();
		
		for(File f : inputFiles)
		{
			Log.debug( this.getClass(), "Opening " +  f.getAbsolutePath());
			
			OtuWrapper wrapper = new OtuWrapper(f);
			
			File outFile = getOutputFile(f);
			
			Log.debug(this.getClass(), "Writting to " +  outFile.getAbsolutePath());
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
			
			writer.write("sample\tShannonDiversity\n");
			
			List<String> sampleNames = wrapper.getSampleNames();
			
			for(String name : sampleNames)
			{
				writer.write(name + "\t" + wrapper.getShannonEntropy(name) + "\n");
			}
			
			writer.flush(); writer.close();
			
			Log.debug(this.getClass(), "Finished ShannonDiversity module for file: " + f);
			
		}
	}

	@Override
	public String getDescription()
	{
		return "Calculate shannon diversity as sum p(logp)";
	}

	@Override
	public String getCitationString()
	{	
		return "Module developed by Anthony Fodor" + System.lineSeparator() 
			+ "BioLockJ " + BioLockJUtil.getVersion();
	}


	@Override
	public boolean isValidInputModule( final BioModule module ) {
		return module instanceof BuildTaxaTables;
	}

	/*
	 * ignored for now.  This class overrides the TransformTaxaTable runModule method
	 */
	@Override
	protected TaxaLevelTable transform( TaxaLevelTable inputData, List<String> filteredSampleIDs,
		List<String> filteredTaxaIDs ) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getProcessSuffix() {
		return "shannon";
	}
	
}
