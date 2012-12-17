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

import java.awt.Point;

import controlP5.ControlP5;
import controlP5.Slider;

import processing.core.PApplet;
import sj.opencv.Capture;
import sj.opencv.CxCore;
import sj.opencv.CxCore.CompareMode;
import sj.opencv.HighGui;
import sj.opencv.ImgProc;
import sj.opencv.IplImage;
import sj.opencv.PUtils;
import sj.opencv.Scalar;
import sj.opencv.Constants.ColorModel;
import sj.opencv.Constants.PixelDepth;

/**
 * @author siggi
 * @date Jul 29, 2010
 */
public class FloodFill extends PApplet{

	int w = 320;
	int h = 240;

	IplImage im;
	IplImage im_res;
	Capture capture;

	int click_x = w/2;
	int click_y = h/2;

	// Slider values
	Slider low_slider;
	Slider high_slider;

	@Override
	public void setup(){
		size(2*w + 2, h+150);

		// Camera initiated to capture from device
		capture = HighGui.captureFromCAM(0);

		im = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);
		im_res = CxCore.createImage(w+2, h+2, PixelDepth.IPL_DEPTH_8U, ColorModel.GRAY);

		// Init GUI
		ControlP5 controlP5 = new ControlP5(this);
		low_slider = controlP5.addSlider("low_val", 	0, 	10,	5, 	20, 		h+20, 	10, 	100);
		high_slider = controlP5.addSlider("high_val", 	0, 	10,	5, 	80, 		h+20, 	10, 	100);
	}

	@Override
	public void draw(){
		// When a frame becomes available
		if( HighGui.queryFrame(capture, im) ){
			background(70);

			// Draw it
			image(PUtils.getPImage(im), 0, 0);

			// Start by clearing the mask (it needs to be cleared for repeatable behavior)
			CxCore.set(im_res, new Scalar(0), null);

			// Perform the floodfilling
			ImgProc.floodFill(im, new Point( click_x, click_y ), new Scalar(low_slider.value()), new Scalar(high_slider.value()), im_res);

			// This simply changes the mask from being a 0-1 value to 0-255 value for viewing
			CxCore.cmpS(im_res, 0, im_res, CompareMode.CV_CMP_NOT_EQUAL);

			image(PUtils.getPImage(im_res), w, 0);
		}
	}

	@Override
	public void mouseClicked(){
		click_x = mouseX;
		click_y = mouseY;

		if( click_x<0 ) click_x = 0;
		if( click_x>=w ) click_x = w-1;
		if( click_y<0 ) click_x = 0;
		if( click_y>=h ) click_x = h-1;
	}
}
