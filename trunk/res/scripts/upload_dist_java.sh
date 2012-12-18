#!/bin/sh

if [ "$#" -lt 2 ]; then
	echo "Usage: script.sh revision_nr password"
	exit
fi

# Create the dist files
sh make_dist_java.sh "$1"


# Upload the "all" file
summary="The opencv-java library (includes binaries for platform: osx64/win32/win64) revision ${1}"
python googlecode_upload.py -s "${summary}" -p opencv-java -u siggioa@gmail.com -w "$2" ../../dist-java/"opencv-java_r${1}.zip"
