# SeqFileValidator
Add to module run order:                    
`#BioModule biolockj.module.seq.SeqFileValidator`

## Description 
This BioModule validates fasta/fastq file formats are valid and enforces min/max read lengths.

## Properties 
*Properties are the `name=value` pairs in the [configuration](../../../Configuration#properties) file.*                   

### SeqFileValidator properties: 
| Property| Description |
| :--- | :--- |
| *seqFileValidator.requireEqualNumPairs* | _boolean_ <br>Options: Y/N; require number of forward and reverse reads<br>*default:*  Y |
| *seqFileValidator.seqMaxLen* | _integer_ <br>maximum number of bases per read<br>*default:*  *null* |
| *seqFileValidator.seqMinLen* | _integer_ <br>minimum number of bases per read<br>*default:*  *null* |

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
`SeqFileValidator.imageOwner`<br>
`SeqFileValidator.imageName`<br>
`SeqFileValidator.imageTag`<br>

## Citation 
Module developed by Mike Sioda                   
BioLockJ v1.3.9

