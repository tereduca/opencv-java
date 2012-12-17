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

import controlP5.*;
import examples.*;
import processing.core.*;
import sj.opencv.Capture;
import sj.opencv.CxCore;
import sj.opencv.HighGui;
import sj.opencv.IplImage;
import sj.opencv.PUtils;
import sj.opencv.Scalar;
import sj.opencv.Constants.ColorModel;
import sj.opencv.Constants.PixelDepth;

/**
 * @author siggi
 * @date Jul 29, 2010
 */
public class Subtraction extends PApplet{

	int w = 320;
	int h = 240;

	IplImage im;
	IplImage im_sub;
	IplImage im_background = null;
	Capture capture;

	Slider sub_slider;

	@Override
	public void setup(){
		size(3*w, 2*h+150);

		// Camera initiated to capture from device
		capture = HighGui.captureFromCAM(0);

		im = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);
		im_sub = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);

		// Init GUI
		ControlP5 controlP5 = new ControlP5(this);
		sub_slider = controlP5.addSlider("sub_val", 0, 255, 100,  	20, 		2*h+20, 	10, 	100);

		System.err.println("Press b to capture the background");
	}

	@Override
	public void draw(){

		// When a frame becomes available
		if( HighGui.queryFrame(capture, im) ){
			background(70);

			// Draw it
			image(PUtils.getPImage(im), 0, 0);

			// Grab a scalar value from a gui object
			Scalar scalar = new Scalar(sub_slider.value());

			// Calculate an absolute difference between cam image and the scalar value
			CxCore.absDiffS(im, im_sub, scalar);
			image(PUtils.getPImage(im_sub), w, 0);


			// Calculate the opposite difference between cam image and the scalar value
			CxCore.subRS(im, scalar, im_sub, null);
			image(PUtils.getPImage(im_sub), 2*w, 0);

			// Subtract background image from the cam image if it has been captured
			if( im_background != null ){
				image(PUtils.getPImage(im_background), 0, h);

				CxCore.absDiff(im, im_background, im_sub);
				image(PUtils.getPImage(im_sub), 1*w, h);

				CxCore.sub(im, im_background, im_sub, null);
				image(PUtils.getPImage(im_sub), 2*w, h);
			}
			else{
				text("Press b to capture background", 100, h+50);
			}
		}
	}

	@Override
	public void keyPressed(){
		if( key == 'b' ){
			// Capture background image when button is pressed
			im_background = CxCore.cloneImage(im);
		}
	}
}
