# MetaPhlAn2
Add to module run order:                    
`#BioModule biolockj.module.biobakery.metaphlan.MetaPhlAn2`

## Description 
Profile the composition of microbial communities using [MetaPhlAn](https://github.com/biobakery/MetaPhlAn).

## Properties 
*Properties are the `name=value` pairs in the [configuration](../../../Configuration#properties) file.*                   

### MetaPhlAn2 properties: 
| Property| Description |
| :--- | :--- |
| *exe.bowtie2* | _executable_ <br>Path for the "bowtie2" executable; if not supplied, any script that needs the bowtie2 command will assume it is on the PATH.<br>*default:*  *null* |
| *exe.bowtie2build* | _executable_ <br>Path for the "bowtie2build" executable; if not supplied, any script that needs the bowtie2build command will assume it is on the PATH.<br>*default:*  *null* |
| *exe.metaphlan* | _executable_ <br>Path for the "metaphlan" executable; if not supplied, any script that needs the metaphlan command will assume it is on the PATH.<br>*default:*  *null* |
| *metaphlan.checkParams* | _boolean_ <br>Should BioLockJ check the user-provided parameters to metaphlan2.<br>*default:*  Y |
| *metaphlan.consistentModules* | _boolean_ <br>Ensure same core settings for modules in the in the metaphlan2 family in the same pipeline.<br>*default:*  Y |
| *metaphlan.mpa_dir* | _file path_ <br>The path to the metaphlan directory. To use the mpa_dir environment variable, use `${mpa_dir}`.<br>*default:*  *null* |
| *metaphlan.params* | _string_ <br>Additional parameters to use with metaphlan2. Several options are handled specially. See details.<br>*default:*  *null* |

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
[MetaPhlAn](https://github.com/biobakery/MetaPhlAn) is one of many tools in the BioBakery project.This module facilitates using MetaPhlAn2 as part of a BioLockJ pipeline.                   
Arguments to be used be passed to MetaPhlAn can be added via the _metaphlan.params_ property.                   
The input and output locations will be specified automatically by BioLockJ.  Outputs are named using the <sample id>_<analysis type>.txt .The MetaPhlAn2 analsyis type is rel_ab_w_read_stats by default, but can be specified in _metaphlan.params_. --nproc will be set based on the _script.numThreads_ property and cannot be included in the _metaphlan.params_ value.                    
If _metaphlan.checkParams=Y_ then BioLockJ will review the params for metaphlan to check for common problems.BioLockJ will check that if any of these arguments are used there is a value: --input_type, --mpa_pkl, --bowtie2db, -x, --bt2_ps, --tmp_dir, --bowtie2_exe, --bowtie2_build, --bowtie2out, --tax_lev, --stat_q, --ignore_markers, -t, --nreads, --pres_th, -o, --sample_id_key, --sample_id, -s, --samout, --biom, --biom_output_file, --mdelim, --metadata_delimiter_char, --nproc, --read_min_len, --min_cu_len, --min_alignment_len, --stat, --clade, --min_abBioLockJ will check that if any of these arguments are used there is no following value: --no_map, --ignore_viruses, --ignore_eukaryotes, --ignore_bacteria, --ignore_archaea, --avoid_disqm, --install, -v, --version, -h, --helpThese properties are set using specific BioLockJ properties: -o, --nproc, --bowtie2db, --mpa_pklBioLockJ will also check to ensure there are no arguments that do not match the recognized set (these are typically mistakes) and no arugments that would prevent the program from running (such as -v or -h ).This check is done during check dependences, before the first module starts.

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
`MetaPhlAn2.imageOwner`<br>
`MetaPhlAn2.imageName`<br>
`MetaPhlAn2.imageTag`<br>

## Citation 
The BioLockJ module was developed by Ivory Blakley to facilitate using MetaPhlan2.                   
To cite MetaPhlan2, please cite:                    
Integrating taxonomic, functional, and strain-level profiling of diverse microbial communities with bioBakery 3 Francesco Beghini, Lauren J McIver, Aitor Blanco-Míguez, Leonard Dubois, Francesco Asnicar, Sagun Maharjan, Ana Mailyan, Paolo Manghi, Matthias Scholz, Andrew Maltez Thomas, Mireia Valles-Colomer, George Weingart, Yancong Zhang, Moreno Zolfo, Curtis Huttenhower, Eric A Franzosa, Nicola Segata. eLife (2021)

