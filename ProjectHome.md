# OpenCV-Java #
This project aims to provide convenient and rather complete Java wrappers with a "friendly" license for a lot of the functionality in the [OpenCV](http://opencv.willowgarage.com/wiki) image processing project.

**Contents**


## Processing Integration ##
"Processing is an open source programming language and environment for people who want to create images, animations, and interactions. Initially developed to serve as a software sketchbook and to teach fundamentals of computer programming within a visual context, Processing also has evolved into a tool for generating finished professional work.
http://processing.org

This library has a version that operates well within the Processing framework. Simply download our Processing distribution from the downloads section, move the "opencv\_java" directory into your "libraries" directory, which lives in your Sketchbook directory (usually ~/Documents/Processing), and just try out any of our multiple example sketches.

The Depending on which platform and architecture (osx/linux/win and 64bit/32bit) you are running, you might need to set these options in the "preferences.txt" file of the Processing system (which can be found in the Preferences pane of the GUI.
```
run.options.bits=64
run.options.bits.macosx=64
```


### Example Processing Sketch ###
```
import sj.opencv.*;
import sj.opencv.Constants.*;
import sj.opencv.CxCore.*;
import sj.opencv.ImgProc.*;
import sj.opencv.HighGui.*;

IplImage im, im_gray, im_thresh;
Capture capture;

void setup(){
	size(640, 240);
	// Camera initiated to capture from device
	capture = HighGui.captureFromCAM(0);
        // Initialize all image buffers
	im = CxCore.createImage(320, 240, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);
	im_gray = CxCore.createImage(320, 240, PixelDepth.IPL_DEPTH_8U, ColorModel.GRAY);
	im_thresh = CxCore.createImage(320, 240, PixelDepth.IPL_DEPTH_8U, ColorModel.GRAY);
        println("Move mouse to change threshold parameter");
}

void draw(){
	// When a frame becomes available start processing
	if( HighGui.queryFrame(capture, im) ){
		background(70);
		// Draw it
		image(PUtils.getPImage(im), 0, 0);  // Draw raw camera image
		ImgProc.cvtColor(im, im_gray);      // Convert camera image to grayscale
                // And here we perform the adaptive threshold value
		ImgProc.adaptiveThreshold(im_gray, im_thresh, 255, AdaptiveThreshAlg.CV_ADAPTIVE_THRESH_MEAN_C, 
                    AdaptiveThreshType.CV_THRESH_BINARY, 3, (int)thresh/50);
		image(PUtils.getPImage(im_thresh), 320, 0);  // Draw adaptive thresholded image
	}
}

int thresh = 0;
void mouseMoved(){
  thresh = mouseX;
}
```

## Code Outline and Principles ##
coming soon

## Loading Native OpenCV Libraries ##
coming soon