
The BioLockJ program is launched through the `biolockj` script. See [`biolockj --help`](GENERATED/biolockj-help.md).

Support programs can access information about BioLockJ modules and properties through [`biolockj-api`](GENERATED/BioLockJ-Api.md). 

There are also several helper scripts for small specific tasks, these are all found under $BLJ/script and added to the `$PATH` after the basic installation:

### Bash Commands

| Command | Description |
| :-- | :-- |
| **[last-pipeline](https://github.com/BioLockJ-Dev-Team/BioLockJ/blob/master/script/last-pipeline)** | Get the path to the most recent pipeline.<br>ideal for: <br> `cd $(last-pipeline)`<br>``` ls `last-pipeline` ``` |
| **cd-blj** | Go to most recent pipeline & list contents. This is not a script, it is an alias that is added to your bash profile by the install script.  The line defining it should look like:<br> `alias cd-blj='cd $(last-pipeline); quick_pipeline_view'` |
| **[quick_pipeline_view](https://github.com/BioLockJ-Dev-Team/BioLockJ/blob/master/script/quick_pipeline_view)** | essentially just `pwd` and `ls`; designed for the cd-blj alias. |
| **[blj_reset](https://github.com/BioLockJ-Dev-Team/BioLockJ/blob/master/script/blj_reset)** | Reset pipeline status to incomplete.<br>If restarted, execution will start with the current module.  |




### Deprecated Commands

| Command | Description <br> (Replacement) |
| :-- | :-- |
| **blj_log** | Tail last 1K lines from current or most recent pipeline log file. <br>**Replacement**:<br> `cd $(last-pipeline); tail -1000 *.log` |
| **blj_summary** | Print current or most recent pipeline summary. <br>**Replacement**:<br> `cd $(last-pipeline); cat summary.txt` |
| **blj_complete** | Manually completes the current module and pipeline status. This functionality should never be needed. For the rare occasions when it is appropriate, it can be done manually.<br>**Replacement**:<br> `touch biolockjComplete` |
| **blj_reset** | Reset pipeline status to incomplete.<br>If restarted, execution will start with the current module.<br>The need for this functionality is common; and a bash wrapper script still exists.  <br>**Alternative**:<br> `java -cp ${BLJ}/dist/BioLockJ.jar biolockj.launch.Reset ${PWD}` |
| **blj_download** | If on cluster, extract and print the command syntax from the summary.txt file to download pipeline results to your local workstation directory: *pipeline.downloadDir*. <br>**no replacement**:<br> You will need to review your pipelines summary file to find the download command. |
