
## BioLockJ on Windows

_Currently, windows is not officially supported as a system to run BioLockJ_

There are a few avenues for running BioLockJ on a Windows machine.  The WSL2 avenue is the most likely to be supported into the future.  The other two should be considered experimental.

### Windows Subsystem for Linux (WSL2)

**This option has been shown to work, but it is not rigorously tested in our current release testing process, and so is _not officially_ supported.**

#### 1. Install WSL2

Set up WSL2 on your machine and link it to your linux subsystem, see the [Microsoft documentation](https://docs.microsoft.com/en-us/windows/wsl/install-win10).

#### 2. Install linux distribution

For our tests, we chose the most recent Ubuntu distribution, see [tested environments](../Supported-Environments/#Tested-environments); presumably others also work.

Once you have installed a linux distribution, you will need to make sure it is running with WSL2. <br> Note: this command is written for powershell (go to the windows start menu and type powershell, open as administrator) not Ubuntu, and not command prompt.
```
#PowerShell
wsl --list --verbose
```
Find Ubuntu (or your distro of choice) and make sure the VERSION is "2".   <br>If its not, go back to the Microsoft documentation so see how to set it.

#### 3. Install java

Install java in the linux subsystem.  
```
sudo apt-get update
sudo apt install default-jre
```

Verify that java is installed in the linux subsystem:
```
java -version
```

#### 4. Install docker (with WSL2)

See [docker documentation](https://docs.docker.com/docker-for-windows/wsl/). <br>
Set up docker to work with your linux subsystem on Windows.  <br>You may need to look through the settings in your version of docker desktop to make sure that WSL2 is the engine for docker, and that docker is enabled for the linux distribution you plan to use.  <br>You'll need to be able to run the docker hello world example from your linux subsystem:

```bash
docker run hello-world

# Hello from Docker!
# This message shows that your installation appears to be working correctly.

```

#### 5. Install BioLockJ

Follow the standard instructions to for [Getting Started](Getting-Started.md) with BioLockJ, operating within the linux subsystem.  

_In theory, operating within this system will be identical to working with BioLockJ in a unix-like operating system.  However we recommend (and run tests) using docker, as this removes the added troubleshooting of adapting to subtle differences across environments, which could be compounded by the system stacking, not to mention the often tedious task of installing all dependencies for all pipelines._


##Now head to [Getting Started](Getting-Started.md)!

---

### Alternatives to useing WSL2

_These are other options that have been developed for running BioLockJ on Windows.  These are currently considered far less reliable than using WSL2.  The documentation for these alternatives is here to facilitate future exploration of these options.  It is not recommended for BioLockJ users._

#### Pure-Docker

In the pure-docker case, a handful of power-shell commands are used to launch a Docker container.  The Docker container includes all the required software and environment, including java to run the launch process and manager process, and the required environment to run each module.  <br>See [Working in Pure Docker](Pure-Docker.md).


#### Docker with a Powershell launch

In this case, java running on the host machine is required to launch the program; but the manager process and the required environment to run each module is all handled by Docker containers.  <br>See [Docker with PowerShell launch](Getting-Started-Powershell.md).

