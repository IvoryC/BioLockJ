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

public class ShannonDiversity extends JavaModuleImpl implements ApiModule
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
	
			String[] splits= f.getName().split("_");
			String taxaName = splits[splits.length-1].replace(".tsv", "");

			String newName = "";
			for(int x=0; x < splits.length-1; x++)
				newName = splits[x] + "_";
			
			newName = newName + "Shannon_" + taxaName + ".tsv";
			
			File outFile = new File( getOutputDir() + File.separator+ newName);
			
			Log.debug(this.getClass(), "Trying to write to " +  outFile.getAbsolutePath());
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
			
			writer.write("sample\tShannonDiversity\n");
			
			List<String> sampleNames = wrapper.getSampleNames();
			
			for(String name : sampleNames)
			{
				writer.write(name + "\t" + wrapper.getShannonEntropy(name) + "\n");
			}
			
			writer.flush(); writer.close();
			
			Log.debug(this.getClass(), "Finished ShannonDiversity module");
			
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
		return "Module developed by Anthony Fodor";
	}


	@Override
	public boolean isValidInputModule( final BioModule module ) {
		return module instanceof BuildTaxaTables;
	}
	
}
