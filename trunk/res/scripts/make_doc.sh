#!/bin/sh

# Move to main project dir
cd ../..

# remove previous dist
rm -rf doc
mkdir doc

# create javadocs
javadoc -quiet -public -d doc -sourcepath src/sj-src/ -subpackages sj
