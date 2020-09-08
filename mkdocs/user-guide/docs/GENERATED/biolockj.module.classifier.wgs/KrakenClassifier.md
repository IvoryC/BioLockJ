# KrakenClassifier
Add to module run order:                    
`#BioModule biolockj.module.classifier.wgs.KrakenClassifier`

## Description 
Classify WGS samples with KRAKEN.

## Properties 
*Properties are the `name=value` pairs in the [configuration](../../../Configuration#properties) file.*                   

### KrakenClassifier properties: 
| Property| Description |
| :--- | :--- |
| *exe.kraken* | _executable_ <br>Path for the "kraken" executable; if not supplied, any script that needs the kraken command will assume it is on the PATH.<br>*default:*  *null* |
| *kraken.db* | _file path_ <br>file path to Kraken kmer database directory<br>*default:*  *null* |
| *kraken.krakenParams* | _list_ <br>additional parameters to use with kraken<br>*default:*  --only-classified-output, --preload |

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
Classify WGS samples with [KRAKEN](http://ccb.jhu.edu/software/kraken/). 
If running in docker, the default docker container contains a kmer database which will be used if no database is supplied through the `kraken.db` property.

## Adds modules 
**pre-requisite modules**                    
*none found*                   
**post-requisite modules**                    
biolockj.module.implicit.parser.wgs.KrakenParser                   

## Docker 
If running in docker, this module will run in a docker container from this image:<br>
```
biolockjdevteam/kraken_classifier:v1.3.9
```
This can be modified using the following properties:<br>
`KrakenClassifier.imageOwner`<br>
`KrakenClassifier.imageName`<br>
`KrakenClassifier.imageTag`<br>

## Citation 
Wood DE, Salzberg SL: Kraken: ultrafast metagenomic sequence classification using exact alignments. Genome Biology 2014, 15:R46.

