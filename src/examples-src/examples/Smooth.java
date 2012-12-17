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
import sj.opencv.ImgProc.SmoothType;
import sj.opencv.ImgProc;
import sj.opencv.IplImage;
import sj.opencv.PUtils;
import sj.opencv.Constants.ColorModel;
import sj.opencv.Constants.PixelDepth;

/**
 * @author siggi
 * @date Jul 29, 2010
 */
public class Smooth extends PApplet{

	int w = 320;
	int h = 240;

	IplImage im;
	IplImage im_smooth;
	Capture capture;


	// Slider values
	RadioButton r;
	Slider size1_slider;
	Slider size2_slider;
	Slider param3_slider;
	Slider param4_slider;

	@Override
	public void setup(){
		size(2*w, h+150);

		// Camera initiated to capture from device
		capture = HighGui.captureFromCAM(0);

		im = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);
		im_smooth = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);

		// Init GUI
		ControlP5 controlP5 = new ControlP5(this);
		size1_slider = controlP5.addSlider("size1", 	3, 9, 	20, 		h+20, 	10, 	100);
		size2_slider = controlP5.addSlider("size2", 	3, 9, 	70, 		h+20, 	10, 	100);
		param3_slider = controlP5.addSlider("param3", 	1, 30, 	120, 		h+20, 	10, 	100);
		param4_slider = controlP5.addSlider("param4", 	1, 30, 	170, 		h+20, 	10, 	100);
		r = controlP5.addRadioButton("thresh_type", 400, h+20);
		for (int i=0; i < SmoothType.values().length; i++) {
			r.addItem(SmoothType.values()[i].toString(), i+1);
		}
		r.activate(0);
	}

	@Override
	public void draw(){
		// When a frame becomes available
		if( HighGui.queryFrame(capture, im) ){
			background(70);

			// Draw it
			image(PUtils.getPImage(im), 0, 0);

			SmoothType type = SmoothType.CV_BLUR;
			if( r.value() != -1) type = SmoothType.values()[(int)r.value()-1];

			int size1 = (int)size1_slider.value();
			if( size1%2 == 0 ) size1++;
			int size2 = (int)size2_slider.value();
			if( size2%2 == 0 ) size2++;

			ImgProc.smooth(im, im_smooth, type, size1, size2, param3_slider.value(), param4_slider.value());
			image(PUtils.getPImage(im_smooth), w, 0);
		}
	}

}
