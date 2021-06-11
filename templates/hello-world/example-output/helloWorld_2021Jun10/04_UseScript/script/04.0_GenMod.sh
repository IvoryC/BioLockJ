#!/bin/bash

# BioLockJ.v1.4.0: ${scriptDir}/04.0_GenMod.sh

export BLJ=/Users/ieclabau/git/BioLockJ

pipeDir="/Users/ieclabau/git/sheepdog_testing_suite/MockMain/pipelines/helloWorld_2021Jun10"
modDir="${pipeDir}/04_UseScript"
scriptDir="${modDir}/script"
tempDir="${modDir}/temp"
logDir="${modDir}/log"
outputDir="${modDir}/output"

touch "${scriptDir}/04.0_GenMod.sh_Started"

exec 1>${logDir}/04.0_GenMod.log
exec 2>&1

cd ${scriptDir}

function scriptFailed() {
    echo "Line #${2} failure status code [ ${3} ]:  ${1}" >> "${scriptDir}/04.0_GenMod.sh_Failures"
    exit ${3}
}

function executeLine() {
    ${1}
    statusCode=$?
    [ ${statusCode} -ne 0 ] && scriptFailed "${1}" ${2} ${statusCode}
}

executeLine "${modDir}/resources/helloScript.sh" ${LINENO}
touch "${scriptDir}/04.0_GenMod.sh_Success"
echo 'Created Success flag.'
