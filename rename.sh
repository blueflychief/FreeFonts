#!/bin/bash

find ./dafont ./fontsquirrel ./googlefonts -type f -name info.json -mindepth 3 -print | while read file
do
	sed -i 's|"varient":|"variant":|' $file
	echo $file
done