# Metadata                   
                   
Any information that is given on a per-sample basis is metadata.                   
                   
BioLockJ pipelines do not separate biological information from technical information.                   
                   
| Property| Description |
| :--- | :--- |
| *metadata.barcodeColumn* | _string_ <br>metadata column with identifying barcodes<br>*default:*  BarcodeSequence |
| *metadata.columnDelim* | _string_ <br>defines how metadata columns are separated; Typically files are tab or comma separated.<br>*default:*  \t |
| *metadata.commentChar* | _string_ <br>metadata file comment indicator; Empty string is a valid option indicating no comments in metadata file.<br>*default:*  *null* |
| *metadata.fileNameColumn* | _list_ <br>name of the metadata column(s) with input file names<br>*default:*  *null* |
| *metadata.filePath* | _string_ <br>If absolute file path, use file as metadata.<br>If directory path, must find exactly 1 file within, to use as metadata.<br>*default:*  *null* |
| *metadata.nullValue* | _string_ <br>metadata cells with this value will be treated as empty<br>*default:*  NA |
| *metadata.required* | _boolean_ <br>If Y, require metadata row for each sample with sequence data in input dirs; If N, samples without metadata are ignored.<br>*default:*  N |
| *metadata.useEveryRow* | _boolean_ <br>If Y, require a sequence file for every SampleID (every row) in metadata file; If N, metadata can include extraneous SampleIDs.<br>*default:*  *null* |
                   
If no metadata table is supplied to the pipeline, then the ImportMetaData module (which is implicitly added to all pipelines) will look at the input samples and create an empty metadata file.                   
                   
