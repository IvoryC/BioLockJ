#! /bin/bash

# This script calls hclust2
# It assumes that hclust2.py is on the PATH.

INPUT=$1
echo "Reading file: $INPUT"

BASE=$(basename $1)
OUT=hclust_${BASE/.txt/.png}
OUTPUT="../output/$OUT"
echo "Creating file: $OUTPUT"

hclust2.py \
	-i $INPUT \
	-o $OUTPUT \
	--f_dist_f braycurtis \
	--s_dist_f braycurtis \
	--cell_aspect_ratio 0.5 \
	-l \
	--flabel_size 10 \
	--slabel_size 10 \
	--max_flabel_len 100 \
	--max_slabel_len 100 \
	--minv 0.1 \
	--dpi 300

echo "All done."