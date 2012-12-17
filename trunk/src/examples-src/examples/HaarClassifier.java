/* Description and License
 * A Java library that wraps the functionality of the native image 
 * processing library OpenCV
 *
 * (c) Sigurdur Orn Adalgeirsson (siggi@alum.mit.edu)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 */
 
package examples;

import java.awt.Dimension;
import java.awt.Rectangle;

import com.sun.jna.Structure;

import processing.core.PApplet;
import sj.opencv.Capture;
import sj.opencv.CxCore;
import sj.opencv.CxCore.LineType;
import sj.opencv.HaarClassifierCascade;
import sj.opencv.HighGui;
import sj.opencv.IplImage;
import sj.opencv.MemStorage;
import sj.opencv.ObjDetect;
import sj.opencv.ObjDetect.HaarClassifierFlag;
import sj.opencv.PUtils;
import sj.opencv.Scalar;
import sj.opencv.Constants.ColorModel;
import sj.opencv.Constants.PixelDepth;
import sj.opencv.jna.JNAOpenCV;
import sj.opencv.jna.cxcore.CvSize;
import sj.opencv.jna.cxcore.CvSize.ByValue;
import sj.opencv.jna.highgui.HighguiLibrary.CvArr;


public class HaarClassifier extends PApplet {

    int w = 320;
    int h = 240;

    IplImage im;
    Capture capture;
    MemStorage storage;
    HaarClassifierCascade cascade;
    HaarClassifierFlag[] flags;
    Dimension minSize;
    Scalar red;

    @Override
    public void setup(){
        size(w, h);

        // Camera initiated to capture from device
        capture = HighGui.captureFromCAM(0);
        im = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);

        // Create memory storage and Haar classifier cascade
        storage = CxCore.createMemStorage(0);

        cascade = ObjDetect.loadHaarClassifierCascade(PUtils.guessDataLocation(this, "haarcascades/haarcascade_frontalface_default.xml"));

        // Set classifier flags and min detection window
        flags = new HaarClassifierFlag[]{HaarClassifierFlag.CV_HAAR_DO_CANNY_PRUNING};

        red = new Scalar(0,0,255,0);
    }

    @Override
    public void draw(){
        // When a frame becomes available
        if( HighGui.queryFrame(capture, im) ){

            // Detect
            Rectangle[] objects = ObjDetect.haarDetectObjects(im, cascade, storage);

            // Draw a rectangle for each detected object.
            for (int i=0 ; i < objects.length ; i++) {
                CxCore.rectangle(im, objects[i], red, 1, LineType.DEFAULT, 0);
            }

            // Draw it
            image(PUtils.getPImage(im), 0, 0);
        }
    }

}
