# SraMetaData
Add to module run order:                    
`#BioModule biolockj.module.getData.sra.SraMetaData`

## Description 
Extract metadata via pysradb from local copy of SRAmetadb.sqlite.

## Properties 
*Properties are the `name=value` pairs in the [configuration](../../../Configuration#properties) file.*                   

### SraMetaData properties: 
| Property| Description |
| :--- | :--- |
| *exe.pysradb* | _executable_ <br>Path for the "pysradb" executable; if not supplied, any script that needs the pysradb command will assume it is on the PATH.<br>*default:*  *null* |
| *sra.metaDataDir* | _file path_ <br>path to the directory where the *SRAmetadb.sqlite* database is stored.<br>*default:*  *null* |
| *sra.sraProjectId* | _list_ <br>The project id(s) referencesing a project in the NCBI SRA. example: SRP009633, ERP016051<br>*default:*  *null* |

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
The **biolockj.module.getData.sra.SraMetaDB** module is added a pre-requisite to ensure that the database is available.

## Adds modules 
**pre-requisite modules**                    
biolockj.module.getData.sra.SraMetaDB                   
**post-requisite modules**                    
*none found*                   

## Docker 
If running in docker, this module will run in a docker container from this image:<br>
```
biolockjdevteam/pysradb:v1.3.9
```
This can be modified using the following properties:<br>
`SraMetaData.imageOwner`<br>
`SraMetaData.imageName`<br>
`SraMetaData.imageTag`<br>

## Citation 
Module developed by Malcolm Zapatas and Ivory Blakley                   
BioLockJ v1.3.9

