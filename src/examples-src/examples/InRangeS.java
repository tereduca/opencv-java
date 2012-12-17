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
import controlP5.ControlP5;
import controlP5.Slider;

import processing.core.PApplet;
import sj.opencv.Capture;
import sj.opencv.CxCore;
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
public class InRangeS extends PApplet{

	int w = 320;
	int h = 240;

	IplImage im;
	IplImage im_range;
	Capture capture;

	// Slider values
	Slider low_slider;
	Slider high_slider;

	@Override
	public void setup(){
		size(2*w, h+150);

		// Camera initiated to capture from device
		capture = HighGui.captureFromCAM(0);

		im = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);
		im_range = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.GRAY);

		// Init GUI
		ControlP5 controlP5 = new ControlP5(this);
		low_slider = controlP5.addSlider("low_val", 	0, 	255,	100, 	20, 		h+20, 	10, 	100);
		high_slider = controlP5.addSlider("high_val", 	0, 	255,	150, 	80, 		h+20, 	10, 	100);
	}

	@Override
	public void draw(){
		// When a frame becomes available
		if( HighGui.queryFrame(capture, im) ){
			background(70);

			// Draw it
			image(PUtils.getPImage(im), 0, 0);

			CxCore.inRangeS(im, new Scalar(low_slider.value()), new Scalar(high_slider.value()), im_range);
			image(PUtils.getPImage(im_range), w, 0);
		}
	}

}
