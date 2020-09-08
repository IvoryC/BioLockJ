# RdpClassifier
Add to module run order:                    
`#BioModule biolockj.module.classifier.r16s.RdpClassifier`

## Description 
Classify 16s samples with [RDP](http://rdp.cme.msu.edu/classifier/classifier.jsp).

## Properties 
*Properties are the `name=value` pairs in the [configuration](../../../Configuration#properties) file.*                   

### RdpClassifier properties: 
| Property| Description |
| :--- | :--- |
| *rdp.containerJar* | _string_ <br>File path for RDP java executable JAR in docker.<br>*default:*  /app/classifier.jar |
| *rdp.db* | _file path_ <br>File path used to define an alternate RDP database<br>*default:*  *null* |
| *rdp.jar* | _file path_ <br>File path for RDP java executable JAR<br>*default:*  *null* |
| *rdp.javaParams* | _list_ <br>the parameters to java when running rdp.<br>*default:*  *null* |
| *rdp.params* | _list_ <br>parameters to use when running rdp. (must include "-f fixrank")<br>*default:*  -f fixrank |

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
| *pipeline.defaultSeqMerger* | _string_ <br>Java class name for default module used combined paired read files<br>*default:*  biolockj.module.seq.PearMergeReads |
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
biolockj.module.implicit.parser.r16s.RdpParser                   

## Docker 
If running in docker, this module will run in a docker container from this image:<br>
```
biolockjdevteam/rdp_classifier:v1.3.9
```
This can be modified using the following properties:<br>
`RdpClassifier.imageOwner`<br>
`RdpClassifier.imageName`<br>
`RdpClassifier.imageTag`<br>

## Citation 
Module developed by Mike Sioda                   
BioLockJ v1.3.9

