# r properties                   
                   
These properties are directed to R modules.                   
                   
| Property| Description |
| :--- | :--- |
| *r.colorBase* | _string_ <br>base color used for labels & headings in the PDF report; Must be a valid color in R.<br>*default:*  black |
| *r.colorFile* | _file path_ <br>path to a tab-delimited file giving the color to use for each value of each metadata field plotted.<br>*default:*  *null* |
| *r.colorHighlight* | _string_ <br>color is used to highlight significant OTUs in plot<br>*default:*  red |
| *r.colorPalette* | _string_ <br>palette argument passed to [get_palette {ggpubr}](https://www.rdocumentation.org/packages/ggpubr/versions/0.2/topics/get_palette) to select colors for some output visualiztions<br>*default:*  *null* |
| *r.colorPoint* | _string_ <br>default color of scatterplot and strip-chart plot points<br>*default:*  black |
| *r.debug* | _boolean_ <br>Options: Y/N. If Y, will generate R Script log files<br>*default:*  Y |
| *r.excludeFields* | _list_ <br>Fields from the metadata that will be excluded from any auto-determined typing, or plotting; R reports must contain at least one valid nominal or numeric metadata field.<br>*default:*  *null* |
| *r.nominalFields* | _list_ <br>Override default property type by explicitly listing it as nominal.<br>*default:*  *null* |
| *r.numericFields* | _list_ <br>Override default property type by explicitly listing it as numeric.<br>*default:*  *null* |
| *r.pch* | _integer_ <br>Sets R plot pch parameter for PDF report<br>*default:*  21 |
| *r.pvalCutoff* | _numeric_ <br>p-value cutoff used to assign label _r.colorHighlight_<br>*default:*  0.05 |
| *r.rareOtuThreshold* | _numeric_ <br>If >=1, R will filter OTUs found in fewer than this many samples. If <1, R will interperate the value as a percentage and discard OTUs not found in at least that percentage of samples<br>*default:*  1 |
| *r.reportFields* | _list_ <br>Metadata fields to include in reports; Fields listed here must exist in the metadata file. R reports must contain at least one valid field.<br>*default:*  *null* |
| *r.saveRData* | _boolean_ <br>If Y, all R script generating BioModules will save R Session data to the module output directory to a file using the extension ".RData"<br>*default:*  *null* |
| *r.timeout* | _integer_ <br>defines the number of minutes before R script fails due to timeout. If set to 0, an estimate is used.<br>*default:*  0 |
| *r.useUniqueColors* | _boolean_ <br>force to use a unique color for every value in every field plotted; only recommended for low numbers of metadata columns/values.<br>*default:*  *null* |
                   
Several plotting options are available.                   
