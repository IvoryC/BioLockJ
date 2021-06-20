# MetaPhlAn_DB
Add to module run order:                    
`#BioModule biolockj.module.biobakery.metaphlan.MetaPhlAn_DB`

## Description 
Install the reference database required by [MetaPhlAn](https://github.com/biobakery/MetaPhlAn).

## Properties 
*Properties are the `name=value` pairs in the [configuration](../../../Configuration#properties) file.*                   

### MetaPhlAn_DB properties: 
| Property| Description |
| :--- | :--- |
| *exe.bowtie2* | _executable_ <br>Path for the "bowtie2" executable; if not supplied, any script that needs the bowtie2 command will assume it is on the PATH.<br>*default:*  *null* |
| *exe.bowtie2build* | _executable_ <br>Path for the "bowtie2build" executable; if not supplied, any script that needs the bowtie2build command will assume it is on the PATH.<br>*default:*  *null* |
| *exe.metaphlan* | _executable_ <br>Path for the "metaphlan" executable; if not supplied, any script that needs the metaphlan command will assume it is on the PATH.<br>*default:*  *null* |
| *metaphlan.bowtie2db* | _file path_ <br>Path to the directory containing the bowtie2 reference database, passed to metaphlan via the --bowtie2db argument.<br>*default:*  *null* |
| *metaphlan.consistentModules* | _boolean_ <br>Ensure same core settings for modules in the in the metaphlan2 family in the same pipeline.<br>*default:*  Y |
| *metaphlan.dbIndex* | _string_ <br>The version of the database to use, passed to metaphlan via the --index parameter. Specifying this value is recommended. Example: mpa_v30_CHOCOPhlAn_201901<br>*default:*  *null* |
| *metaphlan.mpa_dir* | _file path_ <br>The path to the metaphlan directory. To use the mpa_dir environment variable, use `${mpa_dir}`.<br>*default:*  *null* |

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
Use this module to verify that a given index of the database is present or to download/build it if it is not.  Downloading requires internet access.                   
The command run by this module will be something like:                   
`metaphlan --install --bowtie2db /path/to/my/dbs/metaphlan/metaphlan_databases/ --index mpa_v30_CHOCOPhlAn_201901`

## Adds modules 
**pre-requisite modules**                    
*none found*                   
**post-requisite modules**                    
*none found*                   

## Docker 
If running in docker, this module will run in a docker container from this image:<br>
```
biobakery/metaphlan2:2.7.7_cloud_r1
```
This can be modified using the following properties:<br>
`MetaPhlAn_DB.imageOwner`<br>
`MetaPhlAn_DB.imageName`<br>
`MetaPhlAn_DB.imageTag`<br>

## Citation 
The BioLockJ module was developed by Ivory Blakley to facilitate using MetaPhlan2.                   
To cite MetaPhlan2, please cite:                    
Integrating taxonomic, functional, and strain-level profiling of diverse microbial communities with bioBakery 3 Francesco Beghini, Lauren J McIver, Aitor Blanco-Míguez, Leonard Dubois, Francesco Asnicar, Sagun Maharjan, Ana Mailyan, Paolo Manghi, Matthias Scholz, Andrew Maltez Thomas, Mireia Valles-Colomer, George Weingart, Yancong Zhang, Moreno Zolfo, Curtis Huttenhower, Eric A Franzosa, Nicola Segata. eLife (2021)

