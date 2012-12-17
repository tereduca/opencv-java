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

import processing.core.PApplet;
import sj.opencv.Capture;
import sj.opencv.CxCore;
import sj.opencv.HighGui;
import sj.opencv.ImgProc;
import sj.opencv.ImgProc.AdaptiveThreshAlg;
import sj.opencv.ImgProc.AdaptiveThreshType;
import sj.opencv.IplImage;
import sj.opencv.PUtils;
import sj.opencv.Constants.ColorModel;
import sj.opencv.Constants.PixelDepth;
import controlP5.*;

/**
 * @author siggi
 * @date Jul 29, 2010
 */
public class AdaptiveThreshold extends PApplet{

	int w = 320;
	int h = 240;

	IplImage im;
	IplImage im_gray;
	IplImage im_thresh;
	Capture capture;


	// Slider values
	Slider param_1_slider;
	Slider param_2_slider;

	@Override
	public void setup(){
		size(2*w, h+150);

		// Camera initiated to capture from device
		capture = HighGui.captureFromCAM(0);

		im = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);
		im_gray = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.GRAY);
		im_thresh = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.GRAY);

		// Init GUI
		ControlP5 controlP5 = new ControlP5(this);
		param_1_slider = controlP5.addSlider("param1", 		2, 600, 	20, 		h+20, 	10, 	100);
		param_2_slider = controlP5.addSlider("param2", 		-70, 70, 	80, 		h+20, 	10, 	100);
	}

	@Override
	public void draw(){

		// When a frame becomes available
		if( HighGui.queryFrame(capture, im) ){
			background(70);

			// Draw it
			image(PUtils.getPImage(im), 0, 0);

			ImgProc.cvtColor(im, im_gray);

			int block_size =  (int)param_1_slider.value();
			if( block_size % 2 != 1 ){
				block_size++;
			}

			ImgProc.adaptiveThreshold(im_gray, im_thresh, 255, AdaptiveThreshAlg.CV_ADAPTIVE_THRESH_MEAN_C, AdaptiveThreshType.CV_THRESH_BINARY, block_size, param_2_slider.value());
			image(PUtils.getPImage(im_thresh), w, 0);
		}
	}

}
