package biolockj.module.biobakery.metaphlan;

public class MetaPhlAn3 extends MetaPhlAn2 {

	public MetaPhlAn3() {
	}
	
	@Override
	public String getDockerImageName() {
		return "metaphlan";
	}

	@Override
	public String getDockerImageTag() {
		return "3.0.7";
	}

}
