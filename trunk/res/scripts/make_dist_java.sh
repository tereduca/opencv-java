#!/bin/sh

if [ "$#" == 0 ]; then
	echo "Usage: script.sh revision_nr"
	exit;
fi

# Make the main jar
sh make_jar.sh
sh make_doc.sh

# Move to main project dir
cd ../..

# Create directories if needed
mkdir -p 	dist-java/lib
mkdir -p 	dist-java/src
mkdir -p 	dist-java/res/media
mkdir -p 	dist-java/res/haarcascades

# export example source code to dist folder
svn export src/examples-src dist-java/src/examples-src

# copy docs over
cp -r doc dist-java/

# copy media over
cp res/media/apples.jpg 									dist-java/res/media
cp res/media/i_am_ironman.avi								dist-java/res/media
cp res/haarcascades/haarcascade_frontalface_default.xml 	dist-java/res/haarcascades/

# copy external jars over
cp lib/jar-external/core.jar	 				dist-java/lib
cp lib/jar-external/controlP5.jar 				dist-java/lib
cp lib/jar-external/jna.jar 					dist-java/lib
cp lib/jar-external/jnaerator-0.10-shaded.jar 	dist-java/lib

# copy opencv-java libraries over
cp bin/opencv-java.jar 							dist-java/lib/
svn export lib/opencv-java/osx 					dist-java/lib/osx
svn export lib/opencv-java/windows 				dist-java/lib/windows

# export licenses to distribution folder
svn export licenses dist-java/licenses

# Create the "all" zip file
cd dist-java
zip "opencv-java_r${1}.zip" -qr lib licenses res src doc
