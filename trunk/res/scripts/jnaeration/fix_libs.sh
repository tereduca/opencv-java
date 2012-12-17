#!/bin/sh

dir="/Users/siggi/Downloads/OpenCV-2.4.0/build/lib"
dst_dir="/Users/siggi/Downloads/OpenCV-2.4.0/build/jna-lib"

#libs=( "core" "imgproc" "objdetect" "highgui" "calib3d" "flann" "features2d" )
libs=(  "calib3d" "contrib" "core" "features2d" "flann" "gpu" "imgproc" "objdetect" "highgui" "legacy" "ml" "nonfree" "photo" "stitching" "ts" "video" "videostab" )


rm -rf $dst_dir
mkdir $dst_dir

for lib in "${libs[@]}" 
do
	cp ${dir}/libopencv_${lib}.2.4.0.dylib $dst_dir
done

for lib1 in "${libs[@]}" 
do
	for lib2 in "${libs[@]}" 
	do
		install_name_tool -change ${dir}/libopencv_${lib1}.2.4.dylib @loader_path/libopencv_${lib1}.2.4.dylib ${dst_dir}/libopencv_${lib2}.2.4.0.dylib
	done
done

# create links 
cd $dst_dir
for lib in "${libs[@]}" 
do
	ln -s libopencv_${lib}.2.4.0.dylib libopencv_${lib}.2.4.dylib
	ln -s libopencv_${lib}.2.4.0.dylib libopencv_${lib}.dylib
done
