# FAQ, Troublshooting and Special Cases


---
### **Question:** How much does it cost to use BioLockJ ?
---
**Answer:** BioLockJ itself free and open-source.

BioLockJ is designed for large data sets; and it is often necissary to purchase computational resources to handle large datasets and to run the processes that BioLockJ will manage.  This cost often comes in the form of buying an effective computer, subscribing to a cluster, or purchasing cloud computeing power.

---
### **Question:** What are the system requirements for running BioLockJ ?
---
**Answer:** Either unix-and-java or docker, details below.

Easy mode: you have a unix system and you can run docker.  You're covered.  BioLockJ requires java, but if you can run docker, then all of the java-components can run inside the docker container.

Easy-ish mode: no unix, but you can run docker.  See [Pure-Docker](../Pure-Docker).

Local host mode: No docker.  You need to have a unix-like system and java 1.8 or later.  The launch process for BioLockJ will be easy, but the majority of modules have essential dependencies and you will have to install each of those dependencies on your own system.  See [Dependencies](../Dependencies/).

In terms of memory, ram and cpus; the amount required really depends on the size of the data you are processing and the needs of the algorithms you are running.  

_**In general,**_ processing sequence data requires a computer cluster or a cloud-computing system (more than a typical individual-user machine). After sequence data have been summarized as tables, all subsequent steps are orders of magnetude smaller and can usually run on a laptop within a matter of minutes.  

Most datasets can be dramatically sub-sampled to allow a laptop user to run a test of the pipeline; this does not produce usable results, but allows the user to test and troubleshoot the pipeline in a convenient setting before moving it to a bigger system.


---
### **Question:** BioLockJ says that my pipeline is running...now what?
---
**Answer:** Check on your pipeline's progress.

