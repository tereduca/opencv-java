#!/bin/sh


# Move to main project dir
cd ../..

# remove previous dist
mkdir dist-processing
mkdir dist-processing/examples
mkdir dist-processing/libraries
mkdir dist-processing/libraries/opencv_java
mkdir dist-processing/libraries/opencv_java/library
mkdir dist-processing/libraries/controlP5
mkdir dist-processing/libraries/controlP5/library

# copy jars and libraries over
cp lib/jar-external/controlP5.jar dist-processing/libraries/controlP5/library
cp lib/jar-external/jna.jar dist-processing/libraries/opencv_java/library
cp lib/jar-external/jnaerator-0.10-shaded.jar dist-processing/libraries/opencv_java/library

cp bin/opencv-java.jar dist-processing/libraries/opencv_java/library/opencv_java.jar
cp -R lib/opencv-java/osx dist-processing/libraries/opencv_java/library

# Create pde files
cd bin
java sj.util.FileManipulation -pde ../src/examples-src/examples/ ../dist-processing/examples/
cd ..
