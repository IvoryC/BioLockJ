# NormalizeByReadsPerMillion
Add to module run order:                    
`#BioModule biolockj.module.report.taxa.NormalizeByReadsPerMillion`

## Description 
new counts = counts / (total counts in sample / 1 million)

## Properties 
*Properties are the `name=value` pairs in the [configuration](../../../Configuration#properties) file.*                   

### NormalizeByReadsPerMillion properties: 
*none*

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
| *script.defaultHeader* | _string_ <br>Store default script header for MAIN script and locally run WORKER scripts.<br>*default:*  #!/bin/bash |
| *script.numThreads* | _integer_ <br>Used to reserve cluster resources and passed to any external application call that accepts a numThreads parameter.<br>*default:*  8 |
| *script.numWorkers* | _integer_ <br>Set number of samples to process per script (if parallel processing)<br>*default:*  1 |
| *script.permissions* | _string_ <br>Used as chmod permission parameter (ex: 774)<br>*default:*  770 |
| *script.timeout* | _integer_ <br>Sets # of minutes before worker scripts times out.<br>*default:*  *null* |

## Details 
Represent each count value in the table as: counts / (total counts in sample / 1 million)                   
If report log base is not null, *LogTransformTaxaTables* is added as a post-requisite to do the log transformation and *AddPseudoCount* is added as a pre-requisite before normalization to avoid taking the log of 0.                   
*If the pipeline input does not include at least one taxa table, then the BuildTaxaTables class is added by this module as a pre-requisite.*


## Adds modules 
**pre-requisite modules**                    
*pipeline-dependent*                   
**post-requisite modules**                    
biolockj.module.report.taxa.LogTransformTaxaTables                   

## Docker 
If running in docker, this module will run in a docker container from this image:<br>
```
biolockjdevteam/biolockj_controller:v1.3.9
```
This can be modified using the following properties:<br>
`NormalizeByReadsPerMillion.imageOwner`<br>
`NormalizeByReadsPerMillion.imageName`<br>
`NormalizeByReadsPerMillion.imageTag`<br>

## Citation 
Module developed by Ivory Blakley                   
BioLockj v1.3.9

