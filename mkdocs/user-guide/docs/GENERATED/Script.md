# script properties                   
                   
Nearly all modules are "script modules".  The module writes one or more scripts to divide the work load, and each script is run                                  
on an independent cluster node (if `pipeline.env=cluster`), or                               
on an independent aws node (if `pipeline.env=aws`), or                                   
one at a time on the current machine (if `pipeline.env=local`).                   
                   
| Property| Description |
| :--- | :--- |
| *script.defaultHeader* | _string_ <br>Store default script header for MAIN script and locally run WORKER scripts.<br>*default:*  #!/bin/bash |
| *script.fileRefreshDelay* | _integer_ <br>delay this many seconds after scripts complete to allow the file system to reflect changes from a worker node/container/virtual machine.<br>*default:*  1 |
| *script.numThreads* | _integer_ <br>Used to reserve cluster resources and passed to any external application call that accepts a numThreads parameter.<br>*default:*  8 |
| *script.numWorkers* | _integer_ <br>Set number of samples to process per script (if parallel processing)<br>*default:*  1 |
| *script.permissions* | _string_ <br>Used as chmod permission parameter (ex: 774)<br>*default:*  770 |
| *script.timeout* | _integer_ <br>Sets # of minutes before worker scripts times out.<br>*default:*  *null* |
                   
All script modules have a "script" subdirectory.                   
                   
There is one "MAIN" script for the module, which launches the worker scripts.  Each worker script does the work for one _batch_; a set of samples.                     
