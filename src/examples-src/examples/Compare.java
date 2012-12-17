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
import processing.core.PApplet;
import sj.opencv.Capture;
import sj.opencv.CxCore;
import sj.opencv.HighGui;
import sj.opencv.ImgProc;
import sj.opencv.IplImage;
import sj.opencv.PUtils;
import sj.opencv.Constants.ColorModel;
import sj.opencv.Constants.PixelDepth;
import sj.opencv.CxCore.CompareMode;

/**
 * @author siggi
 * @date Jul 29, 2010
 */
public class Compare extends PApplet{

	int w = 320;
	int h = 240;

	IplImage im;
	IplImage im_gray;
	IplImage im_dst;
	Capture capture;

	RadioButton r;

	@Override
	public void setup(){
		size(3*w, h+150);


		// Camera initiated to capture from device
		capture = HighGui.captureFromCAM(0);

		im = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.BGR);
		im_gray = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.GRAY);
		im_dst = CxCore.createImage(w, h, PixelDepth.IPL_DEPTH_8U, ColorModel.GRAY);

		// Init GUI
		ControlP5 controlP5 = new ControlP5(this);
		r = controlP5.addRadioButton("comp_type", 80, h+20);
		for (int i=0; i<CompareMode.values().length; i++) {
			r.addItem(CompareMode.values()[i].toString(), i+1);
		}
		r.activate(0);
		System.out.println("Move mouse across width to change compare value");
	}

	@Override
	public void draw(){
		// When a frame becomes available
		if( HighGui.queryFrame(capture, im) ){
			// Draw it
			image(PUtils.getPImage(im), 0, 0);

			// Change to 1 channel
			ImgProc.cvtColor(im, im_gray);
			image(PUtils.getPImage(im_gray), w, 0);

			CompareMode type = CompareMode.CV_CMP_EQUAL;
			if( r.value() != -1) type = CompareMode.values()[(int)r.value()-1];

			CxCore.cmpS(im_gray, 255*mouseX/width, im_dst, type);
			image(PUtils.getPImage(im_dst), 2*w, 0);
		}
	}
}
