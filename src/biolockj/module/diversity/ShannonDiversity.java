package biolockj.module.diversity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import biolockj.Log;
import biolockj.api.ApiModule;
import biolockj.module.BioModule;
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
			
			TaxaLevelTable wrapper = TaxaUtil.readTaxaTable(f);
			
			File outFile = getOutputFile(f);
			
			Log.debug(this.getClass(), "Writting to " +  outFile.getAbsolutePath());
			
			List<String> sampleNames = wrapper.listSamples();
			
			TaxaLevelTable newData = new TaxaLevelTable(wrapper.getLevel());
			for(String name : sampleNames)
			{
				Double val = getShannonEntropy(wrapper, name);
				HashMap<String, Double> cell = new HashMap<>();
				cell.put( "ShannonDiversity", val );
				newData.put(name, cell);
			}
			
			TaxaUtil.writeDataToFile( outFile, newData );
			Log.debug(this.getClass(), "Finished ShannonDiversity module for [" + wrapper.getLevel() + "] level.");
			
		}
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
	
	private double getShannonEntropy(TaxaLevelTable table, String sample) throws Exception
	{
		double sum = 0;

		Collection<Double> innerList = table.get( sample ).values();

		for (Double d : innerList)
			sum += d;

		List<Double> newList = new ArrayList<Double>();

		for (Double d : innerList)
			newList.add(d / sum);

		sum = 0;
		for (Double d : newList)
			if (d > 0)
			{
				sum += d * Math.log(d);

			}

		return -sum;
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

	@Override
	protected String getProcessSuffix() {
		return "shannon";
	}
	
}
