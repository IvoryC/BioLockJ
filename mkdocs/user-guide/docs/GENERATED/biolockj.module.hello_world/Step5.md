# Step5
Add to module run order:                    
`#BioModule biolockj.module.hello_world.Step5`

## Description 
Print the classic phrase: hello world.

## Properties 
*Properties are the `name=value` pairs in the [configuration](../../../Configuration#properties) file.*                   

### Step5 properties: 
| Property| Description |
| :--- | :--- |
| *hello.excitmentLevel* | _integer_ <br>The number of ! to use with the phrase.<br>*default:*  *null* |
| *hello.myName* | _string_ <br>A name to use instead of 'world'.<br>*default:*  world |

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
_version: 0.0.0_ 
Say hello.  By default, use the classic phrase "hello, world.".  Optionally supply a name such a John to print "hello, John.".

## Adds modules 
**pre-requisite modules**                    
*none found*                   
**post-requisite modules**                    
*none found*                   

## Docker 
If running in docker, this module will run in a docker container from this image:<br>
```
library/ubuntu:latest
```
This can be modified using the following properties:<br>
`Step5.imageOwner`<br>
`Step5.imageName`<br>
`Step5.imageTag`<br>

## Citation 
Module developed by Ivory Blakley.

