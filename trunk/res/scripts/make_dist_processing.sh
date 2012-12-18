#!/bin/sh

if [ "$#" == 0 ]; then
	echo "Usage: script.sh revision_nr"
	exit;
fi

# Make the main jar
sh make_jar.sh

# Move to main project dir
cd ../..

# Create directories if needed
mkdir -p dist-processing/libraries/controlP5/library
mkdir -p dist-processing/libraries/opencv_java/library

# copy media over
mkdir -p dist-processing/examples/LoadImage/data/media
mkdir -p dist-processing/examples/CaptureFromMovie/data/media
mkdir -p dist-processing/examples/HaarClassifier/data/haarcascades
cp res/media/apples.jpg 									dist-processing/examples/LoadImage/data/media/
cp res/media/i_am_ironman.avi								dist-processing/examples/CaptureFromMovie/data/media/
cp res/haarcascades/haarcascade_frontalface_default.xml 	dist-processing/examples/HaarClassifier/data/haarcascades/

# copy external jars over
cp lib/jar-external/controlP5.jar 				dist-processing/libraries/controlP5/library
cp lib/jar-external/jna.jar 					dist-processing/libraries/opencv_java/library
cp lib/jar-external/jnaerator-0.10-shaded.jar 	dist-processing/libraries/opencv_java/library

# copy opencv-java libraries over
cp bin/opencv-java.jar 							dist-processing/libraries/opencv_java/library/opencv_java.jar
svn export lib/opencv-java/osx 					dist-processing/libraries/opencv_java/library/osx
svn export lib/opencv-java/windows 				dist-processing/libraries/opencv_java/library/windows

# Create pde files
cd bin
java utilities.FileManipulation -pde ../src/examples-src/examples/ ../dist-processing/examples/
cd ..

# Create the "all" zip file
cd dist-processing
zip "opencv-java_processing_r${1}.zip" -qr examples libraries

# Create the osx zip file
#mv libraries/opencv_java/library/windows/ .
#zip "opencv-java_processing_osx64_r${1}.zip" -qr examples libraries
#mv windows/ libraries/opencv_java/library/

# Create the windows 32 bit zip
#mv libraries/opencv_java/library/osx/ .
#mv libraries/opencv_java/library/windows/64bit/ .
#zip "opencv-java_processing_win32_r${1}.zip" -qr examples libraries
#mv osx/ libraries/opencv_java/library/
#mv 64bit/ libraries/opencv_java/library/windows/

# Create the windows 64 bit zip
#mv libraries/opencv_java/library/osx/ .
#mv libraries/opencv_java/library/windows/32bit/ .
#zip "opencv-java_processing_win64_r${1}.zip" -qr examples libraries
#mv osx/ libraries/opencv_java/library/
#mv 32bit/ libraries/opencv_java/library/windows/
