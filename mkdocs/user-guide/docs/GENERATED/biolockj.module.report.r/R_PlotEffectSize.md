# R_PlotEffectSize
Add to module run order:                    
`#BioModule biolockj.module.report.r.R_PlotEffectSize`

## Description 
Generate horizontal barplot representing effect size (Cohen's d, r<sup>2</sup>, and/or fold change) for each reportable metadata field and each *report.taxonomyLevel* configured.

## Properties 
*Properties are the `name=value` pairs in the [configuration](../../../Configuration#properties) file.*                   

### R_PlotEffectSize properties: 
| Property| Description |
| :--- | :--- |
| *r_PlotEffectSize.disableCohensD* | _boolean_ <br>Options: Y/N. If N (default), produce plots for binary attributes showing effect size calculated as Cohen's d. If Y, skip this plot type.<br>*default:*  *null* |
| *r_PlotEffectSize.disableFoldChange* | _boolean_ <br>Options: Y/N. If N (default), produce plots for binary attributes showing the fold change. If Y, skip this plot type.<br>*default:*  Y |
| *r_PlotEffectSize.disablePvalAdj* | _boolean_ <br>Options: Y/N. If Y, the non-adjusted p-value is used when determining which taxa to include in the plot and which should get a (*). If N (default), the adjusted p-value is used.<br>*default:*  *null* |
| *r_PlotEffectSize.disableRSquared* | _boolean_ <br>Options: Y/N. If N (default), produce plots showing effect size calculated as the r-squared value. If Y, skip this plot type.<br>*default:*  *null* |
| *r_PlotEffectSize.excludePvalAbove* | _numeric_ <br>Options: [0,1], Taxa with a p-value above this value are excluded from the plot.<br>*default:*  1 |
| *r_PlotEffectSize.maxNumTaxa* | _integer_ <br>Each plot is given one page. This is the maximum number of bars to include in each one-page plot.<br>*default:*  40 |
| *r_PlotEffectSize.parametricPval* | _boolean_ <br>Options: Y/N. If Y, the parametric p-value is used when determining which taxa to include in the plot and which should get a (*). If N (default), the non-parametric p-value is used.<br>*default:*  *null* |
| *r_PlotEffectSize.taxa* | _list_ <br>Override other criteria for selecting which taxa to include in the plot by specifying wich taxa should be included<br>*default:*  *null* |

### General properties applicable to this module: 
| Property| Description |
| :--- | :--- |
| *cluster.batchCommand* | _string_ <br>Terminal command used to submit jobs on the cluster<br>*default:*  *null* |
| *cluster.jobHeader* | _string_ <br>Header written at top of worker scripts<br>*default:*  *null* |
| *cluster.modules* | _list_ <br>List of cluster modules to load at start of worker scripts<br>*default:*  *null* |
| *cluster.prologue* | _string_ <br>To run at the start of every script after loading cluster modules (if any)<br>*default:*  *null* |
| *cluster.statusCommand* | _string_ <br>Terminal command used to check the status of jobs on the cluster<br>*default:*  *null* |
| *docker.saveContainerOnExit* | _boolean_ <br>If Y, docker run command will NOT include the --rm flag<br>*default:*  *null* |
| *docker.verifyImage* | _boolean_ <br>In check dependencies, run a test to verify the docker image.<br>*default:*  *null* |
| *exe.Rscript* | _executable_ <br>Path for the "Rscript" executable; if not supplied, any script that needs the Rscript command will assume it is on the PATH.<br>*default:*  *null* |
| *pipeline.defaultStatsModule* | _string_ <br>Java class name for default module used generate p-value and other stats<br>*default:*  biolockj.module.report.r.R_CalculateStats |
| *r.colorFile* | _file path_ <br>path to a tab-delimited file giving the color to use for each value of each metadata field plotted.<br>*default:*  *null* |
| *r.debug* | _boolean_ <br>Options: Y/N. If Y, will generate R Script log files<br>*default:*  Y |
| *r.saveRData* | _boolean_ <br>If Y, all R script generating BioModules will save R Session data to the module output directory to a file using the extension ".RData"<br>*default:*  *null* |
| *r.timeout* | _integer_ <br>the # minutes before R Script will time out and fail; If undefined, no timeout is used.<br>*default:*  10 |
| *script.defaultHeader* | _string_ <br>Store default script header for MAIN script and locally run WORKER scripts.<br>*default:*  #!/bin/bash |
| *script.numThreads* | _integer_ <br>Used to reserve cluster resources and passed to any external application call that accepts a numThreads parameter.<br>*default:*  8 |
| *script.numWorkers* | _integer_ <br>Set number of samples to process per script (if parallel processing)<br>*default:*  1 |
| *script.permissions* | _string_ <br>Used as chmod permission parameter (ex: 774)<br>*default:*  770 |
| *script.timeout* | _integer_ <br>Sets # of minutes before worker scripts times out.<br>*default:*  *null* |

## Details 
*none*

## Adds modules 
**pre-requisite modules**                    
*pipeline-dependent*                   
**post-requisite modules**                    
*none found*                   

## Docker 
If running in docker, this module will run in a docker container from this image:<br>
```
biolockjdevteam/r_module:v1.3.9
```
This can be modified using the following properties:<br>
`R_PlotEffectSize.imageOwner`<br>
`R_PlotEffectSize.imageName`<br>
`R_PlotEffectSize.imageTag`<br>

## Citation 
BioLockJ v1.3.9                   
Module developted by Ivory Blakley.

