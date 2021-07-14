# All Modules
*This is an auto-generated list of all modules with links to auto-generated module documentation.*

[AddMetadataToPathwayTables](biolockj.module.report.humann2/AddMetadataToPathwayTables.md)                   
[AddMetadataToTaxaTables](biolockj.module.report.taxa/AddMetadataToTaxaTables.md) - *Map metadata onto taxa tables using sample ID.*                   
[AddPseudoCount](biolockj.module.report.taxa/AddPseudoCount.md) - *Add a pseudocount (+1) to each value in each taxa table.*                   
[AwkFastaConverter](biolockj.module.seq/AwkFastaConverter.md) - *Convert fastq files into fasta format.*                   
[BuildQiimeMapping](biolockj.module.implicit.qiime/BuildQiimeMapping.md)                   
[BuildTaxaTables](biolockj.module.report.taxa/BuildTaxaTables.md) - *Convert OTU-tables split by sample into taxa tables split by level.*                   
[CompileOtuCounts](biolockj.module.report.otu/CompileOtuCounts.md)                   
[DESeq2](biolockj.module.rnaseq/DESeq2.md) - *Determine statistically significant differences using DESeq2.*                   
[Demultiplexer](biolockj.module.implicit/Demultiplexer.md) - *Demultiplex samples into separate files for each sample.*                   
[EdgeR](biolockj.module.rnaseq/EdgeR.md) - *Determine statistically significant differences using edgeR.*                   
[Email](biolockj.module.report/Email.md) - *Send an email containing the pipeline summary when the pipeline either completes or fails.*                   
[ForEachFile](biolockj.module.diy/ForEachFile.md) - *Like GenMod, but done for each file in a previous module's output dir.*                   
[ForEachLevel](biolockj.module.diy/ForEachLevel.md) - *Like GenMod, but done for each taxonomic level.*                   
[ForEachLoop](biolockj.module.diy/ForEachLoop.md) - *Like GenMod, but done for each string in a comma-separated list.*                   
[ForEachSample](biolockj.module.diy/ForEachSample.md) - *Like GenMod, but done for each sample listed in the metadata.*                   
[GenMod](biolockj.module.diy/GenMod.md) - *Allows user to add their own scripts into the BioLockJ pipeline.*                   
[GenomeAssembly](biolockj.module.assembly/GenomeAssembly.md) - *Assemble WGS sequences with MetaSPAdes, bin contigs with Metabat2 and check quality with checkM.*                   
[Gunzipper](biolockj.module.seq/Gunzipper.md) - *Decompress gzipped files.*                   
[HUMAnN2](biolockj.module.classifier.wgs/Humann2Classifier.md) - *Profile the presence/absence and abundance of microbial pathways in a community from metagenomic or metatranscriptomic sequencing data.*                   
[Hello_Friends](biolockj.module.hello_world/Hello_Friends.md) - *Print the classic phrase: hello world. With some variation.*                   
[Hello_World](biolockj.module.hello_world/Hello_World.md) - *Print the classic phrase: Hello World!*                   
[Humann2Parser](biolockj.module.implicit.parser.wgs/Humann2Parser.md) - *Build OTU tables from HumanN2 classifier module output.*                   
[ImportMetadata](biolockj.module.implicit/ImportMetadata.md) - *Read existing metadata file, or create a default one.*                   
[JsonReport](biolockj.module.report/JsonReport.md)                   
[KneadData](biolockj.module.seq/KneadData.md) - *Run the Biobakery [KneadData](https://bitbucket.org/biobakery/kneaddata/wiki/Home) program to remove contaminated DNA.*                   
[Kraken2Classifier](biolockj.module.classifier.wgs/Kraken2Classifier.md) - *Classify WGS samples with [KRAKEN 2](https://ccb.jhu.edu/software/kraken2/).*                   
[Kraken2Parser](biolockj.module.implicit.parser.wgs/Kraken2Parser.md) - *Build OTU tables from [KRAKEN](http://ccb.jhu.edu/software/kraken/) mpa-format reports.*                   
[KrakenClassifier](biolockj.module.classifier.wgs/KrakenClassifier.md) - *Classify WGS samples with KRAKEN.*                   
[KrakenParser](biolockj.module.implicit.parser.wgs/KrakenParser.md) - *Build OTU tables from [KRAKEN](http://ccb.jhu.edu/software/kraken/) mpa-format reports.*                   
[LogTransformTaxaTables](biolockj.module.report.taxa/LogTransformTaxaTables.md) - *Log-transform the raw taxa counts on Log10 or Log-e scales.*                   
[MergeQiimeOtuTables](biolockj.module.implicit.qiime/MergeQiimeOtuTables.md)                   
[Merge_MetaPhlAn_Tables](biolockj.module.biobakery.metaphlan/Merge_MetaPhlAn_Tables.md) - *Run the merge_metaphlan_tables.py utility from [MetaPhlAn](https://github.com/biobakery/MetaPhlAn).*                   
[MetaPhlAn2](biolockj.module.biobakery.metaphlan/MetaPhlAn2.md) - *Profile the composition of microbial communities using [MetaPhlAn](https://github.com/biobakery/MetaPhlAn).*                   
[MetaPhlAn3](biolockj.module.biobakery.metaphlan/MetaPhlAn3.md) - *Profile the composition of microbial communities using [MetaPhlAn](https://github.com/biobakery/MetaPhlAn).*                   
[MetaPhlAn_DB](biolockj.module.biobakery.metaphlan/MetaPhlAn_DB.md) - *Install the reference database required by [MetaPhlAn](https://github.com/biobakery/MetaPhlAn).*                   
[Metaphlan2Classifier](biolockj.module.classifier.wgs/Metaphlan2Classifier.md) - *Classify WGS samples with [MetaPhlAn2](http://bitbucket.org/biobakery/metaphlan2).*                   
[Metaphlan2Parser](biolockj.module.implicit.parser.wgs/Metaphlan2Parser.md)                   
[Multiplexer](biolockj.module.seq/Multiplexer.md) - *Multiplex samples into a single file, or two files (one with forward reads, one with reverse reads) if multiplexing paired reads.*                   
[NormalizeByReadsPerMillion](biolockj.module.report.taxa/NormalizeByReadsPerMillion.md) - *new counts = counts / (total counts in sample / 1 million)*                   
[NormalizeTaxaTables](biolockj.module.report.taxa/NormalizeTaxaTables.md) - *Normalize taxa tables for sequencing depth.*                   
[PearMergeReads](biolockj.module.seq/PearMergeReads.md) - *Run pear, the Paired-End reAd mergeR*                   
[QiimeClassifier](biolockj.module.implicit.qiime/QiimeClassifier.md)                   
[QiimeClosedRefClassifier](biolockj.module.classifier.r16s/QiimeClosedRefClassifier.md) - *Pick OTUs using a closed reference database and construct an OTU table via the QIIME script pick_closed_reference_otus.py*                   
[QiimeDeNovoClassifier](biolockj.module.classifier.r16s/QiimeDeNovoClassifier.md) - *Run the QIIME pick_de_novo_otus.py script on all fasta sequence files*                   
[QiimeOpenRefClassifier](biolockj.module.classifier.r16s/QiimeOpenRefClassifier.md) - *Run the QIIME pick_open_reference_otus.py script on all fasta sequence files*                   
[QiimeParser](biolockj.module.implicit.parser.r16s/QiimeParser.md)                   
[R_CalculateStats](biolockj.module.report.r/R_CalculateStats.md) - *Generate a summary statistics table with [adjusted and unadjusted] [parameteric and non-parametirc] p-values and r<sup>2</sup> values for each reportable metadata field and each *report.taxonomyLevel* configured.*                   
[R_PlotEffectSize](biolockj.module.report.r/R_PlotEffectSize.md) - *Generate horizontal barplot representing effect size (Cohen's d, r<sup>2</sup>, and/or fold change) for each reportable metadata field and each *report.taxonomyLevel* configured.*                   
[R_PlotMds](biolockj.module.report.r/R_PlotMds.md) - *Generate sets of multidimensional scaling plots showing 2 axes at a time (up to the <*r_PlotMds.numAxis*>th axis) with color coding based on each categorical metadata field (default) or by each field given in *r_PlotMds.reportFields**                   
[R_PlotOtus](biolockj.module.report.r/R_PlotOtus.md) - *Generate OTU-metadata box-plots and scatter-plots for each reportable metadata field and each *report.taxonomyLevel* configured*                   
[R_PlotPvalHistograms](biolockj.module.report.r/R_PlotPvalHistograms.md) - *Generate p-value histograms for each reportable metadata field and each *report.taxonomyLevel* configured*                   
[RarefyOtuCounts](biolockj.module.report.otu/RarefyOtuCounts.md) - *Applies a mean iterative post-OTU classification rarefication algorithm so that each output sample will have approximately the same number of OTUs.*                   
[RarefySeqs](biolockj.module.seq/RarefySeqs.md) - *Randomly sub-sample sequences to reduce all samples to the configured maximum.*                   
[RdpClassifier](biolockj.module.classifier.r16s/RdpClassifier.md) - *Classify 16s samples with [RDP](http://rdp.cme.msu.edu/classifier/classifier.jsp).*                   
[RdpHierParser](biolockj.module.implicit.parser.r16s/RdpHierParser.md) - *Create taxa tables from the _hierarchicalCount.tsv files output by RDP.*                   
[RdpParser](biolockj.module.implicit.parser.r16s/RdpParser.md) - *Build OTU tables from [RDP](http://rdp.cme.msu.edu/classifier/classifier.jsp) reports.*                   
[RegisterNumReads](biolockj.module.implicit/RegisterNumReads.md)                   
[RemoveLowOtuCounts](biolockj.module.report.otu/RemoveLowOtuCounts.md) - *Removes OTUs with counts below report.minCount.*                   
[RemoveLowPathwayCounts](biolockj.module.report.humann2/RemoveLowPathwayCounts.md)                   
[RemoveScarceOtuCounts](biolockj.module.report.otu/RemoveScarceOtuCounts.md)                   
[RemoveScarcePathwayCounts](biolockj.module.report.humann2/RemoveScarcePathwayCounts.md)                   
[Rmarkdown](biolockj.module.diy/Rmarkdown.md) - *Render a custom R markdown.*                   
[SeqFileValidator](biolockj.module.seq/SeqFileValidator.md) - *This BioModule validates fasta/fastq file formats are valid and enforces min/max read lengths.*                   
[ShannonDiversity](biolockj.module.diversity/ShannonDiversity.md) - *Calculate shannon diversity as sum p(logp)*                   
[SraDownload](biolockj.module.getData.sra/SraDownload.md) - *SraDownload downloads and compresses short read archive (SRA) files to fastq.gz*                   
[SraMetaDB](biolockj.module.getData.sra/SraMetaDB.md) - *Makes sure that the SRAmetadb exists, downloads if it does not already exist.*                   
[SraMetaData](biolockj.module.getData.sra/SraMetaData.md) - *Extract metadata via pysradb from local copy of SRAmetadb.sqlite.*                   
[SrpSrrConverter](biolockj.module.getData.sra/SrpSrrConverter.md) - *Create an SraAccList.txt file from an SRA project identifier.*                   
[Step5](biolockj.module.hello_world/Step5.md) - *Print the classic phrase: hello world.*                   
[Stop](biolockj.module/Stop.md) - *Stop a pipeline.*                   
[TrimPrimers](biolockj.module.seq/TrimPrimers.md) - *Remove primers from reads, option to discard reads unless primers are attached to both forward and reverse reads.*                   
