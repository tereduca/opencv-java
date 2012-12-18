#!/bin/sh

if [ "$#" -lt 2 ]; then
	echo "Usage: script.sh revision_nr password"
	exit
fi

# Create the dist files
sh make_dist_processing.sh "$1"


# Upload the "all" file
summary="The opencv-java library for Processing (includes binaries for platform: osx64/win32/win64) revision ${1}"
python googlecode_upload.py -s "${summary}" -p opencv-java -u siggioa@gmail.com -w "$2" ../../dist-processing/"opencv-java_processing_r${1}.zip"

# Upload the osx file
#summary="The opencv-java library for Processing (includes binaries for platform: osx64) revision ${1}"
#python googlecode_upload.py -s "${summary}" -p opencv-java -u siggioa@gmail.com -w "$2" ../../dist-processing/"opencv-java_processing_osx64_r${1}.zip"

# Upload the "win32" file
#summary="The opencv-java library for Processing (includes binaries for platform: win32) revision ${1}"
#python googlecode_upload.py -s "${summary}" -p opencv-java -u siggioa@gmail.com -w "$2" ../../dist-processing/"opencv-java_processing_win32_r${1}.zip"

# Upload the "win64" file
#summary="The opencv-java library for Processing (includes binaries for platform: win64) revision ${1}"
#python googlecode_upload.py -s "${summary}" -p opencv-java -u siggioa@gmail.com -w "$2" ../../dist-processing/"opencv-java_processing_win64_r${1}.zip"
