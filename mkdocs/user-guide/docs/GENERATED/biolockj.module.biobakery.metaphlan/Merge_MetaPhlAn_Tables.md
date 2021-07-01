# Merge_MetaPhlAn_Tables
Add to module run order:                    
`#BioModule biolockj.module.biobakery.metaphlan.Merge_MetaPhlAn_Tables`

## Description 
Run the merge_metaphlan_tables.py utility from [MetaPhlAn](https://github.com/biobakery/MetaPhlAn).

## Properties 
*Properties are the `name=value` pairs in the [configuration](../../../Configuration#properties) file.*                   

### Merge_MetaPhlAn_Tables properties: 
| Property| Description |
| :--- | :--- |
| *exe.merge_metaphlan_tables.py* | _executable_ <br>Path for the "merge_metaphlan_tables.py" executable; if not supplied, any script that needs the merge_metaphlan_tables.py command will assume it is on the PATH.<br>*default:*  *null* |
| *metaphlan.consistentModules* | _boolean_ <br>Ensure same core settings for modules in the in the metaphlan2 family in the same pipeline.<br>*default:*  Y |
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
*none*

## Adds modules 
**pre-requisite modules**                    
*none found*                   
**post-requisite modules**                    
*none found*                   

## Docker 
If running in docker, this module will run in a docker container from this image:<br>
```
biobakery/metaphlan:3.0.7
```
This can be modified using the following properties:<br>
`Merge_MetaPhlAn_Tables.imageOwner`<br>
`Merge_MetaPhlAn_Tables.imageName`<br>
`Merge_MetaPhlAn_Tables.imageTag`<br>

## Citation 
The BioLockJ module was developed by Ivory Blakley to facilitate using MetaPhlan2.                   
To cite MetaPhlan2, please cite:                    
Integrating taxonomic, functional, and strain-level profiling of diverse microbial communities with bioBakery 3 Francesco Beghini, Lauren J McIver, Aitor Blanco-Míguez, Leonard Dubois, Francesco Asnicar, Sagun Maharjan, Ana Mailyan, Paolo Manghi, Matthias Scholz, Andrew Maltez Thomas, Mireia Valles-Colomer, George Weingart, Yancong Zhang, Moreno Zolfo, Curtis Huttenhower, Eric A Franzosa, Nicola Segata. eLife (2021)

