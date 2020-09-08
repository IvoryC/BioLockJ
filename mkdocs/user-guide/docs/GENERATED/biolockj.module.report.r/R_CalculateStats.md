# R_CalculateStats
Add to module run order:                    
`#BioModule biolockj.module.report.r.R_CalculateStats`

## Description 
Generate a summary statistics table with [adjusted and unadjusted] [parameteric and non-parametirc] p-values and r<sup>2</sup> values for each reportable metadata field and each *report.taxonomyLevel* configured.

## Properties 
*Properties are the `name=value` pairs in the [configuration](../../../Configuration#properties) file.*                   

### R_CalculateStats properties: 
| Property| Description |
| :--- | :--- |
| *r_CalculateStats.pAdjustMethod* | _string_ <br>the p.adjust "method" parameter<br>*default:*  BH |
| *r_CalculateStats.pAdjustScope* | _string_ <br>defines R p.adjust( n ) parameter is calculated. Options:  GLOBAL, LOCAL, TAXA, ATTRIBUTE<br>*default:*  LOCAL |

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
`R_CalculateStats.imageOwner`<br>
`R_CalculateStats.imageName`<br>
`R_CalculateStats.imageTag`<br>

## Citation 
BioLockJ v1.3.9                   
Module developted by Mike Sioda.