See the [Getting Started page](../Getting-Started/#review-your-first-pipeline).

If you are using a unix-like system, you can use the `cd-blj` alias to jump to the most recent pipeline.  On any system, the path to the new pipeline is printed during the launch process, it will be folder immediatly under your **$BLJ_PROJ** folder.  Look in that directory.  When I pipeline forms it creates the "precheckStarted" flag and then replaces that with the "precheckComplete" flag when all dependencies/settings are confirmed.  Then the pipeline starts the first module, and the flag is replaced with "biolockjStarted".  This generally takes a few seconds or less.  The subfolder for the current module will also have the "biolockjStarted" flag.  When a module is finished, the module flag is replaced with "biolockjComplete".  When the last module is finished, the pipeline flag is finally changed to "biolockjComplete".  From the pipeline folder, `ls 0*` is a quick way to see the current progress, becuase that will show the flag files and subfolders for each of the first ten modules. (That's "LS zero star", or "LS one star" if you have more than ten modules.)

If any module encounters an error, and cannot complete, then that module is marked with the "biolockjFailed" flag, the pipeline shuts down, and the pipeline is also marked with "biolockjFailed".  Extensive information is available in the pipeline's log file.  A more concise message describing the error, and sometimes solutions, is written to the biolockjFailed flag.

If your pipeline fails, use `cat biolockjFailed` to see the error message.


---
### **Question:** My pipeline failed...now what?
---
**Answer:** See [Failure Recovery](Failure-Recovery.md)

Most often, there is a consice error message that may even have instructions for fixing the pipeline.
```
cd-blj
cat biolockjFailed
```
Don't be discouraged. It is normal to go through several, even many, failed attempts as you figure out how all the parts come together.


---
### **Question:** If biolockj indicates that my pipeline may have failed to start, how do I determine the cause of the failure?
---
**Answer:** Use `-f`.

By default, BioLockJ runs the java component in the background, and only a minimal, helpful message is printed on the screen.  If there was some problem in getting that short, helpful message to the screen, you can use the `--foreground` or `-f` option to force biolockj to run in the foreground, thus printing everything to the screen.  Often the print-out ends shortly after a helpful message.


---
### **Question:** Sometimes BioLockJ adds modules to my pipeline.  How can I tell what modules will be added?
---
**Answer:** Read the docs; or use `-p`

With the `--precheck-only` or `-p` option, BioLockJ will create the pipeline and go through the check-dependencies phase for each module, but even without finding errors it will not actually run the pipeline.  This allows you see what modules will be run, see the pipeline folder layout, and see if any errors will prevent the pipeline from starting.  This is also ideal when you know you want to change more options or add more modules before you run the pipeline; but you want to check if there anything that needs to be fixed in what you have so far.

In the documentation for each module, there is a section called "Adds modules".  A module may give the class path of another module that it adds before or after itself.  Many modules say *"none found"* to indicate that this module does not add any other modules before or after itself.  Sometimes this section will say *"pipeline-dependent"* and more details are given in the "Details" section to explain which other modules might be added and when / why.

Modules that are added by other modules are called _pre-requisite modules_.  Modules that are added by the BioLockJ backbone are called _implicit modules_.  These can be disabled with the properties `pipeline.disableAddPreReqModules` and `pipeline.disableAddImplicitModules`, respectively.


---
### **Question:** I get an error message about a property, but I have that property configured correctly.  What gives?
---
**Answer:** Use `-u`.

This is often the result of a typo somewhere.  Generally, BioLockJ runs a check-dependencies protocol on each module, and all required properties should be checked during that process, and it stops when it first finds a problem.  With the `--unused-props` or `-u` option, biolockj will check dependencies for all modules, even after one fails, and any properties that were never used will be printed to the screen.  This often highlights typos in property names, or properties that are not used by the currenlty configured modules.  Keep in mind, this only reports properties in your primary config file, not in any of your defaultProps files.


---
### **Question:** A module script is failing because an environent variable is missing. But I know I defined that variable, and I can see it with `echo`. Why can't the script see it ?
---
**Answer:** Use `-e`; or reference it in your configuration file in the ${VAR} format

Where possible, avoid relying on environment variables.  Consider defining a value in your config file and/or adding the value to a parameter list that will be used with the script.  

Variables from your local envirnment must be explicitly passed into the module environments. See [the Configuration page](../Configuration/#variables).


---
### **Question:** On a cluster system, I need a particular module to run on the head node.
---
**Answer:** Use module-specific properties to control the cluster properties for that module.

See [the Configuration page](../Configuration/#module-specific-forms) for more details about module-specific forms of general properties.

#### Example:
On this cluster, the compute nodes do not have internet access, only the head node does. The first module in the pipeline is the SraDownload module to get the data, which requries internet access.

All pipelines run on this cluster include a reference to the properties set up specifically for this cluster:                 
`pipeline.defaultProps=${BLJ}/ourCluster.properties`

This group chose to store their system configurations in the BioLockJ folder, which they reference using the fully dressed ${BLJ} variable.  In this file, they have configurations for launching jobs:
```
cluster.batchCommand = qsub
SraDownload.batchCommand = /bin/bash
```

BioLockJ launches jobs using `qsub <script>`. For ONLY the SraDownload module, the property `SraDownload.batchCommand` overrides `cluster.batchCommand`; so for only this module, the jobs will be launched using `/bin/bash` which runs on the current node rather than launching a compute node.  All config files that reference this file and launch on the head node, will run the SraDownload modude on the head node.


---
### **Question:** How do I configure my pipeline for multiplexed data?
---
**Answer:** See the [Demultiplexer module Details](../GENERATED/biolockj.module.implicit/Demultiplexer/#details).


---
### **Question:** How should I configure my properties for a dataset that is one-sample-per-file (ie not multiplexed)?
---
**Answer1:** BioLockJ can extract the sample name from the filename; see [ Input](../GENERATED/Input/).

OR 

**Answer2:** BioLockJ can connect the sample id to the file name given in one or more columns in the metadata; see [Metadata](../GENERATED/Metadata/).


---
### **Question:** Shutting down a pipeline.  How do I stop a pipeline that is running?
---
**Answer:** Use `kill`, `docker stop` and possibly scheduler commands such as `qdel`.

BioLockJ will shut down appropriately on its own when a pipeline either completes or fails.  

_Sometimes_, it is necessary to shut down the program pre-maturely.

This is not an ideal exit and the steps depend on your environment.  The main program is terminated by killing the java process.  Any worker-processes that are still in progress will need to be shut down directly (or allowed to time out). _If you are allowing worker-processess to time, you must NOT delete the pipeline folder. Those processes will write to that pipeline folder, and any new pipeline you make will get a new folder as long as the original still exists._

To kill the BioLockJ program on a local system, get the id of the java process and kill it:
```bash
ps
#  PID TTY          TIME CMD
#   1776 pts/0    00:00:00 bash
#   1728 pts/0    00:00:00 ps
#   4437 pts/0    00:00:00 java
kill 4437
```
On a local system, workers are under the main program, so they will also be terminated. 

To kill the BioLockJ program running in docker, get the ID of the docker container and use `docker stop`.
```bash
docker ps
# CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
# f55a39311eb5        ubuntu              "/bin/bash"         16 minutes ago      Up 16 minutes                           brave_cori
docker stop f55a39311eb5
```
In a docker pipeline, the container IDs for workers will also appear under ps.
If you need to distinguish the BioLockJ containers from other docker containers running on your machine, you can see a list of them in the current modules script directory in a file named `MAIN*.sh_Started`.

To kill the BioLockJ program that is run in the foreground (ie, the `-f` arg was used), then killing the current process will kill the program.  This is usually done with `ctrl`+`c` .

To kill the BioLockJ program on a cluster environment, use `kill` just like the local case to stop a process on the head node, and use `qdel` (or the equivilent on your scheduler) to terminate workers running on compute nodes.

---
### **Question:** How can I get color-coded syntax for a BioLockJ config file?
---
**Answer:** Treat it like a java properties file.

A BioLockJ config file _is_ a java properties file, with added "#BioModule" lines indicating which modules to run.  BioLockJ config files typically use the extension ".config" (preferred) or ".properties". Some editors have configurations to color-code text in a meaningful way based on the type of file.   For example, Sublime text will automatically apply the syntax highlighting for a java properties file to a file that ends in ".properties".                                  
To extend this Sublime Text functionality to files that in ".config":                         
 1. open a file with the ".config" extension,                                   
 2. go to View -> Syntax -> Open all with current extension -> Java -> Java Properties
 
 ( Much thanks to this helpful post: [stack over flow](https://stackoverflow.com/questions/8088475/how-to-customise-file-type-to-syntax-associations-in-sublime-text) )

---
### **Question:** Why doesn't my pipeline run in docker ?
---
**Answer:** test docker, test file sharing

First of all, make sure docker is installed in running.
```bash
docker run hello-world

# Hello from Docker!
# This message shows that your installation appears to be working correctly.

```
If this failed, then you need to troubleshoot docker before you move forward.

When docker is fundamentally working, a common problem is file-sharing.  In most cases, file sharing is enabled and you will see a pop-up window asking for permission to share folders you have not shared previously.  If this is not the case, you may need to enable file sharing.

File sharing, also called volume sharing, is what allows programs inside docker containers to interact with files stored on your computer. Depending on your version of Docker Desktop, this setting may be under `Docker > Preferences > File Sharing`, or `Preferences > Resources > File Sharing` or something similar. Make sure this feature is enabled.  Any file that must be read by any part of the BioLockJ pipeline must be under one of the share-enabled folders.  The BioLockJ Projects directory (BLJ_PROJ) must also be under one of these share-enabled folders.

A quick test of file sharing:
```bash
mkdir ~/testFolder
echo 'hello sharing!' > ~/testFolder/testFile.txt
docker run --rm -v ~/testFolder:/testFolder ubuntu cat /testFolder/testFile.txt

# hello sharing!

```
If this fails, then you'll need to troubleshoot docker file sharing before you move forward.


The start process may be slow.<br>
The first time that you run a pipeline, docker downloads the images called for in that pipeline.  For some images, this download may take several minutes.  For pipelines with many modules, each using distinct docker images, there may be many 'several minute' downloads.  As of version 1.3.15, these downloads happen in the background and no messages are printed to the screen from the BioLockJ start command; which can give the appearance of a frozen start-up.  In another tab, run `docker image ls`, wait several minutes, and run the same command again.  If the list is growing, then BioLockJ is not frozen, it is downloading images while (unfortunately) printing nothing.  If you set `docker.verifyImage=N` in your config file, then images will be downloaded when the module is reached, rather than all at once at the start.  

