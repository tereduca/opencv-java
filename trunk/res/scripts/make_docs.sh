#!/bin/sh

# Move to main project dir
cd ../..

# remove previous dist
rm -rf dist/doc
mkdir dist/doc

# create javadocs
javadoc -public -d dist/docs -sourcepath java-src/src/ -subpackages sj
