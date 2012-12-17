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
import controlP5.RadioButton;
import controlP5.Slider;

import processing.core.PApplet;
import sj.opencv.Capture;
import sj.opencv.CxCore;
import sj.opencv.HighGui;
import sj.opencv.ImgProc;
import sj.opencv.IplImage;
import sj.opencv.PUtils;
import sj.opencv.Constants.ColorModel;
import sj.opencv.Constants.PixelDepth;
import sj.opencv.ImgProc.ThresholdType;

/**
 * @author siggi
 * @date Jul 29, 2010
 */
public class Canny extends PApplet{

	int w = 320;
	int h = 240;

	IplImage im;
	IplImage im_canny;
	Capture capture;

	// Slider values
	Slider thresh_slider1;
	Slider thresh_slider2;
	Slider aperature_slider;

	@Override
	public void setup(){
		size(2*w, h+150);

		// Camera initiated to capture from device
		capture = HighGui.captureFromCAM(0);

		im = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);
		im_canny = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.GRAY);

		// Init GUI
		ControlP5 controlP5 = new ControlP5(this);
		thresh_slider1 = controlP5.addSlider("threshold1", 1, 255, 	20, 		h+20, 	10, 	100);
		thresh_slider2 = controlP5.addSlider("threshold2", 1, 255, 	80, 		h+20, 	10, 	100);
		aperature_slider = controlP5.addSlider("aperature", 3, 7, 	140, 		h+20, 	10, 	100);
	}

	@Override
	public void draw(){
		// When a frame becomes available
		if( HighGui.queryFrame(capture, im) ){
			background(70);

			// Draw it
			image(PUtils.getPImage(im), 0, 0);

			int ap = Math.round( aperature_slider.value() );
			if( ap%2==0 ) ap++;

			ImgProc.canny(im, im_canny, thresh_slider1.value(), thresh_slider2.value(), ap);
			image(PUtils.getPImage(im_canny), w, 0);
		}
	}

}
