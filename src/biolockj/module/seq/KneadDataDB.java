package biolockj.module.seq;

import biolockj.dataType.seq.Bowtie2ReferenceGenome;

/**
 * A database object for the KneadData program.
 * https://github.com/biobakery/kneaddata
 * 
 * The documentation talks about downloading pre-made reference databases, and options for making custom ones.
 * It sounds like a kneaddata reference database is simply an indexed bowtie2 reference genome.
 * However it also, says that if you make your own you may need to convert U > T before indexing.
 * 
 * So, while "KneadDataDB" is not perfectly synonymous with "Bowtie2 reference genome", its sounds like that's essentially what it is.
 * Future versions of the {@link KneadDataDB} and {@link Bowtie2ReferenceGenome} classes may distinctions as they become apparent.
 * 
 * @author Ivory Blakley
 *
 */
public class KneadDataDB extends Bowtie2ReferenceGenome {

}
