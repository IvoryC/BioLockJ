# Humann2Parser
Add to module run order:                    
`#BioModule biolockj.module.implicit.parser.wgs.Humann2Parser`

## Description 
Build OTU tables from HumanN2 classifier module output.

## Properties 
*Properties are the `name=value` pairs in the [configuration](../../../Configuration#properties) file.*                   

### Humann2Parser properties: 
| Property| Description |
| :--- | :--- |
| *humann2.keepUnintegrated* | _boolean_ <br>if true, keep UNINTEGRATED column in count tables<br>*default:*  *null* |
| *humann2.keepUnmapped* | _boolean_ <br>if true, keep UNMAPPED column in count tables<br>*default:*  *null* |

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
| *report.numHits* | _boolean_ <br>Options: Y/N. If Y, and add Num_Hits to metadata<br>*default:*  Y |
| *report.unclassifiedTaxa* | _boolean_ <br>report unclassified taxa<br>*default:*  Y |
| *script.defaultHeader* | _string_ <br>Store default script header for MAIN script and locally run WORKER scripts.<br>*default:*  #!/bin/bash |
| *script.numThreads* | _integer_ <br>Used to reserve cluster resources and passed to any external application call that accepts a numThreads parameter.<br>*default:*  8 |
| *script.numWorkers* | _integer_ <br>Set number of samples to process per script (if parallel processing)<br>*default:*  1 |
| *script.permissions* | _string_ <br>Used as chmod permission parameter (ex: 774)<br>*default:*  770 |
| *script.timeout* | _integer_ <br>Sets # of minutes before worker scripts times out.<br>*default:*  *null* |

## Details 
*none*

## Adds modules 
**pre-requisite modules**                    
*none found*                   
**post-requisite modules**                    
*none found*                   

## Docker 
If running in docker, this module will run in a docker container from this image:<br>
```
biolockjdevteam/biolockj_controller:v1.3.9
```
This can be modified using the following properties:<br>
`Humann2Parser.imageOwner`<br>
`Humann2Parser.imageName`<br>
`Humann2Parser.imageTag`<br>

## Citation 
Module developed by Mike Sioda                   
BioLockJ v1.3.9

