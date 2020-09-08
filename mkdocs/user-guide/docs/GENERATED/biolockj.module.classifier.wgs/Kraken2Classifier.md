# Kraken2Classifier
Add to module run order:                    
`#BioModule biolockj.module.classifier.wgs.Kraken2Classifier`

## Description 
Classify WGS samples with [KRAKEN 2](https://ccb.jhu.edu/software/kraken2/).

## Properties 
*Properties are the `name=value` pairs in the [configuration](../../../Configuration#properties) file.*                   

### Kraken2Classifier properties: 
| Property| Description |
| :--- | :--- |
| *exe.kraken2* | _executable_ <br>Path for the "kraken2" executable; if not supplied, any script that needs the kraken2 command will assume it is on the PATH.<br>*default:*  *null* |
| *kraken2.db* | _file path_ <br>file path to Kraken2 kmer database directory<br>*default:*  *null* |
| *kraken2.kraken2Params* | _list_ <br>additional parameters to use with kraken2<br>*default:*  *null* |

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
biolockj.module.implicit.parser.wgs.Kraken2Parser                   

## Docker 
If running in docker, this module will run in a docker container from this image:<br>
```
biolockjdevteam/kraken2_classifier:v1.3.9
```
This can be modified using the following properties:<br>
`Kraken2Classifier.imageOwner`<br>
`Kraken2Classifier.imageName`<br>
`Kraken2Classifier.imageTag`<br>

## Citation 
Improved metagenomic analysis with Kraken 2                   
Derrick E. Wood, Jennifer Lu, Ben Langmead                   
bioRxiv 762302; doi: https://doi.org/10.1101/762302

