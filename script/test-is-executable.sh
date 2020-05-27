#!/bin/bash

# $1 - arg 1 - executable to be tested

[ -f $1 ] && [ -x $1 ] && exit 0
[ -f $1 ] && [ ! -x $1 && exit 127
[ ! -f $1 ] && $1
