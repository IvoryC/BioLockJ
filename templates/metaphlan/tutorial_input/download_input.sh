#! /bin/bash

# This script downloads the test input files given in the metaphlan documentation,
# See: https://github.com/biobakery/biobakery/wiki/metaphlan2#input-files

DEST_DIR=$1
if test -z $DEST_DIR; then
	echo "Using default destination path..."
	DEST_DIR=${BLJ}/templates/metaphlan/tutorial_input/fasta
fi

SOURCE_URL=https://github.com/biobakery/biobakery/raw/master/demos/biobakery_demos/data/metaphlan2/input

function getFile(){
	NAME=$1
	FILE=${DEST_DIR}/$NAME
	if test -f "$FILE"; then
	    echo "$FILE exists."
	else
	    echo "$FILE does not exists. Dowloading..."
	    curl --output $FILE -L ${SOURCE_URL}/$NAME 
	fi
}

getFile SRS014476-Supragingival_plaque.fasta.gz
getFile SRS014494-Posterior_fornix.fasta.gz
getFile SRS014459-Stool.fasta.gz
getFile SRS014464-Anterior_nares.fasta.gz
getFile SRS014470-Tongue_dorsum.fasta.gz
getFile SRS014472-Buccal_mucosa.fasta.gz

echo "All done."
