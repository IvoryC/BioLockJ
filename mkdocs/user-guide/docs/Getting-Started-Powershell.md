
## BioLockJ on Windows using PowerShell

_Currently, windows is not officially supported as a system to run BioLockJ_

There are a few avenues for running BioLockJ on a Windows machine.  The WSL2 avenue is the most likely to be supported into the future.  The other two should be considered experimental.

### Windows Subsystem for Linux (WSL2)

Currently, the recommended option for running BioLockJ on Windows is to use WSL2 and a linux distribution; preferably using docker.  See [Getting Started for Windows](Getting-Started-Windows.md).

### The PowerShell Alternative

In this case, java running on the host machine is required to launch the program; but the manager process and the required environment to run each module is all handled by Docker containers.  As of BioLockJ v1.3.18 (and earlier) this method is known to have some problems with absolute file paths when using some versions of docker; works when tested with docker version 20.10.0.

**This feature exists, but is still experimental.  It is not guaranteed to work.**

All code chunks in this section assume you are running PowerShell **as administrator**.

#### 1. Download the [latest release](https://github.com/BioLockJ-Dev-Team/BioLockJ/releases/latest) & unpack the tarball.  

Third party tools such as [7Zip](https://www.7-zip.org/) allow you to unzip tar files on Windows.

Save the uncompressed folder wherever you like to keep executables.
<br>**If** you choose to download the source code, you will need to compile it by running `ant` with the `build.xml` file in the `resources` folder. 

#### 2. Set PowerShell variables

In PowerShell, navigate (cd) into the BioLockJ folder, and run
```
Set-Variable -Name BLJ -Value $PWD
Add-Content $profile "Set-Variable -Name BLJ -Value $BLJ"
```

cd into a folder of your choice, such as C:Users\Documents\biolockj_pipelines, and run
```
mkdir C:Users\Documents\biolockj_pipelines
cd C:Users\Documents\biolockj_pipelines
Set-Variable -Name BLJ_PROJ -Value $PWD
Add-Content $profile "Set-Variable -Name BLJ_PROJ -Value $BLJ_PROJ"
```

Test the variables.
```
$BLJ
$BLJ_PROJ
```

**Note:** The Set-Variable lines apply to the current session; the Add-content lines apply to future sessions.  

If the Add-Conent lines throw an error to effect "could not find path", then you may need to create the parent folder and try again, for example:
```
$profile
## see file path of the profile:  $HOME\Documents\WindowsPowerShell\Microsoft.PowerShell_profile.ps1
mkdir $HOME\Documents\WindowsPowerShell\
```

#### 3. Set an alias for the biolockj executable.

```
Set-Alias -Name biolockj -Value $BLJ\script\run-biolockj.ps1
Add-Content $profile "Set-Alias -Name biolockj -Value $BLJ\script\run-biolockj.ps1"
```

Allow PowerShell to execute scripts on this machine:
```
Set-ExecutionPolicy RemoteSigned
```

Test that calling this alias makes a call to the BioLockJ program.
```
biolockj --version
biolockj --help
```
This should show the biolockj help menu.

Set an alias for the biolockj supporting tool: biolockj-api. 

```
Set-Alias -Name biolockj-api -Value $BLJ\script\run-biolockj-api.ps1
Add-Content $profile "Set-Alias -Name biolockj-api -Value $BLJ\script\run-biolockj-api.ps1"
```

Test that calling this alias makes a call to the BioLockJ program.
```
biolockj-api
```
This should show the biolockj-api help menu.

#### 4. Install docker

See the current instructions for installing docker on your system: 
<br>[https://docs.docker.com/get-started/](https://docs.docker.com/get-started/)
<br>You'll need to be able to run the docker hello world example:

```bash
docker run hello-world

# Hello from Docker!
# This message shows that your installation appears to be working correctly.

```

#### 5. Run test pipeline

When you run the program, you will see a pop-up window asking for permission to share specific folders. Say yes.<br>
If that does not appear, see [why doesn't my pipeline run in docker](../FAQ/#question-why-doesnt-my-pipeline-run-in-docker).

```bash
biolockj -d $BLJ\templates\myFirstPipeline\myFirstPipeline.properties
# 
# Docker container id: 336259e7d3b8d9ab2fa71202258b562664be1bf9645d503a790ae5e9da15ce97
# Initializing BioLockJ..
# Building pipeline:  /Users/joe/apps/BioLockJ/pipelines/myFirstPipeline_2020Jan17
# Fetching pipeline status 
# 
# Pipeline is complete.
```

_In theory, operating within this system will be very much like working with BioLockJ in a unix-like operating system.  The commands will mostly be the same.  However, most pipelines will require using docker, because nearly all modules are launch scripts that assume a bash shell._

