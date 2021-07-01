#! /bin/bash

# This script extracts only the species level (or other taxonomic level) lines from a merged metaphlan output table.
# This was based on instuctions found here:
# https://github.com/biobakery/biobakery/wiki/metaphlan3#create-a-heatmap-with-hclust2

INFILE=../../*Merge_MetaPhlAn_Tables/output/merged_metaphlan_profile.txt
echo "Reading file: $INFILE"

LEVEL=$1
if test -z $LEVEL; then
	LEVEL=species
	echo "Using default level: $LEVEL"
fi

OUT="../output/metaphlan_$LEVEL.txt"
echo "Creating file: $OUT"

if [ "$LEVEL" == "phylum" ]; then 
	echo "Splitting out the phylum table..."
	grep -E "p__|clade" $INFILE | grep -v "c__" | sed 's/^.*p__//g' | sed -e 's/_rel_ab_w_read_stats/_profile/g' | cut -f1,3- >> $OUT
elif [ "$LEVEL" == "class" ]; then 
	echo "Splitting out the class table..."
	grep -E "c__|clade" $INFILE | grep -v "o__" | sed 's/^.*c__//g' | sed -e 's/_rel_ab_w_read_stats/_profile/g' | cut -f1,3- >> $OUT
elif [ "$LEVEL" == "order" ]; then 
	echo "Splitting out the order table..."
	grep -E "o__|clade" $INFILE | grep -v "f__" | sed 's/^.*o__//g' | sed -e 's/_rel_ab_w_read_stats/_profile/g' | cut -f1,3- >> $OUT
elif [ "$LEVEL" == "family" ]; then 
	echo "Splitting out the family table..."
	grep -E "f__|clade" $INFILE | grep -v "g__" | sed 's/^.*f__//g' | sed -e 's/_rel_ab_w_read_stats/_profile/g' | cut -f1,3- >> $OUT
elif [ "$LEVEL" == "genus" ]; then 
	echo "Splitting out the genus table..."
	grep -E "g__|clade" $INFILE | grep -v "s__" | sed 's/^.*g__//g' | sed -e 's/_rel_ab_w_read_stats/_profile/g' | cut -f1,3- >> $OUT
elif [ "$LEVEL" == "species" ]; then 
	echo "Splitting out the species table..."
	grep -E "s__|clade" $INFILE | grep -v "t__" | sed 's/^.*s__//g' | sed -e 's/_rel_ab_w_read_stats/_profile/g' | cut -f1,3- >> $OUT
fi

echo "All done."
