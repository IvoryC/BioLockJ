# **Failure Recovery**

_**It is normal to go through several, even many, failed attempts.**_

It is not time effecient to read and fully understand all of the documentation to compeltely correctly set all parameters for every part of your pipeline.  Most users find it more effecient to read just enough to make an attempt, and then make corrections as needed.  As errors occurr, BioLockJ can highlight the relevant documentation or parameters.  An error message is not a reprimand, it is part of the conversation between the program and the user.  With this in mind, BioLockJ is designed so that, where feasible, any errors that will occur for a pipeline are found within a few seconds of the initial launch. 

## Find the problem

Look at your pipeline folder.  If the pipeline failed, there is a flag file "biolockjFailed".

```
cd-blj
cat biolockjFailed
```

This message often contains instructions about what to fix.  Sometimes it may only be able to describe the problem and you will have to figure out the solution.

In some cases it may be necessary to look a the log file at the top level, or at the log files of an individual module. But in all cases, you should start by looking at the message in the biolockjFailed file.

In very rare cases, BioLockJ may fail to form a pipeline directory, which means there is no central place for messages to be saved to.  If that is the case, launch the pipeline again but add the `--foreground` or `-f` flag (See See [`biolockj --help`](GENERATED/biolockj-help.md).  This will almost certianly hit the same error, but the messages along the way will be printed to the screen.  In most cases, the end of the printed output will have the same error message format as the biolockjFailed file.


## Try again

Once you have fixed the problem (changed a property in the config file, installed dependency, removed bad inputs, etc) you can launch your pipeline again.  

Ideally, the failure happened quickly, and the first attempt can be discarded.  It may take several attempts to successfully launch the pipeline--and that's ok! Each new attempt will get a new folder, with *_number* to distinguish the different attempts.  When you are happy with your pipeline, simply delete failed attempts.  If a pipeline is still running, or has any workers / jobs still running, you should leave the pipeline folder in place until you are sure those jobs are done.  This ensures that any files created or changed by those jobs are separated from any new runs.

Sometimes, the pipeline fails someitme *after* the check-dependencies phase.  If a failed pipeline has progress that you want to keep, then you can restart that pipeline rather than launching from scratch.

Failed pipelines can be restarted to save the progress made by successfully completed modules.  To restart a failed pipeline, add the `--restart` flag. See [`biolockj --help`](GENERATED/biolockj-help.md).

```
biolockj -r <pipeline root>
```

or

```
biolockj --restart <pipeline root>
```

This will preserve the output of any module that has been marked with "biolockjComplete".  All other module directories will be deleted and recreated.

## back tracking

The restart process will automatically determine the first module to run as the first module that is not marked as complete.

In some cases, you may want to re-run a completed module.  You can manually indicate which module should be the first one to restart by resetting the pipeline to that module before restarting the pipeline.

```
cd <dir of module to restart>
blj_reset $PWD
cd ..
biolockj --restart $PWD
```

Any module _after_ that one in the module run order will also be rerun.

## build on completed pipelines

When you initially build out your pipeline, its best to take small steps.  Configure a few modules, and launch the pipeline.  You will probably have to go through a few attempts to get everything right for those modules. 

```
biolockj myAnalysis.properties
```

Once you are happy with that, you can add modules to your module run order, and restart the completed pipeline to pick up where it left off.

```
biolockj -c myAnalysis.properties --restart ${BLJ_PROJ}/myAnalysis_5_2020Aug20
```

Any modules that were complete in the myAnalysis_5_2020Aug20 pipeline will remain.  This is only valid for appending an existing module run order.  

Often, you may use biolockj to encapsulate the "boiler plate" processing steps in your pipeline, and then take the outputs to work with manually for data exploration.  After exploring the data, you will refine your analyis question and settle on the figures you want to produce, and the code to do that.  *Then come back to BioLockJ*, and add your downstream scripts as GenMod steps, building on the pipeline you started with.  This makes your custome downstream steps just as organized and reproducible as the initial boiler-plate phase.  

