package biolockj.dataType.seq;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

public class FastqFormat implements SequenceFormat {

	SequenceAlphabet qualScoreEncoding;
	protected String[] ends = {"fq", "fastq"};
	
	public FastqFormat(SequenceAlphabet qualScoreEncoding) {
		this.qualScoreEncoding = qualScoreEncoding;
	}
	
	public FastqFormat(String encoding) {
		this.qualScoreEncoding = commonEncodings.get( encoding );
	}
	
	@Override
	public FilenameFilter getFilenameFilter() {
		return new FilenameFilter() {
			
			@Override
			public boolean accept( File dir, String name ) {
				boolean accept = false;
				for (String ending : ends) {
					if (name.endsWith( ending ) ) accept = true;
				}
				return accept;
			}
			
		};
	}
	
	public SequenceAlphabet getQualScoreEncoding() {
		return qualScoreEncoding;
	}
	
	@SuppressWarnings("serial")
	static Map<String, SequenceAlphabet> commonEncodings = new HashMap<String, SequenceAlphabet>() {{
		put("Sanger", Sanger);
		put("Solexa", Solexa);
		put("Illumina", Illumina1_3);
	}};
	
	
	public static final SequenceAlphabet Sanger = new SequenceAlphabet() {
		@Override
		public char[] getCharacters() {
			//ASCII 33 to 126
			char[] chars = new char[93];
			for ( int i = 33; i < 127; i++ ) {
				chars[i] = (char) i;
			}
			return chars;
		}
	};
	
	public static final SequenceAlphabet Solexa = new SequenceAlphabet() {
		@Override
		public char[] getCharacters() {
			//ASCII 59 to 126
			char[] chars = new char[93];
			for ( int i = 59; i < 127; i++ ) {
				chars[i] = (char) i;
			}
			return chars;
		}
	};
	
	public static final SequenceAlphabet Illumina1_3 = new SequenceAlphabet() {
		@Override
		public char[] getCharacters() {
			//ASCII 64 to 126
			char[] chars = new char[93];
			for ( int i = 64; i < 127; i++ ) {
				chars[i] = (char) i;
			}
			return chars;
		}
	};

}
